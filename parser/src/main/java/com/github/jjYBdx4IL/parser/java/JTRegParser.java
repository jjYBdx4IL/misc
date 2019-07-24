/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.parser.java;

import static com.github.jjYBdx4IL.utils.text.Unicode.removeNonprintableCharacters;

import com.github.jjYBdx4IL.parser.ParseException;
import com.github.jjYBdx4IL.parser.java.ExceptionParser.ParsedException;
import com.github.jjYBdx4IL.utils.time.TimeUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

// CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JTRegParser {

    private final static String UNKNOWN = "unknown";
    private final static String JTREG_SEPARATOR = "--------------------------------------------------";

    public static class TestSuite {

        private int numTests = 0;
        private int numFailed = 0;
        private int numErrors = 0;
        private int numSkipped = 0;
        private String name = null;
        private final List<TestResult> results;
        private Date date = new Date();
        private String hostname = null;
        private Properties props = null;
        private StringBuilder stdout = new StringBuilder();
        private StringBuilder stderr = new StringBuilder();
        private double seconds = 0f;

        TestSuite() {
            results = new ArrayList<>();
        }

        public void add(TestResult r) {
            results.add(r);
            numTests++;
            numFailed += r.isFailed() ? 1 : 0;
            numSkipped += r.isSkipped() ? 1 : 0;
            numErrors += r.isError() ? 1 : 0;
            seconds += r.getSeconds();
        }

        public List<TestResult> get() {
            return results;
        }

        /**
         * @return the numTests
         */
        public int getNumTests() {
            return numTests;
        }

        /**
         * @return the numFailed
         */
        public int getNumFailed() {
            return numFailed;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        public final static String TESTSUITE = "testsuite";
        public final static String ATTR_NAME = "name";
        public final static String ATTR_VALUE = "value";
        public final static String ATTR_TIME = "time";
        public final static String ATTR_TESTS = "tests";
        public final static String ATTR_FAILURES = "failures";
        public final static String ATTR_ERRORS = "errors";
        public final static String ATTR_SKIPPED = "skipped";
        public final static String SYSTEM_OUT = "system-out";
        public final static String SYSTEM_ERR = "system-err";
        public final static String TIMESTAMP = "timestamp";
        public final static String HOSTNAME = "hostname";
        public final static String PROPERTIES = "properties";
        public final static String PROPERTY = "property";

        public void toAntJUnitXML(OutputStream os) throws XMLStreamException {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xml = outputFactory.createXMLStreamWriter(os, "UTF-8");

            xml.writeStartDocument("UTF-8", "1.0");
            xml.writeStartElement(TESTSUITE);
            xml.writeAttribute(ATTR_NAME, name == null ? UNKNOWN : removeNonprintableCharacters(name));
            xml.writeAttribute(TIMESTAMP, removeNonprintableCharacters(TimeUtils.toISO8601(date)));
            xml.writeAttribute(HOSTNAME, hostname == null ? UNKNOWN : removeNonprintableCharacters(hostname));

            xml.writeAttribute(ATTR_TESTS, "" + numTests);
            xml.writeAttribute(ATTR_FAILURES, "" + numFailed);
            xml.writeAttribute(ATTR_ERRORS, "" + numErrors);
            xml.writeAttribute(ATTR_SKIPPED, "" + numSkipped);
            xml.writeAttribute(ATTR_TIME, String.format(Locale.ROOT, "%.3f", seconds));

            xml.writeStartElement(PROPERTIES);
            if (props != null) {
                final Enumeration<? extends Object> e = props.propertyNames();
                while (e.hasMoreElements()) {
                    final String name = (String) e.nextElement();
                    xml.writeStartElement(PROPERTY);
                    xml.writeAttribute(ATTR_NAME, removeNonprintableCharacters(name));
                    xml.writeAttribute(ATTR_VALUE, removeNonprintableCharacters(props.getProperty(name)));
                    xml.writeEndElement(); // PROPERTY
                }
            }
            xml.writeEndElement(); // PROPERTIES

            for (TestResult result : results) {
                result.appendTo(xml);
            }
            if (stdout.length() > 0) {
                xml.writeStartElement(SYSTEM_OUT);
                xml.writeCharacters(removeNonprintableCharacters(stdout.toString()));
                xml.writeEndElement(); // SYSTEM_OUT
            }
            if (stderr.length() > 0) {
                xml.writeStartElement(SYSTEM_ERR);
                xml.writeCharacters(removeNonprintableCharacters(stderr.toString()));
                xml.writeEndElement(); // SYSTEM_ERR
            }

            xml.writeEndElement(); // TESTSUITE
            xml.writeEndDocument();
        }

        /**
         * @return the numErrors
         */
        public int getNumErrors() {
            return numErrors;
        }

        /**
         * @return the numSkipped
         */
        public int getNumSkipped() {
            return numSkipped;
        }

        public int getNumpassed() {
            return getNumTests() - getNumFailed() - getNumSkipped() - getNumErrors();
        }
    }

    public enum Status {

        SUCCESS, FAILED, ERROR, SKIPPED;
    }

    /**
     * <code>
     * &lt;testcase name=&quot;testOversoldException&quot; classname=&quot;a.b.c.XYZTest&quot; time=&quot;0&quot;/&gt;
     * &lt;testcase name=&quot;testCalcRealizedGainsForTrade&quot; classname=&quot;a.b.c.XYZTest&quot; time=&quot;0.002&quot;&gt;
     *     &lt;failure message=&quot;expected:&amp;lt;1.0&amp;gt; but was:&amp;lt;0.0&amp;gt;&quot; type=&quot;java.lang.AssertionError&quot;&gt;&lt;![CDATA[java.lang.AssertionError: expected:&lt;1.0&gt; but was:&lt;0.0&gt;
     *         at org.junit.Assert.fail(Assert.java:88)
     *         at org.junit.Assert.failNotEquals(Assert.java:743)
     *         at org.junit.Assert.assertEquals(Assert.java:494)
     *         at org.junit.Assert.assertEquals(Assert.java:592)
     *         at a.b.c.XYZTest.test(XYZTest.java:43)
     * ]]&gt;&lt;/failure&gt;
     * &lt;/testcase&gt;
     * </code>
     */
    public static class TestResult {

        private final String name;
        private final String classname;
        private final String stdout;
        private final String stderr;
        private final double seconds;
        private final Status status;
        private String message;
        private String type;
        private String strace;

        TestResult(String name, String classname, Status status, String stdout, double seconds)
            throws ParseException, IOException {
            this.name = name;
            this.stdout = stdout;
            this.status = status;
            this.seconds = seconds;
            this.classname = classname;
            this.stderr = null;
            this.type = null;
            this.strace = null;
            this.message = null;
            if (stdout == null) {
                return;
            }
            try (InputStream is = new ByteArrayInputStream(stdout.getBytes("UTF-8"))) {
                ParsedException exc = ExceptionParser.parseFirst(is);
                if (exc == null) {
                    return;
                }
                this.type = exc.getTypeName();
                this.message = exc.getMessage();
                this.strace = exc.getStackTrace();
            }
        }

        /**
         * @return the failed
         */
        public boolean isFailed() {
            return Status.FAILED.equals(this.status);
        }

        /**
         * @return the stdout
         */
        public String getStdout() {
            return stdout;
        }

        /**
         * @return the seconds
         */
        public double getSeconds() {
            return seconds;
        }

        /**
         * @return the skipped
         */
        public boolean isSkipped() {
            return Status.SKIPPED.equals(this.status);
        }

        public boolean isError() {
            return Status.ERROR.equals(this.status);
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        public final static String TESTCASE = "testcase";
        public final static String ATTR_NAME = "name";
        public final static String ATTR_TIME = "time";
        public final static String ATTR_CLASSNAME = "classname";
        public final static String ATTR_TYPE = "type";
        public final static String ATTR_MESSAGE = "message";
        public final static String FAILURE = "failure";
        public final static String ERROR = "error";
        public final static String ATTR_SKIPPED = "skipped";
        public final static String SYSTEM_OUT = "system-out";
        public final static String SYSTEM_ERR = "system-err";

        public void appendTo(XMLStreamWriter xml) throws XMLStreamException {
            xml.writeStartElement(TESTCASE);
            xml.writeAttribute(ATTR_NAME, name == null ? UNKNOWN : removeNonprintableCharacters(name));
            xml.writeAttribute(ATTR_CLASSNAME, removeNonprintableCharacters(classname));
            xml.writeAttribute(ATTR_TIME, String.format(Locale.ROOT, "%.3f", seconds));
            if (Status.SKIPPED.equals(status)) {
                xml.writeStartElement(ATTR_SKIPPED);
                if (message != null) {
                    xml.writeAttribute(ATTR_MESSAGE, removeNonprintableCharacters(message));
                }
                xml.writeEndElement(); // ATTR_SKIPPED
            }
            if (Status.FAILED.equals(status) || Status.ERROR.equals(status)) {
                xml.writeStartElement(Status.FAILED.equals(status) ? FAILURE : ERROR);
                if (message != null) {
                    xml.writeAttribute(ATTR_MESSAGE, removeNonprintableCharacters(message));
                }
                xml.writeAttribute(ATTR_TYPE, type == null ? UNKNOWN : removeNonprintableCharacters(type));
                if (strace != null && strace.length() > 0) {
                    xml.writeCharacters(removeNonprintableCharacters(strace));
                }
                xml.writeEndElement(); // ? FAILURE : ERROR
            }
            if (stdout != null && stdout.length() > 0) {
                xml.writeStartElement(SYSTEM_OUT);
                xml.writeCharacters(removeNonprintableCharacters(stdout));
                xml.writeEndElement(); // SYSTEM_OUT
            }
            if (stderr != null && stderr.length() > 0) {
                xml.writeStartElement(SYSTEM_ERR);
                xml.writeCharacters(removeNonprintableCharacters(stderr));
                xml.writeEndElement(); // SYSTEM_ERR
            }
            xml.writeEndElement(); // TESTCASE
        }
    }

    private final static Pattern TEST_START_PATTERN = Pattern.compile("^TEST:\\s*(\\S+)$");
    private final static Pattern FINAL_TEST_RESULTS_PATTERN = Pattern
        .compile("^Test results:(?: passed: ([0-9,]+))?;?(?: failed: ([0-9,]+))?;?(?: error: ([0-9,]+))?$");
    private final static Pattern TEST_END_PATTERN = Pattern
        .compile("TEST RESULT: (Passed|Failed|Error)\\.\\s*(\\S.*)$");
    private final static String JTREG_LIT_PASSED = "Passed";
    private final static String JTREG_LIT_FAILED = "Failed";
    private final static String JTREG_LIT_ERROR = "Error";
    private final static String EOL = "\n";
    private final static Pattern SUMMARY_PATTERN = Pattern.compile("^Summary:\\s*(\\S+)\\s*$");
    private final static Pattern SUMMARY_FAILED_PATTERN = Pattern.compile("^FAILED:\\s+(\\S+)$");
    private final static Pattern FINAL_STATS_PATTERN = Pattern
        .compile("^TEST STATS: name=(\\S+)\\s+run=(\\d+)\\s+pass=(\\d+)\\s+fail=(\\d+)$");
    private final static Pattern TIME_PATTERN = Pattern
        .compile("^(?:TIME|  (?:build|compile|testng|shell|main)):\\s+(\\d+\\.\\d+)\\s+seconds$");

    @SuppressWarnings("unused")
    public static TestSuite parse(InputStream is) throws ParseException, IOException {
        TestSuite suite = new TestSuite();
        Scanner s = new Scanner(is);
        try {
            Matcher m = null;
            int state = 0;
            int ln = 0;
            StringBuilder testStdout = null;
            String testFilename = null;
            Status testStatus = null;
            String testMessage = null;
            double testSeconds = .0;
            Set<String> failedTestFilenames = new HashSet<>();
            boolean allowGarbageAtStart = true;
            /**
             * states:.
             * <ul>
             * <li>0: separator line, -----------------... -> 1
             * <li>1: wait for: "Test results: passed: 2; failed: 1; error: 1"
             * -> 3 or "TEST: " -> 2
             * <li>2: wait for: "TEST RESULT: " -> 0
             * <li>3: wait for: "Summary: jdk_core"
             * <li>4: wait for: "TEST STATS: name=jdk_core run=4 pass=2 fail=2"
             * -> 5, or "FAILED: java/net/Inet6Address/B6558853.java" -> 4
             * <li>5: dump rest of input
             * </ul>
             */
            while (s.hasNextLine()) {
                String l = s.nextLine();
                ln++;
                state: switch (state) {
                    case 0:
                        if (JTREG_SEPARATOR.equals(l)) {
                            state++;
                            testStdout = new StringBuilder();
                            allowGarbageAtStart = false;
                            continue;
                        }
                        if (allowGarbageAtStart) {
                            continue;
                        }
                        break;
                    case 1:
                        testStdout.append(l).append(EOL);
                        m = TEST_START_PATTERN.matcher(l);
                        if (m.find()) {
                            testFilename = m.group(1);
                            state++;
                            continue;
                        }
                        m = FINAL_TEST_RESULTS_PATTERN.matcher(l);
                        if (m.find()) {
                            int testsPassed = Integer
                                .parseInt(m.group(1) == null ? "0" : m.group(1).replaceAll("[^0-9]", ""));
                            int testsFailed = Integer
                                .parseInt(m.group(2) == null ? "0" : m.group(2).replaceAll("[^0-9]", ""));
                            int testsError = Integer
                                .parseInt(m.group(3) == null ? "0" : m.group(3).replaceAll("[^0-9]", ""));
                            if (suite.getNumpassed() != testsPassed || suite.getNumErrors() != testsError
                                || suite.getNumFailed() != testsFailed) {
                                throw new ParseException(ln);
                            }
                            state = 3;
                        }
                        continue;
                    case 2:
                        testStdout.append(l).append(EOL);
                        m = TIME_PATTERN.matcher(l);
                        if (m.find()) {
                            testSeconds += Double.parseDouble(m.group(1).replaceAll("[^0-9\\.]", ""));
                        }
                        m = TEST_END_PATTERN.matcher(l);
                        if (m.find()) {
                            testMessage = m.group(2);
                            switch (m.group(1)) {
                                case JTREG_LIT_PASSED:
                                    testStatus = Status.SUCCESS;
                                    break;
                                case JTREG_LIT_FAILED:
                                    testStatus = Status.FAILED;
                                    break;
                                case JTREG_LIT_ERROR:
                                    testStatus = Status.ERROR;
                                    break;
                                default:
                                    break state;
                            }

                            String testName = testFilename.replaceFirst(".*[\\\\/]([^\\\\/]+)", "$1");
                            String className = testFilename;
                            if (className.matches(".*\\.java")) {
                                className = className.replaceFirst("(.*)\\.java", "$1").replaceAll("[\\\\/]", ".");
                            }
                            suite.add(
                                new TestResult(testName, className, testStatus, testStdout.toString(), testSeconds));

                            if (Status.ERROR.equals(testStatus) || Status.FAILED.equals(testStatus)) {
                                failedTestFilenames.add(testFilename);
                            }

                            testStdout = null;
                            testFilename = null;
                            testStatus = null;
                            testMessage = null;
                            testSeconds = .0;

                            state = 0;
                        }
                        continue;
                    case 3:
                        m = SUMMARY_PATTERN.matcher(l);
                        if (m.find()) {
                            suite.setName(m.group(1));
                            state++;
                        }
                        continue;
                    case 4:
                        m = SUMMARY_FAILED_PATTERN.matcher(l);
                        if (m.find()) {
                            String failedTestFilename = m.group(1);
                            if (!failedTestFilenames.remove(failedTestFilename)) {
                                throw new ParseException(ln);
                            }
                            continue;
                        }
                        m = FINAL_STATS_PATTERN.matcher(l);
                        if (m.find()) {
                            String suiteName = m.group(1);
                            int testsRun = Integer.parseInt(m.group(2));
                            int testsPassed = Integer.parseInt(m.group(3));
                            int testsNotPassed = Integer.parseInt(m.group(4));

                            if (suite.getNumTests() != testsRun || suite.getNumpassed() != testsPassed
                                || suite.getNumErrors() + suite.getNumFailed() != testsNotPassed
                                || !suite.getName().equals(suiteName)) {
                                throw new ParseException(ln);
                            }

                            state++;
                            continue;
                        }
                        break;
                    case 5:
                        continue;
                    default:
                        break;
                }
                throw new ParseException("failed to parse input at line " + ln + ": " + l);
            }

            if (state != 5) {
                throw new ParseException("wrong state " + state, ln);
            }

            if (!failedTestFilenames.isEmpty()) {
                throw new IllegalStateException();
            }
        } finally {
            s.close();
        }
        return suite;
    }

}
