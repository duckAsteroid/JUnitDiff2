package org.duck.asteroid.junitdiff;

import org.duck.asteroid.junitdiff.io.*;
import org.duck.asteroid.junitdiff.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * The program entry point for JUnitDiff2
 */
public class Main
{
	/** Code returned if there is a fatal error processing input */
	public static final int EXIT_ERROR = -1;


	/**
	 * Entry point -
	 * parses arguments {@link #expandArgs(String[])}
	 * performs analysis {@link #analyse(AnalysisInput)}
	 * reports results {@link #writeOutput(Model, String)}
	 * @param args Program arguments see {@link #printUsage()}
	 */
	public static final void main(String ... args) {
		AnalysisInput input = expandArgs(args);
		if (input.getInputSources().size() < 2) {
			printUsage();
			System.exit(EXIT_ERROR);
		}
		Model m = analyse(input);
		String outputFile = input.getOption(AnalysisInput.OPT_OUTPUT);
		try
		{
			writeOutput(m, outputFile);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(EXIT_ERROR);
		}
		finally
		{
			input.closeAllResources();
		}
		System.exit(m.getIDs().size());
	}

	/**
	 * Tell the user how to use this program if the args are bad
	 */
	private static void printUsage()
	{
		System.out.println("You must supply arguments (either files, ZIP file(s) or >=1 directories) - that yield 2 or more files for comparison");
		System.out.println("To change output file use the -output=<filename> option (default "+AnalysisInput.DEFAULT_OUTPUT+")");
		System.out.println("The class names can be mapped using an optional -mapping=<properties filename>");
	}

	/**
	 * Parse the input arguments and expand into a set of sources/resources and options encapsulated in an analysis input
	 * @param args the runtime args to parse
	 * @return an analysis input object encapsulating the expanded arguments
	 */
	private static AnalysisInput expandArgs(String[] args)
	{
		AnalysisInput result = new AnalysisInput();
		for (String arg : args )
		{
			if (arg.startsWith("-"))
			{
				int split = arg.indexOf('=');
				String command = arg.substring(1, split);
				String value = arg.substring(split + 1);
				result.addOption(command, value);
			}
			else
			{
				parseFileArg(arg, result);
			}
		}
		return result;
	}

	/**
	 * Parse what might be a java.io.File argument
	 * @param arg the potential filename
	 * @param input The input to add the result to
	 */
	private static void parseFileArg(String arg, AnalysisInput input)
	{
		try
		{
			File f = new File(arg);
			if (f.exists()) {
				if (f.isDirectory()) {
					File[] list = f.listFiles((dir, name) -> name.endsWith(".xml"));
					for(File child : list)
					{
						addXmlFile(input, child);
					}
				}
				else if(f.getName().endsWith(".zip")) {
					ZipFile zip = new ZipFile(f);
					input.addResource(zip);
					Enumeration<? extends ZipEntry> e = zip.entries();
					while(e.hasMoreElements()){
						ZipEntry zipEntry = e.nextElement();
						if (zipEntry.getName().endsWith(".xml")) {
							input.addSource(new Source.ZipEntrySource(zip, zipEntry));
						}
					}
				}
				else if (f.getName().endsWith(".xml")) {
					addXmlFile(input, f);
				}
				else {
					System.err.println("Unknown file type "+f.toString() +" - ignored!");
				}
			}
			else {
				System.err.println("File/directory " + arg+ " does not exist?");
			}
		}
		catch (ZipException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Add an XML file to the input
	 * @param input the input to update
	 * @param child the XML file
	 * @throws FileNotFoundException if not a valid filename
	 */
	private static void addXmlFile(AnalysisInput input, File child) throws FileNotFoundException
	{
		Source.FileSource fileSource = new Source.FileSource(child);
		input.addSource(fileSource);
		input.addResource(fileSource.open());
	}


	/**
	 * Analyse the input to create the model
	 * @param input the analysis input configuration
	 * @return a new model derived from the input
	 */
	private static Model analyse(AnalysisInput input)
	{
		List<Source> inputSources = input.getInputSources();
		Model result = new Model(inputSources);

		// mappings are used to adjust the result for renamed tests
		String mappingFile = input.getOption("mapping");
		Properties mapping = new Properties();
		if (mappingFile != null) {
			try
			{
				mapping.load(new FileInputStream(mappingFile));
				System.out.println("Mappings loaded: "+mapping.size());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		// load the data from each of the sources
		System.out.println("Analysing " + inputSources.size() + " test files:");
		for (Source src : input.getInputSources())
		{
			try
			{
				System.out.print('.');
				XMLReader xmlReader = XMLReaderFactory.createXMLReader();
				xmlReader.setContentHandler(new Reader(mapping, src, result));
				InputSource saxInput = new InputSource(src.open());
				xmlReader.parse(saxInput);
			}
			catch (SAXException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("DONE");
		return result;
	}


	/**
	 * Writes the model to the output file configured in the input params (if specified)
	 * @param m the model
	 * @param outputFile the file to write
	 * @throws IOException If something bad happens writing the file
	 */
	private static void writeOutput(Model m, String outputFile) throws IOException
	{
		System.out.println("Writing results to "+outputFile);
		CsvWriter writer = new CsvWriter(m, new File(outputFile));
		writer.write();
		writer.close();
	}
}
