package org.duck.asteroid.junitdiff;

import org.duck.asteroid.junitdiff.io.Source;

import java.io.File;
import java.util.*;

/**
 * Created by Chris on 28/01/2016.
 */
public class Model
{
	/** A map of result lists keyed by TestIDs */
	private HashMap<TestID, TestResult[]> model = new HashMap<>();
	/** all the file sources in the model */
	private final List<Source> sources;

	public Model(List<Source> sources) {
		this.sources = Collections.unmodifiableList(sources);
	}

	public List<Source> getSources()
	{
		return sources;
	}

	public TestResult[] getResults(final TestID id) {
		TestResult[] results = getResults(id, false);
		return results;
	}

	protected TestResult[] getResults(final TestID id, final boolean create)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("test ID cannot be null");
		}
		if (!model.containsKey(id) && create)
		{
			TestResult[] results = new TestResult[sources.size()];
			model.put(id, results);
		}
		return model.get(id);
	}

	public void addResult(TestID id, TestResult result) {
		TestResult[] results = getResults(id, true);
		int index = sources.indexOf(result.getSource());
		if (index >= 0) {
			results[index] = result;
		}
	}

	public Collection<TestID> getIDs() {
		return model.keySet();
	}

	public List<TestID> getSortedIDs() {
		ArrayList<TestID> ids = new ArrayList<>(getIDs());
		Collections.sort(ids, (o1, o2) -> o1.toString().compareTo(o2.toString()));
		return ids;
	}



}
