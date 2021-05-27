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
package com.github.jjYBdx4IL.utils.io;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Recursive watch service with debounce and dynamic addition of new
 * directories.
 */
public class DirWatchService implements Runnable {

    private boolean trace = false;
    
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private boolean recursive = false;
    private int debounceMillis = 0;
    private Callback callback = null;
    private final Path dir;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Constructor for DirWatchService, a recursive watch service with debounce
     * and dynamic addition of newly created directories.
     * 
     * @param dir the root path
     * @throws IOException on error
     */
    public DirWatchService(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.dir = dir;
    }
    
    public void stop() throws IOException {
        watcher.close();
    }
    
    /**
     * Register the given directory with the WatchService.
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                if (trace) {
                    System.out.format("register: %s\n", dir);
                }
            } else {
                if (!dir.equals(prev)) {
                    if (trace) {
                        System.out.format("update: %s -> %s\n", prev, dir);
                    }
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static class BackLogEntry {
        public final String kind;
        public final Path path;
        
        public BackLogEntry(String kind, Path path) {
            this.kind = kind;
            this.path = path;
        }
    }
    
    public static interface Callback {
        void process(BackLogEntry ble);
        
        /**
         * called once after last event + debounceMillis of inactivity.
         */
        void debounced();
    }
    
    /**
     * Implement Runnable interface.
     */
    public void run() {
        try {
            run2();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Process all events for keys queued to the watcher.
     */
    private void run2() throws IOException {
        if (recursive) {
            if (trace) {
                System.out.format("Scanning %s ...\n", dir);
            }
            registerAll(dir);
            if (trace) {
                if (keys.size() == 1) {
                    System.out.println("Done registering " + keys.size() + " directory.");
                } else {
                    System.out.println("Done registering " + keys.size() + " directories.");
                }
            }
        } else {
            register(dir);
        }
        
        final List<BackLogEntry> backlog = new ArrayList<>();
        
        for (;;) {

            WatchKey key;
            try {
                if (debounceMillis > 0 && !backlog.isEmpty()) {
                    key = watcher.poll(debounceMillis, TimeUnit.MILLISECONDS);
                } else {
                    key = watcher.take();
                }
            } catch (InterruptedException x) {
                return;
            }
            
            if (key == null) {
                if (debounceMillis > 0 && !backlog.isEmpty()) {
                    if (callback != null) {
                        for (BackLogEntry ble : backlog) {
                            callback.process(ble);
                        }
                        callback.debounced();
                    }
                    backlog.clear();
                }
                continue;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                if (trace) {
                    System.err.println("WatchKey not recognized!!");
                }
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    if (trace) {
                        System.err.println("overflow");
                    }
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                if (trace) {
                    System.out.format("%s: %s\n", event.kind().name(), child);
                }
                
                // dynamically register newly created sub-dirs
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        if (trace) {
                            x.printStackTrace();
                        }
                    }
                }
                
                BackLogEntry ble = new BackLogEntry(event.kind().name(), child);
                if (debounceMillis > 0) {
                    backlog.add(ble);
                } else if (callback != null) {
                    callback.process(ble);
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    //
    // Chain config methods.
    //
    
    /**
     * Enable recursive watch.
     */
    public DirWatchService recursive() {
        this.recursive = true;
        return this;
    }

    /**
     * Set debounce delay.
     */
    public DirWatchService debounce(int millis) {
        this.debounceMillis = millis;
        return this;
    }

    /**
     * Set callback.
     */
    public DirWatchService callback(Callback callback) {
        this.callback = callback;
        return this;
    }
    
    public DirWatchService trace() {
        this.trace = true;
        return this;
    }
}