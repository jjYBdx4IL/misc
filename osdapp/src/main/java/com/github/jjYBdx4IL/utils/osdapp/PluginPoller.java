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

import com.github.jjYBdx4IL.utils.net.WakeOnLAN;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.TimerTask;

import javax.swing.JLabel;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
class PluginPoller extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(PluginPoller.class);
    private final String executable;
    private final JLabel labelToUpdate;
    private final WakeOnLAN wol;

    PluginPoller(String executable, WakeOnLAN wol, JLabel label)
            throws MalformedURLException {
        this.executable = executable;
        this.wol = wol;
        this.labelToUpdate = label;
    }

    @Override
    public void run() {
        boolean sendWoL = false;
        DisplayLevel level = DisplayLevel.OK;
        String message = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ProcessBuilder pb = new ProcessBuilder(executable);
            Process p = pb.start();
            IOUtils.copy(p.getInputStream(), baos);
            int exitCode = p.waitFor();
            sendWoL = exitCode % 2 == 1;
            if (exitCode/2 == 1) {
                level = DisplayLevel.WARNING;
            }
            else if (exitCode/2 > 1) {
                level = DisplayLevel.CRITICAL;
            }
            baos.close();
            message = baos.toString();
        } catch (IOException | RuntimeException | InterruptedException ex) {
            LOG.warn("plugin execution failed", ex);
            level = DisplayLevel.CRITICAL;
            message = "plugin failed";
        }

        LOG.debug(message);
        LOG.debug("" + level);
        DeliverMessageUpdate.deliver(message, level, labelToUpdate);

        // WoL
        if (sendWoL && wol != null) {
            try {
                LOG.debug("sending wake-on-lan packet");
                wol.send();
            } catch (IOException ex) {
                LOG.warn("failed to send wake-on-lan packet", ex);
            }
        }
    }
}
