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
package com.github.jjYBdx4IL.parser.linux;

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;

import com.github.jjYBdx4IL.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ZFS status output parser.
 *
 * @author Github jjYBdx4IL Projects
 */
public class ZfsStatusParser {

    private static final Logger LOG = LoggerFactory.getLogger(ZfsStatusParser.class);
    private static final Pattern poolLinePattern = Pattern.compile("^  pool:");
    private static final Pattern stateLinePattern = Pattern.compile("^ state:\\s*(\\S+)$");
    private static final Pattern scrubOrScanLinePattern = Pattern.compile("^(  scan| scrub):");
    private static final Pattern scrubOrScanActivityLinePattern = Pattern
        .compile("^(  scan| scrub):.* in progress[, ]");
    private static final Pattern inProgressSincePattern = Pattern
        .compile(" in progress since [a-zA-Z]+ ([a-zA-Z]+ \\d+ \\d+:\\d+:\\d+ \\d+)");
    private static final Pattern configLinePattern = Pattern.compile("^config:");
    private static final Pattern configWhitelistLinePattern = Pattern.compile(
        "^(\\s*|\\s+NAME\\s+STATE\\s+READ\\s+WRITE\\s+CKSUM\\s*|.*\\s+0\\s+0\\s+0\\s*(?:\\s+\\(resilvering\\))?)$");
    private static final Pattern errorsLinePattern = Pattern.compile("^errors:");

