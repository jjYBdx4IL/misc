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
package com.github.jjYBdx4IL.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;

/**
 *
 * @author jjYBdx4IL
 */
public class Log4jCaptureTestBase {

    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Log4jCaptureTestBase.class.getName());
    protected static final Logger testLogger = Logger.getLogger(Log4jCaptureTestBase.class.getName() + ".testlogger");
    protected final static List<LoggingEvent> events = new ArrayList<>();

    static {
        testLogger.setLevel(Level.ALL);
        testLogger.removeAllAppenders();
        testLogger.addAppender(new AppenderSkeleton() {

            @Override
            protected void append(LoggingEvent event) {
                events.add(event);
            }

            @Override
            public void close() {
            }

            @Override
            public boolean requiresLayout() {
                return false;
            }
        });

    }

    @Before
    public void beforeTest() {
        events.clear();
    }

}
