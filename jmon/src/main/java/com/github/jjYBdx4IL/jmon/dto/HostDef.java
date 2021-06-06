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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

// host config
public class HostDef {
    
    public String hostname;
    public Map<String, ServiceDef> services = new HashMap<>();
    public String[] includes;
    public Defaults defaults = new Defaults();

    public void validate() {
        checkArgument(hostname != null && !hostname.isBlank() && hostname.trim().equals(hostname));
        services.forEach((k,v) -> v.validate());
    }
    
    public void addServices(HostDef ihd) throws MergeException {
        for (Entry<String, ServiceDef> e : ihd.services.entrySet()) {
            if (services.containsKey(e.getKey())) {
                throw new MergeException("service definition " + e.getKey() + " cannot be merged: already exists");
            }
            services.put(e.getKey(), e.getValue());
        }
    }
    
    @SuppressWarnings("serial")
    public static class MergeException extends Exception {
        public MergeException(String msg) {
            super(msg);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HostDef [hostname=").append(hostname).append(", services=").append(services)
            .append(", includes=").append(Arrays.toString(includes)).append(", defaults=").append(defaults).append("]");
        return builder.toString();
    }

    public void fillInDefaults() {
        services.forEach((k,v) -> v.fillInDefaults(defaults));
    }

    public void fillInServiceNames() {
        services.forEach((k,v) -> {v.name = k;});
    }

    public void fillInServiceHostDefs(HostDef hostdef) {
        services.forEach((k,v) -> {v.hostDef = hostdef;});
    }
}
