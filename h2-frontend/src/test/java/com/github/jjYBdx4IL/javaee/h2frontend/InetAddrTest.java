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
package com.github.jjYBdx4IL.javaee.h2frontend;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

public class InetAddrTest {

    static final String[] POSITIVES = new String[] { "::1", "127.289.1233.0", "127.0.0.1", "127.1.1.1" };
    static final String[] NEGATIVES = new String[] { ":.1", "128.0.0.1", "127.0.0,1" };

    static final String[] FORWARD_NAMES = new String[] { "X-Forwarded-For", "Forwarded", "forwarded", "FORWARDED",
        "x-Forwarded-for", "X-Forwarded-FOR" };

    @Test
    public void testIsLocalhostAddress() {

        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        
        // fail on no headers
        when(mockedRequest.getRemoteAddr()).thenReturn("::1");
        when(mockedRequest.getHeaderNames()).thenReturn(null);
        assertFalse(InetAddr.isLocalhostAddress(mockedRequest));
        
        // test addresses
        when(mockedRequest.getHeaderNames()).thenReturn(toEnum("X-bla", "Authentication"));
        for (String positive : POSITIVES) {
            when(mockedRequest.getRemoteAddr()).thenReturn(positive);
            assertTrue(InetAddr.isLocalhostAddress(mockedRequest));
        }
        for (String negative : NEGATIVES) {
            when(mockedRequest.getRemoteAddr()).thenReturn(negative);
            assertFalse(InetAddr.isLocalhostAddress(mockedRequest));
        }
        
        // test forward headers
        when(mockedRequest.getRemoteAddr()).thenReturn("::1");
        for (String forward : FORWARD_NAMES) {
            when(mockedRequest.getHeaderNames()).thenReturn(toEnum("X-bla", forward, "Authentication"));
            assertFalse(InetAddr.isLocalhostAddress(mockedRequest));
        }
    }
    
    private Enumeration<String> toEnum(String... strings) {
        Vector<String> v = new Vector<>(strings.length);
        for (String s : strings) {
            v.add(s);
        }
        return v.elements();
    }

}
