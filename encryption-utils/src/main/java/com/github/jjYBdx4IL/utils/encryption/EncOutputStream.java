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

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is not public because it does not provide any decryption/password checks.
 * 
 * <p>
 * Use at your own risk only!</p>
 * 
 * @author jjYBdx4IL
 *
 */
class EncOutputStream extends FilterOutputStream {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptedOutputStream.class);

    public static final int SALT_BYTE_LENGTH = 8;
    public static final int DEFAULT_KEY_BITS = 256;
    public static final int KEY_DERIVATION_ITERATIONS = 65536;
    public static final String KEY_DERIVATION_FUNCTION = "PBKDF2WithHmacSHA1";
    public static final String KEY_ALG = "AES";
    public static final String CIPHER_SPEC = "AES/CBC/PKCS5Padding";

    private final char[] password;
    private Cipher cipher = null;
    private final byte[] sba = new byte[1];
    private boolean encryptionClosed = false; // call cipher.doFinal() only once

    public EncOutputStream(OutputStream out, char[] password) {
        super(out);
        this.password = password;
    }

    public EncOutputStream(OutputStream out, String password) {
        super(out);
        this.password = password.toCharArray();
    }

    private void init() throws IOException {
        try {
            byte[] salt = new byte[SALT_BYTE_LENGTH];
            SecureRandom rnd = new SecureRandom();
            rnd.nextBytes(salt);

            if (LOG.isTraceEnabled()) {
                LOG.trace("generated salt :" + Hex.encodeHexString(salt));
            }

            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_FUNCTION);

            KeySpec spec = new PBEKeySpec(password, salt, KEY_DERIVATION_ITERATIONS, DEFAULT_KEY_BITS);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), KEY_ALG);

            cipher = Cipher.getInstance(CIPHER_SPEC);
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] initVec = params.getParameterSpec(IvParameterSpec.class).getIV();

            if (LOG.isTraceEnabled()) {
                LOG.trace("initVec is :" + Hex.encodeHexString(initVec));
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
                oos.writeObject(new EncryptedStreamHeader(salt, initVec));
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException
                | InvalidParameterSpecException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void flush() throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("flush()");
        }
        if (cipher == null) {
            init();
        }
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("close()");
        }
        if (cipher == null) {
            init();
        }
        try {
            if (!encryptionClosed) {
                out.write(cipher.doFinal());
                encryptionClosed = true;
            }
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            throw new IOException(ex);
        }
        out.close();
    }

    @Override
    public void write(int b1) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("write(int)");
        }
        if (cipher == null) {
            init();
        }

        sba[0] = (byte) b1;
        out.write(cipher.update(sba));
    }

    @Override
    public void write(byte[] b1) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("write(byte[])");
        }
        if (cipher == null) {
            init();
        }

        out.write(cipher.update(b1));
    }

    @Override
    public void write(byte[] b1, int off, int len) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("write(byte[],off,len)");
        }
        if (cipher == null) {
            init();
        }

        out.write(cipher.update(b1, off, len));
    }

}
