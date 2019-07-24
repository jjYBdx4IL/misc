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
package com.github.jjYBdx4IL.parser.java;

import static com.github.jjYBdx4IL.parser.RegularExpression.JAVA_TYPENAME;
import static com.github.jjYBdx4IL.parser.RegularExpression.JAVA_TYPENAME_ARG_PKGNAME;
import static com.github.jjYBdx4IL.parser.RegularExpression.JAVA_TYPENAME_ARG_SIMPLENAME;

import com.github.jjYBdx4IL.parser.ParseException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ExceptionParser {

    private final static Pattern EXCEPTION_HEADER = Pattern.compile("^" + JAVA_TYPENAME + ":\\s*(?<message>|\\S.*)$");
    private final static Pattern STRACE_LINE = Pattern.compile("^\\tat \\S+\\([^\\)]+\\)$");
    private final static String EOL = "\n";

    public static List<ParsedException> parse(InputStream is) throws ParseException {
        List<ParsedException> result = new ArrayList<>();
        Scanner s = new Scanner(is);
        try {
            Matcher m = null;
            String l = null;
            int ln = 0;
            int state = 0;
            String exceptionTypePkgName = null;
            String exceptionTypeSimpleName = null;
            String exceptionMessage = null;
            StringBuilder exceptionSTrace = null;
            int straceLines = 0;
            boolean reprocessLine = false;
            /**
             * states:
             * <ul>
             * <li>0: look for "$type: $message"
             * <li>1: append to stack trace while TRACE_LINE_PATTERN matches;
             * finish exception on empty line. -> 0
             * </ul>
             */
            while (s.hasNextLine()) {
                if (!reprocessLine) {
                    l = s.nextLine();
                }
                reprocessLine = false;
                ln++;
                switch (state) {
                    case 0:
                        m = EXCEPTION_HEADER.matcher(l);
                        if (m.find()) {
                            exceptionTypePkgName = m.group(JAVA_TYPENAME_ARG_PKGNAME);
                            exceptionTypeSimpleName = m.group(JAVA_TYPENAME_ARG_SIMPLENAME);
                            exceptionMessage = m.group("message");
                            exceptionSTrace = new StringBuilder();
                            exceptionSTrace.append(l).append(EOL);
                            straceLines = 0;
                            state++;
                        }
                        continue;
                    case 1:
                        if (l.length() == 0) {
                            if (straceLines > 0) {
                                result.add(new ParsedException(exceptionTypePkgName, exceptionTypeSimpleName,
                                    exceptionMessage, exceptionSTrace.toString()));
                            }
                            break;
                        }
                        m = STRACE_LINE.matcher(l);
                        if (m.find()) {
                            exceptionSTrace.append(l).append(EOL);
                            straceLines++;
                            continue;
                        }
                    default:
                        break;
                }
                // try again
                exceptionTypePkgName = null;
                exceptionTypeSimpleName = null;
                exceptionMessage = null;
                exceptionSTrace = null;
                straceLines = 0;
                if (state == 1) {
                    reprocessLine = true;
                }
                state = 0;
            }

            if (state != 0) {
                throw new ParseException(ln);
            }
        } finally {
            s.close();
        }
        return result;
    }

    public static ParsedException parseFirst(InputStream is) throws ParseException {
        List<ParsedException> list = parse(is);
        return list.isEmpty() ? null : list.get(0);
    }

    public static class ParsedException {

        private final String typePackageName;
        private final String typeSimpleName;
        private final String message;
        private final String stackTrace;

        public ParsedException(String typePackageName, String typeSimpleName, String message, String stackTrace) {
            this.typePackageName = typePackageName;
            this.typeSimpleName = typeSimpleName;
            this.message = message;
            this.stackTrace = stackTrace;
        }

        /**
         * @return the typePackageName
         */
        public String getTypePackageName() {
            return typePackageName;
        }

        /**
         * @return the typeSimpleName
         */
        public String getTypeSimpleName() {
            return typeSimpleName;
        }

        public String getTypeName() {
            if (typePackageName == null || typePackageName.length() == 0) {
                return typeSimpleName;
            }
            return typePackageName + "." + typeSimpleName;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @return the stackTrace
         */
        public String getStackTrace() {
            return stackTrace;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ParsedException [");
            builder.append("message=");
            builder.append(message);
            builder.append(", stackTrace=");
            builder.append(stackTrace);
            builder.append(", typePackageName=");
            builder.append(typePackageName);
            builder.append(", typeSimpleName=");
            builder.append(typeSimpleName);
            builder.append("]");
            return builder.toString();
        }

    }
}
