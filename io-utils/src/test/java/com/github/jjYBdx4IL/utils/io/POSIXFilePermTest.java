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

//CHECKSTYLE:OFF
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class POSIXFilePermTest {

    @Test
    public void testToNumber() {
        Set<PosixFilePermission> attrs = new HashSet<>();
        attrs.add(PosixFilePermission.OWNER_EXECUTE);
        attrs.add(PosixFilePermission.OWNER_EXECUTE);
        attrs.add(PosixFilePermission.OWNER_READ);
        attrs.add(PosixFilePermission.OWNER_WRITE);
        attrs.add(PosixFilePermission.GROUP_READ);
        attrs.add(PosixFilePermission.GROUP_EXECUTE);
        assertEquals("750", POSIXFilePerm.toNumber(attrs));
    }

}
