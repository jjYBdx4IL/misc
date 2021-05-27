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

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Use at your own risk only!
 * 
 * <p>This class assumes UTF-8 everywhere.
 * 
 * <p>There is a reference library from Google called Tink. However, it does not
 * provide simple password-based encryption/decryption.
 */
public class CryptUtils {

    private SecureRandom random = new SecureRandom();

    public CryptUtils() {

    }

    public static final int DEF_SALT_LENGTH = 8; // in bytes
    public static final int DEF_KEY_SIZE = 256; // in bits
    public static final int DEF_KEY_HASH_ITERATIONS = 64 * 1024;
    public static final int DEF_GCM_NONCE_LENGTH = 12; // in bytes
    public static final int DEF_GCM_TAG_LENGTH = 16; // in bytes
    public static final String DEF_AAD = "v1";
    public static final String DEF_PWD_HASH_ALG = "PBKDF2WithHmacSHA512";
    public static final String DEF_CIPHER_ALG = "AES/GCM/NoPadding";

    private int saltLength = DEF_SALT_LENGTH;
    private int keySize = DEF_KEY_SIZE;
    private int keyHashIterations = DEF_KEY_HASH_ITERATIONS;
    private int gcmNonceLength = DEF_GCM_NONCE_LENGTH;
    private int gcmTagLength = DEF_GCM_TAG_LENGTH;
    private String pwdHashAlg = DEF_PWD_HASH_ALG;
    private String cipherAlg = DEF_CIPHER_ALG;
    private String aad = null; // non-encrypted, but authenticated data that must match on both ends

    public CryptUtils setSaltLength(int saltLength) {
        this.saltLength = saltLength;
        return this;
    }

    public CryptUtils setKeySize(int keySize) {
        this.keySize = keySize;
        return this;
    }

    public CryptUtils setKeyHashIterations(int keyHashIterations) {
        this.keyHashIterations = keyHashIterations;
        return this;
    }

    public CryptUtils setGcmNonceLength(int gcmNonceLength) {
        this.gcmNonceLength = gcmNonceLength;
        return this;
    }

    public CryptUtils setGcmTagLength(int gcmTagLength) {
        this.gcmTagLength = gcmTagLength;
        return this;
    }

    /**
     * Set the AAD. By default (null), the AAD is constructed from the parameter
     * settings of this class.
     * 
     * <p>Futher reading:
     * <ul>
     * <li>About AAD:
     * https://crypto.stackexchange.com/questions/35727/does-aad-make-gcm-encryption-more-secure
     * <li>About AAD: https://bugs.openjdk.java.net/browse/JDK-8062828
     * </ul>
     * 
     * @param aad the AAD
     */
    public CryptUtils setAad(String aad) {
        this.aad = aad;
        return this;
    }

    public CryptUtils setPwdHashAlg(String pwdHashAlg) {
        this.pwdHashAlg = pwdHashAlg;
        return this;
    }

    public CryptUtils setCipherAlg(String cipherAlg) {
        this.cipherAlg = cipherAlg;
        return this;
    }

    /**
     * Decrypt cipher text.
     * 
     * @param ciphertext the encrypted message
     * @param secret     key
     * @param nonce      unique random value
     * @throws GeneralSecurityException on any error
     */
    public byte[] decrypt(byte[] ciphertext, SecretKey secret, byte[] nonce) throws GeneralSecurityException {
        String aadToUse = aad != null ? aad : computeAad();
        Cipher cipher = Cipher.getInstance(cipherAlg);
        GCMParameterSpec spec = new GCMParameterSpec(gcmTagLength * 8, nonce);
        cipher.init(Cipher.DECRYPT_MODE, secret, spec);
        cipher.updateAAD(aadToUse.getBytes(StandardCharsets.UTF_8));
        return cipher.doFinal(ciphertext);
    }

