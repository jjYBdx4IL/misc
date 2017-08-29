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

import com.github.jjYBdx4IL.utils.env.Maven;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FileScannerTest {

    private final static File tempDir = Maven.getTempTestDir(FileScannerTest.class);
    private final static String testFile1RelPath = "sub1" + File.separator + "test1.png";
    private final static File testFile1 = new File(tempDir, testFile1RelPath);
    private final static String quotedFileSeparator = File.separator.replace("\\", "\\\\");

    @BeforeClass
    public static void before() throws IOException {
        FileUtils.cleanDirectory(tempDir);
        testFile1.getParentFile().mkdirs();
        testFile1.createNewFile();
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        List<File> files = new FileScanner().getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1.getAbsolutePath(), files.get(0).getPath());
    }

    @Test
    public void testAbsolute() throws Exception {
        List<File> files = new FileScanner(false).getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1.getAbsolutePath(), files.get(0).getPath());
    }

    @Test
    public void testAbsoluteNoFileListPopulation() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        List<File> files = new FileScanner(false){
            @Override
            public void handleFile(File file) {
                counter.incrementAndGet();
                assertEquals(testFile1.getAbsolutePath(), file.getPath());
            }
        }.disableFileListPopulation().getFiles(tempDir);
        assertEquals(0, files.size());
        assertEquals(1, counter.get());
    }

    @Test
    public void testRelative() throws Exception {
        List<File> files = new FileScanner(true).getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1RelPath, files.get(0).getPath());
    }

    @Test
    public void testAbsoluteRegex() throws Exception {
        List<File> files = new FileScanner("^sub1" + quotedFileSeparator, false).getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1.getAbsolutePath(), files.get(0).getPath());

        files = new FileScanner("ub1" + quotedFileSeparator, false).getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1.getAbsolutePath(), files.get(0).getPath());

        files = new FileScanner("sub" + quotedFileSeparator, false).getFiles(tempDir);
        assertEquals(0, files.size());
    }

    @Test
    public void testRelativeRegex() throws Exception {
        List<File> files = new FileScanner("^sub1" + quotedFileSeparator, true).getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1RelPath, files.get(0).getPath());

        files = new FileScanner("ub1" + quotedFileSeparator, true).getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1RelPath, files.get(0).getPath());

        files = new FileScanner("sub" + quotedFileSeparator, true).getFiles(tempDir);
        assertEquals(0, files.size());
    }

    @Test
    public void testRelativeDirectRegex() throws Exception {
        List<File> files = new FileScanner(Pattern.compile("^sub1" + quotedFileSeparator), true).getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1RelPath, files.get(0).getPath());

        files = new FileScanner(Pattern.compile("ub1" + quotedFileSeparator), true).getFiles(tempDir);
        assertEquals(1, files.size());
        assertEquals(testFile1RelPath, files.get(0).getPath());

        files = new FileScanner(Pattern.compile("sub" + quotedFileSeparator), true).getFiles(tempDir);
        assertEquals(0, files.size());
    }
}
