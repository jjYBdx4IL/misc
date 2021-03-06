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
package com.github.jjYBdx4IL.utils.encryption.gnupg;

import com.github.jjYBdx4IL.utils.proc.ProcRunner;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * GnuPG command line wrapper. Developed against GnuPG 1.4.22 on Windows
 * 10/cygwin/amd64 and GnuPG 2.2.4 on Linux/amd64.
 */
public class GnuPgClWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(GnuPgClWrapper.class);

    private String gnuPgHomeDir = null;
    private boolean logErrorOutput = false;
    private ProcRunner proc = null;

    public GnuPgClWrapper() {

    }

    public void setGnuPgHomeDir(String gnuPgHomeDir) {
        this.gnuPgHomeDir = gnuPgHomeDir;
    }

    /**
     * Decrypt ASCII-armored text and return the decrypted text as a string.
     * 
     * @param input
     *            the encrypted, ASCII-armored input string
     * 
     * @return the decrypted text
     * @throws GnuPgClDecryptionException if there was a problem
     * @throws InterruptedException on process interruption 
     */
    public String decryptTextAa(String input) throws GnuPgClDecryptionException, InterruptedException {
        File inputFile = null;
        try {
            inputFile = File.createTempFile("tmp", ".tmp");
            FileUtils.writeStringToFile(inputFile, input, "UTF-8");
            int exitCode = runGpg(inputFile, false);
            if (exitCode != 0) {
                if (isLogErrorOutput()) {
                    runGpg(inputFile, true);
                    throw new GnuPgClDecryptionException(
                        "gpg exit code was: " + exitCode + ", gpg output was: " + proc.getOutputBlob());
                }
                throw new GnuPgClDecryptionException("gpg exit code was: " + exitCode);
            }
            return proc.getOutputBlob();
        } catch (IOException e) {
            throw new GnuPgClDecryptionException(e);
        } finally {
            if (inputFile != null && inputFile.exists()) {
                if (!inputFile.delete()) {
                    LOG.warn("failed to delete " + inputFile.getAbsolutePath() + ", scheduled deletion on VM shutdown");
                    inputFile.deleteOnExit();
                }
            }
        }
    }

    private int runGpg(File inputFile, boolean includeErrorOutput) throws IOException, InterruptedException {
        proc = new ProcRunner(includeErrorOutput, "gpg", "-d", inputFile.getName());
        proc.setWorkDir(inputFile.getParentFile());
        if (gnuPgHomeDir != null) {
            proc.environment().put("GNUPGHOME", gnuPgHomeDir);
        }
        return proc.run();
    }

    public boolean isLogErrorOutput() {
        return logErrorOutput;
    }

    /**
     * If gpg exits with a non-zero exit code, re-run it to fetch the error
     * output? Should be used in test units mostly because it could ask the user
     * to enter the password a second time.
     * 
     * @param logErrorOutput whether or not to dump the error output from GnuPG
     */
    public void setLogErrorOutput(boolean logErrorOutput) {
        this.logErrorOutput = logErrorOutput;
    }
}
