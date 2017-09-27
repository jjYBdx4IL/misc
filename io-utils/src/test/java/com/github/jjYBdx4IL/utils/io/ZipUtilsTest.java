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

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ZipUtilsTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Test
    public void testTest() throws Exception {
        File testZip = new File(getClass().getResource("test.zip").toURI());
        assertTrue(testZip.exists() && testZip.isFile() && testZip.length() > 50);
        byte[] testZipContents = FileUtils.readFileToByteArray(testZip);

        // positive test
        assertTrue(ZipUtils.test(testZip));
        
        // some selected negative tests
        testModifiedZip(testZipContents, 0);
        testModifiedZip(testZipContents, 1);
        testModifiedZip(testZipContents, 2);
        testModifiedZip(testZipContents, 3);
        testModifiedZip(testZipContents, 150);
        testModifiedZip(testZipContents, 155);
        
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
