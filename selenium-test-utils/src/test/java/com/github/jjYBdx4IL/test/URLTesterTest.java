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
package com.github.jjYBdx4IL.test;

import java.io.IOException;

import org.junit.After;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link URLTester}.
 *
 * @author jjYBdx4IL
 */
// CHECKSTYLE IGNORE EmptyBlock FOR NEXT 1000 LINES
public class URLTesterTest extends HttpStatusCodeTestBase {

    public URLTesterTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of assertExists method, of class URLTester.
     */
    @Test
    public void testAssertExists() throws Exception {
        URLTester.assertExists(getServerUrl() + URI_200_OK);
        try {
            URLTester.assertExists(getServerUrl() + URI_500_ERROR);
            fail();
        } catch (IOException ex) {
        }
    }

    /**
     * Test of assertNotExists method, of class URLTester.
     */
    @Test
    public void testAssertNotExists() throws Exception {
        URLTester.assertNotExists(getServerUrl() + URI_404_NOT_FOUND);
        try {
            URLTester.assertNotExists(getServerUrl() + URI_500_ERROR);
            fail();
        } catch (IOException ex) {
        }
    }

    /**
     * Test of assertNotFound method, of class URLTester.
     */
    @Test
    public void testAssertNotFound() throws Exception {
        URLTester.assertNotFound(getServerUrl() + URI_404_NOT_FOUND);
    }

    /**
     * Test of getContent method, of class URLTester.
     */
    @Test
    public void testGetContent() throws Exception {
        assertEquals(TESTRESPONSE, URLTester.getContent(getServerUrl() + URI_200_OK));
    }

    /**
     * Test of assertContains method, of class URLTester.
     */
    @Test
    public void testAssertContains() throws Exception {
        URLTester.assertContains(getServerUrl() + URI_200_OK, TESTRESPONSE);
        URLTester.assertContains(getServerUrl() + URI_200_OK, TESTRESPONSE_PART);
        try {
            URLTester.assertContains(getServerUrl() + URI_404_NOT_FOUND, TESTRESPONSE);
            fail();
        } catch (IOException ex) {
        }
        try {
            URLTester.assertContains(getServerUrl() + URI_500_ERROR, TESTRESPONSE);
            fail();
        } catch (IOException ex) {
        }
    }
}
