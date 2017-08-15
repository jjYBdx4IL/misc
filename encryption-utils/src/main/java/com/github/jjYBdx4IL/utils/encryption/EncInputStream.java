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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
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
 * Use at your own risk only!
 * </p>
 * 
 * @author jjYBdx4IL
 *
 */
class EncInputStream extends FilterInputStream {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptedInputStream.class);

    private final char[] password;
    private Cipher cipher = null;
    private CipherInputStream cipherInputStream = null;

    public EncInputStream(InputStream in, char[] password) {
        super(in);
        this.password = password;
    }

    public EncInputStream(InputStream in, String password) {
        this(in, password.toCharArray());
    }

    private void init() throws IOException {
        try {
            Object obj = null;
            try (ObjectInputStream ois = new ObjectInputStream(in)) {
                obj = ois.readObject();
            }

            if (obj == null) {
                throw new IOException("failed to read encryption header");
            }
            if (!(obj instanceof EncryptedStreamHeader)) {
                throw new IOException("bad stream header class");
            }
            EncryptedStreamHeader header = (EncryptedStreamHeader) obj;

            if (header.salt == null) {
                throw new IOException("no salt stored in header");
            }
            if (header.iv == null) {
                throw new IOException("no init vec stored in header");
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace("restored salt :" + Hex.encodeHexString(header.salt));
                LOG.trace("restored iv :" + Hex.encodeHexString(header.iv));
            }

            SecretKeyFactory factory = SecretKeyFactory.getInstance(EncOutputStream.KEY_DERIVATION_FUNCTION);
            KeySpec spec = new PBEKeySpec(password, header.salt, EncOutputStream.KEY_DERIVATION_ITERATIONS,
                    EncOutputStream.DEFAULT_KEY_BITS);

            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), EncOutputStream.KEY_ALG);

            cipher = Cipher.getInstance(EncOutputStream.CIPHER_SPEC);
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(header.iv));

            cipherInputStream = new CipherInputStream(in, cipher);
        } catch (NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
                | NoSuchAlgorithmException | InvalidKeySpecException | ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public int read() throws IOException {
        if (cipherInputStream == null) {
            init();
        }
        return cipherInputStream.read();
    }

    @Override
    public int read(byte[] b1, int off, int len) throws IOException {
        if (cipherInputStream == null) {
            init();
        }
        return cipherInputStream.read(b1, off, len);
    }

    @Override
    public int read(byte[] b1) throws IOException {
        if (cipherInputStream == null) {
            init();
        }
        return cipherInputStream.read(b1);
    }

    @Override
    public void close() throws IOException {
        if (cipherInputStream != null) {
            cipherInputStream.close();
        }
        super.close();
    }

    @Override
    public int available() throws IOException {
        if (cipherInputStream == null) {
            init();
        }
        return cipherInputStream.available();
    }

    @Override
    public boolean markSupported() {
        if (cipherInputStream == null) {
            try {
                init();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return cipherInputStream.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        if (cipherInputStream == null) {
            init();
        }
        cipherInputStream.reset();
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (cipherInputStream == null) {
            try {
                init();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        cipherInputStream.mark(readlimit);
    }

    @Override
    public long skip(long n1) throws IOException {
        if (cipherInputStream == null) {
            init();
        }
        return cipherInputStream.skip(n1);
    }

}
