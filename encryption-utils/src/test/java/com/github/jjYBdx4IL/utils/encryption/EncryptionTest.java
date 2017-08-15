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
package com.github.jjYBdx4IL.utils.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * <p>
 * Use at your own risk only!
 * </p>
 * 
 * @author jjYBdx4IL
 *
 */
public class EncryptionTest {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptionTest.class);

    @Test
    public void test() throws Exception {
        final String content = "testcontent";
        final String password = "testpassword";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (EncryptedOutputStream eos = new EncryptedOutputStream(baos, password)) {
            eos.write(content.getBytes("UTF-8"));
        }

        byte[] encryptedData = baos.toByteArray();
        assertNotNull(encryptedData);
        LOG.trace(String.format(Locale.ROOT, "%d - %s", encryptedData.length, Hex.encodeHexString(encryptedData)));

        String decrypted = null;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(encryptedData);
                EncryptedInputStream eis = new EncryptedInputStream(bais, password)) {
            decrypted = IOUtils.toString(eis, "UTF-8");
        }

        assertNotNull(decrypted);
        assertEquals(content, decrypted);
    }

    @Test
    public void testBadPassword() throws Exception {
        final String content = "testcontent";
        String password = "testpassword";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (EncryptedOutputStream eos = new EncryptedOutputStream(baos, password)) {
            eos.write(content.getBytes("UTF-8"));
        }

        byte[] encryptedData = baos.toByteArray();
        assertNotNull(encryptedData);
        LOG.trace(String.format(Locale.ROOT, "%d - %s", encryptedData.length, Hex.encodeHexString(encryptedData)));

        for (int i = 0; i < 100; i++) {
            password = "testpasswor" + Integer.toString(i);
            try {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(encryptedData);
                        EncryptedInputStream eis = new EncryptedInputStream(bais, password)) {
                    IOUtils.toString(eis, "UTF-8");
                }
                fail();
            } catch (IOException ex) {
                //LOG.trace("", ex);
            }
        }
    }

    @Test
    public void testBuffered() throws Exception {
        final String content = "testcontent";
        final String password = "testpassword";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (EncryptedOutputStream eos = new EncryptedOutputStream(baos, password);
                BufferedOutputStream bos = new BufferedOutputStream(eos, 1024)) {
            bos.write(content.getBytes("UTF-8"));
        }

        byte[] encryptedData = baos.toByteArray();
        assertNotNull(encryptedData);
        LOG.trace(String.format(Locale.ROOT, "%d - %s", encryptedData.length, Hex.encodeHexString(encryptedData)));

        String decrypted = null;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(encryptedData);
                EncryptedInputStream eis = new EncryptedInputStream(bais, password)) {
            decrypted = IOUtils.toString(eis, "UTF-8");
        }

        assertNotNull(decrypted);
        assertEquals(content, decrypted);
    }

}
