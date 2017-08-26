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
package com.github.jjYBdx4IL.utils.xml;

//CHECKSTYLE:OFF
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XMLUtilsTest {

    @Test
    public void testStrip() throws Exception {
        assertEquals("<a> </a>", strip("<a> </a>"));
        assertEquals("<a><b>  </b></a>", strip("<a> <b>  </b></a>"));
        assertEquals("<a><b>  </b><b/></a>", strip("<a> <b>  </b><b/>\n</a>"));
        assertEquals("<a> <b/>text</a>", strip("<a> <b/>text</a>"));
    }

    private static String strip(String xml) throws Exception {
        return XMLUtils.stripXMLHeader(XMLUtils.strip(xml));
    }
    
}
