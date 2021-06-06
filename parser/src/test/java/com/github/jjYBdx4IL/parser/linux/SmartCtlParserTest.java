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
package com.github.jjYBdx4IL.parser.linux;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.github.jjYBdx4IL.parser.linux.SmartCtlParser.Result;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class SmartCtlParserTest {

    @Test
    public void testName() throws Exception {
        String input = IOUtils.toString(
            ZfsStatusParserTest.class.getResourceAsStream("smart_a_output_longok"), UTF_8);
        Result r = SmartCtlParser.parse(input);
        assertEquals("WD-WCC7K0YKSETV", r.devSerial);
        assertEquals(1019, r.pwrOnHrs);
        assertEquals(Long.valueOf(19), r.shortTestAgeHrs);
        assertEquals(Long.valueOf(1), r.longTestAgeHrs);
        
        input = IOUtils.toString(
            ZfsStatusParserTest.class.getResourceAsStream("smart_a_output_nolong"), UTF_8);
        r = SmartCtlParser.parse(input);
        assertEquals("WD-WCC7K2ATSZXJ", r.devSerial);
        assertEquals(10, r.pwrOnHrs);
        assertEquals(Long.valueOf(10), r.shortTestAgeHrs);
        assertNull(r.longTestAgeHrs);
    }
}
