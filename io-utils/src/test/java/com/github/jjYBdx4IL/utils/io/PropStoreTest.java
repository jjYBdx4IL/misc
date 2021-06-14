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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;

public class PropStoreTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void testPropStoreGetI() throws Exception {
        Path f = folder.getRoot().toPath();
        Path store = f.resolve("store");
        PropStore ps = PropStore.geti(store);
        ps.entry("test").set(3L);
        
        String storeContent = Files.readString(store);
        assertTrue(storeContent.contains("test=3"));
    }
    
    @Test
    public void testPropStoreGet() throws Exception {
        Path f = folder.getRoot().toPath();
        Path store = f.resolve("store");
        PropStore ps = PropStore.get(store);
        ps.entry("test").set(3L);
        
        String storeContent = Files.readString(store);
        assertFalse(storeContent.contains("test"));
    }
}
