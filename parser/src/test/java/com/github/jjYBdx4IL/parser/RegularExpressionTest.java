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
package com.github.jjYBdx4IL.parser;

import static com.github.jjYBdx4IL.parser.RegularExpression.JAVA_TYPENAME;
import static com.github.jjYBdx4IL.parser.RegularExpression.JAVA_TYPENAME_ARG_PKGNAME;
import static com.github.jjYBdx4IL.parser.RegularExpression.JAVA_TYPENAME_ARG_SIMPLENAME;

import com.github.jjYBdx4IL.utils.junit4.RegularExpressionTestBase;

import org.junit.Test;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class RegularExpressionTest extends RegularExpressionTestBase {

    @Test
    public void testJAVA_CLASSNAME() {
        assertRegexMatch(JAVA_TYPENAME, "0");
        assertRegexMatch(JAVA_TYPENAME, "D", null, "D");
        assertRegexMatch(JAVA_TYPENAME, "a.D", "a", "D");
        assertRegexMatch(JAVA_TYPENAME, "a.b.D", "a.b", "D");
        assertRegexMatch(JAVA_TYPENAME, "a.b.c.D", "a.b.c", "D");
        assertRegexMatchNG(JAVA_TYPENAME, "a.b.c.D", JAVA_TYPENAME_ARG_PKGNAME, "a.b.c", JAVA_TYPENAME_ARG_SIMPLENAME, "D");
    }

}
