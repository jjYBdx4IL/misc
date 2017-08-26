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

import com.github.jjYBdx4IL.utils.proc.ProcRunner;

import java.io.IOException;


//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class WindowsUtils {
    private static final String REGSTR_TOKEN = "REG_SZ";

    public static String getCurrentUserDesktopPath() {
        try {
            ProcRunner procRunner = new ProcRunner(false, "reg", "query",
                    "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "/v", "DESKTOP");
            int exitCode = procRunner.run();
            if (exitCode != 0) {
                throw new RuntimeException("registry command returned bad exit code");
            }
            String result = procRunner.getOutputBlob();
            int p = result.indexOf(REGSTR_TOKEN);
            if (p == -1) {
                throw new RuntimeException("registry command returned bad output: " + result);
            }
            return result.substring(p + REGSTR_TOKEN.length()).trim();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private WindowsUtils() {
    }
}
