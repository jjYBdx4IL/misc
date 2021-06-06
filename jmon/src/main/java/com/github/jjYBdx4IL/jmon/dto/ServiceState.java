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
package com.github.jjYBdx4IL.jmon.dto;

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;
import static com.google.common.base.Preconditions.checkArgument;

public class ServiceState {
    
    public static final String STATUS_STR_OK = "OK";
    public static final int STATUS_CODE_OK = 0;
    public static final String STATUS_STR_WARN = "WARN";
    public static final int STATUS_CODE_WARN = 1;
    public static final String STATUS_STR_ERROR = "ERROR";
    public static final int STATUS_CODE_ERROR = 2;
    
    public int status = 0;
    public String msg = "";
    public long millisSinceEpoch = 0;
    public int tries = 0; // ignored for passive checks
    
    public transient ServiceDef def;
    public transient HostState hostState;
    
    public ServiceState(ServiceDef def, HostState hostState) {
        this.def = def;
        this.hostState = hostState;
    }

    public long nextExec() {
        if (def.passive) {
            if (status < 2) {
                return millisSinceEpoch + def.timeout.getSeconds() * 1000L + 100L;
            } else {
                return System.currentTimeMillis() + 7 * 24 * 3600 * 1000L;
            }
        }
        
        if (status == 0 || tries >= def.tries) {
            return millisSinceEpoch + def.checkIval.getSeconds() * 1000L;
        }
        
        return millisSinceEpoch + def.retryIval.getSeconds() * 1000L;
    }
    
    public String asReport() {
        return f("%s - %s - %s - %s%n", def.hostDef.hostname, def.name, getStatusString(), msg);
    }

    private String getStatusString() {
        if (status == STATUS_CODE_OK) {
            return STATUS_STR_OK;
        }
        else if (status == STATUS_CODE_WARN) {
            return STATUS_STR_WARN;
        }
        else {
            return STATUS_STR_ERROR;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServiceState [status=").append(status).append(", msg=").append(msg)
            .append(", millisSinceEpoch=").append(millisSinceEpoch).append(", tries=").append(tries).append(", def=")
            .append(def).append("]");
        return builder.toString();
    }

    public boolean isTimeout() {
        checkArgument(def.passive);
        return millisSinceEpoch + def.timeout.getSeconds() * 1000L < System.currentTimeMillis();
    }
}
