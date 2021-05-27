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
package com.github.jjYBdx4IL.utils.env;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class WindowsUtils {
    private static final String REGSTR_TOKEN = "REG_SZ";

    public static String getCurrentUserDesktopPath() {
        String s = execRegQueryExe(
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "DESKTOP");
        
        int p = s.indexOf(REGSTR_TOKEN);
        if (p == -1) {
            throw new RuntimeException("registry command returned bad output: " + s);
        }
        return s.substring(p + REGSTR_TOKEN.length()).trim();
    }

    /**
     * @return the first cygwin installation directory we find
     */
    public static String getCygwinInstallationPath() {
        try {
            String s = execRegQueryExe("HKCU\\Software\\Cygwin\\installations", "*");
            Pattern p = Pattern.compile("^\\s+\\S+\\s+REG_SZ\\s+\\S+([a-zA-Z]:\\S+)");
            for (String _s : s.split("\\r?\\n")) {
                Matcher m = p.matcher(_s); 
                if (m.find()) {
                    return m.group(1); 
                }
            }
            return null;
        } catch (RuntimeException err) {
            return null;
        }
    }
    
    /**
     * Get string value of a registry entry. Uses the "reg.exe" command line tool.
     * 
     * @param regPath for example "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders"
     * @param regKey for example "Desktop"
     * @return the string (REG_SZ) value for the specified key
     */
    public static String execRegQueryExe(String regPath, String regKey) {
        File tempFile = null;
        File tempFileErr = null;
        try {
            tempFile = File.createTempFile(".tmp", ".tmp");
            tempFileErr = File.createTempFile(".tmp", ".tmp");
            ProcessBuilder pb = new ProcessBuilder("reg", "query",
                regPath, "/v", regKey);
            pb.redirectError(tempFileErr);
            pb.redirectOutput(tempFile);
            int exitCode = pb.start().waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("registry command returned bad exit code");
            }
            return FileUtils.readFileToString(tempFile, Charset.defaultCharset());
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        } finally {
            FileUtils.deleteQuietly(tempFile);
            FileUtils.deleteQuietly(tempFileErr);
        }
    }
    
    private WindowsUtils() {
    }
}


