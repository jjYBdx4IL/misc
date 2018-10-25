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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AddressUtils {

    public static InetAddress getNonLocalHostIPAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
                .hasMoreElements();) {
                NetworkInterface iface = ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress() && inetAddr.getHostAddress().startsWith("192.168.")) {
                            // Found non-loopback site-local address. Return it
                            // immediately...
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily
                            // site-local.
                            // Store it as a candidate to be returned if
                            // site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback
                            // non-site-local addresses as candidates,
                            // only the first. For subsequent iterations,
                            // candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other
                // non-loopback address.
                // Server might have a non-site-local address assigned to its
                // NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
        } catch (SocketException e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
        // At this point, we did not find a non-loopback address.
        throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
    }

    /**
     * Returns true only iff none of the ip address resolved are associated with
     * the local machine including its public ip addresses.
     * 
     * @param hostname the hostname or literal IP address to check
     * @return true iff none of the ip address resolved are associated with the
     *         local machine including its public ip addresses.
     * @throws UnknownHostException
     *             if the given hostname cannot be resolved
     */
    public static boolean isSimpleNonLocalAddress(String hostname) throws UnknownHostException {
        if (hostname == null || hostname.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (InetAddress addr : InetAddress.getAllByName(hostname)) {
            if (addr.isLoopbackAddress() || addr.isSiteLocalAddress() || addr.isAnyLocalAddress()
                || addr.isLinkLocalAddress() || addr.isMCGlobal() || addr.isMCLinkLocal()
                || addr.isMCNodeLocal() || addr.isMCOrgLocal() || addr.isMCSiteLocal()
                || addr.isMulticastAddress()) {
                return false;
            }
        }
        return true;
    }

    private AddressUtils() {
    }

}
