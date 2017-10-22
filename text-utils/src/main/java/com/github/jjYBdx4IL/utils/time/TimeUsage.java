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
package com.github.jjYBdx4IL.utils.time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//CHECKSTYLE:OFF
public class TimeUsage implements AutoCloseable {

    private static final String DEFAULT_NAME = "default";

    private final String name;
    private final long started;
    private long stopped = -1;
    private final List<TimeUsage> childs = new ArrayList<>();
    private long lastDumpShownAt = System.currentTimeMillis();
    private final long dumpIvalMs = 60 * 1000L;

    public TimeUsage() {
        this(null);
    }

    public TimeUsage(String name) {
        this.name = name == null || name.isEmpty() ? DEFAULT_NAME : name;
        this.started = System.currentTimeMillis();
    }

    public TimeUsage startSub(String name) {
        TimeUsage sub = new TimeUsage(name);
        childs.add(sub);
        return sub;
    }

    public void stop() {
        if (!isOpen()) {
            return;
        }
        for (TimeUsage child : childs) {
            child.stop();
        }
        stopped = System.currentTimeMillis();
    }
    
    public boolean isOpen() {
        return stopped == -1;
    }

    public long duration() {
        if (isOpen()) {
            return System.currentTimeMillis() - started;
        }
        return stopped - started;
    }

    @Override
    public void close() {
        stop();
    }
    
    public void ivalDump() {
        long now = System.currentTimeMillis();
        if (lastDumpShownAt + dumpIvalMs > now) {
            return;
        }
        lastDumpShownAt = now;
        System.out.println(toString());
    }

    @Override
    public String toString() {
        Map<String, Long> cumulativeByName = new HashMap<>();
        Set<String> unclosed = new HashSet<>();
        for (TimeUsage child : childs) {
            cumulativeByName.put(child.name, new Long(0));
        }
        for (TimeUsage child : childs) {
            if (child.isOpen()) {
                unclosed.add(child.name);
            } else {
                cumulativeByName.put(child.name, cumulativeByName.get(child.name).longValue() + child.duration());
            }
        }

        StringBuilder sb = new StringBuilder();
        if (isOpen()) {
            sb.append(String.format("%s (unclosed) [", name, duration()));
        } else {
            sb.append(String.format("%s %d ms (100%%) [", name, duration()));
        }
        boolean first = true;
        for (TimeUsage child : childs) {
            Long timeSpent = cumulativeByName.get(child.name);
            if (timeSpent == null) {
                continue;
            }
            cumulativeByName.remove(child.name);
            
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            if (unclosed.contains(child.name)) {
                sb.append(String.format("%s (unclosed)", child.name));
                continue;
            }

            sb.append(String.format("%s %d ms (%.2f%%)", child.name, timeSpent.longValue(),
               timeSpent.longValue() == 0 ? 0f : timeSpent.longValue() * 100.f / duration()));
        }
        sb.append("]");
        return sb.toString();
    }

}
