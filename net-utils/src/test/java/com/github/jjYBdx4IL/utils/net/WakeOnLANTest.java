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
package com.github.jjYBdx4IL.utils.net;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.net.WakeOnLAN;
import com.github.jjYBdx4IL.utils.env.Env;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WakeOnLANTest {

    public WakeOnLANTest() {
    }

    @Test
    public void testConstructor() throws UnknownHostException {
        new WakeOnLAN("127.0.0.1", "01:23:45:67:89:ab");
        new WakeOnLAN("127.0.0.1", "01:23:45:67:89:aB");

        try {
            new WakeOnLAN("127.0.0.1", "01:23:45:67:89:aj");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new WakeOnLAN("127.0.0.1", "01:23:45:67:89:a");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            new WakeOnLAN("127.0.0.256", "01:23:45:67:89:ab");
            fail();
        } catch (UnknownHostException ex) {
        }
    }

    @Test
    public void testIfaceNameConstructor() throws SocketException {
    	Assume.assumeTrue(Env.isLinux());

        String existingIfaceName = selectNetIfaceWithHWAddr().getDisplayName();

        new WakeOnLAN(existingIfaceName);
        
        try {
            new WakeOnLAN("eth989343");
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    private static NetworkInterface selectNetIfaceWithHWAddr() throws SocketException {
        for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (iface.getHardwareAddress() != null) {
                return iface;
            }
        }
        return null;
    }
}
