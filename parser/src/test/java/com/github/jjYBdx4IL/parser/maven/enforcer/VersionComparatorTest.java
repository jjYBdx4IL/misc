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
package com.github.jjYBdx4IL.parser.maven.enforcer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionComparatorTest {

    @Test
    public void test() {
        assertTrue(VersionComparator.largerThan("2", "1"));
        assertTrue(VersionComparator.largerThan("02", "1"));
        assertTrue(VersionComparator.largerThan("1.1", "1"));
        assertFalse(VersionComparator.largerThan("1", "1"));
        assertFalse(VersionComparator.largerThan("1", "1.1"));
        assertFalse(VersionComparator.largerThan("1", "2.1"));
        assertFalse(VersionComparator.largerThan("1", "2"));
        assertFalse(VersionComparator.largerThan("1", "1.2a"));
    }
}
