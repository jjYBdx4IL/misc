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
package com.github.jjYBdx4IL.utils.text;

import com.github.jjYBdx4IL.utils.time.TimeUtils;

public class Eta<T extends Number> {

    public static final long DEFAULT_PERIODMS = 10000L;
    
    private final T endValue;
    private final T startValue;
    private final long startMs;
    private final long periodMs;

    /**
     * Create an ETA instance.
     * 
     * @param startValue
     *            where progress starts
     * @param endValue
     *            where progress will end
     * @param periodMs
     *            defines how often {@link #toStringPeriodical(Number)} will
     *            return a non-null value
     */
    public Eta(T startValue, T endValue, long periodMs) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.periodMs = periodMs;
        this.startMs = System.currentTimeMillis();
    }

    public Eta(T startValue, T endValue) {
        this(startValue, endValue, DEFAULT_PERIODMS);
    }

    public Eta(T endValue) {
        this(null, endValue, DEFAULT_PERIODMS);
    }

    /**
     * Convert current progress indicated by currentValue to a string describing
     * progress and ETA.
     * 
     * @param currentValue
     *            the current progress
     * @return a string with progress details incl. ETA
     */
    public String toString(T currentValue) {
        final long elapsedMs = System.currentTimeMillis() - startMs;
        final float processed = (currentValue.floatValue() - (startValue != null ? startValue.floatValue() : 0f));
        final float speedMs = processed / (float) elapsedMs;
        return String.format("%s in %s (%.2f/s, ETA: %s)",
            processed,
            TimeUtils.millisToDuration(elapsedMs),
            speedMs * 1e3f,
            processed == 0f
                ? "NaN"
                : TimeUtils.millisToDuration((long) ((endValue.floatValue() - currentValue.floatValue()) / speedMs))
        );
    }

    private long lastToStringPeriodical = 0;

    /**
     * Use this method to avoid spammning eta messages. Returns non-null only
     * every periodMs.
     * 
     * @param currentValue
     *            the current progress
     * @return the result of {@link #toString(Number)}, or null if called sooned
     *         than periodMs after the last call.
     */
    public String toStringPeriodical(T currentValue) {
        long now = System.currentTimeMillis();
        if (now - lastToStringPeriodical < periodMs) {
            return null;
        }
        lastToStringPeriodical = now;
        return toString(currentValue);
    }

}
