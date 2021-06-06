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

import com.github.jjYBdx4IL.jmon.cmdqueue.AddReportCommand;
import com.github.jjYBdx4IL.jmon.cmdqueue.IReportCommand;
import com.github.jjYBdx4IL.jmon.cmdqueue.RemoveReportCommand;
import com.github.jjYBdx4IL.jmon.cmdqueue.ShutdownCommand;
import com.github.jjYBdx4IL.jmon.dto.ReportingStatus;
import com.github.jjYBdx4IL.jmon.dto.ServiceState;
import com.github.jjYBdx4IL.utils.io.IoUtils;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ReporterThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(ReporterThread.class);

    public static final long MAX_NUMBER_OF_REPORTS_IN_SUMMARY = 50;
    public static final long DEFAULT_WAIT_MILLIS = 60 * 1000L;
    
    private long waitMillis = DEFAULT_WAIT_MILLIS;
    private long notificationSendIvalMillis = 3600 * 1000L;
    private long retryDelayMillis = 300 * 1000L;

    private ReportingStatus status = null;
    private final Path lastReportStatusPath;

    private final Map<String, String> problemReports = new HashMap<>();
    private final BlockingQueue<IReportCommand> commandQueue = new LinkedBlockingQueue<>();

    public ReporterThread(Model m) {
        super("ReporterThread");
        lastReportStatusPath = Config.cfgDir.resolve("reporting_state");
        if (Files.exists(lastReportStatusPath)) {
            try {
                status = Config.gson.fromJson(Files.readString(lastReportStatusPath),
                    ReportingStatus.class);
            } catch (JsonSyntaxException | IOException ex) {
                LOG.error("error reading last NotificationStatus: ", ex);
                // we'll eventually recover from this by writing a new one
            }
        }
        if (status == null) {
            status = new ReportingStatus();
        }
    }

    @Override
    public void run() {
        LOG.debug("reporter thread started");
        try {
            boolean done = false;
            while (!done) {
                IReportCommand command = commandQueue.poll(Math.max(waitMillis, 1L), TimeUnit.MILLISECONDS);
                if (command != null) {
                    if (command instanceof ShutdownCommand) {
                        done = true;
                    }
                    else if (command instanceof AddReportCommand) {
                        AddReportCommand c = (AddReportCommand) command;
                        problemReports.put(c.reportId, c.reportContent);
                    }
                    else if (command instanceof RemoveReportCommand) {
                        RemoveReportCommand c = (RemoveReportCommand) command;
                        problemReports.remove(c.reportId);
                    }
                }
                if (!done) {
                    doSomeReporting();
                }
            }
        } catch (

        InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            LOG.debug("reporter thread quitting");
        }
    }

    private String buildProblemReport() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(f("%d active problems.\n", problemReports.size()));
        
        if (problemReports.size() > MAX_NUMBER_OF_REPORTS_IN_SUMMARY) {
            sb.append("Too many problems. Skipping listing.\n");
        } else {
            problemReports.forEach((state, report) -> {
                sb.append(report);
                sb.append("\n");
            });
        }
        
        return sb.toString();
    }
    
    private void doSomeReporting() {
        LOG.debug("doSomeReporting()");
        
        final long now = System.currentTimeMillis();
        
        waitMillis = DEFAULT_WAIT_MILLIS; // restore default
        
        // force delay for retries
        if (status.sendFailure) {
            final long nextRetry = status.lastSent + retryDelayMillis;
            if (nextRetry > now) {
                waitMillis = nextRetry - now;
                return;
            }
        }
        
        final boolean escalation = !problemReports.isEmpty() && status.lastStatusReported == 0;
        final boolean deescalation = problemReports.isEmpty() && status.lastStatusReported != 0;

        // report escalations or de-escalations immediately, unless there was a send failure
        if (escalation) {
            sendReport(buildProblemReport());
            return;
        }
        else if (deescalation) {
            sendReport(null);
            return;
        }
        else if (problemReports.isEmpty()) {
            return;
        }
        else { // re-report issues
            long nextPossibleTime = status.lastSent
                + (status.sendFailure ? retryDelayMillis : notificationSendIvalMillis);
            if (nextPossibleTime > now) { // last issue report/send try too recent?
                waitMillis = nextPossibleTime - now + 100L;
                return;
            }

            sendReport(buildProblemReport());
            return;
        }        
    }

    private void sendReport(String report) {
        try {
            if (report == null) {
                Config.reporter.send("JMon Recovery Report", "All clear!");
                status.lastContentSent = null;
                status.lastStatusReported = 0;
            } else {
                Config.reporter.send("JMon Error Report", report);
                status.lastStatusReported = 2;
            }
            status.lastContentSent = report;
            status.sendFailure = false;
        } catch (Exception ex) {
            LOG.error("failed to send notification", ex);
            status.sendFailure = true;
        }

        status.lastSent = System.currentTimeMillis();

        try {
            IoUtils.safeWriteTo(lastReportStatusPath, Config.gson.toJson(status));
        } catch (IOException ex) {
            LOG.error("", ex);
        }
    }

    public void shutdown() throws InterruptedException {
        addCommand(new ShutdownCommand());
        join();
    }

    protected void addCommand(IReportCommand command) {
        commandQueue.add(command);
    }

    public void addReport(ServiceState s) {
        commandQueue.add(new AddReportCommand(s.def.id(), s.asReport()));
    }
    
    public void removeReport(ServiceState s) {
        commandQueue.add(new RemoveReportCommand(s.def.id()));
    }
}
