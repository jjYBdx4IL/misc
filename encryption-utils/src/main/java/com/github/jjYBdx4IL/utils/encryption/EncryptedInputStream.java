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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Use at your own risk only!.
 * 
 * @author jjYBdx4IL
 */
public class EncryptedInputStream extends FilterInputStream {

    private final GZIPInputStream gzis;

    public EncryptedInputStream(InputStream is, char[] password) throws IOException {
        super(new EncInputStream(is, password));
        gzis = new GZIPInputStream(in);
    }

    public EncryptedInputStream(InputStream is, String password) throws IOException {
        this(is, password.toCharArray());
    }

    @Override
    public int read() throws IOException {
        return gzis.read();
    }

    @Override
    public int read(byte[] b1, int off, int len) throws IOException {
        return gzis.read(b1, off, len);
    }

    @Override
    public int read(byte[] b1) throws IOException {
        return gzis.read(b1);
    }

    @Override
    public void close() throws IOException {
        gzis.close();
    }

    @Override
    public int available() throws IOException {
        return gzis.available();
    }

    @Override
    public boolean markSupported() {
        return gzis.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        gzis.reset();
    }

    @Override
    public synchronized void mark(int readlimit) {
        gzis.mark(readlimit);
    }

    @Override
    public long skip(long n1) throws IOException {
        return gzis.skip(n1);
    }

}
