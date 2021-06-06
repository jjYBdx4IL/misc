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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartCtlParser {

    protected static final Pattern PAT_DEV_SERIAL = Pattern.compile("\\nSerial Number:\\s+(\\S.*)");
    protected static final Pattern PAT_PWRONHRS = Pattern.compile("\\n\\s*9\\s+Power_On_Hours\\s+.*?\\s+(\\d+)\\n");
    protected static final Pattern PAT_shortTestRegex = Pattern
        .compile("\\n#\\s*\\d+\\s+Short offline\\s+Completed without error\\s+00\\%\\s+(\\d+)\\s+-");
    protected static final Pattern PAT_longTestRegex = Pattern
        .compile("\\n#\\s*\\d+\\s+Extended offline\\s+Completed without error\\s+00\\%\\s+(\\d+)\\s+-");

    /**
     * Parses output of <code>smartctl -a /dev/sdX</code>.
     */
    public static Result parse(String input) throws ParseException {
        Long shortTestAge = null;
        Long longTestAge = null;
        
        final String infoSectionStr = getSection(input, "\n\n=== START OF INFORMATION SECTION ===");
        
        Matcher m = PAT_DEV_SERIAL.matcher(infoSectionStr);
        if (!m.find()) {
            throw new ParseException("cannot find device serial");
        }
        final String devSerial = m.group(1);

        // extract the device's internal time reference (power on hours) ...

        //<empty line>
        //SMART Attributes Data Structure revision number : 16
        //  Vendor Specific SMART Attributes with Thresholds :
        //ID# ATTRIBUTE_NAME          FLAG     VALUE WORST THRESH TYPE      UPDATED  WHEN_FAILED RAW_VALUE
        //  1 Raw_Read_Error_Rate     0x002f   200   200   051    Pre - fail  Always - 0
        //...
        //  9 Power_On_Hours          0x0032   100   100   000    Old_age   Always - 10
        //...
        //<empty line>

        final String attrsSectionStr = getSection(input, "\n\nSMART Attributes Data Structure revision number");
        if (attrsSectionStr.isEmpty()) {
            throw new ParseException("cannot find SMART Attributes section for " + devSerial);
        }

        m = PAT_PWRONHRS.matcher(attrsSectionStr);
        if (!m.find()) {
            throw new ParseException("cannot find Power_On_Hours SMART Attribute for " + devSerial);
        }

        final long pwrOnHrs = Long.parseLong(m.group(1));
        
        // ... and compare it against last successful completions of both short and long self-tests:

        //<empty line>
        //SMART Self-test log structure revision number 1
        //Num  Test_Description    Status                  Remaining  LifeTime(hours)  LBA_of_first_error
        //# 1  Short offline       Completed without error       00 % 0 -
        //<empty line>
        final String selfTestLogStr = getSection(input, "\n\nSMART Self-test log structure");
        if (selfTestLogStr.isEmpty()) {
            throw new ParseException("cannot find Self-test log structure output for " + devSerial);
        }

        m = PAT_shortTestRegex.matcher(selfTestLogStr);
        if (m.find()) {
            shortTestAge = pwrOnHrs - Long.parseLong(m.group(1));
        }

        m = PAT_longTestRegex.matcher(selfTestLogStr);
        if (m.find()) {
            longTestAge = pwrOnHrs - Long.parseLong(m.group(1));
        }
        
        return new Result(devSerial, pwrOnHrs, shortTestAge, longTestAge);
    }

    protected static String getSection(String input, String start) {
        int pos = input.indexOf(start);
        if (pos == -1) {
            return "";
        }
        int endpos = input.indexOf("\n\n", pos + start.length());
        if (endpos == -1) {
            return input.substring(pos);
        }
        return input.substring(pos, endpos);
    }

    public static class Result {
        public final String devSerial;
        public final long pwrOnHrs;
        public final Long shortTestAgeHrs;
        public final Long longTestAgeHrs;
        
        /**
         * The parse result.
         */
        public Result(String devSerial, long pwrOnHrs, Long shortTestAgeHrs, Long longTestAgeHrs) {
            this.devSerial = devSerial;
            this.pwrOnHrs = pwrOnHrs;
            this.shortTestAgeHrs = shortTestAgeHrs;
            this.longTestAgeHrs = longTestAgeHrs;
        }
    }
}
