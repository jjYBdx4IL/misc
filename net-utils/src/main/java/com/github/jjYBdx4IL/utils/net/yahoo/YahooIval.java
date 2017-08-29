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
package com.github.jjYBdx4IL.utils.net.yahoo;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public enum YahooIval {
    DAY("d"), WEEK("w"), MONTH("m");
    private final String queryPartValue;

    // CHECKSTYLE IGNORE HiddenField FOR NEXT 1 LINE
    YahooIval(String queryPartValue) {
        this.queryPartValue = queryPartValue;
    }

    @Override
    public String toString() {
        return queryPartValue;
    }
}
