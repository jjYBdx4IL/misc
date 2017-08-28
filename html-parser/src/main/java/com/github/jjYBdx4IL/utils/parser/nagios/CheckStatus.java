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
package com.github.jjYBdx4IL.utils.parser.nagios;

//CHECKSTYLE:OFF
import java.util.Date;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CheckStatus {

    private final String host;
    private final String service;
    private final Status status;
    private final Date lastCheck;
    private final long durationMillis;
    private final int attempt;
    private final int maxAttempts;
    private final String statusInfo;
    private final boolean ack;
    private final boolean downtime;

    // CHECKSTYLE IGNORE .* FOR NEXT 2 LINES
    public CheckStatus(String host, String service, Status status, Date lastCheck, long durationMillis,
            int attempt, int maxAttempts, String statusInfo, boolean ack, boolean downtime) {
        this.host = host;
        this.service = service;
        this.status = status;
        if (lastCheck != null) {
            this.lastCheck = new Date(lastCheck.getTime());
        } else {
            this.lastCheck = null;
        }
        this.durationMillis = durationMillis;
        this.attempt = attempt;
        this.maxAttempts = maxAttempts;
        this.statusInfo = statusInfo;
        this.ack = ack;
        this.downtime = downtime;
    }

    public Status getEffectiveStatus(boolean okIfAck, boolean okIfDowntime) {
        Status effectiveStatus = status;
        if (okIfAck && isAck()) {
            effectiveStatus = Status.OK;
        }
        if (okIfDowntime && isDowntime()) {
            effectiveStatus = Status.OK;
        }
        return effectiveStatus;
    }

    public boolean isHard() {
        return attempt == maxAttempts;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return the lastCheck
     */
    public Date getLastCheck() {
        if (this.lastCheck != null) {
            return new Date(lastCheck.getTime());
        } else {
            return null;
        }
    }

    /**
     * @return the durationMillis
     */
    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * @return the attempt
     */
    public int getAttempt() {
        return attempt;
    }

    /**
     * @return the maxAttempts
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }

    /**
     * @return the statusInfo
     */
    public String getStatusInfo() {
        return statusInfo;
    }

    /**
     * @return the ack
     */
    public boolean isAck() {
        return ack;
    }

    /**
     * @return the downtime
     */
    public boolean isDowntime() {
        return downtime;
    }
}
