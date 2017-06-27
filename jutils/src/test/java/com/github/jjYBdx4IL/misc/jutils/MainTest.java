/*
 * #%L
 * Java Command Line Utilities
 * %%
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 * %%
 * #L%
 */
package com.github.jjYBdx4IL.misc.jutils;

import com.github.jjYBdx4IL.utils.ProcRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
public class MainTest {

    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);
    private static File workDir;

    private static int exitCode = -1;
    private static List<String> output = null;
    private static String outputBlob = null;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        workDir = new File(MainTest.class.getResource(".").toURI());
        workDir = new File(workDir, "exampledir");
    }

    private static void run(String... args) throws IOException {
        List<String> _args = new ArrayList<>();
        _args.add("java");
        _args.add("-jar");
        _args.add(System.getProperty("testJar"));
        Collections.addAll(_args, args);
        LOG.info("running external process: " + StringUtils.join(_args, " "));
        ProcRunner pr = new ProcRunner(true, _args);
        pr.setWorkDir(workDir);
        exitCode = pr.run();
        output = pr.getOutputLines();
        assertNotNull(output);
        outputBlob = String.join(System.lineSeparator(), output);
        assertNotNull(outputBlob);
        assertFalse(">>" + outputBlob + "<<" + System.lineSeparator(), outputContains("Exception"));
    }

    private static int countMatchesML(String regex) {
        Pattern p = Pattern.compile(regex);
        int c = 0;
        for (String line : output) {
            Matcher m = p.matcher(line);
            while (m.find()) {
                c++;
            }
        }
        return c;
    }

    private static boolean outputContains(String regex) {
        return countMatchesML(regex) > 0;
    }

    @Test
    public void testFullHelp() throws IOException {
        run();
        assertEquals(1, exitCode);
        assertTrue(isFullHelp());

        run("-h");
        assertEquals(1, exitCode);
        assertTrue(isFullHelp());

        run("--help");
        assertEquals(1, exitCode);
        assertTrue(isFullHelp());
    }

    @Test
    public void testSingleCmdHelp() throws IOException {
        run("grep", "-h");
        assertEquals(1, exitCode);
        assertTrue(isSingleCmdHelp());

        run("-h", "grep");
        assertEquals(1, exitCode);
        assertTrue(isSingleCmdHelp());
    }

    @Test
    public void testInvalidCommand() throws IOException {
        run("ljkasdlasd");
        assertEquals(1, exitCode);
        assertTrue(isFullHelp());
    }

    @Test
    public void testInvalidCommandOptCount() throws IOException {
        run("grep");
        assertEquals(1, exitCode);
        assertTrue(outputBlob, isSingleCmdHelp());
        assertEquals(1, countMatchesML("invalid number of arguments"));

        run("grep", "", "", "");
        assertEquals(1, exitCode);
        assertTrue(outputBlob, isSingleCmdHelp());
        assertEquals(1, countMatchesML("invalid number of arguments"));
    }

    @Test
    public void testBgr() throws IOException {
        run("bgr", "--devtests");
        assertEquals(outputBlob, 0, exitCode);
    }
    
    @Test
    public void testGrep() throws IOException {
        String emptyArg = SystemUtils.IS_OS_WINDOWS ? "\"\"" : "";
        
        run("grep", emptyArg, emptyArg);
        assertEquals(outputBlob, 0, exitCode);
        assertEquals(outputBlob, 5, output.size());
        assertEquals("2/2 files matched.", output.get(4));

        run("grep", emptyArg, emptyArg, "-q");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 2, output.size());

        run("-q", "grep", emptyArg, emptyArg);
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 2, output.size());

        run("-q", "grep", "ABC", emptyArg);
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "ABC$"));

        run("-q", "grep", "def", emptyArg);
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "GHI$"));

        run("-q", "grep", emptyArg, "abc");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "ABC$"));

        run("-q", "grep", emptyArg, "GHi");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "GHI$"));

        run("-q", "grep", "a", "G");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 0, output.size());
    }

    private boolean isFullHelp() {
        return countMatchesML("^usage: jutils") >= 3;
    }

    private boolean isSingleCmdHelp() {
        return countMatchesML("^usage: jutils") == 2;
    }

}
