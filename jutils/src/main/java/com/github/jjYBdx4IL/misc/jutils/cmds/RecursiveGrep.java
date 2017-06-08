/*
 * #%L
 * Java Command Line Utilities
 * %%
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 * %%
 * #L%
 */
package com.github.jjYBdx4IL.misc.jutils.cmds;

import com.github.jjYBdx4IL.misc.jutils.JUtilsCommandAnnotation;
import com.github.jjYBdx4IL.misc.jutils.JUtilsCommandInterface;

import com.github.jjYBdx4IL.utils.io.FileScanner;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
@JUtilsCommandAnnotation(
        name = "grep",
        help = "recursive grep starting in the current working directory",
        usage = "<filename-regex> <content-regex>",
        minArgs = 2,
        maxArgs = 2
)
public class RecursiveGrep implements JUtilsCommandInterface {

    private static final Logger log = LoggerFactory.getLogger(RecursiveGrep.class);
    private static final String OPTNAME_QUIET = "q";
    private boolean optQuiet = false;

    @Override
    public int run(CommandLine line) {
        final Pattern fileSearchPattern = Pattern.compile(line.getArgs()[0], Pattern.CASE_INSENSITIVE);
        final Pattern contentSearchPattern = Pattern.compile(line.getArgs()[1], Pattern.CASE_INSENSITIVE);
        final AtomicInteger filesMatched = new AtomicInteger(0);
        final AtomicInteger filesTotal = new AtomicInteger(0);
        optQuiet = line.hasOption(OPTNAME_QUIET);

        File rootDir = new File(System.getProperty("user.dir"));
        FileScanner fileScanner = new FileScanner(fileSearchPattern) {

            @Override
            public void handleFile(File file) throws IOException {
                filesTotal.incrementAndGet();
                if (searchFileContents(file, contentSearchPattern)) {
                    filesMatched.incrementAndGet();
                }
            }

        }.disableFileListPopulation();
        try {
            fileScanner.getFiles(rootDir);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (!optQuiet) {
            System.out.println(String.format("%d/%d files matched.", filesMatched.intValue(), filesTotal.intValue()));
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
