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
package com.github.jjYBdx4IL.utils.logic;

//CHECKSTYLE:OFF
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public abstract class Condition {

    private static final Logger LOG = LoggerFactory.getLogger(Condition.class);

    public static final long DEFAULT_TIMEOUT_SECS = 300;
    public static final long DEFAULT_DELAY_MS = 1000;
    public static final boolean DEFAULT_HANDLE_ASSERTION_ERRORS = false;

    private final boolean handleAssertionErrors;
    private final long timeoutSeconds;
    private final long delayMs;
    private long started = -1L;
    private long timeoutTime = -1L;

    public Condition() {
        this(DEFAULT_HANDLE_ASSERTION_ERRORS, DEFAULT_TIMEOUT_SECS, DEFAULT_DELAY_MS);
    }

    public Condition(boolean handleAssertionErrors) {
        this(handleAssertionErrors, DEFAULT_TIMEOUT_SECS, DEFAULT_DELAY_MS);
    }

    public Condition(long _timeoutSeconds) {
        this(DEFAULT_HANDLE_ASSERTION_ERRORS, _timeoutSeconds, DEFAULT_DELAY_MS);
    }

    public Condition(boolean handleAssertionErrors, long _timeoutSeconds) {
        this(handleAssertionErrors, _timeoutSeconds, DEFAULT_DELAY_MS);
    }

    public Condition(long _timeoutSeconds, long _delayMs) {
        this(DEFAULT_HANDLE_ASSERTION_ERRORS, _timeoutSeconds, _delayMs);
    }

    public Condition(boolean handleAssertionErrors, long _timeoutSeconds, long _delayMs) {
        this.handleAssertionErrors = handleAssertionErrors;
        timeoutSeconds = _timeoutSeconds;
        delayMs = _delayMs;
    }

    public abstract boolean test();

    public boolean waitUntil() throws InterruptedException {
        boolean test = false;
        started = System.currentTimeMillis();
        timeoutTime = getStarted() + getTimeoutSeconds() * 1000L;
        do {
            if (handleAssertionErrors) {
                try {
                    test = test();
                } catch (AssertionError ex) {
                    LOG.debug("", ex);
                    test = false;
                }
            } else {
                test = test();
            }
            if (!test && getTimeoutTime() > System.currentTimeMillis()) {
                Thread.sleep(getDelayMs());
            }
        } while (!test && getTimeoutTime() > System.currentTimeMillis());
        return test;
    }

    /**
     * @return the timeoutSeconds
     */
    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * @return the delayMs
     */
    public long getDelayMs() {
        return delayMs;
    }

    /**
     * @return the started
     */
    public long getStarted() {
        return started;
    }

    /**
     * @return the timeoutTime
     */
    public long getTimeoutTime() {
        return timeoutTime;
    }
}
