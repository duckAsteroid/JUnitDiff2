package org.duck.asteroid.junitdiff.io;

import org.duck.asteroid.junitdiff.Model;
import org.duck.asteroid.junitdiff.TestID;
import org.duck.asteroid.junitdiff.TestResult;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Writes a CSV from the model
 */
public class CsvWriter
{

	private static final String HEADER = "class,name,";

	private static final String EMPTY_RESULT = ",";

	public static final char SEPARATOR = ',';

	public static final String DURATION = "duration";

	private final Model model;

	private final PrintWriter writer;

	public CsvWriter(Model model, File f) throws IOException
	{
		this(model, new PrintWriter(new FileWriter(f)));
	}

	public CsvWriter(Model model, PrintWriter writer)
	{
		this.model = model;
		this.writer = writer;
	}
	
	public void write() {
		writeHeader();
		List<TestID> ids = model.getSortedIDs();
		System.out.println("Writing "+ ids.size() +" rows:");
		for (TestID id: ids )
		{
			TestResult[] results = model.getResults(id);
			writeRow(id, results);
		}
		System.out.println("DONE");
	}

	private void writeHeader()
	{
		writer.append(HEADER);
		writer.append(SEPARATOR);
		Iterator<Source> iter = model.getSources().iterator();
		while(iter.hasNext())
		{
			Source src = iter.next();
			writer.append(src.getName());
			writer.append(SEPARATOR);
			writer.append(DURATION);
			if (iter.hasNext()) {
				writer.append(SEPARATOR);
			}
		}
		writer.println();
	}

	private void writeRow(TestID id, TestResult[] results)
	{
		writer.append(id.getClassName());
		writer.append(SEPARATOR);
		writer.append(id.getName());
		writer.append(SEPARATOR);
		for(int i = 0; i < results.length; i++) {
			TestResult result = results[i];
			if (result != null) {
				writer.append(result.getState().name());
				writer.append(SEPARATOR);
				writer.append(result.getDuration().toString());
			}
			else {
				writer.append(EMPTY_RESULT);
			}
			if (i < results.length - 1) {
				writer.append(SEPARATOR);
			}
		}
		writer.println();
	}


	public void close()
	{
		writer.close();
	}
}
