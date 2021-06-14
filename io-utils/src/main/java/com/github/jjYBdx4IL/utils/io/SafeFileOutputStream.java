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
package com.github.jjYBdx4IL.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Write to temp file, sync and rename to final destination on close, preventing
 * partial writes and deletions of perfectly valid files on failures.
 */
public class SafeFileOutputStream extends FileOutputStream {

    protected final File finalFile;
    protected final File tempFile;
    protected boolean saveOnClose;

    protected SafeFileOutputStream(File tempFile, File finalFile, boolean saveOnClose) throws IOException {
        super(tempFile, true);
        this.tempFile = tempFile;
        this.finalFile = finalFile;
        this.saveOnClose = saveOnClose;
    }

    /**
     * Factory method. The returned stream does not get saved on close by
     * default and therefore must be confirmed by a call to {@link #save()}. Otherwise all
     * output gets discarded on close.
     */
    public static SafeFileOutputStream get(File file) throws IOException {
        File tempFile = createTempFile(file);
        return new SafeFileOutputStream(tempFile, file, false);
    }

    /**
     * Alternative factory method. The returned stream gets saved on close by default.
     * Use {@link #abort()} to abort.
     */
    public static SafeFileOutputStream getDefSave(File file) throws IOException {
        File tempFile = createTempFile(file);
        return new SafeFileOutputStream(tempFile, file, true);
    }

    private static File createTempFile(File dest) throws IOException {
        File tempFile = File.createTempFile("." + dest.getName(), null, dest.getParentFile());
        return tempFile;
    }

    /**
     * Save data on close.
     */
    public void save() {
        saveOnClose = true;
    }
    
    /**
     * Discard data on close.
     */
    public void abort() {
        saveOnClose = false;
    }

    @Override
    public void close() throws IOException {
        super.close();
        try {
            if (saveOnClose) {
                IoUtils.syncRenameTo(tempFile, finalFile);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

}
