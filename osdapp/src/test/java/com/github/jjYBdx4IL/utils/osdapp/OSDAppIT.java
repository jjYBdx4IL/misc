/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.osdapp;

import static com.github.jjYBdx4IL.utils.junit4.Screenshot.X_OFFSET.CENTER;
import static com.github.jjYBdx4IL.utils.junit4.Screenshot.Y_OFFSET.TOP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.parser.linux.ZFSStatusParserTest;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.gfx.ImageUtils;
import com.github.jjYBdx4IL.utils.io.FindUtils;
import com.github.jjYBdx4IL.utils.junit4.ImageTester;
import com.github.jjYBdx4IL.utils.junit4.Screenshot;
import com.github.jjYBdx4IL.utils.logic.Condition;
import com.github.jjYBdx4IL.utils.parser.nagios.NagiosParserTest;
import com.github.jjYBdx4IL.utils.proc.ProcessUtil;
import com.github.jjYBdx4IL.utils.text.Snippets;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//CHECKSTYLE:OFF
public class OSDAppIT extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OSDAppIT.class);
    private static final File UNPACKED_DIST_DIR;
    static {
        try {
            UNPACKED_DIST_DIR = FindUtils.globOne("/target/osdapp-*-bin.dir/osdapp/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Server server = null;

    @Before
    public void before() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assumeFalse(SystemUtils.IS_OS_WINDOWS);

        server = new Server(0);
        server.setHandler(this);
        server.start();
    }

    @After
    public void after() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    public URL getUrl(String path) throws MalformedURLException, UnknownHostException {
        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        InetAddress addr = InetAddress.getLocalHost();
        return new URL(
            String.format(Locale.ROOT, "%s://%s:%d%s", "http", addr.getHostAddress(), connector.getLocalPort(), path));
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        LOG.info(String.format(Locale.ROOT, "handle(%s, ...)", target));

        String _jenkinsRes = null;
        String _nagiosRes = null;
        synchronized (this) {
            _jenkinsRes = jenkinsRes;
            _nagiosRes = nagiosRes;
        }

        if (("/jenkins" + JenkinsPoller.API_XML_URL_EXT).equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/xml");
            try (CompressorInputStream input = new CompressorStreamFactory()
                .createCompressorInputStream(getClass().getResourceAsStream(_jenkinsRes))) {
                IOUtils.copy(input, response.getWriter(), "UTF-8");
            } catch (CompressorException ex) {
                throw new IOException(ex);
            }
        } else if ("/nagios".equals(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            try (InputStream input = NagiosParserTest.class.getResourceAsStream(_nagiosRes)) {
                IOUtils.copy(input, response.getWriter(), "UTF-8");
            }
        }

        baseRequest.setHandled(true);
    }

    private final static File targetDir = Maven.getMavenBuildDir(OSDAppIT.class);
    private final static File tempDir = Maven.getTempTestDir(OSDAppIT.class);
    private final static File stdoutDump = new File(tempDir, "stdout");
    private final static File stderrDump = new File(tempDir, "stderr");
    private static String stdout;
    private static String stderr;
    private static int exitValue;
    private static int widthCmdArg;
    private static File screenshotFile;
    private final static int CROP_BAR_Y_OFFSET = 0;
    private final static int CROP_BAR_HEIGHT = 18;
    private GraphicsDevice defaultScreen = null;
    private String jenkinsRes = null, nagiosRes = null;

    private Condition defaultTerminationCondition = new Condition() {

        @Override
        public boolean test() {
            return System.currentTimeMillis() >= getStarted() + 10000L;
        }
    };

    @Before
    public void before2() throws IOException {
        assumeFalse(GraphicsEnvironment.isHeadless());

        FileUtils.cleanDirectory(tempDir);
        defaultScreen = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
    }

    protected int runOSD(
        String cmd,
        String jenkinsRes,
        String nagiosRes,
        String zfsRes,
        String screenShotName) throws InterruptedException, IOException, CompressorException {
        return runOSD(cmd, jenkinsRes, nagiosRes, zfsRes, screenShotName, defaultTerminationCondition);
    }

    protected int runOSD(
        String cmd,
        String jenkinsRes,
        String nagiosRes,
        String zfsRes,
        String screenShotName,
        Condition terminationCondition) throws InterruptedException, IOException, CompressorException {
        File zpoolStatusOutput = new File(targetDir, "zpoolStatusOutput");
        File sudoFile = new File(targetDir, "sudo");
        String sudoFileContents = "#!/bin/bash\n"
            + "exec cat \"" + zpoolStatusOutput.getCanonicalPath() + "\"\n";
        try (OutputStream os = new FileOutputStream(sudoFile)) {
            IOUtils.write(sudoFileContents, os, "UTF-8");
        }
        sudoFile.setExecutable(true);
        try (OutputStream os = new FileOutputStream(zpoolStatusOutput)) {
            IOUtils.copy(ZFSStatusParserTest.class.getResourceAsStream(zfsRes), os);
        }

        File diskStandbyScriptFile = new File(targetDir, "disk-standby.sh");
        String disStandbyScriptContents = "#!/bin/bash\n"
            + "exit 0\n";
        try (OutputStream os = new FileOutputStream(diskStandbyScriptFile)) {
            IOUtils.write(disStandbyScriptContents, os, "UTF-8");
        }
        diskStandbyScriptFile.setExecutable(true);

        synchronized (this) {
            this.jenkinsRes = jenkinsRes;
            this.nagiosRes = nagiosRes;
        }

        LOG.info("cmd: " + cmd);
        cmd = cmd.replaceFirst("(--" + OSDApp.OPTNAME_DISK_STANDBY_SCRPIT + "\\s+)\\S+",
            "$1" + diskStandbyScriptFile.getAbsolutePath());
        LOG.info("cmd: " + cmd);
        cmd = cmd.replaceFirst("(--" + OSDApp.OPTNAME_NAGIOS_URL + "\\s+)\\S+",
            "$1" + getUrl("/nagios").toExternalForm());
        LOG.info("cmd: " + cmd);
        cmd = cmd.replaceFirst("(--" + OSDApp.OPTNAME_JENKINS_URL + "\\s+)\\S+",
            "$1" + getUrl("/jenkins" + JenkinsPoller.API_XML_URL_EXT).toExternalForm()
                .replaceFirst(JenkinsPoller.API_XML_URL_EXT + "$", ""));
        LOG.info("cmd: " + cmd);
        widthCmdArg = Integer.parseInt(cmd.replaceFirst("^.*\\s+-w\\s+(\\S+)(|\\s+.*)$", "$1"));
        String[] cmdArgs = cmd.trim().split("\\s+");
        LOG.info("cmd: " + StringUtils.join(cmdArgs, " "));

        ProcessBuilder pb = new ProcessBuilder(cmdArgs);
        pb.redirectOutput(stdoutDump);
        pb.redirectError(stderrDump);
        pb.environment().put("PATH", targetDir.getCanonicalPath() + File.pathSeparator + pb.environment().get("PATH"));
        pb.directory(UNPACKED_DIST_DIR);

        final Process p = pb.start();
        assertTrue(terminationCondition.waitUntil());
        if (screenShotName != null) {
            screenshotFile = Screenshot.takeDesktopScreenshot(defaultScreen, screenShotName, true);
        }
        exitValue = ProcessUtil.reliableDestroy(p);

        stdout = IOUtils.toString(stdoutDump.toURI(), "UTF-8");
        stderr = IOUtils.toString(stderrDump.toURI(), "UTF-8");
        LOG.info("stdout:" + System.lineSeparator() + stdout);
        LOG.info("stderr:" + System.lineSeparator() + stderr);
        exitValue = p.exitValue();
        return exitValue;
    }

    @Test
    public void testREADMECommandAllOK() throws Exception {

        String cmd = getREADMESnippet("COMMAND EXAMPLE");
        runOSD(cmd, "jenkins_api_joblist_2.xml.xz", "detailedStatusCgiDump2.html", "zpool_status_2.txt", "all_ok");

        assertOutputOk(stdout);
        assertOutputOk(stderr);

        // check for non-green colors and save example for use in docs etc.
        BufferedImage img2 = cropScreenshot("all_ok_bar_only");
        ImageTester.assertGreenish(img2);
    }

    @Test
    public void testJenkinsError() throws Exception {

        String cmd = getREADMESnippet("COMMAND EXAMPLE");
        runOSD(cmd, "jenkins_api_joblist_1.xml.xz", "detailedStatusCgiDump2.html", "zpool_status_2.txt", "not_ok");

        assertOutputOk(stdout);
        assertOutputOk(stderr);

        // check for non-green colors and save example for use in docs etc.
        BufferedImage img2 = cropScreenshot("not_ok_bar_only");
        ImageTester.assertRGBXor(img2, true, true, false);
    }

    @Test
    public void testPlugin() throws Exception {
        runPluginTest(0);
        runPluginTest(1);
        runPluginTest(2);
        runPluginTest(3);
        runPluginTest(4);
        runPluginTest(5);
    }

    private void runPluginTest(final int exitCode) throws Exception {
        String cmd = getREADMESnippet("PLUGIN COMMAND EXAMPLE");
        cmd = createPluginExe(cmd, "<PLUGIN TEXT OUTPUT, EXIT CODE " + exitCode + ">", exitCode);
        runOSD(cmd, "jenkins_api_joblist_2.xml.xz", "detailedStatusCgiDump2.html", "zpool_status_2.txt", null,
            new Condition(true, 15) {

                @Override
                public boolean test() {
                    BufferedImage img = Screenshot.createDesktopScreenshot(
                        defaultScreen, CENTER, TOP, -widthCmdArg / 2, CROP_BAR_Y_OFFSET, widthCmdArg, CROP_BAR_HEIGHT);
                    if (exitCode == 0 || exitCode == 1) {
                        ImageTester.assertGreenish(img);
                    }
                    if (exitCode == 2 || exitCode == 3) {
                        ImageTester.assertNotGreenish(img);
                        ImageTester.assertNotReddish(img);
                    }
                    if (exitCode == 4 || exitCode == 5) {
                        ImageTester.assertReddish(img);
                    }
                    saveImage(img, "plugin_" + exitCode + "_bar_only");
                    return true;
                }
            });

        assertOutputOk(stdout);
        assertOutputOk(stderr);
    }

    private static void assertOutputOk(String output) {
        for (String line : output.split("\r?\n")) {
            assertFalse(line.toLowerCase(Locale.ROOT).contains("exception"));
            if (line.contains("INFO SimpleXmlAppCfg - reading app configuration file")) {
                continue;
            }
            if (line.contains("WARN SSLUtils - Disabling certificate checks.")) {
                continue;
            }
        }
    }

    private static BufferedImage cropScreenshot(String croppedName) throws IOException {
        return cropScreenshot(croppedName, screenshotFile);
    }

    private static BufferedImage cropScreenshot(String croppedName, File inputFile) throws IOException {
        BufferedImage img = ImageIO.read(screenshotFile);
        BufferedImage img2 = cropScreenshot(img);
        saveImage(img2, croppedName);
        return img2;
    }

    private static BufferedImage cropScreenshot(BufferedImage img) throws IOException {
        return ImageUtils.autoCrop(img.getSubimage(
            (img.getWidth() - widthCmdArg) / 2, CROP_BAR_Y_OFFSET, widthCmdArg, CROP_BAR_HEIGHT), 3);
    }

    private static File saveImage(BufferedImage img, String name) {
        File file = new File(targetDir, "screenshots" + File.separator + name + ".png");
        try {
            ImageIO.write(img, "png", file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return file;
    }

    private static String getREADMESnippet(String snippetName) throws IOException {
        File readmeFile = new File(UNPACKED_DIST_DIR, "README.md");
        String readme = FileUtils.readFileToString(readmeFile, "UTF-8");
        assertNotNull(readme);
        LOG.info("readme file: " + readmeFile.getAbsolutePath());
        LOG.info("readme: " + readme);
        String cmd = Snippets.extract(readme).get(snippetName);
        assertNotNull(snippetName, cmd);
        LOG.info("cmd: " + cmd);
        cmd = cmd.replaceAll("(\\\\\\s*)?\r?\n", " ");
        cmd = cmd.replaceFirst("(-jar\\s+)(\\S+)",
            "$1" + (targetDir.getAbsolutePath() + File.separator).replace("\\", "\\\\") + "$2");
        cmd = cmd.replace("eth0", getNonLoopbackNetDevNameIfPossible());
        return cmd;
    }

    private static String createPluginExe(String cmd, String stdout, int exitCode) throws IOException {
        File plugin = new File(targetDir, "plugin.exe");
        String contents = "#!/bin/bash\n"
            + "echo \"" + stdout.replace("\"", "\\\"") + "\"\n"
            + "exit " + exitCode + "\n";
        try (OutputStream os = new FileOutputStream(plugin)) {
            IOUtils.write(contents, os, "UTF-8");
        }
        plugin.setExecutable(true);
        return cmd.replace("<some-script-or-executable>", plugin.getAbsolutePath());
    }

    private static String getNonLoopbackNetDevNameIfPossible() throws SocketException {
        String loopbackName = "lo";
        for (NetworkInterface nif : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (nif.isLoopback()) {
                loopbackName = nif.getDisplayName();
                continue;
            }
            if (nif.getHardwareAddress() == null) {
                continue;
            }
            return nif.getDisplayName();
        }
        return loopbackName;
    }
}
