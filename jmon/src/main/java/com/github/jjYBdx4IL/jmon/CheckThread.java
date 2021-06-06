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

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;

import com.github.jjYBdx4IL.jmon.checks.CheckResult;
import com.github.jjYBdx4IL.jmon.dto.ServiceState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CheckThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(CheckThread.class);

    private final CountDownLatch stopLatch = new CountDownLatch(1);
    private long waitMillis = 100;
    private final Model model;
    private final TreeSet<TimedExecution> queue = new TreeSet<>(new TimedExecution.Comparatr());
    private ReporterThread reporterThread = null;
    
    public CheckThread(Model m) {
        super("CheckThread");
        model = m;
    }

    public void init() {
        model.populateExecutionQueue(queue);
    }

    @Override
    public void run() {
        LOG.debug("check thread started");
        if (queue.isEmpty()) {
            LOG.info("no active checks configured, check thread terminates itself.");
            return;
        }
        
        try {
            waitMillis = queue.first().plannedExecEpochMillis - System.currentTimeMillis();
            while (!stopLatch.await(Math.max(waitMillis, 1L), TimeUnit.MILLISECONDS)) {
                doSomeWork();
                waitMillis = queue.first().plannedExecEpochMillis - System.currentTimeMillis();
            }
        } catch (InterruptedException e) {
            LOG.debug("check thread aborting");
            throw new RuntimeException(e);
        }
        LOG.debug("check thread terminating regularly");
    }

    private void doSomeWork() {
        TimedExecution te = queue.pollFirst();
        ServiceState s = te.state;
        synchronized (s) {
            CheckResult r = null;
            if (!s.def.passive) {
                try {
                    r = te.checkInstance.execute();
                } catch (Exception ex) {
                    r = new CheckResult(ex.getMessage(), ServiceState.STATUS_CODE_ERROR);
                }
            } else {
                if (s.isTimeout()) {
                    r = new CheckResult("Timeout", 2);
                }
            }
            
            if (r != null) {
                synchronized (te.state.hostState) {
                    final boolean wasProblem = s.status > 1 && (s.def.passive || s.tries >= s.def.tries);
                    final boolean isProblem = r.status > 1 && (s.def.passive || s.tries >= s.def.tries - 1);
                    
                    s.millisSinceEpoch = System.currentTimeMillis();
                    s.status = r.status;
                    s.msg = r.msg;
                    if (s.status > 0) {
                        s.tries++;
                    } else {
                        s.tries = 0;
                    }
                    te.plannedExecEpochMillis = s.nextExec();
                    
                    if (isProblem && !wasProblem) {
                        LOG.debug("registered as new problem");
                        reporterThread.addReport(s);
                    } else if (!isProblem && wasProblem) {
                        LOG.debug("problem solved");
                        reporterThread.removeReport(s);
                    }
                    
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(f("%s@%s. status %d, tries %d, msg: %s", te.state.def.name,
                            te.state.def.hostDef.hostname, te.state.status, te.state.tries, te.state.msg));
                    }
                    
                    te.state.hostState.save(false);
                }
            }
        }
        queue.add(te);
        LOG.debug("timed executions scheduled: {}", queue.size());
    }

    public void shutdown() throws InterruptedException {
        stopLatch.countDown();
        join();
    }

    public void setReporterThread(ReporterThread reporterThread) {
        this.reporterThread = reporterThread;
    }
}