    public static final DateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss yyyy", Locale.ROOT);

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Transforms the output of the <code>zpool status</code> command into an alert/error
     * level. <ul> <li>0: OK (pool state: "ONLINE") <li>1: degrading (pool
     * state: "ONLINE"): not yet degraded, but single devices show correctable
     * (due to redundancy) CRC, read or write errors without being offline.
     * <li>2: pool state: "DEGRADED" <li>3: pool state: "UNAVAIL" or everything
     * else </ul>
     *
     * @param zpoolStatusCmdOutput
     *            the console output of the "zpool status" command
     * @return the parsed {@link Result}
     * @throws com.github.jjYBdx4IL.parser.ParseException
     *             if the input is not correct/not understoof by the parser
     */
    public static Result parse(String zpoolStatusCmdOutput) throws ParseException {
        int numPools = 0;
        // 0: " pool:"
        // 1: " state:"
        // 2:ignore everything until "scrub:" or "scan:"
        // 3: ignore everything until "config:"
        // 4: look for read/write/checksum errors until "errors:" or " pool:"
        // 5: ignore everything until EOF or " pool:" (->1)
        int state = 0;
        AlertLevel alertLevel = AlertLevel.OK;
        int lineNumber = 0;
        boolean scrubOrScanActivityPresent = false;
        Instant oldestInProgressSince = null;
        for (String l : zpoolStatusCmdOutput.split("\r?\n")) {
            lineNumber++;
            if (LOG.isTraceEnabled()) {
                LOG.trace(f("line %02d, state %d: %s", lineNumber, state, l));
            }
            AlertLevel level = AlertLevel.OK;
            switch (state) {
                case 0:
                    if (!poolLinePattern.matcher(l).find()) {
                        throw new ParseException(
                            createExceptionMessage(zpoolStatusCmdOutput, lineNumber, "zpool name"));
                    }
                    numPools++;
                    state++;
                    break;
                case 1:
                    Matcher m = stateLinePattern.matcher(l);
                    if (!m.find()) {
                        throw new ParseException(
                            createExceptionMessage(zpoolStatusCmdOutput, lineNumber, "zpool state"));
                    }
                    try {
                        level = PoolState.valueOf(m.group(1)).getAlertLevel();
                    } catch (IllegalArgumentException ex) {
                        throw new ParseException(createExceptionMessage(zpoolStatusCmdOutput, lineNumber,
                            "zpool state, unknown state: " + m.group(1)));
                    }
                    state++;
                    break;
                case 2:
                    if (scrubOrScanLinePattern.matcher(l).find()) {
                        state++;
                    }
                    if (scrubOrScanActivityLinePattern.matcher(l).find()) {
                        scrubOrScanActivityPresent = true;
                    }
                    m = inProgressSincePattern.matcher(l);
                    if (m.find()) {
                        try {
                            Instant inProgressSince = sdf.parse(m.group(1)).toInstant();
                            if (oldestInProgressSince == null || inProgressSince.isBefore(oldestInProgressSince)) {
                                oldestInProgressSince = inProgressSince;
                            }
                        } catch (java.text.ParseException e) {
                            throw new ParseException("failed to parse date: " + l);
                        }
                    }
                    break;
                case 3:
                    if (configLinePattern.matcher(l).find()) {
                        state++;
                    }
                    break;
                case 4:
                    if (poolLinePattern.matcher(l).find()) {
                        numPools++;
                        state = 1;
                    } else if (errorsLinePattern.matcher(l).find()) {
                        state++;
                    } else if (!configWhitelistLinePattern.matcher(l).find()) {
                        level = AlertLevel.DEGRADING;
                    }
                    break;
                case 5:
                    if (poolLinePattern.matcher(l).find()) {
                        numPools++;
                        state = 1;
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }
            alertLevel = level.worseThan(alertLevel) ? level : alertLevel;
        }
        if (state == 0) {
            throw new ParseException(createExceptionMessage(zpoolStatusCmdOutput, lineNumber, "zpool name"));
        }
        if (state == 1) {
            throw new ParseException(createExceptionMessage(zpoolStatusCmdOutput, lineNumber, "zpool state"));
        }
        if (state == 2) {
            throw new ParseException(createExceptionMessage(zpoolStatusCmdOutput, lineNumber, "zpool scrub/scan"));
        }
        if (state == 3) {
            throw new ParseException(createExceptionMessage(zpoolStatusCmdOutput, lineNumber, "zpool config"));
        }
        if (state == 4) {
            throw new ParseException(createExceptionMessage(zpoolStatusCmdOutput, lineNumber, "zpool errors"));
        }
        if (oldestInProgressSince != null && !scrubOrScanActivityPresent) {
            throw new IllegalStateException();
        }
        return new Result(alertLevel, numPools, scrubOrScanActivityPresent, oldestInProgressSince);
    }

    private static String createExceptionMessage(String zpoolStatusCmdOutput, int errorLineNumber,
        String errorMessage) {
        StringBuilder sb = new StringBuilder();
        int lineNumber = 0;
        for (String l : zpoolStatusCmdOutput.split(System.lineSeparator())) {
            if (lineNumber == errorLineNumber) {
                sb.append("   ^-- ERROR! invalid zpool status format, expected ");
                sb.append(errorMessage);
                sb.append(System.lineSeparator());
            }
            lineNumber++;
            sb.append(l);
            sb.append(System.lineSeparator());
        }
        if (lineNumber == errorLineNumber) {
            sb.append("   ^-- ERROR! invalid zpool status format, expected ");
            sb.append(errorMessage);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private ZfsStatusParser() {
    }

    public enum AlertLevel {

        OK(0), DEGRADING(1), DEGRADED(2), FAILURE(3);

        private final int numericLevel;

        AlertLevel(int numericLevel) {
            this.numericLevel = numericLevel;
        }

        public boolean worseThan(AlertLevel other) {
            return numericLevel > other.numericLevel;
        }

        public static AlertLevel max(AlertLevel a, AlertLevel b) {
            return a.worseThan(b) ? a : b;
        }

        protected static AlertLevel getByNumericLevel(int numericLevel) {
            for (AlertLevel level : values()) {
                if (level.numericLevel == numericLevel) {
                    return level;
                }
            }
            throw new IllegalArgumentException("invalid numeric level: " + numericLevel);
        }
    }

    public static class Result {

        /**
         * the alert level.
         * 
         * @return the alertLevel
         */
        public AlertLevel getAlertLevel() {
            return alertLevel;
        }

        /**
         * number of pools.
         * 
         * @return the numPools
         */
        public int getNumPools() {
            return numPools;
        }

        /**
         * is any activity going on?
         * 
         * <p>This is somewhat redundant with {@link #getInProgressSince()}.
         * 
         * @return the scrubOrScanActivityPresent
         */
        public boolean isScrubOrScanActivityPresent() {
            return scrubOrScanActivityPresent;
        }

        /**
         * For newer ZFS versions this returns the oldest start time of any
         * ongoing operation (resilver/scrub/scan).
         * 
         * @return in progress since-instant
         */
        public Instant getInProgressSince() {
            return inProgressSince;
        }

        private final AlertLevel alertLevel;
        private final int numPools;
        private final boolean scrubOrScanActivityPresent;
        private final Instant inProgressSince;

        /**
         * Constructor.
         */
        public Result(AlertLevel alertLevel, int numPools, boolean scrubOrScanActivityPresent,
            Instant inProgressSince) {
            this.alertLevel = alertLevel;
            this.numPools = numPools;
            this.scrubOrScanActivityPresent = scrubOrScanActivityPresent;
            this.inProgressSince = inProgressSince;
        }


        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Result [alertLevel=").append(alertLevel).append(", numPools=").append(numPools)
                .append(", scrubOrScanActivityPresent=").append(scrubOrScanActivityPresent).append(", inProgressSince=")
                .append(inProgressSince).append("]");
            return builder.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((alertLevel == null) ? 0 : alertLevel.hashCode());
            result = prime * result + ((inProgressSince == null) ? 0 : inProgressSince.hashCode());
            result = prime * result + numPools;
            result = prime * result + (scrubOrScanActivityPresent ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Result other = (Result) obj;
            if (alertLevel != other.alertLevel) {
                return false;
            }
            if (inProgressSince == null) {
                if (other.inProgressSince != null) {
                    return false;
                }
            } else if (!inProgressSince.equals(other.inProgressSince)) {
                return false;
            }
            if (numPools != other.numPools) {
                return false;
            }
            if (scrubOrScanActivityPresent != other.scrubOrScanActivityPresent) {
                return false;
            }
            return true;
        }
    }

    private enum PoolState {

        UNAVAIL, DEGRADED, DEGRADING, ONLINE, UNKNOWN;

        public AlertLevel getAlertLevel() {
            switch (this) {
                case ONLINE:
                    return AlertLevel.OK;
                case DEGRADING:
                    return AlertLevel.DEGRADING;
                case DEGRADED:
                    return AlertLevel.DEGRADED;
                default:
                    return AlertLevel.FAILURE;
            }
        }
    }
}
