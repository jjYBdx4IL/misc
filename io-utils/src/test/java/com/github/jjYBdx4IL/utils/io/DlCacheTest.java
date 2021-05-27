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
package com.github.jjYBdx4IL.utils.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class DlCacheTest {

    private static final String TEST_URL = "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js";
    
    @Test
    public void test() throws MalformedURLException, IOException {
        DlCache.get(new URL(TEST_URL),
                "ff1523fb7389539c84c65aba19260648793bb4f5e29329d2ee8804bc37a3fe6e",
                "f3de1813a4160f9239f4781938645e1589b876759cd50b7936dbd849a35c38ffaed53f6a61dbdd8a1cf43cf4a28aa9fffbfddeec9a3811a1bb4ee6df58652b31").close();
    }
}
