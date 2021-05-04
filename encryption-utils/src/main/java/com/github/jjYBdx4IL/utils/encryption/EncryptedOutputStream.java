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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Password-protected encryption stream implementation. Use a
 * BufferedOutputStream on top of this stream if you use a lot of single byte
 * writes. The output gets prepended with the EncryptedStreamHeader which in
 * turn contains the salt used in the key derivation. Before encrypting, data
 * gets compressed using a GZIPOutputStream. This also implicitly provides a
 * password/data integrity check. Due to GZIP streams containing a
 * checksum-verified header, the implicit password check due to the GZIP stream
 * decoding should be fail fast, ie. happen at the start of decryption and not
 * at the end of it.
 *
 * <p>
 * Use at your own risk only!
 * </p>
 * 
 * @author jjYBdx4IL
 *
 */
public class EncryptedOutputStream extends FilterOutputStream {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptedOutputStream.class);

    private final GZIPOutputStream gzos;

    public EncryptedOutputStream(OutputStream os, char[] password) throws IOException {
        super(new EncOutputStream(os, password));
        gzos = new GZIPOutputStream(out);
    }

    public EncryptedOutputStream(OutputStream os, String password) throws IOException {
        this(os, password.toCharArray());
    }

    @Override
    public void flush() throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("flush()");
        }

        gzos.flush();
    }

    @Override
    public void close() throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("close()");
        }

        gzos.close();
    }

    @Override
    public void write(int b1) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("write(int)");
        }

        gzos.write(b1);
    }

    @Override
    public void write(byte[] b1) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("write(byte[])");
        }

        gzos.write(b1);
    }

    @Override
    public void write(byte[] b1, int off, int len) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("write(byte[],off,len)");
        }

        gzos.write(b1, off, len);
    }

}
