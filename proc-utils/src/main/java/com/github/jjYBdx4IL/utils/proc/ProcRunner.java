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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
public class ProcRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ProcRunner.class);
    private static final long DEFAULT_TIMEOUT = 0L; // no timeout by default

    private ProcessBuilder mProcessBuilder;
    private Charset consoleEncoding = Charset.defaultCharset();

    private final List<String> mOutput = new ArrayList<>();

    public ProcRunner(boolean includeErrorStream, List<String> command) {
        mProcessBuilder = new ProcessBuilder(escapeArgs(command)).redirectErrorStream(includeErrorStream);
    }

    public ProcRunner(boolean includeErrorStream, String... command) {
        mProcessBuilder = new ProcessBuilder(escapeArgs(command)).redirectErrorStream(includeErrorStream);
    }

    public ProcRunner(List<String> command) {
        this(true, command);
    }

    public ProcRunner(String... command) {
        this(true, command);
    }

    public Charset getConsoleEncoding() {
        return consoleEncoding;
    }

    /**
     * Interpret program output in the given encoding. Default is the JVM's
     * default charset as defined by environment and startup parameters.
     * 
     * @param consoleEncoding
     *            the expected console encoding, not null
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
     * @throws IOException
     *             if there was an I/O problem
     */
    public int run() throws IOException {
        return run(DEFAULT_TIMEOUT);
    }

    /**
     * 
     * @param timeout
     *            in milliseconds
     * @return exit value of the process
     * @throws IOException
     *             if there was an I/O problem
     */
    public int run(long timeout) throws IOException {
        final Process p = mProcessBuilder.start();
        Thread t = new Thread() {
            @Override
            public void run() {
                String line;
                mOutput.clear();
                try {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        p.getInputStream(), consoleEncoding))) {
                        line = br.readLine();
                        while (line != null) {
                            mOutput.add(line);
                            line = br.readLine();
                        }
                    }
                } catch (IOException e) {
                    LOG.error("", e);
                }
            }
        };
        t.start();
        try {
            t.join(timeout);
        } catch (InterruptedException e) {
            LOG.error("", e);
        }
        if (t.isAlive()) {
            throw new IOException("external process not terminating.");
        }
        try {
            return p.waitFor();
        } catch (InterruptedException e) {
            LOG.error("", e);
            throw new IOException(e);
        }
    }

    public List<String> getOutputLines() {
        return Collections.unmodifiableList(mOutput);
    }

    public String getOutputBlob() {
        StringBuilder sb = new StringBuilder();
        for (String line : mOutput) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
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

}
