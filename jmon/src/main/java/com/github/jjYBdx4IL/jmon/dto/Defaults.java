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

import java.time.Duration;

public class Defaults {

    public Duration timeout = Duration.ofSeconds(3600);
    public Duration checkIval = Duration.ofSeconds(300);
    public Duration retryIval = Duration.ofSeconds(60);
    public int tries = 3;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Defaults [timeout=").append(timeout).append(", checkIval=").append(checkIval)
            .append(", retryIval=").append(retryIval).append(", tries=").append(tries).append("]");
        return builder.toString();
    }

}
