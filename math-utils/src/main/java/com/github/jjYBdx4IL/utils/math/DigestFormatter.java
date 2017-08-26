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
package com.github.jjYBdx4IL.utils.math;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class DigestFormatter {

    /**
     * Returns 32 characters wide hexadecimal representation of an MD5 digest encoded
     * as a byte[], for example by java.security.MessageDigest.
     *
     * @param digest the digest byte array
     * @return the string representation of the digest
     */
    public static String md5(byte[] digest) {
        if (digest == null || digest.length != 16) {
            throw new IllegalArgumentException("input digest has invalid length");
        }
        StringBuilder sb = new StringBuilder(32);
        for (int i=0; i<digest.length; i++) {
            sb.append(String.format("%02x", digest[i]));
        }
        return sb.toString();
    }

    public static String sha1(byte[] digest) {
        if (digest == null || digest.length != 20) {
            throw new IllegalArgumentException("input digest has invalid length");
        }
        StringBuilder sb = new StringBuilder(40);
        for (int i=0; i<digest.length; i++) {
            sb.append(String.format("%02x", digest[i]));
        }
        return sb.toString();
    }

    private DigestFormatter() {
    }
}
