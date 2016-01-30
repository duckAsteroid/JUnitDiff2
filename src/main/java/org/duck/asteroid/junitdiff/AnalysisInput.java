package org.duck.asteroid.junitdiff;

import org.duck.asteroid.junitdiff.io.Source;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

/**
 * A class gathering together the input to the analysis.
 * A list of sources and resources, and some properties
 */
public class AnalysisInput
{
	public static final String OPT_OUTPUT = "output";

	static final String DEFAULT_OUTPUT = "comparison.csv";

	private final List<Source> inputSources = new ArrayList<>();

	private final List<Closeable> inputResources = new ArrayList<>();

	private final Map<String, String> options = new HashMap<>();

	public AnalysisInput() {
		options.put(OPT_OUTPUT, DEFAULT_OUTPUT);
	}

	public String getOption(String opt) {
		return options.get(opt);
	}

	public List<Source> getInputSources()
	{
		return inputSources;
	}

	public void closeAllResources() {
		for (Closeable c : inputResources )
		{
			try
			{
				c.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}


	public void addResource(Closeable res)
	{
		inputResources.add(res);
	}


	public void addSource(Source src)
	{
		inputSources.add(src);
	}


	public void addOption(String option, String value)
	{
		options.put(option, value);
	}
}
