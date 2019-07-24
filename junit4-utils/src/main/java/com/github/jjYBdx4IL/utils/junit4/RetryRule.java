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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//CHECKSTYLE:OFF
/**
 * Problems:
 * <ul>
 * <li>retries test if it fails on the @BeforeClass stage
 * <li>calculates tests run a bit differently (when you have 3 retries, you will
 * receive test Runs: 4, success 1 that might be confusing)
 * </ul>
 *
 * <p>
 * See http://stackoverflow.com/a/20762914/1050755
 * </p>
 * 
 * @author jjYBdx4IL
 */
public class RetryRule implements TestRule {

    private static final Logger log = LoggerFactory.getLogger(RetryRule.class);

    private final int retryCount;

    public RetryRule(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                Throwable caughtThrowable = null;

                for (int i = 0; i <= retryCount; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable t) {
                        caughtThrowable = t;
                        log.warn(description.getDisplayName() + ": run " + (i + 1) + " failed");
                    }
                }
                log.error(description.getDisplayName() + ": giving up after " + retryCount + " failures");
                throw caughtThrowable;
            }
        };
    }

}
