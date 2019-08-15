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

import com.github.jjYBdx4IL.parser.ParseException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and analyzes "time" command output, which is available on linux boxes.
 *
 */
public class CpuTimeOutputParser {

    public static final Pattern PAT = Pattern.compile("(real|user|sys)\\s+(\\d+)m(\\d+)[,.](\\d+)s");

    /**
     * All values are stored as milliseconds.
     *
     */
    public static class UsageData {
        final long real;
        final long user;
        final long sys;

        UsageData(long real, long user, long sys) {
            this.real = real;
            this.user = user;
            this.sys = sys;
        }

        public String toString() {
            return "UsageData [real=" + real + "ms, user=" + user + "ms, sys=" + sys + "ms]";
        }
    }

    /**
     * Parses and analyzes "time" command output, which is available on linux
     * boxes. Takes a single output in the form:
     * 
     * <pre>
     * real 0m37,766s
     * user 1m32,058s
     * sys  0m2,724s
     * </pre>
     * 
     * @param input
     *            the string to parse
     * @return the parsed result
     * @throws ParseException
     *             on error
     */
    public static UsageData parse(String input) throws ParseException {
        Matcher m = PAT.matcher(input);
        Long real = null;
        Long user = null;
        Long sys = null;
        int count = 0;
        while (m.find()) {
            String key = m.group(1);
            Long minutes = Long.parseLong(m.group(2));
            Long seconds = Long.parseLong(m.group(3));
            String msStr = m.group(4);
            while (msStr.length() < 3) {
                msStr += "0";
            }
            long millis = ((minutes * 60) + seconds) * 1000L + Long.parseLong(msStr);
            if ("real".equals(key)) {
                real = millis;
            } else if ("user".equals(key)) {
                user = millis;
            } else if ("sys".equals(key)) {
                sys = millis;
            } else {
                throw new ParseException("invalid format: " + input);
            }
            count++;
        }
        if (count != 3 || real == null || user == null || sys == null) {
            throw new ParseException("invalid format: " + input);
        }
        return new UsageData(real, user, sys);
    }

    /**
     * Same as {@link #parse(String)}, but the input string may consist of
     * multiple outputs.
     * 
     * @param input
     *            the string to parse
     * @return the parsed result
     * @throws ParseException
     *             on error
     */
    public static List<UsageData> parseMulti(String input) throws ParseException {
        List<UsageData> result = new ArrayList<>();
        String[] parts = input.split("real");
        boolean first = true;
        for (String part : parts) {
            if (first) {
                first = false;
                continue;
            }
            result.add(parse("real" + part));
        }
        return result;
    }
    
    /**
     * Calculate mean and standard deviations for a list of UsageData.
     * 
     * @param data minimu length is 2
     * @return the result
     */
    public static String dumpStats(List<UsageData> data) {
        if (data.size() < 2) {
            throw new IllegalArgumentException("list too short, min length is 2");
        }
        
        long realSum = 0;
        long userSum = 0;
        long sysSum = 0;
        for (UsageData usage : data) {
            realSum += usage.real;
            userSum += usage.user;
            sysSum += usage.sys;
        }
        double realMean = 1d * realSum / data.size();
        double userMean = 1d * userSum / data.size();
        double sysMean = 1d * sysSum / data.size();
        double realSqDeltaSum = 0d;
        double userSqDeltaSum = 0d;
        double sysSqDeltaSum = 0d;
        for (UsageData usage : data) {
            realSqDeltaSum += Math.pow(usage.real - realMean, 2);
            userSqDeltaSum += Math.pow(usage.user - userMean, 2);
            sysSqDeltaSum += Math.pow(usage.sys - sysMean, 2);
        }
        double realSdev = Math.sqrt(realSqDeltaSum) / (data.size() - 1);
        double userSdev = Math.sqrt(userSqDeltaSum) / (data.size() - 1);
        double sysSdev = Math.sqrt(sysSqDeltaSum) / (data.size() - 1);
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("real: (%,.0f +/- %,.0f)ms %s", realMean, realSdev, System.lineSeparator()));
        sb.append(String.format("user: (%,.0f +/- %,.0f)ms %s", userMean, userSdev, System.lineSeparator()));
        sb.append(String.format("sys: (%,.0f +/- %,.0f)ms", sysMean, sysSdev, System.lineSeparator()));
        return sb.toString();
    }
    
    /**
     * Run this file to calculate statistics from the pasted cpu time usage outputs.
     * 
     * @param args not used
     * @throws IOException on error
     * @throws ParseException on error
     */
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Paste to text to parse and press CTRL-D:");
        String input = IOUtils.toString(System.in, Charset.defaultCharset());
        List<UsageData> result = parseMulti(input);
        System.out.println(dumpStats(result));
    }
}
