/*
 * Copyright © 2021 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.svncw;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.jjYBdx4IL.utils.env.WindowsUtils;
import com.github.jjYBdx4IL.utils.proc.ProcRunner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SvnClientWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(SvnClientWrapper.class);

    protected final String svnExe;

    /**
     * Can be used to determine if the given path is part of a subversion
     * checkout (!= managed by SVN). This method does not call any subversion
     * client, it simply traverses parent directories and tries to find and
     * parse the <code>.svn/format</code> file.
     * 
     * @return -1 if startDir is not part of a subversion checkout
     */
    public static int getSvnCoFormat(Path startDir) throws IOException {
        Path root = getSvnCoRoot(startDir);
        try {
            return root == null ? -1
                : Integer.parseInt(FileUtils.readFileToString(root.resolve(".svn/format").toFile(), UTF_8).trim());
        } catch (NumberFormatException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * This method does not call any subversion client, it simply traverses
     * parent directories and tries to find and parse the
     * <code>.svn/format</code> file.
     * 
     * @return the path containing the <code>.svn/format</code> file. null if
     *         not found.
     */
    public static Path getSvnCoRoot(Path startDir) throws IOException {
        Path current = startDir.toAbsolutePath();
        do {
            Path p = current.resolve(".svn/format");
            if (Files.isRegularFile(p)) {
                try {
                    Integer.parseInt(FileUtils.readFileToString(p.toFile(), UTF_8).trim());
                    return current;
                } catch (NumberFormatException ex) {
                    throw new IOException(ex);
                }
            }
            current = current.getParent();
        }
        while (current != null);
        return null;
    }

    /**
     * Shorthand for {@link #getSvnCoFormat(Path)} != -1.
     */
    public static boolean isSvnCheckout(Path dir) throws IOException {
        return getSvnCoFormat(dir) != -1;
    }

    /**
     * Construct svn client wrapper. Will use cygwin on Windows if installed.
     * Otherwise reverts to svn or svn.exe on system path.
     */
    public SvnClientWrapper() {
        if (SystemUtils.IS_OS_WINDOWS) {
            String cygPath = WindowsUtils.getCygwinInstallationPath();
            if (cygPath != null) {
                svnExe = cygPath + "\\bin\\svn.exe";
            } else {
                svnExe = "svn.exe";
            }
        } else {
            svnExe = "svn";
        }
        LOG.debug("SVN_EXE = {}", svnExe);
    }

    /**
     * Creates a runnable ProcRunner for the svn executable.
     */
    public ProcRunner createRunner(Path workingDirectory, List<String> svnArgs) throws IOException {
        File wd = workingDirectory.toFile();
        if (!wd.exists() || !wd.isDirectory()) {
            throw new IOException("working directory does not exist or isn't a directory: " + wd);
        }
        List<String> cmd = new ArrayList<>();
        cmd.add(svnExe);
        cmd.addAll(svnArgs);
        if (LOG.isDebugEnabled()) {
            LOG.debug("cmd: {} ({})", StringUtils.join(cmd, " "), wd.getCanonicalPath());
        }
        ProcRunner pr = new ProcRunner(cmd);
        pr.rootLocaleUtc();
        pr.setWorkDir(wd);
        return pr;
    }

    /**
     * Get svn info output as a map.
     */
    public Map<String, String> getSvnInfo(Path pathSpec) throws IOException, InterruptedException {
        ProcRunner pr = createRunner(pathSpec, Arrays.asList("info", "."));
        int rc = pr.run();
        if (LOG.isTraceEnabled()) {
            LOG.trace("rc = {}, output: {}", rc, pr.getOutputBlob());
        }
        if (rc != 0) {
            throw new IOException("svn info failed: " + pr.getOutputBlob());
        }
        Pattern p = Pattern.compile("^(\\S+[^:]+):\\s(.+)$");
        Map<String, String> map = new HashMap<>();
        for (String s : pr.getOutputLines()) {
            Matcher m = p.matcher(s);
            if (m.find()) {
                map.put(m.group(1), m.group(2));
            }
        }
        return map;
    }

    /**
     * Get output lines from svn status.
     */
    public ConcurrentLinkedQueue<String> getSvnStatus(Path pathSpec) throws IOException, InterruptedException {
        ProcRunner pr = createRunner(pathSpec, Arrays.asList("status", "."));
        int rc = pr.run();
        if (LOG.isTraceEnabled()) {
            LOG.trace("rc = {}, output: {}", rc, pr.getOutputBlob());
        }
        if (rc != 0) {
            throw new IOException("svn status failed: " + pr.getOutputBlob());
        }
        return pr.getOutputLines();
    }

    /**
     * Returns the first line of the svn --version command. Null if the command
     * failed. Can be used to check whether the svn client is available.
     */
    public String version() throws IOException, InterruptedException {
        ProcRunner pr = createRunner(Paths.get(System.getProperty("user.home")), Arrays.asList("--version"));
        int rc = pr.run();
        if (LOG.isTraceEnabled()) {
            LOG.trace("rc = {}, output: {}", rc, pr.getOutputBlob());
        }
        if (rc != 0) {
            return null;
        }
        return pr.getOutputLines().iterator().next();
    }

    public boolean isAvailable() throws IOException, InterruptedException {
        return version() != null;
    }

    /**
     * Return the svn executable as being used by the wrapper.
     */
    public String exe() {
        return svnExe;
    }

    /**
     * Get host part of svn URL returned by svn info.
     */
    public String getSvnHost(Path pathSpec) throws IOException, InterruptedException {
        String url = getSvnInfo(pathSpec).get("URL");
        Pattern p = Pattern.compile("^([^/]*//[^/]+)(?:|/.*)");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * Returns the root path of the svn checkout. Uses information dumped by svn
     * info.
     */
    public Path getSvnRoot(Path someCoDir) throws IOException, InterruptedException {
        someCoDir = someCoDir.toAbsolutePath().normalize();
        Map<String, String> info = getSvnInfo(someCoDir);
        String rel = info.get("Relative URL");
        if (rel == null || rel.length() < 2) {
            throw new IOException("relative url not found: " + someCoDir);
        }
        if (rel.equals("^/")) {
            return someCoDir;
        }
        int n = Paths.get(rel.substring(2)).getNameCount();
        Path p = someCoDir.toAbsolutePath();
        for (int i = 0; i < n; i++) {
            p = p.getParent();
        }
        return p;
    }

    /**
     * Check if svn status output contains anything.
     */
    public boolean checkClean(Path pathSpec) throws IOException, InterruptedException {
        return getSvnStatus(pathSpec).size() == 0;
    }

    /**
     * Returns last changed revision, appends " (dirty)" if svn status output
     * isn't empty.
     */
    public String getSvnLcRevDirty(Path pathSpec) throws IOException, InterruptedException {
        boolean isDirty = !checkClean(pathSpec);
        String rev = getSvnInfo(pathSpec).get("Last Changed Rev");
        if (rev == null || rev.trim().isEmpty()) {
            throw new IOException("last changed rev not found");
        }
        return isDirty ? rev + " (dirty)" : rev;
    }
}
