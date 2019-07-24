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
package com.github.jjYBdx4IL.utils.klazz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class ClassReloader extends ClassLoader {

    private static final Logger log = LoggerFactory.getLogger(ClassReloader.class);
    private final String[] _classPath;
    private final ClassLoader origClassLoader;
    private Set<String> classNameLoadTried = new HashSet<>();
    public final static long NO_REPEAT_MILLIS = 1000l;

    public ClassReloader(String classPath) {
        this(classPath, null);
    }

    /**
     * If you want to load a class with the same name more than once, you need to use a new class loader each
     * time.
     *
     * @param classPath ':'-delimited list of path names
     * @param orig fallback class loader to use when this one cannot find the class in the given classPath;
     * example: Thread.currentThread().getContextClassLoader()
     */
    public ClassReloader(String classPath, ClassLoader orig) {
        super();
        this._classPath = classPath.split(File.pathSeparator);
        this.origClassLoader = orig;
    }

    @Override
    public Class<?> loadClass(String s) throws ClassNotFoundException {
        return findClass(s);
    }

    @Override
    public Class<?> findClass(String s) throws ClassNotFoundException {
        if (log.isDebugEnabled()) {
            log.debug("findClass: " + s);
        }
        // avoid loops with super method calling back here...
        if (classNameLoadTried.contains(s)) {
            throw new ClassNotFoundException(s);
        }
        classNameLoadTried.add(s);

        try {
            long timeout = System.currentTimeMillis() + 10000L;
            ClassFormatError err = null;
            do {
                try {
                    byte[] bytes = loadClassData(s);
                    return defineClass(s, bytes, 0, bytes.length);
                } catch (ClassFormatError ex) {
                    err = ex;
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException ex) {
                    log.error("", ex);
                }
            } while (System.currentTimeMillis() < timeout);
            throw new RuntimeException(err);
        } catch (IOException | ClassNotFoundException ioe) {
            if (origClassLoader != null) {
                return origClassLoader.loadClass(s);
            } else {
                return super.loadClass(s);
            }
        }
    }

    private byte[] loadClassData(String className) throws IOException, ClassNotFoundException {
        String cn = className.replace(".", File.separator);
        File f = null;
        for (String classPathEntry : _classPath) {
            f = new File(classPathEntry + File.separator + cn + ".class");
            if (f.exists()) {
                break;
            }
            f = null;
        }
        if (f == null) {
            throw new ClassNotFoundException(className);
        }
        if (log.isDebugEnabled()) {
            log.debug("class found: " + f.getAbsolutePath());
        }
        int size = (int) f.length();
        byte buff[] = new byte[size];
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
            dis.readFully(buff);
        }
        return buff;
    }

    /**
     * @see #watchLoadAndRun(String,String,ClassLoader)
     * @param classPath the class path
     * @param runnerClassName the runner class name
     * @return the thread
     */
    public static Thread watchLoadAndRun(final String classPath, final String runnerClassName) {
        return watchLoadAndRun(classPath, runnerClassName, null);
    }

    /**
     * Watches the given class inside classpaths for update. Every update triggers a (re)load and subsequent
     * execution of the given class via the {@link Runnable} interface. The class must be an instance of
     * {@link Runnable}.
     *
     * @param classPath all parts of the classpath must be on the same file system
     * @param runnerClassName the runner class name
     * @param orig fallback class loader to use when this one cannot find the class in the given classPath;
     * example: Thread.currentThread().getContextClassLoader()
     * @return unstarted thread
     */
    public static Thread watchLoadAndRun(final String classPath, final String runnerClassName, final ClassLoader orig) {
        final String[] paths = classPath.split(File.pathSeparator);
        final AtomicBoolean started = new AtomicBoolean(false);
        final File subPath = runnerClassName.contains(".")
                ? new File(runnerClassName.replace(".", File.separator)).getParentFile()
                : null;
        Runnable r = new Runnable() {

            private long lastRun = 0l;

            @Override
            public void run() {
                try {
                    WatchService watchService = new File(paths[0]).toPath().getFileSystem().newWatchService();
                    for (String path : paths) {
                        (subPath != null ? new File(path, subPath.getPath()) : new File(path)).toPath().
                                register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    }
                    synchronized (started) {
                        started.set(true);
                        started.notifyAll();
                    }
                    // loop forever to watch directory
                    while (true) {
                        WatchKey watchKey;
                        watchKey = watchService.take(); // this call is blocking until events are present
                        // poll for file system events on the WatchKey
                        for (final WatchEvent<?> event : watchKey.pollEvents()) {
                            log.debug(event.context() + " " + event.kind());
                        }
                        if (System.currentTimeMillis() - lastRun < NO_REPEAT_MILLIS) {
                            log.debug("ignoring too fast event repetition");
                            if (!watchKey.reset()) {
                                log.debug("No longer valid");
                                watchKey.cancel();
                                watchService.close();
                                break;
                            }
                            continue;
                        }

                        Object r = null;
                        try {
                            r = new ClassReloader(classPath, orig).loadClass(runnerClassName).newInstance();
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                            log.warn(ex.getMessage());
                        }
                        if (!(r instanceof Runnable)) {
                            log.warn(runnerClassName + " is not a Runnable instance");
                            r = null;
                        }
                        if (r != null) {
                            try {
                                lastRun = System.currentTimeMillis();
                                ((Runnable) r).run();
                            } catch (Exception ex) {
                                log.warn("", ex);
                            }
                        }

                        // if the watched directory gets deleted, get out of run method
                        if (!watchKey.reset()) {
                            log.debug("No longer valid");
                            watchKey.cancel();
                            watchService.close();
                            break;
                        }
                    }
                } catch (InterruptedException | IOException ex) {
                    log.error("", ex);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
        try {
            synchronized (started) {
                while (!started.get()) {
                    started.wait();
                }
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return t;
    }
}
