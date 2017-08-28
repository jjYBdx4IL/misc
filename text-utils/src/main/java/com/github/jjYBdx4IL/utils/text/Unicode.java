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
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class Unicode {

    private Unicode() {
    }

    public static String removeNonprintableCharacters(String input) {
        StringBuilder newString = new StringBuilder(input.length());
        for (int offset = 0; offset < input.length();) {
            int codePoint = input.codePointAt(offset);
            //log.info("codePoint = " + codePoint);
            offset += Character.charCount(codePoint);
            //log.info("offset = " + offset);

            // Replace invisible control characters and unused code points
            switch (Character.getType(codePoint)) {
                case Character.CONTROL:     // \p{Cc}
                case Character.FORMAT:      // \p{Cf}
                case Character.PRIVATE_USE: // \p{Co}
                case Character.SURROGATE:   // \p{Cs}
                case Character.UNASSIGNED:  // \p{Cn}
                    newString.append("\ufffd");
                    break;
                default:
                    newString.appendCodePoint(codePoint);
                    break;
            }
        }
        return newString.toString();
    }
}
