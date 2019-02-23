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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
//CHECKSTYLE:OFF
@JUtilsCommandAnnotation(
        name = "grep",
        help = "recursive grep starting in the current working directory (glob is ant-style)",
        usage = "<filename-glob> <content-regex>",
        minArgs = 2,
        maxArgs = 2
)
public class RecursiveGrep implements JUtilsCommandInterface {

    private static final String OPTNAME_QUIET = "q";
    private boolean optQuiet = false;

    @Override
    public int run(CommandLine line, String[] args) {
        final String fileSearchGlob = line.getArgs()[0];
        final Pattern contentSearchPattern = Pattern.compile(line.getArgs()[1], Pattern.CASE_INSENSITIVE);
        int filesMatched = 0;
        int filesTotal = 0;
        optQuiet = line.hasOption(OPTNAME_QUIET);

        File rootDir = new File(System.getProperty("user.dir"));
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(rootDir);
        scanner.setIncludes(new String[] { fileSearchGlob });
        scanner.setCaseSensitive(false);
        scanner.scan();

        try {
	        for (String file : scanner.getIncludedFiles()) {
	        	filesTotal++;
	            if (searchFileContents(new File(rootDir, file), contentSearchPattern)) {
	                filesMatched++;
	            }
	        }
        } catch (IOException ex) {
        	throw new RuntimeException(ex);
        }

        if (!optQuiet) {
            System.out.println(String.format("%d/%d files matched.", filesMatched, filesTotal));
        }

        return 0;
    }

    private boolean searchFileContents(File file, Pattern searchPattern) throws IOException {
        // avoid stale symlinks
        if (!file.exists()) {
            return false;
        }

        boolean matched = false;
        try (Scanner scanner = new Scanner(file)) {
            int lineIdx = 0;
            boolean printedHeader = false;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineIdx++;
                if (searchPattern.matcher(line).find()) {
                    if (!printedHeader) {
                        printedHeader = true;
                        if (!optQuiet) {
                            System.out.print("Found matches in: ");
                        }
                        System.out.println(file.getAbsolutePath());
                    }
                    if (!optQuiet) {
                        System.out.println(String.format("%05d: %s", lineIdx, line));
                    }
                    matched = true;
                    if (optQuiet) {
                        break;
                    }
                }
            }
        }
        return matched;
    }

    @Override
    public Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption(OPTNAME_QUIET, "quiet", false, "quiet, show only matching file names");
        return options;
    }

}
