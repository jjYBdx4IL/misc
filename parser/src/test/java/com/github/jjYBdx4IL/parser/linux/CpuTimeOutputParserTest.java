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

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.parser.ParseException;
import com.github.jjYBdx4IL.parser.linux.CpuTimeOutputParser.UsageData;

import org.junit.Test;

import java.util.List;

public class CpuTimeOutputParserTest {

    @Test
    public void testParse() throws ParseException {
        assertEquals("UsageData [real=38197ms, user=98938ms, sys=2804ms]", CpuTimeOutputParser.parse(
            "real    0m38,197s\n" + 
            "user    1m38,938s\n" + 
            "sys 0m2,804s").toString());
    }
    
    @Test
    public void testParseMulti() throws ParseException {
        assertEquals(0, CpuTimeOutputParser.parseMulti("").size());
        
        List<UsageData> result = CpuTimeOutputParser.parseMulti(
            "real    0m38,197s\n" + 
            "user    1m38,938s\n" + 
            "sys 0m2,804s");
        assertEquals("UsageData [real=38197ms, user=98938ms, sys=2804ms]", result.get(0).toString());
        
        result = CpuTimeOutputParser.parseMulti(
            "real    0m38,197s\n" + 
            "user    1m38,938s\n" + 
            "sys 0m2,804s" +
            "real    0m38,198s\n" + 
            "user    1m38,939s\n" + 
            "sys 0m2,805s");
        assertEquals("UsageData [real=38197ms, user=98938ms, sys=2804ms]", result.get(0).toString());
        assertEquals("UsageData [real=38198ms, user=98939ms, sys=2805ms]", result.get(1).toString());
    }
    
    @Test
    public void testDumpStats() throws ParseException {
        List<UsageData> result = CpuTimeOutputParser.parseMulti(
            "real    0m1,0s\n" + 
            "user    0m1,0s\n" + 
            "sys 0m1,0s\n" +
            "real    0m1,0s\n" + 
            "user    0m2,0s\n" + 
            "sys 0m1,0s");
        assertEquals(
            f("real: (1,000 +/- 0)ms %n" + 
            "user: (1,500 +/- 707)ms %n" + 
            "sys: (1,000 +/- 0)ms"),
            CpuTimeOutputParser.dumpStats(result));
    }
}
