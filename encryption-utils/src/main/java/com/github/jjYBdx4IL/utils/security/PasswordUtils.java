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
package com.github.jjYBdx4IL.utils.security;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Use at your own risk.
 * 
 * <a href=
 * "http://stackoverflow.com/questions/2860943/suggestions-for-library-to-hash-passwords-in-java">Source</a>
 */
public class PasswordUtils {
    
    private SecureRandom random = new SecureRandom();
    
    // The higher the number of iterations the more
    // expensive computing the hash is for us
    // and also for a brute force attack.
    public static final int DEF_ITERATIONS = 64 * 1024;
    public static final int DEF_SALTLEN = 12; // bytes
    public static final int DEF_KEYLEN = 256; // bits
    public static final String DEF_CIPHER_ALG = "PBKDF2WithHmacSHA512";

    private int iterations = DEF_ITERATIONS;
    private int saltLen = DEF_SALTLEN;
    private int keyLen = DEF_KEYLEN;
    private String cipherAlg = DEF_CIPHER_ALG;
    
    public PasswordUtils() {
    }
    
    /**
     * Default is {@link #DEF_ITERATIONS}.
     * 
     * @param iterations number of hashing iterations, directly affects security 
     */
    public PasswordUtils setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }
    
    /**
     * Default is {@link #DEF_SALTLEN}.
     * 
     * @param saltLen set the length of the salt in bytes
     */
    public PasswordUtils setSaltLen(int saltLen) {
        this.saltLen = saltLen;
        return this;
    }
    
    /**
     * Default is {@link #DEF_KEYLEN}.
     * 
     * @param keyLen the length of the key derived from the password in bits
     */
    public PasswordUtils setKeyLen(int keyLen) {
        this.keyLen = keyLen;
        return this;
    }

    /**
     * Default is {@link #DEF_CIPHER_ALG}.
     * 
     * @param cipherAlg the cipher alg to use
     */
    public PasswordUtils setCipherAlg(String cipherAlg) {
        this.cipherAlg = cipherAlg;
        return this;
    }
    
    /**
     * Computes a salted PBKDF2 hash of given plaintext password suitable for
     * storing in a database. Empty passwords are not supported.
     * 
     * @param password the password
     * @return the salted hash
     * @throws RuntimeException if there is something wrong with the hash/crypto algorithms
     */
    public String getSaltedHash(String password) throws GeneralSecurityException {
        byte[] salt = random.generateSeed(saltLen);
        // store the salt with the password
        return Base64.getEncoder().encodeToString(salt) + "$" + hash(password, salt);
    }

    /**
     * Checks whether given plaintext password corresponds to a stored salted
     * hash of the password.
     * 
     * @param password the plaintext password
     * @param stored salted hash of the password
     * @return true iff the password matches
     */
    public boolean check(String password, String stored) throws GeneralSecurityException {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            return false;
        }
        String hashOfInput = hash(password, Base64.getDecoder().decode(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    private String hash(String password, byte[] salt) throws GeneralSecurityException {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty passwords are not supported.");
        }
        SecretKeyFactory f = SecretKeyFactory.getInstance(cipherAlg);
        SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, keyLen));
        
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
