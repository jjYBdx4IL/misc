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
package com.github.jjYBdx4IL.utils.text;

/**
 * Support class for a typical cron installation (Vixie Cron on Debian 10).
 */
public class VixieCron {

    /**
     * Quote a command to be listed directly in a crontab.
     */
    public static String qc(String cmd) throws FormatException {
        if (cmd.contains("\r") || cmd.contains("\n")) {
            throw new FormatException("line breaks not allowed in crontab commands");
        }
        
        return cmd.replaceAll("%", "\\\\%");
    }
    
    /**
     * Used by {@link VixieCron#qc(String)}. 
     */
    public static class FormatException extends Exception {
        private static final long serialVersionUID = 4448258241709529260L;

        public FormatException(String msg) {
            super(msg);
        }
    }
    
    private VixieCron() {
        
    }
}
