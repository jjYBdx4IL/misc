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

import java.util.ArrayList;
import java.util.List;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class IgnoreTestExceptionsRule implements TestRule {

    private static final Logger log = LoggerFactory.getLogger(IgnoreTestExceptionsRule.class);
    private final List<Class<?>> exceptions;

    public IgnoreTestExceptionsRule() {
        exceptions = new ArrayList<>();
    }

    public void addException(Class<?> throwableClass) {
        exceptions.add(throwableClass);
    }

    public boolean isIgnore(Throwable t) {
        for (Class<?> c : exceptions) {
            if (c.isAssignableFrom(t.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    if (isIgnore(t)) {
                        log.warn("ignoring SocketTimeoutException thrown by " + description.getDisplayName());
                    } else {
                        throw t;
                    }
                }
            }
        };
    }

}
