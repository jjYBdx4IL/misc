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

import com.github.jjYBdx4IL.parser.linux.ProcNetDevData;
import com.github.jjYBdx4IL.parser.linux.ProcNetDevParser;
import com.github.jjYBdx4IL.utils.net.WakeOnLAN;
import com.github.jjYBdx4IL.utils.text.FileSizeFormatter;

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
class NetDevPoller extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(NetDevPoller.class);
    private final static long WOL_IVAL = 60L * 1000L;
    private final String device;
    private final JLabel labelToUpdate;
    private long lastTransferredBytes = -1L;
    private long lastTransferredBytesTimeMillis = 0L;
    private final long wolRateLimit;
    private final WakeOnLAN wol;
    private long lastWoLSendMillis = 0L;
    private boolean wolPending = false;

    NetDevPoller(String deviceToDisplay, long wolRateLimit, WakeOnLAN wol, JLabel label) {
        this.device = deviceToDisplay;
        this.labelToUpdate = label;
        this.wolRateLimit = wolRateLimit;
        this.wol = wol;
    }

    @Override
    public void run() {
        poll();
    }

    private void poll() {
        DisplayLevel level = DisplayLevel.OK;
        ProcNetDevData dd = ProcNetDevParser.get(device);
        if (dd == null) {
            LOG.error(String.format("Network device %s not found.", device));
            return;
        }
        long timeMillis = System.currentTimeMillis();
        long bytes = dd.getReceiveBytes() + dd.getTransmitBytes();
        if (lastTransferredBytes > -1L) {
            long transferredBytes = bytes - lastTransferredBytes;
            long transferredBytesPerSecond = 1000L * transferredBytes
                    / (timeMillis - lastTransferredBytesTimeMillis);
            String message = String.format(
                    "%1$s: %2$s/s", device, FileSizeFormatter.byteCountToDisplaySize(transferredBytesPerSecond));
            LOG.trace(message);
            LOG.trace("" + level);
            DeliverMessageUpdate.deliver(message, level, labelToUpdate);
            if (transferredBytesPerSecond >= wolRateLimit) {
                wolPending = true;
            }
        }
        lastTransferredBytes = dd.getReceiveBytes() + dd.getTransmitBytes();
        lastTransferredBytesTimeMillis = timeMillis;

        // WoL
        if (wol == null || !wolPending || timeMillis - lastWoLSendMillis < WOL_IVAL) {
            return;
        }
        try {
            LOG.debug("sending wol packet");
            wol.send();
            wolPending = false;
        } catch (IOException ex) {
            LOG.warn("failed to send wol packet", ex);
        }
        lastWoLSendMillis = timeMillis;
    }
}
