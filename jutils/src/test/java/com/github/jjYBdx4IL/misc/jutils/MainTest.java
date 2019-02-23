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
package com.github.jjYBdx4IL.misc.jutils;

import static com.github.jjYBdx4IL.utils.io.FindUtils.globOne;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.jjYBdx4IL.utils.io.FindUtils;
import com.github.jjYBdx4IL.utils.proc.ProcRunner;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
public class MainTest {

    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);
    private static File workDir;
    private static final File UNPACKED_DIST_DIR;
    static {
        try {
            UNPACKED_DIST_DIR = FindUtils.globOne("/target/jutils-*-bin.dir/jutils/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int exitCode = -1;
    private static List<String> output = null;
    private static String outputBlob = null;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        workDir = new File(MainTest.class.getResource(".").toURI());
        workDir = new File(workDir, "exampledir");
        LOG.info("workDir = " + workDir.getAbsolutePath());
    }

    private static void run(String... args) throws IOException {
        List<String> _args = new ArrayList<>();
        _args.add(new File(UNPACKED_DIST_DIR, "jutils").getAbsolutePath());
        Collections.addAll(_args, args);
        LOG.info("running external process: " + StringUtils.join(_args, " "));
        ProcRunner pr = new ProcRunner(true, _args);
        pr.setWorkDir(workDir);
        exitCode = pr.run();
        output = new ArrayList<>();
        pr.getOutputLines().forEach( line -> filter(line) );
        assertNotNull(output);
        outputBlob = String.join(System.lineSeparator(), output);
        assertNotNull(outputBlob);
        assertFalse(">>" + outputBlob + "<<" + System.lineSeparator(), outputContains("Exception"));
    }
    
    private static void filter(String line) {
        if (line.startsWith("Picked up _JAVA_OPTIONS:")) {
            return;
        }
        output.add(line);
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
        run("grep", "", "");
        assertEquals(outputBlob, 0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals("0/0 files matched.", output.get(0));
        
        run("grep", "**", "");
        assertEquals(outputBlob, 5, output.size());
        assertEquals("2/2 files matched.", output.get(4));

        run("grep", "**", "", "-q");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 2, output.size());

        run("-q", "grep", "**", "");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 2, output.size());

        run("-q", "grep", "ABC", "");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "ABC$"));

        run("-q", "grep", "**/ABC", "");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "ABC$"));
        
        run("-q", "grep", "def/**", "");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "GHI$"));

        run("-q", "grep", "**", "abc");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "ABC$"));

        run("-q", "grep", "**", "GHi");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 1, output.size());
        assertEquals(outputBlob, 1, countMatchesML(Pattern.quote(File.separator) + "GHI$"));

        run("-q", "grep", "a*", "G");
        assertEquals(0, exitCode);
        assertEquals(outputBlob, 0, output.size());
    }

    private boolean isFullHelp() {
        return countMatchesML("^usage: jutils") >= 3;
    }

    private boolean isSingleCmdHelp() {
        return countMatchesML("^usage: jutils") == 2;
    }

    @Test
    public void test2Img() throws IOException {
        run("2img", "-html", "-i", globOne("/src/**/a.html").getAbsolutePath(), "-o",
            new File(globOne("/target/"), "a.png").getAbsolutePath());
        assertEquals(outputBlob, 0, exitCode);
        assertEquals(outputBlob, 1, output.size());
    }

}
