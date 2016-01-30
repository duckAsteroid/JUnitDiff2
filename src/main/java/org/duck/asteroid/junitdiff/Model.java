package org.duck.asteroid.junitdiff;

import org.duck.asteroid.junitdiff.io.Source;

import java.io.File;
import java.util.*;

/**
 * This class gathers together the results of analysis.
 * In essence multiple test results (on per source file) keyed by their common test ID.
 */
public class Model
{
	/** A map of result lists keyed by TestIDs */
	private HashMap<TestID, TestResult[]> model = new HashMap<TestID, TestResult[]>();
	/** all the file sources in the model */
	private final List<Source> sources;


	/**
	 * Construct to accept data from the supplied sources
	 * @param sources A list of Sources for the model
	 */
	public Model(List<Source> sources) {
		this.sources = Collections.unmodifiableList(sources);
	}


	/**
	 * A read only list of the sources in the model. The results returned from {@link #getResults(TestID)}
	 * are in the same order as this list.
	 * @return a list of sources
	 */
	public List<Source> getSources()
	{
		return sources;
	}

	/**
	 * Internal helper to get the array of
	 * @param id The test ID to get results for
	 * @return An array of results (may all be null) the size of the number of #getSources
	 */
	public TestResult[] getResults(final TestID id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("test ID cannot be null");
		}
		if (!model.containsKey(id))
		{
			TestResult[] results = new TestResult[sources.size()];
			model.put(id, results);
		}
		return model.get(id);
	}


	/**
	 * Adds a result (from a particular source) to the model under the given Test ID
	 * @param id the ID of the test
	 * @param result the new result
	 */
	public void addResult(TestID id, TestResult result) {
		TestResult[] results = getResults(id);
		int index = sources.indexOf(result.getSource());
		if (index >= 0) {
			results[index] = result;
		}
	}


	/**
	 * An unsorted collection of the TestIDs in the model
	 * @return the test IDs
	 */
	public Collection<TestID> getIDs() {
		return model.keySet();
	}


	/**
	 * Get an alpha sorted list of the TestID.toString() form
	 * @return a sorted list of TestIDs
	 * @see TestID#toString()
	 */
	public List<TestID> getSortedIDs() {
		ArrayList<TestID> ids = new ArrayList<>(getIDs());
		Collections.sort(ids, (o1, o2) -> o1.toString().compareTo(o2.toString()));
		return ids;
	}



}
