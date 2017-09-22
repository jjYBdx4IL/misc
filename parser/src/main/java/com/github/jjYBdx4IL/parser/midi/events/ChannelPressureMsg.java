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
package com.github.jjYBdx4IL.parser.midi.events;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class ChannelPressureMsg extends PMidiMessage implements HasChannel {

    private int channel;
    private final int pressure;

    public ChannelPressureMsg(ShortMessage msg) {
        this.channel = msg.getChannel();
        this.pressure = msg.getData1();
    }

    public int getChannel() {
        return channel;
    }

    public int getPressure() {
        return pressure;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChannelPressureMsg [channel=");
        builder.append(channel);
        builder.append(", pressure=");
        builder.append(pressure);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public MidiMessage toMidiMessage() {
        try {
            return create(channel, pressure);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static ShortMessage create(int channel, int pressure) throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.CHANNEL_PRESSURE, channel, pressure);
    }

    @Override
    public void setChannel(int channel) throws InvalidMidiDataException {
        if (channel < 0 || channel > 15) {
            throw new InvalidMidiDataException("invalid channel index " + channel);
        }
        this.channel = channel;
    }

}
