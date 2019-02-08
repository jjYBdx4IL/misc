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

import org.apache.commons.io.DirectoryWalker;

//CHECKSTYLE:OFF
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FileScanner extends DirectoryWalker<File> {

    private static final Logger LOG = Logger.getLogger(FileScanner.class.getName());
    private final boolean relativize;
    private final Pattern regex;
    private String prefix;
    private boolean populateFileList = true;


    public FileScanner() {
        this((Pattern)null, false);
    }

    public FileScanner(String regex) {
        this(regex, false);
    }

    public FileScanner(Pattern regex) {
        this(regex, false);
    }

    public FileScanner(boolean relativize) {
        this((Pattern)null, relativize);
    }
    
    public FileScanner(String regex, boolean relativize) {
        this(regex != null ? Pattern.compile(regex) : null, relativize);
    }

    public FileScanner(Pattern regex, boolean relativize) {
        super();
        this.relativize = relativize;
        this.regex = regex;
    }

    public FileScanner disableFileListPopulation() {
        populateFileList = false;
        return this;
    }

    public List<File> scanFiles(File startDirectory) throws IOException {
        LOG.log(Level.FINER, "startDirectory: " + startDirectory.getAbsolutePath());
        ArrayList<File> dirs = new ArrayList<>();
        prefix = startDirectory.getAbsolutePath();
        if (!prefix.endsWith(File.separator)) {
            prefix += File.separator;
        }
        walk(startDirectory, dirs);
        return dirs;
    }

    /**
     * Returns a list of files.
     * @param startDirectory the root directory for the search
     * @return the list of matching files
     * @throws IOException if there is a problem with I/O
     */
    public List<File> getFiles(File startDirectory) throws IOException {
        LOG.log(Level.FINER, "startDirectory: " + startDirectory.getAbsolutePath());
        ArrayList<File> dirs = new ArrayList<>();
        prefix = startDirectory.getAbsolutePath();
        if (!prefix.endsWith(File.separator)) {
            prefix += File.separator;
        }
        walk(startDirectory, dirs);
        return dirs;
    }

    @Override
    protected void handleFile(File file, int depth, Collection<File> results) throws IOException {
        String absPath = file.getAbsolutePath();
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.log(Level.FINEST, "handleFile: " + absPath);
        }
        if (!absPath.startsWith(prefix)) {
            throw new IOException(String.format("scanned file %s is not below scanner root directory %s",
                    absPath, prefix));
        }
        String relPath = absPath.substring(prefix.length());
        if (regex != null && !regex.matcher(relPath).find()) {
            return;
        }
        File f = relativize ? new File(relPath) : file;
        if (populateFileList) {
            results.add(f);
        }
        handleFile(f);
    }

    /**
     * Overwrite this method to process files on the fly.
     * 
     * @param file the file
     * @throws IOException if the is an I/O problem
     */
    public void handleFile(File file) throws IOException {
    }
}
