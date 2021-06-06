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

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InstanceLock implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceLock.class);

    public static final Path RUN_LOCK = Paths.get("/run/lock");
    public static final Path RUN_LOCK_FILE = RUN_LOCK.resolve("jmonsrv.lock");

    private final FileOutputStream out;
    private FileLock lock = null;

    public InstanceLock() throws IOException, InterruptedException {
        Path lockFile;
        if (SystemUtils.IS_OS_UNIX && Files.exists(RUN_LOCK)) {
            lockFile = RUN_LOCK_FILE;
        } else {
            lockFile = Config.cfgDir.resolve(".lock");
        }
        LOG.debug("acquiring lock file {}", lockFile);
        out = new FileOutputStream(lockFile.toFile(), true);
        checkNotNull(out);
        for (int i = 0; i < 30; i++) {
            if (i > 0) {
                LOG.debug("failed to acquire lock, retrying...");
                Thread.sleep(1000L);
            }
            try {
                lock = out.getChannel().lock();
                break;
            } catch (OverlappingFileLockException ex) {
            }
        }
        if (lock == null) {
            out.close();
            throw new IOException("failed to acquire lock, giving up");
        }
        LOG.debug("lock acquired: {}", lockFile);
    }

    @Override
    public void close() throws Exception {
        lock.release();
        out.close();
        LOG.debug("lock released");
    }
}
