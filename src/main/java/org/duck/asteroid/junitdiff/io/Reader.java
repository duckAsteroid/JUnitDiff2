package org.duck.asteroid.junitdiff.io;

import org.duck.asteroid.junitdiff.Model;
import org.duck.asteroid.junitdiff.TestID;
import org.duck.asteroid.junitdiff.TestResult;
import org.duck.asteroid.junitdiff.TestState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Using SAX reads data from a test file (XML) and adds it to a model
 */
public class Reader extends org.xml.sax.helpers.DefaultHandler
{

	private static final String TEST_CASE = "testcase";

	private static final String CLASSNAME = "classname";

	private static final String NAME = "name";

	private static final String TIME = "time";

	public static final String ERROR = "error";

	public static final String FAILURE = "failure";

	private final Model model;
	private final Source source;
	private final Properties mapping;
	private final Map<String, Pattern> patternCache = new HashMap<String, Pattern>();

	private TestID currentId;
	private TestResult currentResult;

	public Reader(Properties mapping, Source src, Model model) {
		this.mapping = mapping;
		this.source = src;
		this.model = model;
	}


	/**
	 * <testcase classname="x.y.Z" name="testAbc" time="NN.MMM"></testcase>
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase(TEST_CASE)) {
			String className = attributes.getValue(CLASSNAME);
			String name = attributes.getValue(NAME);
			Double time = Double.valueOf(attributes.getValue(TIME));
			className = doMapping(className);
			currentId = new TestID(className, name);
			currentResult = new TestResult(source, time, TestState.PASS);
		}
		else if (qName.equalsIgnoreCase(ERROR)) {
			currentResult.setState(TestState.ERROR);
		}
		else if (qName.equalsIgnoreCase(FAILURE)) {
			currentResult.setState(TestState.FAILURE);
		}
	}

	private String doMapping(String className)
	{
		for(String regex : mapping.stringPropertyNames())
		{
			if (!patternCache.containsKey(regex)) {
				patternCache.put(regex, Pattern.compile(regex));
			}
			Pattern pattern = patternCache.get(regex);
			Matcher matcher = pattern.matcher(className);
			if( matcher.find()) {
				int start = matcher.start(1);
				int end = matcher.end(1);
				StringBuilder result = new StringBuilder();
				result.append(className.substring(0, start));
				String replacement = mapping.getProperty(regex);
				result.append(replacement);
				result.append(className.substring(end));
				return result.toString();
			}
		}
		return className;
	}


	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equals(TEST_CASE)) {
			model.addResult(currentId, currentResult);
			currentResult = null;
		}
	}
}
