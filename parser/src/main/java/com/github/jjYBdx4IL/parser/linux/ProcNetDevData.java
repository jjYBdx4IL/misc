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
package com.github.jjYBdx4IL.parser.linux;

//CHECKSTYLE:OFF
/**
 * DTO representing one line in /proc/net/dev.
 *
 * @author Github jjYBdx4IL Projects
 */
public class ProcNetDevData {
    // CHECKSTYLE IGNORE .* FOR NEXT 2 LINES
    //Inter-|   Receive                                                |  Transmit
    // face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed

    private final String device;
    private final long receiveBytes;
    private final long receivePackets;
    private final long receiveErrs;
    private final long receiveDrop;
    private final long receiveFifo;
    private final long receiveFrame;
    private final long receiveCompressed;
    private final long receiveMulticast;
    private final long transmitBytes;
    private final long transmitPackets;
    private final long transmitErrs;
    private final long transmitDrop;
    private final long transmitFifo;
    private final long transmitColls;
    private final long transmitCarrier;
    private final long transmitCompressed;

    // CHECKSTYLE IGNORE .* FOR NEXT 5 LINES
    ProcNetDevData(String device,
            Long receiveBytes, Long receivePackets, Long receiveErrs, Long receiveDrop,
            Long receiveFifo, Long receiveFrame, Long receiveCompressed, Long receiveMulticast,
            Long transmitBytes, Long transmitPackets, Long transmitErrs, Long transmitDrop,
            Long transmitFifo, Long transmitColls, Long transmitCarrier, Long transmitCompressed) {
        this.device = device;
        this.transmitDrop = transmitDrop;
        this.transmitFifo = transmitFifo;
        this.transmitColls = transmitColls;
        this.receiveBytes = receiveBytes;
        this.receiveDrop = receiveDrop;
        this.receiveFifo = receiveFifo;
        this.receiveMulticast = receiveMulticast;
        this.transmitBytes = transmitBytes;
        this.transmitPackets = transmitPackets;
        this.receiveFrame = receiveFrame;
        this.receivePackets = receivePackets;
        this.receiveErrs = receiveErrs;
        this.receiveCompressed = receiveCompressed;
        this.transmitErrs = transmitErrs;
        this.transmitCarrier = transmitCarrier;
        this.transmitCompressed = transmitCompressed;
    }

    /**
     * @return the device
     */
    public String getDevice() {
        return device;
    }

    /**
     * @return the receiveBytes
     */
    public long getReceiveBytes() {
        return receiveBytes;
    }

    /**
     * @return the receivePackets
     */
    public long getReceivePackets() {
        return receivePackets;
    }

    /**
     * @return the receiveErrs
     */
    public long getReceiveErrs() {
        return receiveErrs;
    }

    /**
     * @return the receiveDrop
     */
    public long getReceiveDrop() {
        return receiveDrop;
    }

    /**
     * @return the receiveFifo
     */
    public long getReceiveFifo() {
        return receiveFifo;
    }

    /**
     * @return the receiveFrame
     */
    public long getReceiveFrame() {
        return receiveFrame;
    }

    /**
     * @return the receiveCompressed
     */
    public long getReceiveCompressed() {
        return receiveCompressed;
    }

    /**
     * @return the receiveMulticast
     */
    public long getReceiveMulticast() {
        return receiveMulticast;
    }

    /**
     * @return the transmitBytes
     */
    public long getTransmitBytes() {
        return transmitBytes;
    }

    /**
     * @return the transmitPackets
     */
    public long getTransmitPackets() {
        return transmitPackets;
    }

    /**
     * @return the transmitErrs
     */
    public long getTransmitErrs() {
        return transmitErrs;
    }

    /**
     * @return the transmitDrop
     */
    public long getTransmitDrop() {
        return transmitDrop;
    }

    /**
     * @return the transmitFifo
     */
    public long getTransmitFifo() {
        return transmitFifo;
    }

    /**
     * @return the transmitColls
     */
    public long getTransmitColls() {
        return transmitColls;
    }

    /**
     * @return the transmitCarrier
     */
    public long getTransmitCarrier() {
        return transmitCarrier;
    }

    /**
     * @return the transmitCompressed
     */
    public long getTransmitCompressed() {
        return transmitCompressed;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DevData [");
        builder.append("device=");
        builder.append(device);
        builder.append(", receiveBytes=");
        builder.append(receiveBytes);
        builder.append(", receiveCompressed=");
        builder.append(receiveCompressed);
        builder.append(", receiveDrop=");
        builder.append(receiveDrop);
        builder.append(", receiveErrs=");
        builder.append(receiveErrs);
        builder.append(", receiveFifo=");
        builder.append(receiveFifo);
        builder.append(", receiveFrame=");
        builder.append(receiveFrame);
        builder.append(", receiveMulticast=");
        builder.append(receiveMulticast);
        builder.append(", receivePackets=");
        builder.append(receivePackets);
        builder.append(", transmitBytes=");
        builder.append(transmitBytes);
        builder.append(", transmitCarrier=");
        builder.append(transmitCarrier);
        builder.append(", transmitColls=");
        builder.append(transmitColls);
        builder.append(", transmitCompressed=");
        builder.append(transmitCompressed);
        builder.append(", transmitDrop=");
        builder.append(transmitDrop);
        builder.append(", transmitErrs=");
        builder.append(transmitErrs);
        builder.append(", transmitFifo=");
        builder.append(transmitFifo);
        builder.append(", transmitPackets=");
        builder.append(transmitPackets);
        builder.append("]");
        return builder.toString();
    }
}
