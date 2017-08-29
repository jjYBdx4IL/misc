/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.osdapp;

import com.github.jjYBdx4IL.parser.linux.ProcDiskStatsParser;
import com.github.jjYBdx4IL.parser.linux.ProcDiskStatsParser.Data;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JLabel;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
class DiskStandbyPoller extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(DiskStandbyPoller.class);
    private final static long ISSUE_SLEEP_IVAL_MILLIS = 15L * 60L * 1000L;
    private final static String COMMAND_PREPARE = "sudo hdparm -S 0 /dev/%s";
    private final static String COMMAND = "sudo hdparm -y /dev/%s";

    static {
        // force exception if commons-lang dep is missing during integration tests
        StringUtils.join(new String[]{}, "");
    }

    public static String getPrepareCommand(String device) {
        return String.format(COMMAND_PREPARE, device);
    }

    public static String getCommand(String device) {
        return String.format(COMMAND, device);
    }

    private boolean issueSleep(String device) {
        if (script == null) {
            //runCommand(getPrepareCommand(device));
            return runCommand(getCommand(device).trim().split("\\s+"));
        } else {
            return runCommand(new String[]{"sudo", script, "/dev/" + device});
        }
    }

    private boolean runCommand(String[] cmd) {
        String joinedCmd = StringUtils.join(cmd, " ");
        LOG.debug(joinedCmd);
        ProcessBuilder pb = new ProcessBuilder(cmd);
        try {
            Process p = pb.start();
            int exitCode = p.waitFor();
            LOG.debug("exit code = " + exitCode);
            if (exitCode != 0) {
                throw new IOException(joinedCmd + " returned with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException ex) {
            LOG.error("failed to issue sleep command: " + joinedCmd, ex);
            return false;
        }
        return true;
    }
    private final List<String> devices = new ArrayList<>();
    private final List<Integer> standbySeconds = new ArrayList<>();
    private final Data[] lastData;
    private final long[] lastDataMillis;
    private final long[] lastIssueSleepMillis;
    private final boolean[] lastIssueSleepResult;
    private final JLabel labelToUpdate;
    private final String script;

    DiskStandbyPoller(String[] deviceSpecs, String script, JLabel label) {
        this.script = script;
        if (deviceSpecs == null) {
            throw new IllegalArgumentException();
        }
        if (label == null) {
            throw new IllegalArgumentException();
        }
        for (String spec : deviceSpecs) {
            String[] specParts = spec.split(",", 2);
            devices.add(specParts[0]);
            standbySeconds.add(specParts.length > 1 ? Integer.parseInt(specParts[1]) * 60 : -1);
        }
        this.lastData = new Data[devices.size()];
        this.lastDataMillis = new long[devices.size()];
        this.lastIssueSleepMillis = new long[devices.size()];
        this.lastIssueSleepResult = new boolean[devices.size()];
        for (int i = 0; i < devices.size(); i++) {
            this.lastData[i] = null;
            this.lastDataMillis[i] = -1L;
            this.lastIssueSleepMillis[i] = -1L;
            this.lastIssueSleepResult[i] = true;
        }
        this.labelToUpdate = label;
    }

    @Override
    public void run() {
        poll();
    }

    private void poll() {
        DisplayLevel level = DisplayLevel.OK;

        StringBuilder msg = new StringBuilder();
        msg.append("[");

        for (int i = 0; i < devices.size(); i++) {
            String device = devices.get(i);
            Data data = ProcDiskStatsParser.get(device);
            long timeMillis = System.currentTimeMillis();

            if (data == null) {
                LOG.error("failed to retrieve diskstats for " + device);
                level = DisplayLevel.CRITICAL;
                continue;
            }

            if (lastData[i] != null) {
                if (data.equals(lastData[i])) {
                    if (standbySeconds.get(i) != -1
                            && (timeMillis - lastDataMillis[i]) >= standbySeconds.get(i) * 1000L
                            && (timeMillis - lastIssueSleepMillis[i]) >= ISSUE_SLEEP_IVAL_MILLIS) {
                        lastIssueSleepResult[i] = issueSleep(device);
                        lastIssueSleepMillis[i] = timeMillis;
                        // delay next sleep command by ISSUE_SLEEP_IVAL
                        lastDataMillis[i] = timeMillis;
                    }
                    level = lastIssueSleepResult[i] ? level : DisplayLevel.CRITICAL;
                    msg.append(lastIssueSleepResult[i] ? "I" : "E");
                    continue;
                }
                msg.append(lastIssueSleepResult[i] ? "A" : "E");
            } else {
                msg.append(".");
            }

            level = lastIssueSleepResult[i] ? level : DisplayLevel.CRITICAL;

            lastData[i] = data;
            lastDataMillis[i] = timeMillis;
        }

        msg.append("]");
        LOG.debug(msg.toString());

        DeliverMessageUpdate.deliver(msg.toString(), level, labelToUpdate);
    }

}
