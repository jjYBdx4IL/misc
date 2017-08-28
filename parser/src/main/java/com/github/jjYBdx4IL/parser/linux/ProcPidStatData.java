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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * http://linux.die.net/man/5/proc
 * 
 * @author Github jjYBdx4IL Projects
 */
public class ProcPidStatData {

    private final String[] procPidStatContent;

    public ProcPidStatData(int pid) throws IOException {
        this(new File(String.format("/proc/%d/stat", pid)));
    }

    // package access for testing
    @SuppressWarnings("deprecation")
    ProcPidStatData(File statFile) throws IOException {
        final String inputLine;
        try (InputStream is = new FileInputStream(statFile)) {
            inputLine = IOUtils.readLines(is).get(0);
        }
        procPidStatContent = inputLine.split(" ");
        if (procPidStatContent.length < ProcPidStatEntry.values().length) {
            throw new IOException(String.format("invalid format: %s (%s)",
                    inputLine, statFile.getAbsolutePath()));
        }
    }

    public long getLong(ProcPidStatEntry entry) {
        switch (entry) {
            case pid: //: 1289
            case ppid: //: 1272
            case pgid: //: 1271
            case sid: //: 1271
            case tty_nr: //: 0
            case tty_pgrp: //: -1
            case flags: //: 1077936128
            case min_flt: //: 198123
            case cmin_flt: //: 11758436
            case maj_flt: //: 343
            case cmaj_flt: //: 1099
            case utime: //: 195.050000
            case stime: //: 21.800000
            case cutime: //: 2122.050000
            case cstime: //: 86.210000
            case priority: //: 20
            case nice: //: 0
            case num_threads: //: 45
            case it_real_value: //: 0.000000
            case start_time: //: 02.20 01:40 (5014.8s)
            case vsize: //: 7875907584
            case rss: //: 142401
            case rsslim: //: 9223372036854775807
            case start_code: //: 1
            case end_code: //: 1
            case start_stack: //: 0
            case esp: //: 0
            case eip: //: 0
// TODO:
//            case pending: //: 0000000000000000
//            case blocked: //: 0000000000000000
//            case sigign: //: 0000000000000001
//            case sigcatch: //: 0000000001005cce
            case wchan: //: 0
            case zero1: //: 0
            case zero2: //: 0
            case exit_signal: //: 0000000000000011
            case cpu: //: 1
            case rt_priority: //: 0
            case policy: //: 0
                return Long.valueOf(getRaw(entry));
        default:
            break;
        }
        throw new IllegalArgumentException(String.format("%s is not of type long", entry));
    }

    public String getRaw(ProcPidStatEntry entry) {
        return procPidStatContent[entry.index()];
    }

    public String getName() {
        String s = getRaw(ProcPidStatEntry.tcomm);
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length()-1);
        }
        return s;
    }

    public LinuxProcessState getState() {
        return LinuxProcessState.valueOf(getRaw(ProcPidStatEntry.state));
    }

    public long getPID() {
        return getLong(ProcPidStatEntry.pid);
    }

    public long getPPID() {
        return getLong(ProcPidStatEntry.ppid);
    }

    public long getPGID() {
        return getLong(ProcPidStatEntry.pgid);
    }

    /**
     * Resident size in terms of pages.
     * 
     * @return the RSS size in memory page units.
     */
    public long getRSS() {
        return getLong(ProcPidStatEntry.rss);
    }
}
