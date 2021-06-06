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
package com.github.jjYBdx4IL.jmon;

import com.github.jjYBdx4IL.jmon.checks.ICheck;
import com.github.jjYBdx4IL.jmon.dto.ServiceState;

import java.util.Comparator;

public class TimedExecution {

    public long plannedExecEpochMillis;
    public ICheck checkInstance;
    public ServiceState state;

    public TimedExecution(ICheck checkInst, ServiceState serviceState) {
        checkInstance = checkInst;
        state = serviceState;
        plannedExecEpochMillis = serviceState.nextExec();
    }

    public static class Comparatr implements Comparator<TimedExecution> {

        @Override
        public int compare(TimedExecution o1, TimedExecution o2) {
            return Long.compare(o1.plannedExecEpochMillis, o2.plannedExecEpochMillis);
        }
    }
}
