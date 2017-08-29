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

import com.github.jjYBdx4IL.utils.parser.nagios.CheckStatus;
import com.github.jjYBdx4IL.utils.parser.nagios.NagiosParser;
import com.github.jjYBdx4IL.utils.parser.nagios.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JLabel;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
class NagiosPoller extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(NagiosPoller.class);
    private final URL url;
    private final NagiosParser p = new NagiosParser();
    private final JLabel labelToUpdate;
    private final String loginName;
    private final String loginPassword;

    NagiosPoller(URL nagiosStatusCgiUrl, String loginName, String loginPassword, JLabel label)
            throws MalformedURLException {
        this.url = new URL(nagiosStatusCgiUrl.toString());
        this.loginName = loginName;
        this.loginPassword = loginPassword;
        this.labelToUpdate = label;
    }

    @Override
    public void run() {
        try {
            poll();
        } catch (IOException | ParseException ex) {
            LOG.warn("failed to retrieve nagios status data from " + url.toString(), ex);
        }
    }

    private void poll() throws IOException, ParseException {
        int nOk = 0;
        int nWarning = 0;
        int nCritical = 0;
        @SuppressWarnings("unused")
		int nUnknown = 0;
        List<CheckStatus> results;
        try {
            if (loginName != null && loginPassword != null) {
                LOG.debug("using credentials to access nagios status page, login name = " + loginName);
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(loginName, loginPassword.toCharArray());
                    }
                });
            }

            results = p.parse(url);

            if (loginName != null && loginPassword != null) {
                Authenticator.setDefault(null);
            }

            for (CheckStatus cs : results) {
                switch (cs.getEffectiveStatus(true, true)) {
                    case OK:
                        nOk++;
                        break;
                    case WARNING:
                        nWarning++;
                        break;
                    case CRITICAL:
                        nCritical++;
                        break;
                    case UNKNOWN:
                        nUnknown++;
                        break;
                    default:
                }
            }
        } catch (IOException | ParseException | RuntimeException ex) {
            LOG.warn("problem encountered while parsing nagios status", ex);
            // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
            nCritical = 999;
        }
        DisplayLevel level = DisplayLevel.OK;
        if (nCritical > 0) {
            level = DisplayLevel.CRITICAL;
        } else if (nWarning > 0) {
            level = DisplayLevel.WARNING;
        }

        String message = String.format("%1$do%2$dw%3$dc", nOk, nWarning, nCritical);
        LOG.debug(message);
        LOG.debug("" + level);
        DeliverMessageUpdate.deliver(message, level, labelToUpdate);
    }
}
