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

import com.github.jjYBdx4IL.parser.linux.ProcPidStatData;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ProcPidStatDataTest {

    @Test
    public void test1() throws IOException, URISyntaxException {
        ProcPidStatData pidStats = new ProcPidStatData(new File(getClass().getResource("procPidStat1.txt").toURI()));
        assertEquals(1289, pidStats.getPID());
        assertEquals(1272, pidStats.getPPID());
        assertEquals(1271, pidStats.getPGID());
        assertEquals("java", pidStats.getName());
        assertEquals(144421, pidStats.getRSS());
    }

}
