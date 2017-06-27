/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.misc.jutils.cmds;

import com.github.jjYBdx4IL.misc.jutils.JUtilsCommandAnnotation;
import com.github.jjYBdx4IL.misc.jutils.JUtilsCommandInterface;

import com.github.jjYBdx4IL.utils.io.FileScanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
@JUtilsCommandAnnotation(
        name = "mrs",
        help = "searches the local maven repository for classes",
        usage = "<class-name-regex>",
        minArgs = 1,
        maxArgs = 1
)
public class MavenLocalRepoClassFileSearch implements JUtilsCommandInterface {

    private static final Logger log = LoggerFactory.getLogger(MavenLocalRepoClassFileSearch.class);
    private static final Pattern jarFilePattern
            = Pattern.compile("^(?!.*-(sources|javadoc|tests)\\.jar$).*\\.jar$", Pattern.CASE_INSENSITIVE);

    @Override
    public int run(CommandLine line) {
        Pattern searchPattern = Pattern.compile(line.getArgs()[0], Pattern.CASE_INSENSITIVE);
        final AtomicInteger filesMatched = new AtomicInteger(0);
        final AtomicInteger filesTotal = new AtomicInteger(0);

        File repoDir = new File(System.getProperty("user.home"), ".m2/repository");
        FileScanner fileScanner = new FileScanner(jarFilePattern) {

            @Override
            public void handleFile(File file) throws IOException {
                filesTotal.incrementAndGet();
                if (searchJarFile(file, searchPattern)) {
                    filesMatched.incrementAndGet();
                }
            }

        }.disableFileListPopulation();
        try {
            fileScanner.getFiles(repoDir);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        System.out.println(String.format("%d/%d files matched.", filesMatched.intValue(), filesTotal.intValue()));

        return 0;
    }

    private boolean searchJarFile(File jarFile, Pattern searchPattern) throws IOException {
        // avoid stale symlinks
        if (!jarFile.exists()) {
            return false;
        }

        ZipFile zipFile = new ZipFile(jarFile);
        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> zipEntries = (Enumeration<ZipEntry>) zipFile.entries();
        List<String> found = new ArrayList<>();
        while (zipEntries.hasMoreElements()) {
            ZipEntry ze = zipEntries.nextElement();
            String s = ze.getName();
            if (searchPattern.matcher(s).find()) {
                found.add(s);
            }
        }
        if (found.isEmpty()) {
            return false;
        }
        System.out.print("Found matches in: ");
        System.out.println(jarFile.getAbsolutePath());
        for (String s : found) {
            System.out.print("  ");
            System.out.println(s);
        }
        return true;
    }

    @Override
    public Options getCommandLineOptions() {
        return null;
    }

}
