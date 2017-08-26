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
package com.github.jjYBdx4IL.test.selenium;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.test.AdHocHttpServer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.awt.GraphicsEnvironment;
import java.net.URL;

/**
 *
 * @author jjYBdx4IL
 */
public class SeleniumTestBaseTest extends SeleniumTestBase {

    protected static AdHocHttpServer server = null;
    protected static URL testPage1 = null;
    protected final static String testPage1Content
            = "<html><head></head><body>"
            + "  <select class=\"gwt-ListBox\" name=\"selectName\">"
            + "    <option value=\"Select a topic:\">Select a topic:</option>"
            + "    <option value=\"GenericVal\">Generic</option>"
            + "    <option value=\"OSD App\">OSD App</option>"
            + "  </select>"
            + "  <input name=\"inputName1\" type=\"text\">"
            + "</body></html>";

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = new AdHocHttpServer();
        testPage1 = server.addStaticContent("/testPage1",
                new AdHocHttpServer.StaticResponse(testPage1Content));
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (server != null) {
            server.close();
            server = null;
        }
    }

    @Before
    public void before() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }
    
    // TODO: CI env detection
    @Ignore
    @Test
    public void testStress() throws WebElementNotFoundException, InterruptedException {
        for (int i = 0; i < 100; i++) {
            getDriver().get(testPage1.toExternalForm());
            waitForElement("xpath://input[@name='inputName1']");
            stopDriver();
        }
    }

    @Test
    public void testSetInputFieldValue_String_String() throws Exception {
        getDriver().get(testPage1.toExternalForm());

        setInputFieldValue("inputName1", "valueOfInputName1");

        WebElement el = getDriver().findElement(By.xpath("//input[@name='inputName1']"));
        assertEquals("valueOfInputName1", el.getAttribute("value"));

        setInputFieldValue("selectName", "GenericVal");

        el = getDriver().findElement(By.xpath("//select[@name='selectName']"));
        assertEquals("GenericVal", new Select(el).getFirstSelectedOption().getAttribute("value"));
    }

    @Test
    public void testAssertElement() throws Exception {
        getDriver().get(testPage1.toExternalForm());

        assertElement("xpath://select");
        assertElement("xpath:/html");
        assertNotFound("xpath:/body");

        // xpath or
        assertElement("xpath:/select|/html|/body");
        assertNotFound("xpath:/select|/body");
        assertNotFound("xpath:/*/html|/body");
        assertElement("xpath://*[@name='selectName' and (name()='input' or name()='select')]");
    }
}
