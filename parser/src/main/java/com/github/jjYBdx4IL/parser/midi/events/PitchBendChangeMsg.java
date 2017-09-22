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

//CHECKSTYLE:OFF
public class PitchBendChangeMsg extends PMidiMessage implements HasChannel {

    private int channel;
    private final int pitchChange;

    /**
     * PitchBendChangeEvent.
     * 
     * @param msg ShortMessage
     */
    public PitchBendChangeMsg(ShortMessage msg) {
        this.channel = msg.getChannel();
        this.pitchChange = (((msg.getData2() & 0x3f) << 7) | (msg.getData1() & 0x7f))
            + ((msg.getData2() & 0x40) == 0x40 ? -8192 : 0);
    }
    
    public PitchBendChangeMsg(int channel, int pitchChange) throws InvalidMidiDataException {
        if (channel < 0 || channel > 15 || pitchChange < -8192 || pitchChange > 8191) {
            throw new InvalidMidiDataException();
        }
        this.channel = channel;
        this.pitchChange = pitchChange;
    }

    public int getPitchChange() {
        return pitchChange;
    }

    public int getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PitchBendChangeMsg [channel=");
        builder.append(channel);
        builder.append(", pitchChange=");
        builder.append(pitchChange);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public MidiMessage toMidiMessage() {
        try {
            return create(channel, pitchChange);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static ShortMessage create(int channel, int pitchChange) throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.PITCH_BEND, channel,
            pitchChange & 0x7F,
            ((pitchChange & 0x1f80) >> 7) + (pitchChange < 0 ? 0x40 : 0));
    }

    @Override
    public void setChannel(int channel) throws InvalidMidiDataException {
        if (channel < 0 || channel > 15) {
            throw new InvalidMidiDataException("invalid channel index " + channel);
        }
        this.channel = channel;
    }

}