    /**
     * Convenience method that acts as the reverse to
     * {@link #encrypt(String, String)}.
     * 
     * @param data     base64/$ encoded and encrypted message containing salt, nonce
     *                 and encrypted text
     * @param password the secret password used for decryption
     * @return the decrypted text
     * @throws GeneralSecurityException on any error
     */
    public String decrypt(String data, String password) throws GeneralSecurityException {
        String[] saltNonceEncdata = data.split("\\$");
        if (saltNonceEncdata.length != 3) {
            throw new IllegalArgumentException();
        }
        Decoder base64dec = Base64.getDecoder();
        byte[] salt = base64dec.decode(saltNonceEncdata[0]);
        byte[] nonce = base64dec.decode(saltNonceEncdata[1]);
        byte[] encdata = base64dec.decode(saltNonceEncdata[2]);
        SecretKey secret = createKeyFromPwd(password, salt);
        return new String(decrypt(encdata, secret, nonce), StandardCharsets.UTF_8);
    }

    /**
     * Encrpyt a message.
     * 
     * @param message the message to encrypt
     * @param secret  the key
     * @param nonce   unique random value
     * @throws GeneralSecurityException on any error
     */
    public byte[] encrypt(String message, SecretKey secret, byte[] nonce) throws GeneralSecurityException {
        String aadToUse = aad != null ? aad : computeAad();
        Cipher cipher = Cipher.getInstance(cipherAlg);
        GCMParameterSpec spec = new GCMParameterSpec(gcmTagLength * 8, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, secret, spec);
        cipher.updateAAD(aadToUse.getBytes(StandardCharsets.UTF_8));
        return cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Convenience method that returns salt, nonce, encryptedData in a
     * concatenated string consisting of base64 encoded transformations of each in
     * the given order, and separated by a '$'.
     * 
     * @param message  the text to encrypt
     * @param password the secret password used for encryption
     * @return the base64/$ encrypted concatenation of salt, nonce, encrypted
     *         message
     * @throws GeneralSecurityException on any error
     */
    public String encrypt(String message, String password) throws GeneralSecurityException {
        byte[] salt = createSalt();
        SecretKey secret = createKeyFromPwd(password, salt);
        byte[] nonce = createNonce();
        byte[] encdata = encrypt(message, secret, nonce);
        StringBuilder sb = new StringBuilder(2 * (encdata.length + salt.length + nonce.length));
        Encoder base64enc = Base64.getEncoder();
        sb.append(base64enc.encodeToString(salt));
        sb.append("$");
        sb.append(base64enc.encodeToString(nonce));
        sb.append("$");
        sb.append(base64enc.encodeToString(encdata));
        return sb.toString();
    }

    /**
     * Construct a secret key from a given password and a random salt. The salt must
     * match on both ends (encryption and decryption) and is not a secret.
     * 
     * @param password your secret password
     * @param salt     a random salt to make brute force attacks hopefully
     *                 impossible
     * @throws GeneralSecurityException on any error
     */
    public SecretKey createKeyFromPwd(String password, byte[] salt) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(pwdHashAlg);
        KeySpec keyspec = new PBEKeySpec(password.toCharArray(), salt, keyHashIterations, keySize);
        SecretKey tmp = factory.generateSecret(keyspec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * Create a random value.
     * 
     * @return the value
     */
    public byte[] createNonce() {
        byte[] nonce = new byte[gcmNonceLength];
        random.nextBytes(nonce);
        return nonce;
    }

    /**
     * Create a random value.
     * 
     * @return the value
     */
    public byte[] createSalt() {
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);
        return salt;
    }

    private String computeAad() {
        StringBuilder sb = new StringBuilder();
        sb.append(saltLength);
        sb.append(";");
        sb.append(keySize);
        sb.append(";");
        sb.append(keyHashIterations);
        sb.append(";");
        sb.append(gcmNonceLength);
        sb.append(";");
        sb.append(gcmTagLength);
        sb.append(";");
        sb.append(pwdHashAlg);
        sb.append(";");
        sb.append(cipherAlg);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CryptUtils [saltLength=").append(saltLength).append(", keySize=").append(keySize)
                .append(", keyHashIterations=").append(keyHashIterations).append(", gcmNonceLength=")
                .append(gcmNonceLength).append(", gcmTagLength=").append(gcmTagLength).append(", pwdHashAlg=")
                .append(pwdHashAlg).append(", cipherAlg=").append(cipherAlg).append(", aad=").append(aad).append("]");
        return builder.toString();
    }
}
