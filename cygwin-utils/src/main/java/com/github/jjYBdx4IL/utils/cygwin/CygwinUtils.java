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
package com.github.jjYBdx4IL.utils.cygwin;

import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Wrapper methods for Cygwin to leverage the full power of a Cygwin
 * installation under Windows.
 */
public class CygwinUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CygwinUtils.class);

    /**
     * Cygwin installation root. null if not found.
     */
    public static final Path installRoot;

    /**
     * System PATH prepended with Cygwin installation root.
     */
    public static final String extendedPath;

    static {
        String path = null;
        if (Platform.isWindows()) {
            try {
                path = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE,
                    "Software\\Cygwin\\setup", "rootdir");
            } catch (Win32Exception ex) {
                LOG.debug("cygwin installation not found");
            }
        }
        installRoot = path != null ? Paths.get(path) : null;
        if (path != null) {
            String syspath = System.getenv("PATH");
            StringBuilder sb = new StringBuilder(syspath.length());
            sb.append(path);
            sb.append(File.separator);
            sb.append("bin");
            sb.append(File.separator);
            sb.append(File.pathSeparator);
            sb.append(syspath);
            extendedPath = sb.toString();
        } else {
            extendedPath = null;
        }
    }

    /**
     * Wraps the given command into a BASH script to avoid command line
     * interpolation by Windows, and executes it. The result is AutoCloseable
     * and should be property closed to remove all associated files (script,
     * stdout and stderr output files).
     * 
     * <p>Env vars will be set to TZ=UTC, LC_ALL=C, LANG=C. Working directory is
     * the temporary directory where the script and the output files will be
     * created. The script will also <code>set</code> "-Eeu" and "-o pipefail".
     * 
     * @param captureIo
     *            set to true to enable capturing/redirection of
     *            stdout/stderr/stdin to files (stdin from /dev/null).
     * @param cmd
     *            the command to embed and run
     * @return the execution result
     * @throws IOException
     *             on IO issues
     * @throws InterruptedException
     *             on interruption
     */
    public static RunResult bashRun(String cmd, boolean captureIo) throws IOException, InterruptedException {
        LOG.debug("bashRun: {} (capture I/O: {})", cmd, captureIo);
        RunResult rr = new RunResult();
        ProcessBuilder pb = new ProcessBuilder(installRoot.resolve("bin/bash.exe").toString(), "-c",
            "./" + rr.script.toFile().getName());
        if (captureIo) {
            pb.redirectError(rr.stderr.toFile());
            pb.redirectOutput(rr.stdout.toFile());
        } else {
            pb.inheritIO();
        }
        pb.directory(rr.tmpdir.toFile());

        StringBuilder src = new StringBuilder();
        src.append("#!/bin/bash\n");
        src.append("set -o pipefail\n");
        src.append("set -Eeu\n");
        if (captureIo) {
            src.append("exec < /dev/null\n");
        }
        src.append("export TZ=UTC LC_ALL=C LANG=C\n");
        src.append("export PATH=\"/bin:/usr/bin:/usr/local/bin:$HOME/.local/bin:$PATH\"\n");
        // src.append("exec ");
        src.append(cmd);
        src.append("\n");
        src.append("exit $?\n");
        FileUtils.writeStringToFile(rr.script.toFile(), src.toString(), UTF_8);

        Process p = pb.start();
        rr.rc = p.waitFor();
        return rr;
    }

    /**
     * Fire-and-forget variant of {@link #bashRun(String,boolean)}.
     * 
     * @param expectedRc
     *            the expected result code
     * @param captureIo
     *            set to true to enable capturing/redirection of
     *            stdout/stderr/stdin to files (stdin from /dev/null).
     * @param cmd
     *            the command to embed and run
     * @throws IOException
     *             on IO issues
     * @throws InterruptedException
     *             on interruption
     */
    public static void bashRun(String cmd, int expectedRc, boolean captureIo) throws IOException, InterruptedException {
        try (RunResult rr = bashRun(cmd, captureIo)) {
            if (rr.rc != expectedRc) {
                LOG.error("execution failed");
                LOG.error("script was: {}", FileUtils.readFileToString(rr.script.toFile(), UTF_8));
                LOG.error("stdout was: {}", FileUtils.readFileToString(rr.stdout.toFile(), UTF_8));
                LOG.error("stderr was: {}", FileUtils.readFileToString(rr.stderr.toFile(), UTF_8));
                throw new IOException("execution failed, rc = " + rr.rc);
            }
        }
    }

    /**
     * The execution result.
     */
    public static class RunResult implements AutoCloseable {

        /**
         * Temporary directory for the execution.
         */
        public final Path tmpdir;

        /**
         * Execution script.
         */
        public final Path script;

        /**
         * Stdout dump if I/O wasn't inherited.
         */
        public final Path stdout;

        /**
         * Stderr dump if I/O wasn't inherited.
         */
        public final Path stderr;

        /**
         * The return code of the execution.
         */
        public int rc = -1;

        RunResult() throws IOException {
            tmpdir = Files.createTempDirectory(null).toAbsolutePath().normalize();
            script = tmpdir.resolve("script.sh");
            stdout = tmpdir.resolve("stdout.log");
            stderr = tmpdir.resolve("stderr.log");
        }

        @Override
        public void close() throws IOException {
            if (Files.exists(tmpdir)) {
                FileUtils.deleteDirectory(tmpdir.toFile());
            }
        }
    }

}
