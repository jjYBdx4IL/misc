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

import java.time.Duration;

public class ServiceDef {

    public String name;
    public String check; // the check to use, ie. "CertExpiryCheck", case-insensitive
    public String conf; // check conf (may be null)
    public boolean passive = false;
    
    // for passive checks:
    public Duration timeout;
    
    // for active checks:
    public Duration checkIval;
    public Duration retryIval;
    public Integer tries;
    
    public HostDef hostDef;
    
    public void validate() {
        checkArgument(name != null && !name.isBlank() && name.trim().equals(name));
        if (passive) {
            checkArgument(timeout != null && !timeout.isNegative());
        } else {
            checkArgument(check != null);
            checkArgument(checkIval != null && !checkIval.isNegative());
            checkArgument(retryIval != null && !retryIval.isNegative());
            checkArgument(tries > 0);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServiceDef [name=").append(name).append(", check=").append(check).append(", conf=").append(conf)
            .append(", passive=").append(passive).append(", timeout=").append(timeout).append(", checkIval=")
            .append(checkIval).append(", retryIval=").append(retryIval).append(", tries=").append(tries)
            .append("]");
        return builder.toString();
    }

    public void fillInDefaults(Defaults defaults) {
        if (timeout == null) {
            timeout = defaults.timeout;
        }
        if (checkIval == null) {
            checkIval = defaults.checkIval;
        }
        if (retryIval == null) {
            retryIval = defaults.retryIval;
        }
        if (tries == null) {
            tries = defaults.tries;
        }
    }
    
    public String id() {
        return f("%s@%s", name, hostDef.hostname); 
    }
}
