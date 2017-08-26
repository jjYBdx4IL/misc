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
package com.github.jjYBdx4IL.utils.junit4;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class RetryRunner extends BlockJUnit4ClassRunner {

    private static final Logger log = LoggerFactory.getLogger(RetryRunner.class);
    private final long delayMillis;
    private final int maxRetries;

    public RetryRunner(Class<?> klass) throws InitializationError {
        super(klass);
        RetryRunnerConfig config = klass.getAnnotation(RetryRunnerConfig.class);
        if (config == null) {
            delayMillis = RetryRunnerConfig.DEFAULT_DELAY_MILLIS;
            maxRetries = RetryRunnerConfig.DEFAULT_RETRIES;
        } else {
            delayMillis = config.delayMillis();
            maxRetries = config.retries();
        }
    }

    @Override
    public void run(final RunNotifier notifier) {
        log.info("run()");
        EachTestNotifier testNotifier = new EachTestNotifier(notifier,
                getDescription());
        try {
            Statement statement = classBlock(notifier);
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.fireTestIgnored();
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            testNotifier.addFailure(e);
        }
    }

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        log.info("runChild()");
        Description description = describeChild(method);
        if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            runTestUnit(methodBlock(method), description, notifier);
        }
    }
    /**
     * Runs a {@link Statement} that represents a leaf (aka atomic) test.
     * 
     * @param statement the statement
     * @param description the description
     * @param notifier the notifier
     */
    protected final void runTestUnit(Statement statement, Description description,
            RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        log.info("runTestUnit()");
        eachNotifier.fireTestStarted();
        try {
            int retryCounter = 0;
            for (;;) {
                try {
                    statement.evaluate();
                } catch (AssumptionViolatedException e) {
                    eachNotifier.addFailedAssumption(e);
                } catch (Throwable e) {
                    if (retryCounter < maxRetries) {
                        retryCounter++;
                        log.info(String.format("retrying after %.3f seconds (retry %d/%d)",
                                delayMillis / 1000., retryCounter, maxRetries));
                        if (delayMillis > 0) {
                            try {
                                Thread.sleep(delayMillis);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        continue;
                    }
                    eachNotifier.addFailure(e);
                }
                break;
            }
        } finally {
            eachNotifier.fireTestFinished();
        }
    }
}
