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
package com.github.jjYBdx4IL.utils.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArrayUtilsTest {

    @Test
    public void testIndexOfByteArray() throws Exception {
        assertEquals(-1, ArrayUtils.indexOf(new byte[] {}, new byte[] {}));
        assertEquals(-1, ArrayUtils.indexOf(new byte[] {1}, new byte[] {}));
        assertEquals(-1, ArrayUtils.indexOf(new byte[] {}, new byte[] {1}));
        
        assertEquals(0, ArrayUtils.indexOf(new byte[] { 1, 2 }, new byte[] { 1 }));
        assertEquals(1, ArrayUtils.indexOf(new byte[] { 1, 2 }, new byte[] { 2 }));
        assertEquals(-1, ArrayUtils.indexOf(new byte[] { 1, 2 }, new byte[] { 3 }));
        
        assertEquals(0, ArrayUtils.indexOf(new byte[] { 1, 2 }, new byte[] { 1, 2 }));
        assertEquals(-1, ArrayUtils.indexOf(new byte[] { 1, 2 }, new byte[] { 1, 2, 3 }));
        
        assertEquals(1, ArrayUtils.indexOf(new byte[] { 1, 2, 3 }, new byte[] { 2 }));
        assertEquals(1, ArrayUtils.indexOf(new byte[] { 1, 2, 3, 4 }, new byte[] { 2, 3 }));
        assertEquals(-1, ArrayUtils.indexOf(new byte[] { 1, 2, 3, 4 }, new byte[] { 2, 4 }));
        
        assertEquals(0, ArrayUtils.indexOf(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 3 }));
        assertEquals(1, ArrayUtils.indexOf(new byte[] { 1, 2, 3 }, new byte[] { 2, 3 }));
    }
}
