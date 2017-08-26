/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.diskcache;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class DiskCacheTest {

    private static final Logger LOG = LoggerFactory.getLogger(DiskCacheTest.class);
    private static final DiskCache cache = new DiskCache(null, null, true);

    @AfterClass
    public static void afterClass() throws IOException {
        cache.close();
    }
    
    @Test
    public void testSetExpirySecs() throws IOException {
        try {
            cache.put("1", "1".getBytes());
            cache.setExpirySecs(0);
            assertNull(cache.get("1"));
        } finally {
            cache.setExpirySecs(DiskCache.DEFAULT_EXPIRY_SECS);
        }
    }

    @Test
    public void testDoublePut() throws Exception {
        cache.put("testDoublePut", "1234".getBytes());
        cache.put("testDoublePut", "1234".getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws IOException {
        cache.put((String) null, "123".getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws IOException {
        cache.put("testPutNullValue", (byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEmptyKey() throws IOException {
        cache.put("", "123".getBytes());
    }

    @Test
    public void testPutEmptyValue() throws IOException {
        cache.put("testPutEmptyValue", "".getBytes());
        assertArrayEquals("".getBytes(), cache.get("testPutEmptyValue"));
    }

    @Test
    public void testPutGet() throws Exception {
        assertNull(cache.get("123"));
        cache.put("123", "1234".getBytes());
        byte[] a = cache.get("123");
        assertNotNull(a);
        assertArrayEquals("1234".getBytes(), a);
    }

    @Test
    public void testLargeFile() throws IOException {
        assertNull(cache.get("testLargeFile"));
        byte[] buf = new byte[2 * (int) DiskCache.MAX_BLOB_SIZE];
        for (int i = 0; i<buf.length; i++) {
            buf[i] = (byte) i;
        }
        try (ByteArrayInputStream is = new ByteArrayInputStream(buf)) {
            cache.put("testLargeFile", is);
        }
        assertArrayEquals(buf, cache.get("testLargeFile"));
    }

}
