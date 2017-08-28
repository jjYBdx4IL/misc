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

//CHECKSTYLE:OFF
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for /proc/net/dev.
 *
 * @author Github jjYBdx4IL Projects
 */
public class ProcNetDevParser {

    private static final Logger LOG = LoggerFactory.getLogger(ProcNetDevParser.class);
    public static final String PROC_NET_DEV_PATH = "/proc/net/dev";
    // CHECKSTYLE IGNORE .* FOR NEXT 3 LINES
    //Inter-|   Receive                                                |  Transmit
    // face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed
    //  eth0: 324573497  424959 0 0 0  0     0   0 43963006  337366    0    0    0     0       0          0
    private static final Pattern PATTERN = Pattern.compile(
            "^\\s*(\\S+):\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)"
            + "\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)"
            + "\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)", Pattern.CASE_INSENSITIVE);

    public static ProcNetDevData get(String device) {
        try (InputStream is = new FileInputStream(PROC_NET_DEV_PATH)) {
            return get(device, is);
        } catch (IOException ex) {
            LOG.error("failed to open " + PROC_NET_DEV_PATH, ex);
            throw new RuntimeException(ex);
        }
    }

    protected static ProcNetDevData get(String device, InputStream procNetDevInputStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(procNetDevInputStream, "ASCII"));
        String line = r.readLine();
        ProcNetDevData dd = null;
        while (line != null) {
            Matcher m = PATTERN.matcher(line);
            if (m.find() && device.equalsIgnoreCase(m.group(1))) {
                // CHECKSTYLE IGNORE MagicNumber FOR NEXT 9 LINES
                dd = new ProcNetDevData(m.group(1),
                        Long.valueOf(m.group(2)), Long.valueOf(m.group(3)),
                        Long.valueOf(m.group(4)), Long.valueOf(m.group(5)),
                        Long.valueOf(m.group(6)), Long.valueOf(m.group(7)),
                        Long.valueOf(m.group(8)), Long.valueOf(m.group(9)),
                        Long.valueOf(m.group(10)), Long.valueOf(m.group(11)),
                        Long.valueOf(m.group(12)), Long.valueOf(m.group(13)),
                        Long.valueOf(m.group(14)), Long.valueOf(m.group(15)),
                        Long.valueOf(m.group(16)), Long.valueOf(m.group(17)));
                break;
            }
            line = r.readLine();
        }
        return dd;
    }

    private ProcNetDevParser() {
    }
}
