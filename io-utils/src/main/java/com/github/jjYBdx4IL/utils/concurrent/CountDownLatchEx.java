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
package com.github.jjYBdx4IL.utils.concurrent;

/**
 * A CountDownLatch variant that does not suppress notifies.
 */

public class CountDownLatchEx {

    protected long counter;

    /**
     * The constructor.
     */
    public CountDownLatchEx(long count) {
        if (count < 1) {
            throw new IllegalArgumentException();
        }
        counter = count;
    }

    /**
     * The constructor.
     */
    public CountDownLatchEx() {
        this(1);
    }

    /**
     * Wait for notify or countdown to reach 0. Returns when countdown reaches
     * zero or on notify. Returns immeditaly if countdown is already at 0.
     * 
     * @return true if countdown reached 0
     */
    public synchronized boolean await() throws InterruptedException {
        if (counter <= 0) {
            return true;
        }
        wait();
        return counter <= 0;
    }

    /**
     * Wait for notify or countdown to reach 0. Returns when countdown reaches
     * zero or on notify. Returns immeditaly if countdown is already at 0.
     * 
     * @return true if countdown reached 0
     */
    public synchronized boolean await(long millis) throws InterruptedException {
        if (counter <= 0) {
            return true;
        }
        wait(millis);
        return counter <= 0;
    }

    /**
     * Count down and notify once (only) upon reaching 0.
     */
    public synchronized void countDown() {
        if (--counter == 0) {
            notify();
        }
    }
}
