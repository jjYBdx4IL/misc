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
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.parser.ParseException;
import com.github.jjYBdx4IL.parser.linux.ZfsStatusParser.AlertLevel;
import com.github.jjYBdx4IL.parser.linux.ZfsStatusParser.Result;
import com.github.jjYBdx4IL.utils.time.TimeUtils;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ZfsStatusParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(ZfsStatusParserTest.class);

    private static class Res {
        public final String filename;
        public final int expPrio;
        public final int expPools;
        public final int expActivity;
        public final Instant since;

        Res(String fn, int expPrio, int expPools, int expActivity, Instant since) {
            this.filename = fn;
            this.expPrio = expPrio;
            this.expActivity = expActivity;
            this.expPools = expPools;
            this.since = since;
        }
    }

    @Test
    public void testParse2() throws IOException, ParseException, java.text.ParseException {
        Res[] expectedResults = new Res[] {
            new Res("zpool_output_resilver", 0, 2, 1, TimeUtils.parseISO8601("2020-12-13T00:24:31Z").toInstant()),
            new Res("zpool_output_resilver_long", 0, 2, 1, TimeUtils.parseISO8601("2020-12-09T00:24:31Z").toInstant()),
            new Res("zpool_output_scrub", 0, 2, 1, TimeUtils.parseISO8601("2020-12-13T00:24:31Z").toInstant()),
            new Res("zpool_output_scrub_bug1", 0, 2, 0, null),
            new Res("zpool_output_scrub_long", 0, 2, 1, TimeUtils.parseISO8601("2020-12-09T00:24:31Z").toInstant()),
            new Res("zpool_output_scrub_old", 0, 2, 1, TimeUtils.parseISO8601("2020-12-13T00:24:31Z").toInstant())
        };

        for (Res exp : expectedResults) {
            LOG.debug(exp.filename);
            try {
                Result actualResult = parse(exp.filename);
                Result expResult = new Result(AlertLevel.getByNumericLevel(exp.expPrio), exp.expPools,
                    exp.expActivity == 1, exp.since);
                assertEquals(exp.filename, expResult, actualResult);
            } catch (ParseException ex) {
                LOG.info("", ex);
                assertEquals(exp.filename, exp.expPrio, -1);
            }
        }
    }

    @Test
    public void testParse() throws IOException, ParseException {
        int[][] expectedResults = {
            // file num, error prio, numpools, activity
            { 1, 1, 1, 0 },
            { 2, 0, 1, 0 },
            { 3, 2, 1, 0 },
            { 4, 3, 1, 0 },
            { 5, 2, 1, 0 },
            { 6, 2, 1, 0 },
            { 7, 1, 1, 0 },
            { 8, 2, 1, 0 },
            { 9, 2, 2, 0 },
            { 10, 1, 1, 0 },
            { 11, 2, 1, 1 },
            { 12, -1, 1, 0 },
            { 13, -1, 1, 0 },
            { 14, -1, 1, 0 },
            { 15, 2, 3, 0 },
            { 16, 0, 1, 1 },
            { 17, 3, 3, 0 },
        };

        for (int[] expectedResult : expectedResults) {
            String inputFileName = f("zpool_status_%d.txt", expectedResult[0]);
            LOG.debug(inputFileName);
            try {
                Result actualResult = parse(inputFileName);
                Result expResult = new Result(AlertLevel.getByNumericLevel(expectedResult[1]), expectedResult[2],
                    expectedResult[3] == 1, null);
                assertEquals(inputFileName, expResult, actualResult);
            } catch (ParseException ex) {
                assertEquals(inputFileName, expectedResult[1], -1);
                LOG.debug("", ex);
            }
        }
    }

    private Result parse(String res) throws IOException, ParseException {
        String zpoolStatusCmdOutput = IOUtils.toString(
            ZfsStatusParserTest.class.getResourceAsStream(res), UTF_8);
        return ZfsStatusParser.parse(zpoolStatusCmdOutput);
    }

}
