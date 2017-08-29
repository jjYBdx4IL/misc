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
package com.github.jjYBdx4IL.utils.proc;

//CHECKSTYLE:OFF
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ProcessUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessUtil.class);

    public static int reliableDestroy(final Process p) {
        final AtomicBoolean isTerminated = new AtomicBoolean(false);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        p.waitFor();
                        break;
                    } catch (InterruptedException ex) {
                    }
                }
                synchronized (isTerminated) {
                    isTerminated.set(true);
                    isTerminated.notifyAll();
                }
            }
        });

        t.start();

        synchronized (isTerminated) {
            while (!isTerminated.get()) {
                p.destroy();
                try {
                    isTerminated.wait(1000l);
                } catch (InterruptedException ex) {
                    LOG.warn("", ex);
                }
            }
        }

        try {
            t.join();
        } catch (InterruptedException ex) {
            LOG.warn("", ex);
        }

        return p.exitValue();
    }
}
