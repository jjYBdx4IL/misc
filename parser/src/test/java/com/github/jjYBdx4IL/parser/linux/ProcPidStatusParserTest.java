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

//CHECKSTYLE:OFF
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.parser.linux.ProcPidStatusParser;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ProcPidStatusParserTest {

    @Test
    public void test1() throws IOException, URISyntaxException {
        ProcPidStatusParser parser = new ProcPidStatusParser();
        parser.parse(new File(getClass().getResource("procPidStatus1.txt").toURI()));
        assertEquals(1289, parser.pid);
        assertEquals(1272, parser.ppid);
        assertEquals("java", parser.name);
        assertEquals(577784, parser.vmrss);
        assertEquals(0, parser.vmswap);
    }

}
