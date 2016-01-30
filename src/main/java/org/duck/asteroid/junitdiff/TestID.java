package org.duck.asteroid.junitdiff;

/**
 * The identity of a particular test (classname + testName [method])
 */
public class TestID
{
	public static final String UNKNOWN = "???";
	/** The Java class name of the test class */
	private final String className;
	/** The test name (normally the method) */
	private final String name;


	public TestID(String className, String name)
	{
		this.className = className;
		this.name = name;
	}


	public String getClassName()
	{
		if (className != null && className.length() > 0)
			return className;
		else
			return UNKNOWN;
	}


	public String getName()
	{
		if (name != null && name.length() > 0)
			return name;
		else
			return UNKNOWN;
	}


	@Override
	public String toString()
	{
		return getClassName() + "#" + getName();
	}


	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}


	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TestID)
		{
			TestID other = (TestID) obj;
			return this.toString().equals(other.toString());
		}
		else
			return super.equals(obj);
	}
}
