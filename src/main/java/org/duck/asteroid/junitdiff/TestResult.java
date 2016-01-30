package org.duck.asteroid.junitdiff;

import org.duck.asteroid.junitdiff.io.Source;

import java.io.File;

/**
 * An individual test result for a test from a particular result source file
 */
public class TestResult
{
	private final Source source;
	private final Double duration;
	private TestState state;

	public TestResult(Source source, Double duration, TestState state)
	{
		this.source = source;
		this.duration = duration;
		this.state = state;
	}

	public Source getSource()
	{
		return source;
	}

	public TestState getState()
	{
		return state;
	}

	public void setState(TestState state)
	{
		this.state = state;
	}

	public Double getDuration()
	{
		return duration;
	}

}
