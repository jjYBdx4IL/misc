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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.jjYBdx4IL.utils.io.DirWatchService.BackLogEntry;

public class DirWatchServiceTest {
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testName() throws Exception {
        final AtomicLong processCount = new AtomicLong(0);
        final AtomicLong debouncedCount = new AtomicLong(0);
        final AtomicInteger createCount = new AtomicInteger(0);
        final AtomicInteger modifyCount = new AtomicInteger(0);
        final AtomicInteger deleteCount = new AtomicInteger(0);
        final AtomicBoolean error = new AtomicBoolean(false);
        
        final File testFile = new File(folder.getRoot(), "test.dat");
        
        DirWatchService dws = new DirWatchService(folder.getRoot().toPath()).recursive().debounce(200)
        .callback(new DirWatchService.Callback() {
            @Override
            public void process(BackLogEntry ble) {
                processCount.incrementAndGet();
                if (!ble.path.equals(testFile.toPath())) {
                    error.set(true);
                }
                if ("ENTRY_CREATE".equals(ble.kind)) {
                    createCount.incrementAndGet();
                }
                else if ("ENTRY_MODIFY".equals(ble.kind)) {
                    modifyCount.incrementAndGet();
                }
                else if ("ENTRY_DELETE".equals(ble.kind)) {
                    deleteCount.incrementAndGet();
                }
            }

            @Override
            public void debounced() {
                debouncedCount.incrementAndGet();
            }
        });
        
        Thread t = new Thread(dws);
        t.start();
        Thread.sleep(40);
        
        for(int i=0; i<10; i++) {
             FileUtils.writeStringToFile(testFile, "testcontent", UTF_8);
        }
        Thread.sleep(100);
        assertEquals(0, processCount.get());
        assertEquals(0, debouncedCount.get());
        Thread.sleep(200);
        assertTrue(processCount.get() >= 10);
        assertEquals(1, debouncedCount.get());
        
        t.interrupt();
        t.join();
        
        assertFalse(error.get());
    }
}
