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
public class NoteOnMsg extends PMidiMessage implements HasChannel {

    private int channel;
    private final int note;
    private final int velocity;

    /**
     * NoteOffEvent.
     *
     * @param msg
     *            ShortMessage
     */
    public NoteOnMsg(ShortMessage msg) {
        this.channel = msg.getChannel();
        this.note = msg.getData1();
        this.velocity = msg.getData2();
    }

    public int getChannel() {
        return channel;
    }

    public int getNote() {
        return note;
    }

    public int getVelocity() {
        return velocity;
    }

    @Override
    public MidiMessage toMidiMessage() {
        try {
            return create(channel, note, velocity);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static ShortMessage create(int channel, String note, int velocity) throws InvalidMidiDataException {
        return create(channel, PMidiMessage.toIntNote(note), velocity);
    }
    
    public static ShortMessage create(int channel, int note, int velocity) throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.NOTE_ON, channel, note, velocity);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NoteOnMsg [channel=");
        builder.append(channel);
        builder.append(", note=");
        builder.append(note);
        builder.append("(");
        builder.append(getNote(note));
        builder.append("), velocity=");
        builder.append(velocity);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void setChannel(int channel) throws InvalidMidiDataException {
        if (channel < 0 || channel > 15) {
            throw new InvalidMidiDataException("invalid channel index " + channel);
        }
        this.channel = channel;
    }

}
