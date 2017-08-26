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
package com.github.jjYBdx4IL.utils.junit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class RegularExpressionTestBase {

    /**
     * Convenience method to test regular expressions. Like
     * {@link #assertRegexSubMatch(String,String,String...)} but regex must match all of input.
     *
     * @param regex the regular expression to test
     * @param input test string
     * @param expectedValues list length must match the the group count. an empty list asserts that the
     * pattern does not match.
     */
    public static void assertRegexMatch(String regex, String input, String... expectedValues) {
        assertRegexSubMatch("^" + regex + "$", input, expectedValues);
    }

    /**
     * Convenience method to test regular expressions.
     *
     * @param regex the regular expression to test
     * @param input test string
     * @param expectedValues list length must match the the group count. an empty list asserts that the
     * pattern does not match.
     */
    public static void assertRegexSubMatch(String regex, String input, String... expectedValues) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        if (expectedValues.length == 0) {
            assertFalse("regex does not match the test string", m.find());
            return;
        }

        assertTrue("regex matches the test string", m.find());
        assertEquals("group count equals expected values list size", expectedValues.length, m.groupCount());

        int i = 1;
        for (String expValue : expectedValues) {
            assertEquals("group with index " + i + " matches", expValue, m.group(i));
            i++;
        }
    }

    /**
     * Convenience method to test regular expressions. Named groups variant. Like
     * {@link #assertRegexSubMatchNG(String,String,String...)} but regex must match all of input.
     *
     * @param regex the regular expression to test
     * @param input test string
     * @param expectedValues (group name, expected group content) pairs. number of pairs must match the the
     * group count. an empty list asserts that the pattern does not match.
     */
    public static void assertRegexMatchNG(String regex, String input, String... expectedValues) {
        assertRegexSubMatchNG("^" + regex + "$", input, expectedValues);
    }

    /**
     * Convenience method to test regular expressions. Named groups variant.
     *
     * @param regex the regular expression to test
     * @param input test string
     * @param expectedValues (group name, expected group content) pairs. number of pairs must match the the
     * group count. an empty list asserts that the pattern does not match.
     */
    public static void assertRegexSubMatchNG(String regex, String input, String... expectedValues) {
        assertTrue(expectedValues.length % 2 == 0);

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        if (expectedValues.length == 0) {
            assertFalse("regex does not match the test string", m.find());
            return;
        }

        assertTrue("regex matches the test string", m.find());
        assertEquals("group count equals expected values list size/2", expectedValues.length / 2, m.groupCount());

        for (int i = 0; i < expectedValues.length; i += 2) {
            String groupName = expectedValues[i];
            String expValue = expectedValues[i + 1];
            assertEquals("group with name " + groupName + " matches", expValue, m.group(groupName));
        }
    }

}
