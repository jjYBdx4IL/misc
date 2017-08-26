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

import com.github.jjYBdx4IL.utils.klass.ClassReloader;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Rapid Development Runner.
 *
 * @author jjYBdx4IL
 */
//CHECKSTYLE:OFF
public class RDRunner extends BlockJUnit4ClassRunner {

    private static final Logger log = LoggerFactory.getLogger(RDRunner.class);
    private final static long NO_REPEAT_MILLIS = 1000l;

    private final long delayMillis;
    private final int maxRetries;
    private WatchService watchService = null;
    private long lastRun = 0l;
    private final ClassLoader origClassLoader;

    public RDRunner(Class<?> klass) throws InitializationError {
        super(klass);
        origClassLoader = Thread.currentThread().getContextClassLoader();
        RetryRunnerConfig config = klass.getAnnotation(RetryRunnerConfig.class);
        if (config == null) {
            delayMillis = RetryRunnerConfig.DEFAULT_DELAY_MILLIS;
            maxRetries = RetryRunnerConfig.DEFAULT_RETRIES;
        } else {
            delayMillis = config.delayMillis();
            maxRetries = config.retries();
        }
    }

    public static boolean isCoSWatch() {
        if (!Boolean.parseBoolean(System.getProperty("coswatch", "false"))) {
            return false;
        }
        String test = System.getProperty("test", "");
        if (test.indexOf('#') == -1) {
            return false;
        }
        if (test.indexOf("*") > -1) {
            return false;
        }
        return true;
    }

    public static String getMethodName() {
        String test = System.getProperty("test", "");
        if (test.indexOf('#') == -1) {
            throw new IllegalArgumentException("no method name in test property");
        }
        return test.substring(test.indexOf('#')+1);
    }

    @Override
    public void run(final RunNotifier notifier) {
        log.debug("run()");
        EachTestNotifier testNotifier = new EachTestNotifier(notifier,
                getDescription());
        try {
            for (; isCoSWatch();) {
                try {
                    setNewClassLoader();
                    Class<?> klass = getTestClass().getJavaClass();
                    BlockJUnit4ClassRunner r = new BlockJUnit4ClassRunner(Thread.currentThread().getContextClassLoader().loadClass(klass.getName()));
                    Description method = Description.createTestDescription(klass, getMethodName());
                    r.filter(Filter.matchMethodDescription(method));
                    r.run(notifier);
//                    Statement statement = classBlock(notifier);
//                    statement.evaluate();
                } catch (Throwable e) {
                    log.info("", e);
                }
                wait4CoS();
            }

            Statement statement = classBlock(notifier);
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.fireTestIgnored();
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            testNotifier.addFailure(e);
        } finally {
            if (watchService != null) {
                try {
                    watchService.close();
                } catch (IOException ex) {
                    log.error("", ex);
                }
            }
        }
    }

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        log.debug("runChild()");
        Description description = describeChild(method);
        if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            runTestUnit(methodBlock(method), description, notifier);
        }
    }

    @Override
    protected Object createTest() throws Exception {
        log.debug("createTest()");
        return super.createTest();
    }

    /**
     * Runs a {@link Statement} that represents a leaf (aka atomic) test.
     * 
     * @param statement the statement
     * @param description the description
     * @param notifier the notifier
     */
    protected final void runTestUnit(Statement statement, Description description, RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        log.debug("runTestUnit()");
        eachNotifier.fireTestStarted();
        try {
            int retryCounter = isCoSWatch() ? maxRetries : 0;
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

    protected void setNewClassLoader() {
        log.debug("setNewClassLoader()");
        String cp = System.getProperty("surefire.test.class.path");
        if (cp == null) {
            throw new IllegalStateException("surefire.test.class.path sys prop not set");
        }
        StringBuilder dirCPs = new StringBuilder();
        for (String cpPart : cp.split(File.pathSeparator)) {
            File f = new File(cpPart);
            if (!f.isDirectory()) {
                continue;
            }
            if (dirCPs.length() > 0) {
                dirCPs.append(File.pathSeparator);
            }
            log.debug("adding " + cpPart + " to class loader");
            dirCPs.append(cpPart);
        }
        ClassReloader cr = new ClassReloader(dirCPs.toString(), origClassLoader);
        Thread.currentThread().setContextClassLoader(cr);
    }

    protected void wait4CoS() throws IOException, InterruptedException {
        if (!isCoSWatch()) {
            return;
        }

        if (watchService == null) {
            initWatchService();
        }

        for (;;) {
            log.debug("waiting for watch service events");
            WatchKey watchKey = watchService.take();
            for (final WatchEvent<?> event : watchKey.pollEvents()) {
                log.debug(event.context() + " " + event.kind());
            }
            if (!watchKey.reset()) {
                log.debug("No longer valid");
                watchKey.cancel();
                watchService.close();
                watchService = null;
                break;
            }
            if (System.currentTimeMillis() - lastRun < NO_REPEAT_MILLIS) {
                log.debug("ignoring too fast event repetition");
                continue;
            }
            break;
        }
        lastRun = System.currentTimeMillis();
    }

    protected void initWatchService() throws IOException {
        log.debug("initWatchService()");
        String cp = System.getProperty("surefire.test.class.path");
        if (cp == null) {
            throw new IllegalStateException("surefire.test.class.path sys prop not set");
        }
        for (String cpPart : cp.split(File.pathSeparator)) {
            File f = new File(cpPart);
            if (!f.isDirectory()) {
                continue;
            }
            if (watchService == null) {
                FileSystem fs = f.toPath().getFileSystem();
                log.debug("creating watch service on file system " + fs.getRootDirectories().iterator().next().toFile().getPath());
                watchService = fs.newWatchService();
            }
            Files.walkFileTree(f.toPath(), new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    log.debug("registering " + dir.toFile().getPath());
                    dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

}
