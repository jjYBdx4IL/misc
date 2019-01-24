/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.logging;

//CHECKSTYLE:OFF
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides some utility functions to work with Java's own logging implementation.
 *
 * @author Github jjYBdx4IL Projects
 */
public class JavaUtilLoggingUtils {

    public static final String JAVA_NET_URL_LOG_PKG = "sun.net.www.protocol.http";

    /**
     *
     * @param loggerPackage "" for root logger
     * @param level the logging level to set
     */
    public static void setConsoleHandlerLevel(String loggerPackage, Level level) {
        Logger topLogger = Logger.getLogger(loggerPackage);

        Handler consoleHandler = null;
        for (Handler handler : topLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                consoleHandler = handler;
                break;
            }
        }

        if (consoleHandler == null) {
            consoleHandler = new ConsoleHandler();
            topLogger.addHandler(consoleHandler);
        }
        consoleHandler.setLevel(level);
    }

    /**
     * Installs a separate {@link java.util.logging.ConsoleHandler} for the logger package
     * "sun.net.www.protocol.http" and disconnects logging from parent handlers to avoid duplicated output.
     * 
     * @param level the logging level to set
     */
    public static void setJavaNetURLConsoleLoggingLevel(Level level) {
        Logger logger = Logger.getLogger(JAVA_NET_URL_LOG_PKG);
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
        setConsoleHandlerLevel(JAVA_NET_URL_LOG_PKG, level);
    }

    private JavaUtilLoggingUtils() {
    }

}
