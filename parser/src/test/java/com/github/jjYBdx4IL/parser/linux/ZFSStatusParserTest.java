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

import com.github.jjYBdx4IL.parser.ParseException;
import com.github.jjYBdx4IL.parser.linux.ZFSStatusParser.AlertLevel;
import com.github.jjYBdx4IL.parser.linux.ZFSStatusParser.Result;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ZFSStatusParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(ZFSStatusParserTest.class);

    @Test
    public void testParse() throws IOException, ParseException {
        int[][] expectedResults = {
            // file num, error prio, numpools, activity
            {1, 1, 1, 0},
            {2, 0, 1, 0},
            {3, 2, 1, 0},
            {4, 3, 1, 0},
            {5, 2, 1, 0},
            {6, 2, 1, 0},
            {7, 1, 1, 0},
            {8, 2, 1, 0},
            {9, 2, 2, 0},
            {10, 1, 1, 0},
            {11, 2, 1, 1},
            {12, -1, 1, 0},
            {13, -1, 1, 0},
            {14, -1, 1, 0},
            {15, 2, 3, 0},
            {16, 0, 1, 1},
            {17, 3, 3, 0},
        };

        for (int[] expectedResult : expectedResults) {
            String inputFileName = String.format("zpool_status_%d.txt", expectedResult[0]);
            LOG.debug(inputFileName);
            @SuppressWarnings("deprecation")
            String zpoolStatusCmdOutput = IOUtils.toString(
                    ZFSStatusParserTest.class.getResourceAsStream(inputFileName));
            try {
                Result actualResult = ZFSStatusParser.parse(zpoolStatusCmdOutput);
                Result expResult = new Result(AlertLevel.getByNumericLevel(expectedResult[1]), expectedResult[2],
                        expectedResult[3] == 1);
                assertEquals(inputFileName, expResult, actualResult);
            } catch (ParseException ex) {
                assertEquals(inputFileName, expectedResult[1], -1);
                LOG.debug("", ex);
            }
        }
    }

}
