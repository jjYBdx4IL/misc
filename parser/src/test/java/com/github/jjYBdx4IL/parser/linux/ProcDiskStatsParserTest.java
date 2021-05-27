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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.github.jjYBdx4IL.parser.linux.ProcDiskStatsParser.Data;

import org.junit.Test;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ProcDiskStatsParserTest {

    @Test
    public void testGet() throws Exception {
        Data data = ProcDiskStatsParser.get("sda1", ProcDiskStatsParserTest.class.getResourceAsStream("proc_diskstats_1.txt"));
        assertNotNull(data);
        assertEquals(8, data.getMajorNumber());
        assertEquals(1, data.getMinorNumber());
        assertEquals("sda1", data.getDeviceName());
        assertEquals(599, data.getReadsCompletedSuccessfully());
        assertEquals(156, data.getReadsMerged());
        assertEquals(53980, data.getSectorsRead());
        assertEquals(484, data.getTimeSpentReadingMillis());
        assertEquals(90, data.getWritesCompleted());
        assertEquals(0, data.getWritesMerged());
        assertEquals(180, data.getSectorsWritten());
        assertEquals(260, data.getTimeSpentWritingMillis());
        assertEquals(0, data.getIosCurrentlyInProgress());
        assertEquals(568, data.getTimeSpentDoingIOsMillis());
        assertEquals(740, data.getWeightedTimeSpentDoingIOsMillis());
    }

    // newer, extended /proc/diskstats:
    @Test
    public void testGet2() throws Exception {
        Data data = ProcDiskStatsParser.get("sda1", ProcDiskStatsParserTest.class.getResourceAsStream("proc_diskstats_2.txt"));
        assertNotNull(data);
        assertEquals(8, data.getMajorNumber());
        assertEquals(1, data.getMinorNumber());
        assertEquals("sda1", data.getDeviceName());
        assertEquals(235, data.getReadsCompletedSuccessfully());
        assertEquals(21, data.getReadsMerged());
        assertEquals(18926, data.getSectorsRead());
        assertEquals(154, data.getTimeSpentReadingMillis());
        assertEquals(2, data.getWritesCompleted());
        assertEquals(0, data.getWritesMerged());
        assertEquals(2, data.getSectorsWritten());
        assertEquals(0, data.getTimeSpentWritingMillis());
        assertEquals(0, data.getIosCurrentlyInProgress());
        assertEquals(244, data.getTimeSpentDoingIOsMillis());
        assertEquals(154, data.getWeightedTimeSpentDoingIOsMillis());
    }

    @Test
    public void testGetWrongDevice() throws Exception {
        assertNull(ProcDiskStatsParser.get("sda111", ProcDiskStatsParserTest.class.getResourceAsStream("proc_diskstats_1.txt")));
    }

}
