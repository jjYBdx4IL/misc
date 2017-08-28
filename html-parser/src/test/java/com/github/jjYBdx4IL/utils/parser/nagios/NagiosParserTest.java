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
package com.github.jjYBdx4IL.utils.parser.nagios;

//CHECKSTYLE:OFF
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
// CHECKSTYLE IGNORE MagicNumber FOR NEXT 1000 LINES
public class NagiosParserTest {

    @Test
    public void testParse() throws IOException, ParseException {
        NagiosParser instance = new NagiosParser();
        @SuppressWarnings("deprecation")
        List<CheckStatus> results = instance.parse(IOUtils.toString(
                NagiosParserTest.class.getResourceAsStream("detailedStatusCgiDump1.html")));

        assertEquals(34, results.size());

        assertEquals(1, results.get(0).getAttempt());

        // 28d 13h 46m 28s
        assertEquals(1000L * (28L + 60L * (46L + 60L * (13L + 24L * 28L))),
                results.get(0).getDurationMillis());

        assertEquals("gmx.de", results.get(0).getHost());

        // 03-24-2013 09:38:31
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        cal.clear();
        cal.set(2013, 2, 24, 9, 38, 31);
        assertEquals(cal.getTimeInMillis(), results.get(0).getLastCheck().getTime());

        assertEquals(4, results.get(0).getMaxAttempts());

        assertEquals("PING", results.get(0).getService());

        assertEquals(Status.OK, results.get(0).getStatus());

        assertTrue(results.get(0).getStatusInfo().indexOf("PING OK - Packet loss = 0%, RTA = 50.65 ms") == 0);
    }

    @Test
    public void testParse2() throws IOException, ParseException {
        NagiosParser instance = new NagiosParser();
        @SuppressWarnings("deprecation")
        List<CheckStatus> results = instance.parse(IOUtils.toString(
                NagiosParserTest.class.getResourceAsStream("detailedStatusCgiDump2.html")));

        assertEquals(38, results.size());

        assertEquals("i5.openvpn.host.de", results.get(1).getHost());
        assertEquals("ALWAYS-CRITICAL", results.get(1).getService());
        assertEquals(Status.PENDING, results.get(1).getStatus());
        assertEquals(false, results.get(1).isHard());

        assertEquals("i5.openvpn.host.de", results.get(2).getHost());
        assertEquals("ALWAYS-OK", results.get(2).getService());
        assertEquals(Status.PENDING, results.get(2).getStatus());

        assertEquals("i5.openvpn.host.de", results.get(3).getHost());
        assertEquals("ALWAYS-UNKNOWN", results.get(3).getService());
        assertEquals(Status.PENDING, results.get(3).getStatus());

        assertEquals("i5.openvpn.host.de", results.get(4).getHost());
        assertEquals("ALWAYS-WARNING", results.get(4).getService());
        assertEquals(Status.PENDING, results.get(4).getStatus());
    }

    @Test
    public void testParse3() throws IOException, ParseException {
        NagiosParser instance = new NagiosParser();
        @SuppressWarnings("deprecation")
        List<CheckStatus> results = instance.parse(IOUtils.toString(
                NagiosParserTest.class.getResourceAsStream("detailedStatusCgiDump3.html")));

        assertEquals(38, results.size());

        assertEquals("i5.openvpn.host.de", results.get(1).getHost());
        assertEquals("ALWAYS-CRITICAL", results.get(1).getService());
        assertEquals(Status.CRITICAL, results.get(1).getStatus());
        assertEquals(false, results.get(1).isAck());
        assertEquals(true, results.get(1).isHard());

        assertEquals("i5.openvpn.host.de", results.get(2).getHost());
        assertEquals("ALWAYS-OK", results.get(2).getService());
        assertEquals(Status.OK, results.get(2).getStatus());
        assertEquals(false, results.get(2).isAck());

        assertEquals("i5.openvpn.host.de", results.get(3).getHost());
        assertEquals("ALWAYS-UNKNOWN", results.get(3).getService());
        assertEquals(Status.UNKNOWN, results.get(3).getStatus());
        assertEquals(false, results.get(3).isAck());

        assertEquals("i5.openvpn.host.de", results.get(4).getHost());
        assertEquals("ALWAYS-WARNING", results.get(4).getService());
        assertEquals(Status.WARNING, results.get(4).getStatus());
        assertEquals(false, results.get(4).isAck());
    }

