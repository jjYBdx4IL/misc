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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.env.CI;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AddressUtilsTest {

    public AddressUtilsTest() {
    }

    @Ignore
    @Test
    public void testGetNonLocalHostIPAddress() throws Exception {
        // don't run this test where we can't control host configurations
        Assume.assumeFalse(CI.isPublic());
        
        assertTrue(AddressUtils.getNonLocalHostIPAddress().getHostAddress().startsWith("192.168."));
    }

    @Test
    public void testIsSimpleNonLocalAddress() throws UnknownHostException {
        assertTrue(AddressUtils.isSimpleNonLocalAddress("1.2.3.4"));
        assertTrue(AddressUtils.isSimpleNonLocalAddress("ibm.com"));
        assertFalse(AddressUtils.isSimpleNonLocalAddress("localhost"));
        assertFalse(AddressUtils.isSimpleNonLocalAddress("::1"));
        assertFalse(AddressUtils.isSimpleNonLocalAddress("127.0.0.1"));
        assertFalse(AddressUtils.isSimpleNonLocalAddress("127.0.0.2"));
        assertFalse(AddressUtils.isSimpleNonLocalAddress(InetAddress.getLocalHost().getHostAddress()));
    }
    
}
