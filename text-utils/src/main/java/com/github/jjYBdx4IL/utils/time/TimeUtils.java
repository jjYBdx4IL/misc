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
package com.github.jjYBdx4IL.utils.time;

//CHECKSTYLE:OFF
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jjYBdx4IL
 */
public class TimeUtils {

    public final static String TZ = "UTC";
    public final static String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm'Z'";
    public final static String ISO8601_DATE_FMT = "yyyy-MM-dd";
    public final static String ISO8601_SECS_FMT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final Pattern DURATION_PATTERN = Pattern.compile(
        "(?:(\\d+)w)?\\s*(?:(\\d+)d)?\\s*(?:(\\d+)h)?\\s*(?:(\\d+)m)?\\s*(?:(\\d+)s)?", Pattern.CASE_INSENSITIVE);

    /**
     * Converts a given date to its ISO8601 ({@link #ISO8601_FMT}) format.
     * Always uses UTC for string representation of the date.
     * 
     * @param date
     *            the date to format
     * @return the formatted date in ISO8601 without seconds part
     */
    public static String toISO8601(Date date) {
        final TimeZone tz = TimeZone.getTimeZone(TZ);
        final DateFormat df = new SimpleDateFormat(ISO8601_FMT);
        df.setTimeZone(tz);
        return df.format(date);
    }

    /**
     * Converts a given date to its ISO8601 ({@link #ISO8601_DATE_FMT}) format.
     * Always uses UTC for string representation of the date. Only returns the
     * date part.
     * 
     * @param date
     *            the date to format
     * @return the date part of the ISO8601 formatted time 
     */
    public static String toISO8601Date(Date date) {
        final TimeZone tz = TimeZone.getTimeZone(TZ);
        final DateFormat df = new SimpleDateFormat(ISO8601_DATE_FMT);
        df.setTimeZone(tz);
        return df.format(date);
    }

    /**
     * Converts a given date to its ISO8601 ({@link #ISO8601_SECS_FMT}) format.
     * Always uses UTC for string representation of the date.
     * 
     * @param date
     *            the date to format
     * @return the formatted date in ISO8601 with seconds part
     */
    public static String toISO8601WithSeconds(Date date) {
        final TimeZone tz = TimeZone.getTimeZone(TZ);
        final DateFormat df = new SimpleDateFormat(ISO8601_SECS_FMT);
        df.setTimeZone(tz);
        return df.format(date);
    }

    /**
     * Parse a date formatted as ISO8601. This function assumes the date
     * contains seconds, if not it falls back to minute precision.
     * 
     * @param input
     *            the ISO8601 formatted date
     * @return the Date object
     * @throws ParseException
     *             if the input string has a wrong format
     */
    public static Date parseISO8601(String input) throws ParseException {
        final TimeZone tz = TimeZone.getTimeZone(TZ);
        try {
            final DateFormat df = new SimpleDateFormat(ISO8601_SECS_FMT);
            df.setTimeZone(tz);
            df.setLenient(false);
            return df.parse(input);
        } catch (ParseException ex) {
            final DateFormat df = new SimpleDateFormat(ISO8601_FMT);
            df.setTimeZone(tz);
            df.setLenient(false);
            return df.parse(input);
        }
    }

    /**
     * Convert a millisecond duration/time amount into a string formatted like
     * "1w3d4s". For an exact specification of the returned string, refer to
     * {@link #durationToMillis(String)}.
     * 
     * @param millis
     *            the milliseconds to convert
     * @return the string description of the time amount
     */
    public static String millisToDuration(long millis) {
        StringBuilder sb = new StringBuilder();
        if (millis < 0) {
            sb.append("-");
            millis = -millis;
        }
        if (millis < 1000L) {
            return "0s";
        }
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        if (weeks > 0L) {
            sb.append(Long.toString(weeks));
            sb.append("w");
        }
        if (days % 7L > 0L) {
            sb.append(Long.toString(days % 7L));
            sb.append("d");
        }
        if (hours % 24L > 0L) {
            sb.append(Long.toString(hours % 24L));
            sb.append("h");
        }
        if (minutes % 60L > 0L) {
            sb.append(Long.toString(minutes % 60L));
            sb.append("m");
        }
        if (seconds % 60L > 0L) {
            sb.append(Long.toString(seconds % 60L));
            sb.append("s");
        }
        return sb.toString();
    }

    /**
     * Convert a duration/time amount specification into a millisecond value.
     * Example: "2d3h5s" - which stands for 2days, 3 hours and 5 seconds.
     * Supported elements are:
     * <ul>
     * <li>w - week
     * <li>d - day
     * <li>h - hour
     * <li>m - minute
     * <li>s - second
     * </ul>
     * Order is important, each element must be listed not more than once in the
     * order listed above.
     * 
     * @param duration
     *            the duration/time amount in the format described above.
     * @return the number of milliseconds
     */
    public static long durationToMillis(String duration) {
        long millis = 0L;
        Matcher m = DURATION_PATTERN.matcher(duration);
        if (m.find()) {
            if (m.group(1) != null) {
                millis += Long.parseLong(m.group(1));
            }
            millis *= 7L;
            if (m.group(2) != null) {
                millis += Long.parseLong(m.group(2));
            }
            millis *= 24L;
            if (m.group(3) != null) {
                millis += Long.parseLong(m.group(3));
            }
            millis *= 60L;
            if (m.group(4) != null) {
                millis += Long.parseLong(m.group(4));
            }
            millis *= 60L;
            if (m.group(5) != null) {
                millis += Long.parseLong(m.group(5));
            }
            millis *= 1000L;
        }
        return millis;
    }

    /**
     * Check if a given {@link Date} is older than some amount of time.
     * 
     * @param date
     *            the date in question
     * @param ageDurationStr
     *            the amount of time in a format described at
     *            {@link #durationToMillis(String)}
     * @return true if date lies father back in time than what durationStr
     *         indicates
     */
    public static boolean isOlderThan(Date date, String ageDurationStr) {
        long minMillisAge = durationToMillis(ageDurationStr);
        long millisAge = System.currentTimeMillis() - date.getTime();
        return millisAge > minMillisAge;
    }

    private TimeUtils() {
    }
}