    @Test
    public void testParse4() throws IOException, ParseException {
        NagiosParser instance = new NagiosParser();
        @SuppressWarnings("deprecation")
        List<CheckStatus> results = instance.parse(IOUtils.toString(
                NagiosParserTest.class.getResourceAsStream("detailedStatusCgiDump4.html")));

        assertEquals(38, results.size());

        assertEquals("i5.openvpn.host.de", results.get(1).getHost());
        assertEquals("ALWAYS-CRITICAL", results.get(1).getService());
        assertEquals(Status.CRITICAL, results.get(1).getStatus());
        assertEquals(true, results.get(1).isAck());
        assertEquals(true, results.get(1).isHard());
        assertEquals(false, results.get(1).isDowntime());

        assertEquals("i5.openvpn.host.de", results.get(2).getHost());
        assertEquals("ALWAYS-OK", results.get(2).getService());
        assertEquals(Status.OK, results.get(2).getStatus());
        assertEquals(false, results.get(2).isAck());
        assertEquals(false, results.get(2).isDowntime());

        assertEquals("i5.openvpn.host.de", results.get(3).getHost());
        assertEquals("ALWAYS-UNKNOWN", results.get(3).getService());
        assertEquals(Status.UNKNOWN, results.get(3).getStatus());
        assertEquals(true, results.get(3).isAck());
        assertEquals(false, results.get(3).isDowntime());

        assertEquals("i5.openvpn.host.de", results.get(4).getHost());
        assertEquals("ALWAYS-WARNING", results.get(4).getService());
        assertEquals(Status.WARNING, results.get(4).getStatus());
        assertEquals(true, results.get(4).isAck());
        assertEquals(false, results.get(4).isDowntime());
    }

    @Test
    public void testParse5() throws IOException, ParseException {
        NagiosParser instance = new NagiosParser();
        @SuppressWarnings("deprecation")
        List<CheckStatus> results = instance.parse(IOUtils.toString(
                NagiosParserTest.class.getResourceAsStream("detailedStatusCgiDump5.html")));

        assertEquals(38, results.size());

        assertEquals("i5.openvpn.host.de", results.get(1).getHost());
        assertEquals("ALWAYS-CRITICAL", results.get(1).getService());
        assertEquals(true, results.get(1).isDowntime());

        assertEquals("i5.openvpn.host.de", results.get(2).getHost());
        assertEquals("ALWAYS-OK", results.get(2).getService());
        assertEquals(true, results.get(2).isDowntime());

        assertEquals("i5.openvpn.host.de", results.get(3).getHost());
        assertEquals("ALWAYS-UNKNOWN", results.get(3).getService());
        assertEquals(false, results.get(3).isDowntime());

        assertEquals("i5.openvpn.host.de", results.get(4).getHost());
        assertEquals("ALWAYS-WARNING", results.get(4).getService());
        assertEquals(false, results.get(4).isDowntime());
    }

    @Test
    public void testGetEffectiveStatus() throws IOException, ParseException {
        NagiosParser instance = new NagiosParser();
        @SuppressWarnings("deprecation")
        List<CheckStatus> results = instance.parse(IOUtils.toString(
                NagiosParserTest.class.getResourceAsStream("detailedStatusCgiDump5.html")));

        assertEquals(38, results.size());

        assertEquals("i5.openvpn.host.de", results.get(1).getHost());
        assertEquals("ALWAYS-CRITICAL", results.get(1).getService());
        assertEquals(true, results.get(1).isAck());
        assertEquals(true, results.get(1).isDowntime());
        assertEquals(Status.CRITICAL, results.get(1).getEffectiveStatus(false, false));
        assertEquals(Status.OK, results.get(1).getEffectiveStatus(true, false));
        assertEquals(Status.OK, results.get(1).getEffectiveStatus(false, true));
        assertEquals(Status.OK, results.get(1).getEffectiveStatus(true, true));

        assertEquals("i5.openvpn.host.de", results.get(2).getHost());
        assertEquals("ALWAYS-OK", results.get(2).getService());
        assertEquals(false, results.get(2).isAck());
        assertEquals(true, results.get(2).isDowntime());
        assertEquals(Status.OK, results.get(2).getEffectiveStatus(false, false));
        assertEquals(Status.OK, results.get(2).getEffectiveStatus(true, false));
        assertEquals(Status.OK, results.get(2).getEffectiveStatus(false, true));
        assertEquals(Status.OK, results.get(2).getEffectiveStatus(true, true));

        assertEquals("i5.openvpn.host.de", results.get(3).getHost());
        assertEquals("ALWAYS-UNKNOWN", results.get(3).getService());
        assertEquals(true, results.get(3).isAck());
        assertEquals(false, results.get(3).isDowntime());
        assertEquals(Status.UNKNOWN, results.get(3).getEffectiveStatus(false, false));
        assertEquals(Status.OK, results.get(3).getEffectiveStatus(true, false));
        assertEquals(Status.UNKNOWN, results.get(3).getEffectiveStatus(false, true));
        assertEquals(Status.OK, results.get(3).getEffectiveStatus(true, true));

        assertEquals("i5.openvpn.host.de", results.get(4).getHost());
        assertEquals("ALWAYS-WARNING", results.get(4).getService());
        assertEquals(true, results.get(4).isAck());
        assertEquals(false, results.get(4).isDowntime());
        assertEquals(Status.WARNING, results.get(4).getEffectiveStatus(false, false));
        assertEquals(Status.OK, results.get(4).getEffectiveStatus(true, false));
        assertEquals(Status.WARNING, results.get(4).getEffectiveStatus(false, true));
        assertEquals(Status.OK, results.get(4).getEffectiveStatus(true, true));
    }
}
