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

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
// CHECKSTYLE IGNORE MagicNumber FOR NEXT 1000 LINES
public class ProcNetDevParserTest {

    @Test
    public void test() throws Exception {
        ProcNetDevData dd = ProcNetDevParser.get("eth0", ProcNetDevParserTest.class.getResourceAsStream("procNetDevDump1.txt"));
        assertEquals(324573497L, dd.getReceiveBytes());
        assertEquals(424959L, dd.getReceivePackets());
        assertEquals(0L, dd.getReceiveCompressed());
        assertEquals(0L, dd.getReceiveDrop());
        assertEquals(0L, dd.getReceiveErrs());
        assertEquals(0L, dd.getReceiveFifo());
        assertEquals(0L, dd.getReceiveFrame());
        assertEquals(0L, dd.getReceiveMulticast());
        assertEquals(43963006L, dd.getTransmitBytes());
        assertEquals(337366L, dd.getTransmitPackets());
        assertEquals(0L, dd.getTransmitCarrier());
        assertEquals(0L, dd.getTransmitColls());
        assertEquals(0L, dd.getTransmitCompressed());
        assertEquals(0L, dd.getTransmitDrop());
        assertEquals(0L, dd.getTransmitErrs());
        assertEquals(0L, dd.getTransmitFifo());

        dd = ProcNetDevParser.get("tun0", ProcNetDevParserTest.class.getResourceAsStream("procNetDevDump1.txt"));
        assertEquals(208320L, dd.getReceiveBytes());
        assertEquals(2480L, dd.getReceivePackets());
        assertEquals(1L, dd.getReceiveErrs());
        assertEquals(2L, dd.getReceiveDrop());
        assertEquals(3L, dd.getReceiveFifo());
        assertEquals(4L, dd.getReceiveFrame());
        assertEquals(5L, dd.getReceiveCompressed());
        assertEquals(6L, dd.getReceiveMulticast());
        assertEquals(208321L, dd.getTransmitBytes());
        assertEquals(2481L, dd.getTransmitPackets());
        assertEquals(7L, dd.getTransmitErrs());
        assertEquals(8L, dd.getTransmitDrop());
        assertEquals(9L, dd.getTransmitFifo());
        assertEquals(10L, dd.getTransmitColls());
        assertEquals(11L, dd.getTransmitCarrier());
        assertEquals(12L, dd.getTransmitCompressed());
    }
}
