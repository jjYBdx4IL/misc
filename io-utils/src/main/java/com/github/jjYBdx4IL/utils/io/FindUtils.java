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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//CHECKSTYLE:OFF
public class FindUtils {

    // fixed path separator for platform-independent matching regex
    public static final String PATH_SEPARATOR = "/";
    public static final int MAX_DEPTH = 100;

    public static List<File> glob(File startDir, String glob) throws IOException {
        return find(startDir, globToRegex(glob));
    }
    public static List<File> glob(String glob) throws IOException {
        return find(globToRegex(glob));
    }

    public static List<File> find(File startDir, String regex) throws IOException {
        return find(startDir, Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, new ArrayList<File>(), false);
    }
    public static List<File> find(String regex) throws IOException {
        return find(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, new ArrayList<File>(), false);
    }

    public static File globFirst(File startDir, String glob) throws IOException {
        return findFirst(startDir, globToRegex(glob));
    }
    public static File globFirst(String glob) throws IOException {
        return findFirst(globToRegex(glob));
    }

    public static File findFirst(File startDir, String regex) throws IOException {
        List<File> result = new ArrayList<>();
        find(startDir, Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, result, false);
        return result.isEmpty() ? null : result.get(0);
    }
    public static File findFirst(String regex) throws IOException {
        List<File> result = new ArrayList<>();
        find(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, result, false);
        return result.isEmpty() ? null : result.get(0);
    }

    public static File globFirstOrThrow(File startDir, String glob) throws IOException {
        return findFirstOrThrow(startDir, globToRegex(glob));
    }
    public static File globFirstOrThrow(String glob) throws IOException {
        return findFirstOrThrow(globToRegex(glob));
    }

    public static File findFirstOrThrow(File startDir, String regex) throws IOException {
        File result = findFirst(startDir, regex);
        if (result == null) {
            throw new FileNotFoundException();
        }
        return result;
    }
    public static File findFirstOrThrow(String regex) throws IOException {
        File result = findFirst(regex);
        if (result == null) {
            throw new FileNotFoundException();
        }
        return result;
    }

    public static File globOne(File startDir, String glob) throws IOException {
        return findOne(startDir, globToRegex(glob));
    }
    public static File globOne(String glob) throws IOException {
        return findOne(globToRegex(glob));
    }

    public static File findOne(File startDir, String regex) throws IOException {
        List<File> result = new ArrayList<>();
        find(startDir, Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, result, false);
        return result.size() != 1 ? null : result.get(0);
    }
    public static File findOne(String regex) throws IOException {
        List<File> result = new ArrayList<>();
        find(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, result, false);
        return result.size() != 1 ? null : result.get(0);
    }

    public static File globOneOrThrow(File startDir, String glob) throws IOException {
        return findOneOrThrow(startDir, globToRegex(glob));
    }
    public static File globOneOrThrow(String glob) throws IOException {
        return findOneOrThrow(globToRegex(glob));
    }

    public static File findOneOrThrow(File startDir, String regex) throws IOException {
        File result = findOne(startDir, regex);
        if (result == null) {
            throw new IOException();
        }
        return result;
    }
    public static File findOneOrThrow(String regex) throws IOException {
        File result = findOne(regex);
        if (result == null) {
            throw new IOException();
        }
        return result;
    }

    public static List<File> globFiles(File startDir, String glob) throws IOException {
        return findFiles(startDir, globToRegex(glob));
    }
    public static List<File> globFiles(String glob) throws IOException {
        return findFiles(globToRegex(glob));
    }

    public static List<File> findFiles(File startDir, String regex) throws IOException {
        return find(startDir, Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, new ArrayList<File>(), true);
    }
    public static List<File> findFiles(String regex) throws IOException {
        return find(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, new ArrayList<File>(), true);
    }

    public static File globFirstFile(File startDir, String glob) throws IOException {
        return findFirstFile(startDir, globToRegex(glob));
    }
    public static File globFirstFile(String glob) throws IOException {
        return findFirstFile(globToRegex(glob));
    }

