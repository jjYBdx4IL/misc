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
package com.github.jjYBdx4IL.utils.vmmgmt;

import com.sun.jna.NativeLibrary;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class LibvirtUtils {

    /**
     * Checks whether vmmgmt classes can be run on the current platform, ie. whether libvirt is available.
     * Needs additional work, ie. a check for QEMU tooling.
     * <p>
     * Beware! In order for this function to work, the class must NOT reference any libvirt classes!
     * The static initialization might trigger an error due to a try to load the library without
     * catching the error. If you are using this with junit's assume functions, put it into
     * a static function annotated with @org.junit.BeforeClass.
     *
     * @return true iff libvirt is available on the current system
     */
    public static boolean isAvailable() {
        try {
            NativeLibrary.getInstance("virt");
            return true;
        } catch (UnsatisfiedLinkError err) {}
        return false;
    }

    private LibvirtUtils() {
    }

}
