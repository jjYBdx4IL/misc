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

import static org.junit.Assert.assertEquals;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Detects changes in system properties and restores them after each test suite.
 *
 * @author jjYBdx4IL
 */
//CHECKSTYLE:OFF
public class SysPropsRestorerRule implements TestRule {

    private static final Logger log = LoggerFactory.getLogger(SysPropsRestorerRule.class);
    
    private final int expectedChanges;

    public SysPropsRestorerRule() {
        expectedChanges = -1;
    }

    // don't touch this, used for testing this rule
    SysPropsRestorerRule(int expectedChanges) {
        this.expectedChanges = expectedChanges;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    System.getProperties().store(baos, "");
                    try {
                        base.evaluate();
                    } catch (Throwable t) {
                        throw t;
                    } finally {
                        try (InputStream is = new ByteArrayInputStream(baos.toByteArray())) {
                            Properties newProps = System.getProperties();
                            Properties oldProps = new Properties();
                            oldProps.load(is);
                            int nChangesDetected = 0;
                            for (Object key : oldProps.keySet()) {
                                if (newProps.getProperty((String)key) == null) {
                                    log.info(String.format("system property %s was removed", key));
                                    nChangesDetected++;
                                }
                                else if (!newProps.getProperty((String)key).equals(oldProps.getProperty((String)key))) {
                                    log.info(String.format("system property %s was changed: %s => %s", key, oldProps.getProperty((String)key), newProps.getProperty((String)key)));
                                    nChangesDetected++;
                                }
                            }
                            for (Object key : newProps.keySet()) {
                                if (oldProps.getProperty((String)key) == null) {
                                    log.info(String.format("system property %s was added with value %s", key, newProps.getProperty((String)key)));
                                    nChangesDetected++;
                                }
                            }
                            System.setProperties(oldProps);
                            log.info(String.format("%d changes to system properties have been detected and reverted", nChangesDetected));
                            if (expectedChanges >= 0) {
                                assertEquals(expectedChanges, nChangesDetected);
                            }
                        }
                    }
                }
            }
        };
    }

}
