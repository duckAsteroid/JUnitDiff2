# JUnitDiff2
Summarises many JUnit test result files in a single file.
Uses no external dependencies (pure Java 8).

## Result
The program produces a CSV file with a header row - two columns containing the classname and test name; followed by two columns for each test file in the input (source name and duration).

The rows then contain each "unique" Test ID (classname + test name) - followed by the results for each result source:

* the "state" from the result: PASS, FAILURE, ERROR
* the time taken (in seconds) from the result

For example:

    classname,name,A.xml,duration,B.xml,duration
    com.acme.tests.A,testSomething,PASS,3.0,,
    com.acme.tests.B,testSomething,,,PASS,4.0
    com.acme.tests.Both,testStuff,PASS,5.0,PASS,4.0
    com.acme.tests.Both,testBadStuff,FAILURE,1.0,PASS,4.0

## Mapping
Class names can be optionally "mapped" to allow comparison of renamed test classes. Supply a properties file

each line consists of:
<regex>=<replacement>

The regex is expected to identify target classes and have a single group identified for replacement (e.g. com\.acme\.(old\.)package)
The replacement string (which may be empty) is used to replace the group - so a replacement of "new" yields com.acme.new.package in the resulting TestID