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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.jjYBdx4IL.parser.ParseException;
import com.github.jjYBdx4IL.parser.linux.ZfsStatusParser;
import com.github.jjYBdx4IL.parser.linux.ZfsStatusParser.Result;
import com.github.jjYBdx4IL.utils.net.WakeOnLAN;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.TimerTask;

import javax.swing.JLabel;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
class ZFSPoller extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(ZFSPoller.class);
    public final static String SUDOERS_LINE = "your-login-name ALL = (root) NOPASSWD: /sbin/zpool status";
    public final static String COMMAND = "sudo zpool status";
    private final JLabel labelToUpdate;
    private final WakeOnLAN wol;

    ZFSPoller(WakeOnLAN wol, JLabel label) {
        this.labelToUpdate = label;
        this.wol = wol;
    }

    @Override
    public void run() {
        poll();
    }

    private void poll() {

        Result result = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(COMMAND.split("\\s+"));
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
            Process p = pb.start();
            String zpoolStatusCommandOutput = IOUtils.toString(p.getInputStream(), UTF_8);
            p.waitFor();
            if (p.exitValue() != 0) {
                throw new IOException("zpool status command returned bad exit code " + p.exitValue()
                        + ", add the line \"" + SUDOERS_LINE + "\" "
                        + "to your sudoers file.");
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace(zpoolStatusCommandOutput);
            }

            result = ZfsStatusParser.parse(zpoolStatusCommandOutput);
            if (LOG.isDebugEnabled()) {
                LOG.debug(result.toString());
            }
        } catch (IOException | ParseException ex) {
            LOG.error("ZFS zpool status parser error: ", ex);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        //String message = "ZFS:-1(FAILURE)";
        String message = "ZFS:-1";
        DisplayLevel displayLevel = DisplayLevel.CRITICAL;
        boolean scrubOrRaidActivityPresent = false;

        if (result != null) {
            switch (result.getAlertLevel()) {
                case OK:
                    displayLevel = DisplayLevel.OK;
                    break;
                case DEGRADING:
                    displayLevel = DisplayLevel.WARNING;
                    break;
                default:
                    displayLevel = DisplayLevel.CRITICAL;
                    break;
            }
            //message = String.format("ZFS:%d(%s)", result.getNumPools(), result.getAlertLevel().toString());
            message = String.format("ZFS:%d", result.getNumPools());
            scrubOrRaidActivityPresent = result.isScrubOrScanActivityPresent();
        }

        LOG.debug(message);
        LOG.debug("" + displayLevel);
        DeliverMessageUpdate.deliver(message, displayLevel, labelToUpdate);

        // WoL
        if (wol != null && scrubOrRaidActivityPresent) {
            try {
                LOG.debug("sending wake-on-lan packet");
                wol.send();
            } catch (IOException ex) {
                LOG.warn("failed to send wake-on-lan packet", ex);
            }
        }
    }
}
