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
package com.github.jjYBdx4IL.utils.io;

//CHECKSTYLE:OFF
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class POSIXFilePerm {

    public static String toNumber(Set<PosixFilePermission> attrs) {
        int numeric = 0;
        for (Object o : attrs.toArray()) {
            PosixFilePermission p = (PosixFilePermission) o;
            switch (p) {
                case OWNER_READ:
                    numeric += 100;
                    break;
                case OWNER_WRITE:
                    numeric += 200;
                    break;
                case OWNER_EXECUTE:
                    numeric += 400;
                    break;
                case GROUP_READ:
                    numeric += 10;
                    break;
                case GROUP_WRITE:
                    numeric += 20;
                    break;
                case GROUP_EXECUTE:
                    numeric += 40;
                    break;
                case OTHERS_READ:
                    numeric += 1;
                    break;
                case OTHERS_WRITE:
                    numeric += 2;
                    break;
                case OTHERS_EXECUTE:
                    numeric += 4;
                    break;
            }
        }
        return Integer.toString(numeric);
    }
    
    private POSIXFilePerm() {
    }
}
