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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JLabel;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
class JenkinsPoller extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsPoller.class);
    public final static String API_XML_URL_EXT = "/api/xml";
    private final URL apiUrl;
    private final JLabel labelToUpdate;
    private final WakeOnLAN wol;

    JenkinsPoller(URL jenkinsStatusCgiUrl, WakeOnLAN wol, JLabel label)
            throws MalformedURLException, UnknownHostException, SocketException {
        this.apiUrl = new URL(jenkinsStatusCgiUrl.toString() + API_XML_URL_EXT);
        this.labelToUpdate = label;
        this.wol = wol;
    }

    @Override
    public void run() {
        poll();
    }

    @SuppressWarnings("unchecked")
    private void poll() {
        int nOk = 0;
        int nWarning = 0;
        int nCritical = 0;
        int nDisabled = 0;
        boolean building = false;
        DisplayLevel level = DisplayLevel.OK;
        try {
            URLConnection conn = apiUrl.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            Document dom = new SAXReader().read(conn.getInputStream());
            for (Element job : (List<Element>) dom.getRootElement().elements("job")) {
                // job.elementText("name"), job.elementText("color")
                String jobStatusColor = job.elementText("color");
                switch (jobStatusColor) {
                    case "blue":
                    case "blue_anime":
                    case "notbuilt":
                    case "notbuilt_anime":
                        nOk++;
                        break;
                    case "yellow":
                    case "yellow_anime":
                    case "aborted":
                    case "aborted_anime":
                        nWarning++;
                        break;
                    case "red":
                    case "red_anime":
                        nCritical++;
                        break;
                    case "disabled":
                    case "disabled_anime":
                        nDisabled++;
                        break;
                    default:
                        // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
                        nCritical = 999;
                        LOG.error("unknown jenkins job status color: " + jobStatusColor);
                }
                if (jobStatusColor.endsWith("_anime")) {
                    building = true;
                }
            }
        } catch (DocumentException | IOException ex) {
            LOG.warn("failed to read status from " + apiUrl, ex);
            // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
            nCritical = 999;
        }
        if (nCritical > 0) {
            level = DisplayLevel.CRITICAL;
        } else if (nWarning > 0) {
            level = DisplayLevel.WARNING;
        }

        String message = String.format("%1$do%2$dw%3$dc%4$dd", nOk, nWarning, nCritical, nDisabled);
        LOG.debug(message);
        LOG.debug("" + level);
        DeliverMessageUpdate.deliver(message, level, labelToUpdate);

        // WoL
        if (building && wol != null) {
            try {
                LOG.debug("sending wake-on-lan packet");
                wol.send();
            } catch (IOException ex) {
                LOG.warn("failed to send wake-on-lan packet", ex);
            }
        }
    }
}
