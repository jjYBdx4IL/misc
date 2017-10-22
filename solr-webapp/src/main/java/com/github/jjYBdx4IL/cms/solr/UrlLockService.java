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
package com.github.jjYBdx4IL.cms.solr;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.github.jjYBdx4IL.cms.Env;
import com.github.jjYBdx4IL.cms.jpa.dto.WebPageMeta;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UrlLockService {

    private static final Logger LOG = LoggerFactory.getLogger(UrlLockService.class);
    
    private final Cache<String, String> lockCache;
    private final Cache<String, String> deadTimeCache;
    
    public UrlLockService() {
        lockCache = CacheBuilder.newBuilder().build();
        deadTimeCache = CacheBuilder.newBuilder().expireAfterWrite(Env.isDevel() ? 5 : 180, TimeUnit.SECONDS).build();
    }
    
    public Lock acquire(WebPageMeta meta) {
        String host = getHost(meta.getUrl());
        Lock lock = new Lock(host, this);
        try {
            for (InetAddress addr : InetAddress.getAllByName(host)) {
                lock.addAddr(addr.getHostAddress());
            }
        } catch (UnknownHostException e) {
            LOG.info("", e);
        }
        synchronized (this) {
            if (lockCache.getIfPresent(host) != null) {
                return null;
            }
            for (String addr : lock.getAddrs()) {
                if (lockCache.getIfPresent(addr) != null) {
                    return null;
                }
            }
            if (deadTimeCache.getIfPresent(host) != null) {
                return null;
            }
            for (String addr : lock.getAddrs()) {
                if (deadTimeCache.getIfPresent(addr) != null) {
                    return null;
                }
            }
            lockCache.put(host, "");
            for (String addr : lock.getAddrs()) {
                lockCache.put(addr, "");
            }
        }
        return lock;
    }
    
    public void release(Lock lock) {
        synchronized (this) {
            lockCache.invalidate(lock.getHostname());
            for (String addr : lock.getAddrs()) {
                lockCache.invalidate(addr);
            }
            deadTimeCache.put(lock.getHostname(), "");
            for (String addr : lock.getAddrs()) {
                deadTimeCache.put(addr, "");
            }
        }
    }
    
    private String getHost(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static class Lock implements AutoCloseable {
        private final List<String> addrs = new ArrayList<>();
        private final String hostname;
        private final UrlLockService lockService;
        public Lock(String hostname, UrlLockService lockService) {
            checkNotNull(hostname);
            checkNotNull(lockService);
            checkState(!hostname.isEmpty());
            this.hostname = hostname;
            this.lockService = lockService;
        }
        public void addAddr(String addr) {
            addrs.add(addr);
        }
        public List<String> getAddrs() {
            return addrs;
        }
        public String getHostname() {
            return hostname;
        }
        @Override
        public void close() throws Exception {
            lockService.release(this);
        }
    }
}
