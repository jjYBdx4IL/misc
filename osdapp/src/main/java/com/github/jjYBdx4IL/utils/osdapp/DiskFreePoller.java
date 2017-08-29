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

import com.github.jjYBdx4IL.utils.text.FileSizeFormatter;

/*
 * #%L
 * Java OSD
 * %%
 * Copyright (C) 2014 jjYBdx4IL
 * %%
 * #L%
 */

import org.apache.commons.io.FileSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.TimerTask;

import javax.swing.JLabel;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
class DiskFreePoller extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(DiskFreePoller.class);
    private final static long TIMEOUT = 200L;
    private final static BigInteger BIG_INT_1024 = BigInteger.valueOf(1024L);
    private String path;
    private JLabel labelToUpdate;
    private final BigInteger warningFree;
    private final BigInteger criticalFree;

    /**
     *
     * @param diskFreePath
     * @param warningFreeMB -1 to ignore
     * @param criticalFreeMB -1 to ignore
     * @param label
     */
    DiskFreePoller(String diskFreePath, JLabel label) {
        this.path = diskFreePath;
        this.labelToUpdate = label;

        long warningFreeMB = -1L;
        long criticalFreeMB = -1L;

        String[] parts = path.split(":");
        // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
        if (parts.length == 3) {
            this.path = parts[0];
            try {
                warningFreeMB = Long.parseLong(parts[1]);
                criticalFreeMB = Long.parseLong(parts[2]);
            } catch (NumberFormatException ex) {
                LOG.error("invalid format: " + diskFreePath, ex);
                warningFreeMB = -1L;
                criticalFreeMB = -1L;
            }
        }

        if (warningFreeMB < criticalFreeMB) {
            long tmp = warningFreeMB;
            warningFreeMB = criticalFreeMB;
            criticalFreeMB = tmp;
        }
        if (warningFreeMB > -1L) {
            this.warningFree = BigInteger.valueOf(warningFreeMB)
                    .multiply(BIG_INT_1024).multiply(BIG_INT_1024);
        } else {
            this.warningFree = null;
        }
        if (criticalFreeMB > -1L) {
            this.criticalFree = BigInteger.valueOf(criticalFreeMB)
                    .multiply(BIG_INT_1024).multiply(BIG_INT_1024);
        } else {
            this.criticalFree = null;
        }
    }

    @Override
    public void run() {
        try {
            poll();
        } catch (IOException ex) {
            LOG.warn("failed to retrieve diskfree status data for " + path, ex);
        }
    }

    private void poll() throws IOException {
        DisplayLevel level = DisplayLevel.OK;
        long freeKb = FileSystemUtils.freeSpaceKb(path, TIMEOUT);
        BigInteger bi = BigInteger.valueOf(freeKb);
        bi = bi.multiply(BIG_INT_1024);
        if (criticalFree != null && bi.compareTo(criticalFree) <= 0) {
            level = DisplayLevel.CRITICAL;
        } else if (warningFree != null && bi.compareTo(warningFree) <= 0) {
            level = DisplayLevel.WARNING;
        }
        String message = String.format("%1$s: %2$s", path, FileSizeFormatter.byteCountToDisplaySize(bi));
        DeliverMessageUpdate.deliver(message, level, labelToUpdate);
    }
}