    public static File findFirstFile(File startDir, String regex) throws IOException {
        List<File> result = new ArrayList<>();
        find(startDir, Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, result, true);
        return result.isEmpty() ? null : result.get(0);
    }
    public static File findFirstFile(String regex) throws IOException {
        List<File> result = new ArrayList<>();
        find(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, result, true);
        return result.isEmpty() ? null : result.get(0);
    }

    public static File globFirstFileOrThrow(File startDir, String glob) throws IOException {
        return findFirstFileOrThrow(startDir, globToRegex(glob));
    }
    public static File globFirstFileOrThrow(String glob) throws IOException {
        return findFirstFileOrThrow(globToRegex(glob));
    }

    public static File findFirstFileOrThrow(File startDir, String regex) throws IOException {
        File result = findFirstFile(startDir, regex);
        if (result == null) {
            throw new FileNotFoundException();
        }
        return result;
    }
    public static File findFirstFileOrThrow(String regex) throws IOException {
        File result = findFirstFile(regex);
        if (result == null) {
            throw new FileNotFoundException();
        }
        return result;
    }

    public static File globOneFile(File startDir, String glob) throws IOException {
        return findOneFile(startDir, globToRegex(glob));
    }
    public static File globOneFile(String glob) throws IOException {
        return findOneFile(globToRegex(glob));
    }
    
    public static File findOneFile(File startDir, String regex) throws IOException {
        List<File> result = new ArrayList<>();
        find(startDir, Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, result, true);
        return result.size() != 1 ? null : result.get(0);
    }
    public static File findOneFile(String regex) throws IOException {
        List<File> result = new ArrayList<>();
        find(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), "",
            MAX_DEPTH, result, true);
        return result.size() != 1 ? null : result.get(0);
    }

    public static File globOneFileOrThrow(File startDir, String glob) throws IOException {
        return findOneFileOrThrow(startDir, globToRegex(glob));
    }
    public static File globOneFileOrThrow(String glob) throws IOException {
        return findOneFileOrThrow(globToRegex(glob));
    }
    
    public static File findOneFileOrThrow(File startDir, String regex) throws IOException {
        File result = findOneFile(startDir, regex);
        if (result == null) {
            throw new IOException();
        }
        return result;
    }
    public static File findOneFileOrThrow(String regex) throws IOException {
        File result = findOneFile(regex);
        if (result == null) {
            throw new IOException();
        }
        return result;
    }

    protected static List<File> find(Pattern pattern, String prefix, int maxDepth,
        List<File> result, boolean filesOnly) throws IOException {
        return find(new File(System.getProperty("user.dir")), pattern, prefix, maxDepth, result, filesOnly);
    }
    protected static List<File> find(File startDir, Pattern pattern, String prefix, int maxDepth,
        List<File> result, boolean filesOnly) throws IOException {
        
        if (maxDepth == 0) {
            throw new IOException("max depth reached: " + startDir);
        }
        if (!startDir.isDirectory()) {
            throw new IOException("not a directory: " + startDir);
        }

        for (File f : startDir.listFiles()) {
            String relPath = prefix + PATH_SEPARATOR + f.getName();
            if (!filesOnly || f.isFile()) {
                if (pattern.matcher(f.isDirectory() ? relPath + PATH_SEPARATOR : relPath).find()) {
                    result.add(f);
                }
            }
            if (f.isDirectory()) {
                find(f, pattern, relPath, maxDepth - 1, result, filesOnly);
            }
        }
        return result;
    }

    protected static String globToRegex(String glob) {
        StringBuilder sb = new StringBuilder(glob.length());
        sb.append("^");
        int fromIndex = 0;
        while (glob.indexOf("*", fromIndex) != -1) {
            sb.append(glob.substring(fromIndex, glob.indexOf("*", fromIndex)));
            if (glob.indexOf("*", fromIndex) == glob.indexOf("**", fromIndex)) {
                sb.append(".*");
                fromIndex = glob.indexOf("*", fromIndex) + 2;
            } else {
                sb.append("[^/]*");
                fromIndex = glob.indexOf("*", fromIndex) + 1;
            }
        }
        sb.append(glob.substring(fromIndex));
        sb.append("$");
        return sb.toString();
    }
}
