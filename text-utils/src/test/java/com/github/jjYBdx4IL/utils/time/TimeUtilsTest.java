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
package com.github.jjYBdx4IL.utils.time;

//CHECKSTYLE:OFF
import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

import com.github.jjYBdx4IL.utils.time.TimeUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class TimeUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(TimeUtilsTest.class);

    @Test
    public void testToISO8601() {
        Date input = new Date(1409051344183l);
        assertEquals("2014-08-26T11:09Z", TimeUtils.toISO8601(input));
    }

    @Test
    public void testToISO8601WithSeconds() {
        Date input = new Date(1409051344183l);
        assertEquals("2014-08-26T11:09:04Z", TimeUtils.toISO8601WithSeconds(input));
    }

    @Test
    public void testParseISO8601() throws ParseException {
    	assertEquals(1409051344000l, TimeUtils.parseISO8601("2014-08-26T11:09:04Z").getTime());
    	assertEquals(1409051340000l, TimeUtils.parseISO8601("2014-08-26T11:09Z").getTime());
    }

    @Test
    public void testMillisToDuration() {
        assertEquals("0s", TimeUtils.millisToDuration(0L));
        assertEquals("0s", TimeUtils.millisToDuration(999L));
        assertEquals("1s", TimeUtils.millisToDuration(1000L));
        assertEquals("16m40s", TimeUtils.millisToDuration(1000L * 1000L));
    }

    /**
     * Test of millisToDuration method, of class Duration.
     */
    @Test
    public void testMillisToDurationInvertabilityAtRandomPoints() {
        Random r = new Random(0L);
        for (int i = 0; i < 1000; i++) {
            long millis = (long) r.nextInt(Integer.MAX_VALUE) * 1000L;
            String duration = TimeUtils.millisToDuration(millis);
            LOG.trace("duratiion = " + duration + ", millis = " + millis);
            assertEquals(millis, TimeUtils.durationToMillis(duration));
        }
    }

    /**
     * Test of durationToMillis method, of class Duration.
     */
    @Test
    public void testDurationToMillis() {
        assertEquals(0L, TimeUtils.durationToMillis(""));
        assertEquals(1000L, TimeUtils.durationToMillis("1s"));
        assertEquals(60L * 1000L, TimeUtils.durationToMillis("1m"));
        assertEquals(3600L * 1000L, TimeUtils.durationToMillis("1h"));
        assertEquals(24L * 3600L * 1000L, TimeUtils.durationToMillis("1d"));
        assertEquals(7L * 24L * 3600L * 1000L, TimeUtils.durationToMillis("1w"));
        assertEquals(8L * 24L * 3600L * 1000L, TimeUtils.durationToMillis("1w1d"));
        assertEquals(8L * 24L * 3600L * 1000L, TimeUtils.durationToMillis("1w 1d"));
    }
}
