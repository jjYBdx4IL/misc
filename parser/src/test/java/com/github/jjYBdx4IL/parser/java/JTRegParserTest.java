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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.parser.ParseException;
import com.github.jjYBdx4IL.parser.java.JTRegParser.TestSuite;
import com.github.jjYBdx4IL.utils.junit4.PropertyRestorer;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JTRegParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(JTRegParserTest.class);
    private final static PropertyRestorer propertyRestorer = PropertyRestorer.getInstance();
    private final static Charset RES_ENCODING = Charset.forName("UTF-8");

    @After
    public void afterTest() {
        propertyRestorer.restoreProps();
    }

    @Test
    public void testXMLOutputEscape() throws Exception {
        String input = getResAsString(3);
        input = input.substring(0, 420) + "\u0000" + input.substring(420);
        TestSuite suite = JTRegParser.parse(new ByteArrayInputStream(input.getBytes(RES_ENCODING)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        suite.toAntJUnitXML(baos);
    }

    @SuppressWarnings("unused")
	@Test
    public void testParserInputWithGarbageAtStart() throws Exception {
        TestSuite suite = JTRegParser.parse(getRes(3));
    }

    @SuppressWarnings("unused")
	@Test(expected = ParseException.class)
    public void testParserInputWithGarbageInBetween() throws Exception {
        TestSuite suite = JTRegParser.parse(getRes(4));
    }

    @Test
    public void testParserAndXMLOutput() throws Exception {
        TestSuite suite = JTRegParser.parse(getRes(1));

        assertEquals(4, suite.getNumTests());
        assertEquals(2, suite.getNumpassed());
        assertEquals(1, suite.getNumFailed());
        assertEquals(1, suite.getNumErrors());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        suite.toAntJUnitXML(baos);
        LOG.debug(baos.toString());

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(baos.toByteArray()));
        Element root = doc.getRootElement();
        assertEquals("testsuite", root.getName());
        assertEquals("jdk_core", root.getAttributeValue("name"));
        assertEquals("4", root.getAttributeValue("tests"));
        assertEquals("1", root.getAttributeValue("errors"));
        assertEquals("1", root.getAttributeValue("failures"));
        assertEquals("0", root.getAttributeValue("skipped"));

        assertEquals(4, root.getChildren("testcase").size());

        Element test1 = root.getChildren("testcase").get(0);
        assertEquals("sanity.sh", test1.getAttributeValue("name"));
        assertEquals("com/oracle/net/sanity.sh", test1.getAttributeValue("classname"));
        assertEquals("0.060", test1.getAttributeValue("time"));

        Element test2 = root.getChildren("testcase").get(1);
        assertEquals("KeepAliveSockets.java", test2.getAttributeValue("name"));
        assertEquals("com.sun.corba.transport.KeepAliveSockets", test2.getAttributeValue("classname"));

        Element test3 = root.getChildren("testcase").get(2);
        assertEquals(1, test3.getChildren("failure").size());
        Element failure = test3.getChildren("failure").get(0);
        assertEquals("java.net.ConnectException", failure.getAttributeValue("type"));
        assertEquals("Connection timed out", failure.getAttributeValue("message"));
        assertEquals("java.net.ConnectException:", failure.getText().substring(0, "java.net.ConnectException:".length()));
        assertEquals("63.326", test3.getAttributeValue("time"));

        Element test4 = root.getChildren("testcase").get(3);
        assertEquals(1, test4.getChildren("error").size());
        assertEquals("600.145", test4.getAttributeValue("time"));
    }

    @Test
    public void testXMLLocaleIndependence() throws Exception {
        for (Locale locale : new Locale[]{Locale.GERMAN, Locale.ROOT}) {
            propertyRestorer.setDefaultLocale(locale);

            Element xmlRoot = getSuiteXml(1);
            assertTrue("locale fixation", xmlRoot.getAttributeValue("time").matches("[0-9\\.]+"));

            Element test1 = xmlRoot.getChildren("testcase").get(0);
            assertTrue("locale fixation", test1.getAttributeValue("time").matches("[0-9\\.]+"));
        }
    }

    @Test
    public void testParserAndXMLOutputLarge() throws Exception {
        TestSuite suite = JTRegParser.parse(getRes(2));

        assertEquals(3458, suite.getNumTests());
        assertEquals(3446, suite.getNumpassed());
        assertEquals(10, suite.getNumFailed());
        assertEquals(2, suite.getNumErrors());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        suite.toAntJUnitXML(baos);
        LOG.debug(baos.toString());

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(baos.toByteArray()));
        Element root = doc.getRootElement();
        assertEquals("testsuite", root.getName());
        assertEquals("jdk_core", root.getAttributeValue("name"));
        assertEquals("3458", root.getAttributeValue("tests"));
        assertEquals("2", root.getAttributeValue("errors"));
        assertEquals("10", root.getAttributeValue("failures"));
        assertEquals("0", root.getAttributeValue("skipped"));
    }

    protected Element getSuiteXml(int i) throws Exception {
        TestSuite suite = JTRegParser.parse(getRes(1));

        assertEquals(4, suite.getNumTests());
        assertEquals(2, suite.getNumpassed());
        assertEquals(1, suite.getNumFailed());
        assertEquals(1, suite.getNumErrors());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        suite.toAntJUnitXML(baos);
        LOG.debug(baos.toString());

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(baos.toByteArray()));
        return doc.getRootElement();

    }

    protected InputStream getRes(int i) throws IOException {
        return JTRegParserTest.class
                .getResourceAsStream(String.format("jtreg_output_%d.txt", i));
    }
    protected String getResAsString(int i) throws IOException {
        try (InputStream is = getRes(i)) {
            return IOUtils.toString(is, RES_ENCODING);
        }
    }

}
