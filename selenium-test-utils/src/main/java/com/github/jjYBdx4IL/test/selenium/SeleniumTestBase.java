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

import com.github.jjYBdx4IL.test.JsoupTools;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class SeleniumTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumTestBase.class);
    public static final String GWT_DEBUG_ID_PREFIX = "gwt-debug-";
    private static WebDriver driver;
    public static final String OUTPUT_DIR = "target/screenshots/";
    public static final String SCREENSHOT_EXT = ".png";
    protected static final int DEFAULT_WAIT_SECS = 30;
    protected static final int CLICK_WAIT4ELEMENT_MILLIS = 120 * 1000;
    protected static final int CLICK_WAIT4ELEMENT_POLL_MILLIS = 2 * 1000;
    private static final Driver DEFAULT_DRIVER = Driver.CHROME;
    private Driver defaultDriver = DEFAULT_DRIVER;
    private static long screenShotId = 0L;
    private String testName = null;
    private static boolean seleniumDriverRestartAfterEachTest = false;

    @AfterClass
    public static void tearDown() {
        stopDriver();
    }
    
    @After
    public void after() {
        if (seleniumDriverRestartAfterEachTest) {
            stopDriver();
        }
    }
    
    protected static void enabledSeleniumDriverRestartAfterEachTest() {
        seleniumDriverRestartAfterEachTest = true;
    }

    public static void stopDriver() {
        if (driver != null) {
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    /**
     * Local screenshots take a screenshot of the entire desktop. They are required to catch alerts.
     *
     * @param name the name
     */
    public void takeLocalScreenshot(String name) {
        String outputFilePath = String.format(Locale.ROOT, "%s%03d_%s%s", OUTPUT_DIR, ++screenShotId, name,
                SCREENSHOT_EXT);
        Screenshot.takeDesktopScreenshot(outputFilePath);
    }

    /**
     * Take a screenshot of the browser's current page.
     *
     * @param name the name
     */
    public void takeScreenshot(String name) {
        TakesScreenshot takesScreenshot;
        if (getDriver() instanceof TakesScreenshot) {
            takesScreenshot = (TakesScreenshot) getDriver();
        } else {
            LOG.debug("current driver does not support taking screenshots");
            return;
        }
        try {
            String outputFilePath = String.format(Locale.ROOT, "%s%03d_%s%s", OUTPUT_DIR, ++screenShotId, name,
                    SCREENSHOT_EXT);
            File output = new File(outputFilePath);
            LOG.info("writing remote screenshot: " + output.getCanonicalPath());
            File scrFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            if (output.exists()) {
                LOG.warn("screenshot output file exists, overwriting it");
                if (!output.delete()) {
                    LOG.error("failed to remove previous screenshot file");
                }
            }
            FileUtils.moveFile(scrFile, output);
        } catch (IOException | WebDriverException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void takeScreenshot() {
        if (testName == null) {
            testName = getClass().getSimpleName();
        }
        takeScreenshot(testName);
    }

    public void setTestName(String _testName) {
        testName = _testName;
    }

    /**
     * Tries to force a key up event even when the value argument is empty or null.
     * 
     * @param el the input element
     * @param value the value to set
     */
    public void setInputFieldValue(WebElement el, String value) {
        if ("select".equals(el.getTagName().toLowerCase(Locale.ROOT))) {
            new Select(el).selectByValue(value);
            return;
        }
        if (value != null && !value.isEmpty()) {
            el.clear();
            el.sendKeys(value);
        } else {
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            el.sendKeys(Keys.DELETE);
        }
    }

    public void setInputFieldValue(String inputElementName, String value) throws WebElementNotFoundException {
        WebElement el = getInputByName(inputElementName);
        setInputFieldValue(el, value);
    }

    public void waitUntil(Function<? super WebDriver, ?> isTrue) {
        new WebDriverWait(getDriver(), CLICK_WAIT4ELEMENT_MILLIS / 1000).until(isTrue);
    }

    public String switchToWindowOtherThan(String windowHandle) {
        Iterator<String> it = getDriver().getWindowHandles().iterator();
        String switchToWindowHandle = it.next();
        if (switchToWindowHandle.equals(windowHandle)) {
            switchToWindowHandle = it.next();
        }

        getDriver().switchTo().window(switchToWindowHandle);

        return switchToWindowHandle;
    }

    public String switchToOtherWindow() {
        return switchToWindowOtherThan(getDriver().getWindowHandle());
    }

    /**
     * @return the driver
     */
    public WebDriver getDriver() {
        return getDriver(defaultDriver);
    }

    public static WebDriver getDriver(Driver driverType) {
        if (driver == null) {
            switch (driverType) {
                case CHROME:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    //chromeOptions.setBinary("/usr/bin/chromium-browser");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--dbus-stub");
                    setDriver(new ChromeDriver(chromeOptions));
                    //((WebDriver.Window)getDriver()).setSize(new Dimension(1024, 768));
                    break;
                case FIREFOX:
                    FirefoxOptions opts = new FirefoxOptions();
                    FirefoxProfile fp = new FirefoxProfile();
                    fp.setPreference("dom.max_chrome_script_run_time", 3000);
                    fp.setPreference("dom.max_script_run_time", 3000);
                    fp.setPreference("app.update.auto", false);
                    fp.setPreference("app.update.enabled", false);
                    fp.setPreference("browser.tabs.warnOnClose", false);
                    fp.setPreference("browser.tabs.warnOnOpen", false);
                    opts.setProfile(fp);
                    setDriver(new FirefoxDriver(opts));
                    break;
                case HTMLUNIT:
                    setDriver(new HtmlUnitDriver(true));
                    break;
                default:
                    throw new RuntimeException("not yet supported driver: " + driverType);
            }
            dumpVersion();
        }
        return driver;
    }

    public static void dumpVersion() {
        if (!(driver instanceof RemoteWebDriver)) {
            return;
        }
        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        LOG.info("browser: " + caps.getBrowserName() + " "
                + caps.getVersion() + " (" + caps.getPlatform() + ")");
    }

    /**
     * @param aDriver the driver to set
     */
    public static void setDriver(WebDriver aDriver) {
        driver = aDriver;
    }

    @Rule
    public TestRule seleniumTestWatcher = new TestWatcher() {

        @Override
        protected void succeeded(Description description) {
        }

        @Override
        protected void failed(Throwable e, Description description) {
            if (driver == null) {
                return;
            }
            try {
                if (e instanceof UnhandledAlertException) {
                    LOG.error(((UnhandledAlertException) e).getAlertText());
                    takeLocalScreenshot("UnhandledAlertException");
                } else if (e instanceof TimeoutException) {
                    LOG.error("timeout", e);
                    takeLocalScreenshot("TimeoutException");
                    LOG.error("body content: " + getPrettyPageSource());
                } else {
                    LOG.error("testFailure", e);
                    takeLocalScreenshot("testFailure");
                    takeScreenshot("testFailure");
                    LOG.error("body content: " + getPrettyPageSource());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
            }
        }
    };

    public void assertNotFound(String text) {
        try {
            assertElement(text);
            Assert.fail();
        } catch (WebElementNotFoundException ex) {
        }
    }

    public void assertNotFoundOrNotDisplayed(String text) {
        WebElement e;
        try {
            e = assertElement(text);
            if (!e.isDisplayed()) {
                return;
            }
            Assert.fail();
        } catch (WebElementNotFoundException ex) {
        }
    }

    /**
     * Assert that the element in question exists, is displayed, and is disabled.
     *
     * @param text the text value of the element to select via {@link #findElement}
     * @throws com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException if not found
     */
    public void assertElementDisabled(String text) throws WebElementNotFoundException {
        WebElement e = assertElement(text);
        if (!e.isDisplayed()) {
            throw new WebElementNotFoundException("found but not displayed: " + text);
        }
        if (e.isEnabled()) {
            throw new WebElementNotFoundException("found but not disabled: " + text);
        }
    }

    /**
     * Assert that the element in question exists, is displayed, but is enabled.
     *
     * @param text the text value of the element to select via {@link #findElement}
     * @throws com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException if not found
     */
    public void assertElementEnabled(String text) throws WebElementNotFoundException {
        WebElement e = assertElement(text);
        if (!e.isDisplayed()) {
            throw new WebElementNotFoundException("found but not displayed: " + text);
        }
        if (!e.isEnabled()) {
            throw new WebElementNotFoundException("found but not enabled: " + text);
        }
    }

    /**
     * Wait for some element using {@link #waitForElement}, then click it.
     *
     * @param text the text value of the element to select
     * @throws WebElementNotFoundException if not found
     * @return the element
     */
    public WebElement click(String text) throws WebElementNotFoundException {
        LOG.info("click(" + text + ")");
        WebElement el = waitForElement(text);
        clickWaitForClickable(el);
        return el;
    }
    
    public void clickWaitForClickable(WebElement el) {
        new WebDriverWait(getDriver(), DEFAULT_WAIT_SECS).until(ExpectedConditions.elementToBeClickable(el));
        new WebDriverWait(getDriver(), DEFAULT_WAIT_SECS).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    el.click();
                    return true;
                } catch (WebDriverException ex) {
                }
                return false;
            }
        });
    }

    public void scrollAndClick(WebElement clickable) {
        Actions actions = new Actions(getDriver());
        actions.moveToElement(clickable).click().perform();
    }

    public void waitForAttribute(final By locator, final String attrName, final Pattern pattern) {
        LOG.info("waitForAttribute(" + locator + ", " + attrName + ", " + pattern + ")");
        new WebDriverWait(getDriver(), DEFAULT_WAIT_SECS).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                WebElement button = driver.findElement(locator);
                String value = button.getAttribute(attrName);
                if (pattern.matcher(value).find()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * Wait until there is some element returned by {@link #findElement} which is displayed and enabled. The timeout is
     * given by {@link #CLICK_WAIT4ELEMENT_MILLIS}.
     *
     * @param text the text value of the element to select
     * @param displayed require displayed
     * @param enabled require enabled
     * @return the element
     * @throws WebElementNotFoundException if not found
     */
    public WebElement waitForElement(String text, Boolean displayed, Boolean enabled)
            throws WebElementNotFoundException {
        LOG.info("waitForElement(" + text + ")");

        WebElement e = null;
        long timeout = System.currentTimeMillis() + CLICK_WAIT4ELEMENT_MILLIS;
        do {
            try {
                e = findElement(text);
                if (e != null && enabled != null && enabled.booleanValue() != e.isEnabled()) {
                    e = null;
                }
                if (e != null && displayed != null && displayed.booleanValue() != e.isDisplayed()) {
                    e = null;
                }
            } catch (StaleElementReferenceException ex) {
                e = null;
            }
            if (e == null) {
                try {
                    Thread.sleep(CLICK_WAIT4ELEMENT_POLL_MILLIS);
                } catch (InterruptedException ex) {
                    LOG.error("", ex);
                }
            }
        } while ((e == null) && System.currentTimeMillis() < timeout);
        if (e == null) {
            throw new WebElementNotFoundException(text);
        }
        return e;
    }

    public WebElement waitForElement(String text) throws WebElementNotFoundException {
        return waitForElementDisplayedAndEnabled(text);
    }

    public WebElement waitForElementDisplayedAndEnabled(String text) throws WebElementNotFoundException {
        return waitForElement(text, true, true);
    }

    public WebElement waitForElementDisplayedAndDisabled(String text) throws WebElementNotFoundException {
        return waitForElement(text, true, false);
    }

    public WebElement findElement(String text) {
        List<WebElement> elements;
        if (text.startsWith("xpath:")) {
            elements = getDriver().findElements(By.xpath(text.substring("xpath:".length())));
            if (elements.size() > 0) {
                return elements.get(0);
            }
            return null;
        }
        elements = getDriver().findElements(By.xpath("//button[text()='" + text + "']"));
        if (elements.size() > 0) {
            return elements.get(0);
        }
        elements = getDriver().findElements(By.xpath("//label[text()='" + text + "']"));
        if (elements.size() > 0) {
            return elements.get(0);
        }
        elements = getDriver().findElements(By.xpath("//div[text()='" + text + "']"));
        if (elements.size() > 0) {
            return elements.get(0);
        }
        return null;
    }

    public WebElement getButtonByName(String name) throws WebElementNotFoundException {
        return getElementByName("button", name);
    }

    public WebElement getInputByName(String name) throws WebElementNotFoundException {
        return getElementByName("input|select|textarea", name);
    }

    public WebElement getElementByName(String elementTag, String name) throws WebElementNotFoundException {
        String xpath = "xpath://*[(name()='" + StringUtils.join(elementTag.split("\\|"), "' or name()='") + "') and @name='" + name + "']";
        return waitForElement(xpath, null, null);
    }

    public WebElement assertElement(String text) throws WebElementNotFoundException {
        WebElement e = findElement(text);
        if (e == null) {
            throw new WebElementNotFoundException("WebElement with text »" + text + "« not found; page source = "
                    + getPageSource(getDriver()));
        }
        return e;
    }

    protected String getPageSource() {
        return getPageSource(getDriver());
    }

    protected String getPageSource(WebDriver d) {
        return JsoupTools.prettyFormatHtml(d.getPageSource(), false);
    }

    /**
     * This method uses {@link JsoupTools#prettyFormatHtml(java.lang.String, boolean)}, so beware that some content may
     * get truncated for nicer display.
     *
     * @return the prettified page source
     */
    protected String getPrettyPageSource() {
        return JsoupTools.prettyFormatHtml(getDriver().getPageSource(), true);
    }

    public void sleep(int secs) {
        try {
            Thread.sleep(secs * 1000L);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getXPathForActiveElement() {
        return getXPath(getDriver().switchTo().activeElement());
    }

    public String getXPath(WebElement webElement) {
        String jscript = "function absoluteXPath(element) {"
                + "var comp, comps = [];"
                + "var parent = null;"
                + "var xpath = '';"
                + "var getPos = function(element) {"
                + "var position = 1, curNode;"
                + "if (element.nodeType == Node.ATTRIBUTE_NODE) {"
                + "return null;"
                + "}"
                + "for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling) {"
                + "if (curNode.nodeName == element.nodeName) {"
                + "++position;"
                + "}"
                + "}"
                + "return position;"
                + "};"
                + "if (element instanceof Document) {"
                + "return '/';"
                + "}"
                + "for (; element && !(element instanceof Document); element = element.nodeType == Node.ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {"
                + "comp = comps[comps.length] = {};"
                + "switch (element.nodeType) {"
                + "case Node.TEXT_NODE:"
                + "comp.name = 'text()';"
                + "break;"
                + "case Node.ATTRIBUTE_NODE:"
                + "comp.name = '@' + element.nodeName;"
                + "break;"
                + "case Node.PROCESSING_INSTRUCTION_NODE:"
                + "comp.name = 'processing-instruction()';"
                + "break;"
                + "case Node.COMMENT_NODE:"
                + "comp.name = 'comment()';"
                + "break;"
                + "case Node.ELEMENT_NODE:"
                + "comp.name = element.nodeName;"
                + "break;"
                + "}"
                + "comp.position = getPos(element);"
                + "}"
                + "for (var i = comps.length - 1; i >= 0; i--) {"
                + "comp = comps[i];"
                + "xpath += '/' + comp.name.toLowerCase();"
                + "if (comp.position !== null) {"
                + "xpath += '[' + comp.position + ']';"
                + "}"
                + "}"
                + "return xpath;"
                + "} return absoluteXPath(arguments[0]);";
        return (String) ((JavascriptExecutor) getDriver()).executeScript(jscript, webElement);
    }

    public String getXPathSimple(WebElement webElement) {
        String jscript = "function getPathTo(node) {"
                + "  var stack = [];"
                + "  while(node.parentNode !== null) {"
                + "    stack.unshift(node.tagName);"
                + "    node = node.parentNode;"
                + "  }"
                + "  return stack.join('/');"
                + "}"
                + "return getPathTo(arguments[0]);";
        return (String) ((JavascriptExecutor) getDriver()).executeScript(jscript, webElement);
    }

    public WebElement activeElement() {
        return getDriver().switchTo().activeElement();
    }

    public enum Driver {

        FIREFOX, CHROME, HTMLUNIT
    }

    public Driver getDefaultDriver() {
        return defaultDriver;
    }

    public void setDefaultDriver(Driver defaultDriver) {
        this.defaultDriver = defaultDriver;
    }
}
