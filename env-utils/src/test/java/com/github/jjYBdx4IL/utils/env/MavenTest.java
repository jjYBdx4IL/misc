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
package com.github.jjYBdx4IL.utils.env;

//CHECKSTYLE:OFF
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.File;
import java.net.URI;
import org.apache.commons.io.FileUtils;

import org.junit.Test;

public class MavenTest {

    @Test
    public void testGetBasedirCpOnly() {
        assumeNotNull(System.getProperty("basedir"));
        
        URI uri = Maven.getBasedirCpOnly(getClass());
        assertTrue(new File(System.getProperty("basedir")).equals(new File(uri)));
        
        // fail on class loaded from jar file
        try {
            Maven.getBasedirCpOnly(FileUtils.class);
            fail();
        } catch (IllegalStateException ex) {}
    }

}
