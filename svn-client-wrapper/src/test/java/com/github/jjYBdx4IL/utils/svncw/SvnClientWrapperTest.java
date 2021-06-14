/*
 * Copyright © 2021 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.svncw;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class SvnClientWrapperTest {

    SvnClientWrapper scw = new SvnClientWrapper();
    
    @Before
    public void before() throws IOException, InterruptedException {
        assumeTrue(SvnClientWrapper.isSvnCheckout(Paths.get(".")));
    }
    
    @Test
    public void testGetSvnInfo() throws Exception {
        Map<String, String> r = scw.getSvnInfo(Paths.get("."));
        assertTrue(r.containsKey("URL"));
    }
    
    @Test
    public void testGetSvnInfoR() throws Exception {
        SvnInfoResult r = scw.getSvnInfoR(Paths.get("."));
        assertTrue(r.url != null);
    }
    
    @Test
    public void testGetSvnRoot() throws Exception {
        assertEquals(SvnClientWrapper.getSvnCoRoot(Paths.get(".")), scw.getSvnRoot(Paths.get(".")));
    }
}
