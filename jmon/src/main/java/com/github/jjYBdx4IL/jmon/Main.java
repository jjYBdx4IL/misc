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

import java.util.concurrent.CountDownLatch;

public class Main {

    // for testing:
    IExecModule m = null;
    CountDownLatch startup = new CountDownLatch(1);
    CountDownLatch shutdown = new CountDownLatch(1);
    
    public Main() {
    }
    
    public void run(String[] args) throws Exception {
        try {
            if (!Config.parseCmdLine(args)) {
                startup.countDown();
                return;
            }
            
            if (Config.ACTION.RUN_SERVER.equals(Config.selectedAction)) {
                m = new RunServer();
                try {
                    Config.instanceLock = new InstanceLock();
                    try {
                        m.exec();
                    } finally {
                        startup.countDown();
                        m.shutdown();
                    }
                } finally {
                    if (Config.instanceLock != null) {
                        Config.instanceLock.close();
                    }
                }
            }
            else if (Config.ACTION.STORE_IN_SPOOL.equals(Config.selectedAction)) {
                m = new StoreInSpool();
                try {
                    m.exec();
                } finally {
                    startup.countDown();
                }
            } else {
                startup.countDown();
            }
        } finally {
            startup.countDown();
            shutdown.countDown();
        }
    }
    
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }
}
