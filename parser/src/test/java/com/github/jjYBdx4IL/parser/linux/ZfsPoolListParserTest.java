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

import com.github.jjYBdx4IL.parser.linux.ZfsPoolListParser.Result;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.List;

public class ZfsPoolListParserTest {

    @Test
    public void testName() throws Exception {
        String input = IOUtils.toString(
            ZfsStatusParserTest.class.getResourceAsStream("zpoolListHp"), UTF_8);
        List<Result> r = ZfsPoolListParser.parse(input);
        assertEquals(2, r.size());
        assertEquals("bpool", r.get(0).name);
        assertEquals(265662464, r.get(0).free);
        assertEquals("rpool", r.get(1).name);
        assertEquals(19.f, r.get(1).freePct, 1.f);
    }
}
