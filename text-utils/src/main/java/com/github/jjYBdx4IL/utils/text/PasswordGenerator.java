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
package com.github.jjYBdx4IL.utils.text;

//CHECKSTYLE:OFF
import java.util.Random;
import java.util.regex.Pattern;

public class PasswordGenerator {

    // keep this private because elements of a final array are modifiable
    private static final char[] characters = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789,.;:-+()/[]{}&%$!?=*~"
            .toCharArray();
    /**
     * "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789"
     */
    public static final char[] CHARACTERS55 = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789".toCharArray();
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])";
    public static final int DEFAULT_LENGTH = 8;

    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    public static String generate(int length) {
        Pattern p = Pattern.compile(PASSWORD_REGEX);
        String pwd = "";
        while (!p.matcher(pwd).find()) {
            pwd = _generate(length);
        }
        return pwd;
    }

    /**
     * Calls {@link #generate55(int)} with the default length of 8 and returns
     * the returned value.
     * 
     * @return the generated random password
     */
    public static String generate55() {
        return generate55(DEFAULT_LENGTH);
    }

    /**
     * Generate a random password consisting of hard to confuse latin letters (
     * {@link #CHARACTERS55}). The returned password consists of at least one
     * digit, one lower case and one upper case letter.
     * 
     * @param length the length of the generated password
     * @return the generated random password
     */
    public static String generate55(int length) {
        Pattern p = Pattern.compile(PASSWORD_REGEX);
        String pwd = "";
        while (!p.matcher(pwd).find()) {
            pwd = _generate55(length);
        }
        return pwd;
    }

    private static String _generate(int length) {
        Random random = new Random();
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = characters[random.nextInt(characters.length)];
        }
        return new String(chars);
    }

    private static String _generate55(int length) {
        Random random = new Random();
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = CHARACTERS55[random.nextInt(CHARACTERS55.length)];
        }
        return new String(chars);
    }

    private PasswordGenerator() {
    }
}
