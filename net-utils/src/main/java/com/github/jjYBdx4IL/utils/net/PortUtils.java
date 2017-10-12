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

import java.net.InetSocketAddress;
import java.net.Socket;

//CHECKSTYLE:OFF
public class PortUtils {
    
    public static final int DEFAULT_WAIT_MILLIS = 1000;
    
    public static boolean isOpen(int port) {
        return isOpen("localhost", port, DEFAULT_WAIT_MILLIS);
    }
    
    public static boolean isOpen(String host, int port, int millis) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), millis);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
