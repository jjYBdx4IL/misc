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

//CHECKSTYLE:OFF
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * <a href="https://issues.apache.org/jira/browse/IO-373">https://issues.apache.org/jira/browse/IO-373</a>
 *
 * @author Github jjYBdx4IL Projects
 */
public class FileSizeFormatter extends org.apache.commons.io.FileUtils {

    private static final int DEFAULT_MAXCHARS = 3;
    private static final BigDecimal KILO_DIVISOR = new BigDecimal(1024L);

    enum SizeSuffix {

        B, KB, MB, GB, TB, PB, EB, ZB, YB;
    }

    /**
     * Adopted and improved version of
     * {@link org.apache.commons.io.FileUtils#byteCountToDisplaySize(BigInteger)}.
     * <p>
     * Warning! it is not advised to use <code>maxChars &lt; 3</code> because it produces correctly rounded,
     * but non-intuitive results like "0 KB" for 100 bytes.
     *
     * <ul><li><a
     * href="https://issues.apache.org/jira/browse/IO-226">https://issues.apache.org/jira/browse/IO-226</a> -
     * should the rounding be changed?
     * <li><a
     * href="https://issues.apache.org/jira/browse/IO-373">https://issues.apache.org/jira/browse/IO-373</a></ul>
     *
     * @param size the size in bytes
     * @param maxChars maximum length of digit part, ie. '1.2'
     * @return rounded byte size as {@link java.lang.String}
     */
    public static String byteCountToDisplaySize(BigInteger size, int maxChars) {
        String displaySize;
        BigDecimal bdSize = new BigDecimal(size);
        SizeSuffix selectedSuffix = SizeSuffix.B;
        for (SizeSuffix sizeSuffix : SizeSuffix.values()) {
            if (sizeSuffix.equals(SizeSuffix.B)) {
                continue;
            }
            if (bdSize.setScale(0, RoundingMode.HALF_UP).toString().length() <= maxChars) {
                break;
            }
            selectedSuffix = sizeSuffix;
            bdSize = bdSize.divide(KILO_DIVISOR);
        }
        displaySize = bdSize.setScale(0, RoundingMode.HALF_UP).toString();
        if (displaySize.length() < maxChars - 1) {
            displaySize = bdSize.setScale(
                    maxChars - 1 - displaySize.length(), RoundingMode.HALF_UP).toString();
        }
        return displaySize + " " + selectedSuffix.toString();
    }

    public static String byteCountToDisplaySize(BigInteger size) {
        return byteCountToDisplaySize(size, DEFAULT_MAXCHARS);
    }

    /**
     * See {@link #byteCountToDisplaySize(BigInteger,int)}.
     *
     * @param size the size in bytes
     * @param maxChars maximum length of digit part, ie. '1.2'
     * @return formatted size suitable for display
     */
    public static String byteCountToDisplaySize(long size, int maxChars) {
        return byteCountToDisplaySize(BigInteger.valueOf(size), maxChars);
    }

    public static String byteCountToDisplaySize(long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size), DEFAULT_MAXCHARS);
    }

}
