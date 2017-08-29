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
package com.github.jjYBdx4IL.utils.net.yahoo;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.jjYBdx4IL.utils.net.yahoo.YahooClient;
import com.github.jjYBdx4IL.utils.net.yahoo.YahooIval;
import com.github.jjYBdx4IL.utils.net.yahoo.YahooObservation;
import com.github.jjYBdx4IL.utils.net.yahoo.YahooObservations;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class YahooClientTest {

    @Test
    public void testGetSortedList() throws IOException, ParseException {
        // for stability, don't run this test unless run specifically
        Assume.assumeTrue(Surefire.isSingleTestExecution());

        YahooClient yahoo = new YahooClient();
    	YahooObservations quotes = yahoo.get("^TNX", YahooIval.MONTH);
        List<YahooObservation> list = quotes.getObservations();
        assertTrue(list.size() > 10);
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).getDate().before(list.get(i + 1).getDate()));
        }
    }

}
