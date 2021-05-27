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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ZipUtilsTest {

    private static final File TEST_ZIP;
    
    static {
        try {
            TEST_ZIP = new File(ZipUtilsTest.class.getResource("test.zip").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testExtractRecreate() throws IOException {
        File destDir = new File(tempFolder.getRoot(), "unpacked");
        
        ZipUtils.extractRecreate(TEST_ZIP, null, destDir, 0);
        assertEquals(2, FindUtils.globFiles(destDir, "**").size()); // 2 files
        
        ZipUtils.extractRecreate(TEST_ZIP, "^(?!123)", destDir, 0);
        assertEquals("test", FindUtils.globFiles(destDir, "**").get(0).getName());
        
        ZipUtils.extractRecreate(TEST_ZIP, "^(?!123)", destDir, 1);
        assertEquals(1, FindUtils.globFiles(destDir, "/test").size());
        
        try {
            ZipUtils.extractRecreate(TEST_ZIP, null, destDir, 1);
            fail();
        } catch (IOException ex) {
        }
        
        try {
            ZipUtils.extractRecreate(TEST_ZIP, "not-existing", destDir, 0);
            fail();
        } catch (IOException ex) {
        }
    }
    
    @Test
    public void testTest() throws Exception {
        assertTrue(TEST_ZIP.exists() && TEST_ZIP.isFile() && TEST_ZIP.length() > 50);
        byte[] testZipContents = FileUtils.readFileToByteArray(TEST_ZIP);

        // positive test
        assertTrue(ZipUtils.test(TEST_ZIP));
        
        // some selected negative tests
        testModifiedZip(testZipContents, 0);
        testModifiedZip(testZipContents, 1);
        testModifiedZip(testZipContents, 2);
        testModifiedZip(testZipContents, 3);
        
        // negative tests (not all working)
//        for (int i=0; i<testZip.length(); i++) {
//            File corruptedZip = createInvalidZip(testZipContents, i);
//            if(ZipUtils.test(corruptedZip)) {
//                ProcRunner pr = new ProcRunner("unzip", "-t", corruptedZip.getAbsolutePath());
//                assertEquals(""+ i, 0, pr.run());
//            }
//        }
    }
    
    private void testModifiedZip(byte[] testZipContents, int corruptedPosition) throws IOException {
        File modifiedZip = createInvalidZip(testZipContents, corruptedPosition);
        assertFalse(ZipUtils.test(modifiedZip));
    }

    private File createInvalidZip(byte[] testZipContents, int corruptedPosition) throws IOException {
        File zipFile = tempFolder.newFile();
        byte[] modifiedZipContents = Arrays.copyOf(testZipContents, testZipContents.length);
        if (corruptedPosition != -1) {
            modifiedZipContents[corruptedPosition]++;
        }
        FileUtils.writeByteArrayToFile(zipFile, modifiedZipContents);
        return zipFile;
    }
}
