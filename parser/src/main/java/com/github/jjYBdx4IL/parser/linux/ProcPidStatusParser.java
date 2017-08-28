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
public class ProcPidStatusParser {

    public String name = null;
    public LinuxProcessState state = null;
    public int tgid = -1;
    public int ngid = -1;
    public int pid = -1;
    public int ppid = -1;
    public int tracerPid = -1;
    /**
     * kB
     */
    public long vmrss = -1;
    /**
     * kB
     */
    public long vmswap = -1;
    public int threads = -1;

    public ProcPidStatusParser() throws IOException {
    }

    public ProcPidStatusParser parse(int pid) throws IOException {
        return parse(new File(String.format("/proc/%d/status", pid)));
    }

    // package access for testing
    @SuppressWarnings("deprecation")
    ProcPidStatusParser parse(File statFile) throws IOException {

        try (InputStream is = new FileInputStream(statFile)) {
            for (String l : IOUtils.readLines(is)) {
                String attrName = l.substring(0, l.indexOf(":"));
                switch (attrName) {
                    case "Name":
                        name = l.substring(attrName.length()+1).trim();
                        break;
                    case "State":
                        state = LinuxProcessState.valueOf(l.substring(attrName.length()+1).trim().substring(0, 1));
                        break;
                    case "Tgid":
                        tgid = Integer.valueOf(l.substring(attrName.length()+1).trim());
                        break;
                    case "Ngid":
                        ngid = Integer.valueOf(l.substring(attrName.length()+1).trim());
                        break;
                    case "Pid":
                        pid = Integer.valueOf(l.substring(attrName.length()+1).trim());
                        break;
                    case "PPid":
                        ppid = Integer.valueOf(l.substring(attrName.length()+1).trim());
                        break;
                    case "TracerPid":
                        tracerPid = Integer.valueOf(l.substring(attrName.length()+1).trim());
                        break;
                    case "VmRSS":
                        vmrss = extractLongKB(l, attrName.length()+1);
                        break;
                    case "VmSwap":
                        vmswap = extractLongKB(l, attrName.length()+1);
                        break;
                    case "Threads":
                        threads = Integer.valueOf(l.substring(attrName.length()+1).trim());
                        break;
                }
            }
        }

        return this;
    }

    private long extractLongKB(String s, int offset) {
        if (!s.endsWith(" kB")) {
            throw new IllegalArgumentException(s);
        }
        return Long.valueOf(s.substring(offset, s.length()-3).trim());
    }

}
