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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class WakeOnLAN {

    private static final Logger LOG = LoggerFactory.getLogger(WakeOnLAN.class);

    public static final int DEFAULT_WOL_PORT = 9;

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                if (hex[i].length() != 2) {
                    throw new IllegalArgumentException("Invalid hex digit in MAC address.");
                }
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

    private final int wolPort = DEFAULT_WOL_PORT;
    private final DatagramPacket[] packets;

    public WakeOnLAN(String ipStr, String macStr) throws UnknownHostException {
        packets = new DatagramPacket[]{createPacket(InetAddress.getByName(ipStr), getMacBytes(macStr))};
    }

    public WakeOnLAN(String ifaceDisplayName) throws SocketException {
        packets = createPackets(ifaceDisplayName);
    }

    private DatagramPacket createPacket(InetAddress addr, byte[] macBytes) {
        byte[] bytes = new byte[6 + 16 * macBytes.length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }

        return new DatagramPacket(bytes, bytes.length, addr, wolPort);
    }

    private DatagramPacket[] createPackets(String ifaceDisplayName) throws SocketException {
        List<DatagramPacket> packetList = new ArrayList<>();
        for (NetworkInterface nif : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!nif.getDisplayName().toLowerCase().equals(ifaceDisplayName.toLowerCase())) {
                continue;
            }
            byte[] macBytes = nif.getHardwareAddress();
            if (macBytes == null) {
                throw new IllegalArgumentException("iface " + ifaceDisplayName + " has no hardware address (mac)");
            }
            for (InetAddress addr : Collections.list(nif.getInetAddresses())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("adding wol target: " + addr.getHostAddress() + " / " + Hex.encodeHexString(macBytes));
                }
                packetList.add(createPacket(addr, macBytes));
            }
        }
        if (packetList.isEmpty()) {
            throw new IllegalArgumentException("iface " + ifaceDisplayName + " not found or has no ip address");
        }
        if (packetList.size() > 1) {
            LOG.info("iface " + ifaceDisplayName + " has multiple ip addresses, will send wol packets to all of them");
        }
        return packetList.toArray(new DatagramPacket[]{});
    }

    public void send() throws SocketException, IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            for (DatagramPacket packet : packets) {
                socket.send(packet);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Wake-on-LAN packet sent to port " + wolPort);
        }
    }

}
