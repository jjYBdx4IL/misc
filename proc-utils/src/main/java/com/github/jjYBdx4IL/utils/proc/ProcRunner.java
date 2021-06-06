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
package com.github.jjYBdx4IL.utils.proc;

//CHECKSTYLE:OFF
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class to run external process.
 *
 * Always redirects stderr into stdout, has timeout control
 *
 * @author jjYBdx4IL
 */
public class ProcRunner implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(ProcRunner.class);
    private static final long DEFAULT_TIMEOUT = 0L; // no timeout by default
    private static final long THREAD_SHUTDOWN_TIMEOUT_MS = 10000;
    public static final String LINE_SEP = System.getProperty("line.separator");

    public static final int TIMEOUT_RC = 1000;
    public static final int KILLED_RC = 1001;

    private ProcessBuilder mProcessBuilder;
    private Charset consoleEncoding = Charset.defaultCharset();

    private final ConcurrentLinkedQueue<String> mOutput = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> mError = new ConcurrentLinkedQueue<>();
    private final boolean includeErrorStream;
    private int rc = -1;
    private Thread tout = null;
    private Thread terr = null;
    private Process p = null;

    public ProcRunner(boolean includeErrorStream, List<String> command) {
        mProcessBuilder = new ProcessBuilder(escapeArgs(command)).redirectErrorStream(includeErrorStream);
        this.includeErrorStream = includeErrorStream;
    }

    public ProcRunner(boolean includeErrorStream, String... command) {
        mProcessBuilder = new ProcessBuilder(escapeArgs(command)).redirectErrorStream(includeErrorStream);
        this.includeErrorStream = includeErrorStream;
    }

    public ProcRunner(boolean includeErrorStream, Path exe, String... command) {
        List<String> cmd = new ArrayList<>(command.length + 1);
        cmd.add(exe.toString());
        for (String s : command) {
            cmd.add(s);
        }
        mProcessBuilder = new ProcessBuilder(escapeArgs(cmd)).redirectErrorStream(includeErrorStream);
        this.includeErrorStream = includeErrorStream;
    }
    
    public ProcRunner(List<String> command) {
        this(true, command);
    }

    public ProcRunner(String... command) {
        this(true, command);
    }

    public ProcRunner(Path exe, String... command) {
        List<String> cmd = new ArrayList<>(command.length + 1);
        cmd.add(exe.toString());
        for (String s : command) {
            cmd.add(s);
        }
        includeErrorStream = true;
        mProcessBuilder = new ProcessBuilder(escapeArgs(cmd)).redirectErrorStream(includeErrorStream);
    }

    public Charset getConsoleEncoding() {
        return consoleEncoding;
    }
    
    public ProcRunner rootLocaleUtc() {
        environment().put("LC_ALL", "C");
        environment().put("LANG", "C");
        environment().put("TZ", "UTC");
        return this;
    }
    
    public ProcRunner prependPath(Path element) {
        String s = environment().get("PATH");
        if (s == null || s.trim().isEmpty()) {
            environment().put("PATH", element.toAbsolutePath().toString());
        } else {
            environment().put("PATH", element.toAbsolutePath().toString() + File.pathSeparator + s);
        }
        return this;
    }

    /**
     * Interpret program output in the given encoding. Default is the JVM's default
     * charset as defined by environment and startup parameters.
     * 
     * @param consoleEncoding the expected console encoding, not null
     */
    public void setConsoleEncoding(Charset consoleEncoding) {
        if (consoleEncoding == null) {
            throw new IllegalArgumentException();
        }
        this.consoleEncoding = consoleEncoding;
    }

    public void setWorkDir(File directory) {
        mProcessBuilder.directory(directory);
    }

    protected List<String> escapeArgs(String... args) {
        List<String> _args = new ArrayList<>(args.length);
        if (SystemUtils.IS_OS_UNIX) {
            Collections.addAll(_args, args);
        } else {
            for (String arg : args) {
                _args.add("\"" + arg + "\"");
            }
        }
        return _args;
    }

    protected List<String> escapeArgs(List<String> args) {
        List<String> _args = new ArrayList<>(args.size());
        if (SystemUtils.IS_OS_UNIX) {
            _args.addAll(args);
        } else {
            for (String arg : args) {
                _args.add("\"" + arg + "\"");
            }
        }
        return _args;
    }

    /**
     * No timeout.
     * 
     * @return exit value of the process
     * @throws IOException          if there was an I/O problem
     * @throws InterruptedException
     */
    public int run() throws IOException, InterruptedException {
        return run(DEFAULT_TIMEOUT);
    }

    /**
     * 
     * @param timeout in milliseconds
     * @return exit value of the process
     * @throws IOException          if there was an I/O problem
     * @throws InterruptedException
     */
    public int run(long timeout) throws IOException, InterruptedException {
        return start().waitFor(timeout, TimeUnit.MILLISECONDS);
    }

    public ProcRunner start() throws IOException {
        p = mProcessBuilder.start();
        tout = new Thread() {
            @Override
            public void run() {
                String line;
                mOutput.clear();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(p.getInputStream(), consoleEncoding))) {
                    line = br.readLine();
                    while (line != null) {
                        mOutput.add(line);
                        line = br.readLine();
                    }
                } catch (IOException e) {
                    LOG.error("", e);
                }
            }
        };
        tout.start();

        if (!this.includeErrorStream) {
            terr = new Thread() {
                @Override
                public void run() {
                    String line;
                    mError.clear();
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(p.getErrorStream(), consoleEncoding))) {
                        line = br.readLine();
                        while (line != null) {
                            mError.add(line);
                            line = br.readLine();
                        }
                    } catch (IOException e) {
                        LOG.error("", e);
                    }
                }
            };
            terr.start();
        }
        return this;
    }

    @Override
    public void close() throws InterruptedException, IOException {
        kill();
    }
    
    public int kill() throws InterruptedException, IOException {
        return waitFor(-1, null);
    }

    public int waitFor(long timeout, TimeUnit unit) throws InterruptedException, IOException {
        if (tout == null || p == null || rc >= 0) {
            throw new IllegalStateException();
        }
        if (timeout > 0 && unit == null) {
            unit = TimeUnit.MILLISECONDS;
        }
        try {
            if (timeout == 0) {
                rc = p.waitFor();
                return rc;
            }
            if (timeout < 0) {
                p.destroyForcibly();
                rc = KILLED_RC;
                return rc;
            }
            if (p.waitFor(timeout, unit)) {
                rc = p.exitValue();
                return rc;
            } else {
                p.destroyForcibly();
                rc = TIMEOUT_RC;
                return rc;
            }
        } finally {
            // don't close the streams forcefully here. Instead, if something hangs,
            // use the new Java capabilities to traverse the child process tree and kill stuff
            // one by one. Otherwise we might be cutting of required output.
            if (!this.includeErrorStream) {
                try {
                    terr.join(THREAD_SHUTDOWN_TIMEOUT_MS);
                    if (terr.isAlive()) {
                        terr.interrupt();
                        terr.join(THREAD_SHUTDOWN_TIMEOUT_MS);
                    } else {
                    }
                } catch (InterruptedException e) {
                    LOG.error("", e);
                }
                if (terr.isAlive()) {
                    throw new IOException("external process not terminated");
                }
            }
            try {
                tout.join(THREAD_SHUTDOWN_TIMEOUT_MS);
                if (tout.isAlive()) {
                    tout.interrupt();
                    tout.join(THREAD_SHUTDOWN_TIMEOUT_MS);
                }
            } catch (InterruptedException e) {
                LOG.error("", e);
            }
            if (tout.isAlive()) {
                throw new IOException("external process not terminated");
            }
        }
    }

    public ConcurrentLinkedQueue<String> getOutputLines() {
        return mOutput;
    }
    
    public String getLastLine() {
        String line = null;
        Iterator<String> it = mOutput.iterator();
        while(it.hasNext()) {
            line = it.next();
        }
        return line;
    }

    public String getOutputBlob() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = mOutput.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append(LINE_SEP);
        }
        return sb.toString();
    }

    public ConcurrentLinkedQueue<String> getErrorLines() {
        return mError;
    }

    public String getErrorBlob() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = mError.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append(LINE_SEP);
        }
        return sb.toString();
    }

    /**
     * 
     * @return internal {@link ProcessBuilder}'s environment as returned by
     *         {@link ProcessBuilder#environment()}.
     */
    public Map<String, String> environment() {
        return mProcessBuilder.environment();
    }

    public int getRc() {
        return rc;
    }
    
    public Process getProcess() {
        return p;
    }
}
