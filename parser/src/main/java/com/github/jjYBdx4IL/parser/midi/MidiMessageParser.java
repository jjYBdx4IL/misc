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
package com.github.jjYBdx4IL.parser.midi;

import java.util.Locale;

import javax.sound.midi.MidiMessage;

//CHECKSTYLE:OFF
/**
 * https://www.midi.org/specifications/item/table-1-summary-of-midi-message
 * 
 * @author jjYBdx4IL
 */
public class MidiMessageParser {

    public static MidiEvent parse(MidiMessage e) {
        byte[] b = e.getMessage();
        byte l = (byte) b.length;

        byte b0u = (byte) ((b[0] & 0xF0) >> 4); // byte 0, bits 4-7
        byte c = (byte) (b[0] & 0x0F); // byte 0, bits 0-3 (channel)
        byte b1h = (byte) ((b[1] & 0x80) >> 7); // byte 1, highest bit
        byte b2h = l > 2 ? (byte) ((b[2] & 0x80) >> 7) : -1; // byte 2, highest
                                                             // bit

        if (l == 3 && b0u == 8 && b1h == 0 && b2h == 0) {
            return new NoteOffEvent(c, b[1], b[2]);
        }
        if (l == 3 && b0u == 9 && b1h == 0 && b2h == 0) {
            return new NoteOnEvent(c, b[1], b[2]);
        }
        if (l == 3 && b0u == 10 && b1h == 0 && b2h == 0) {
            return new PolyphonicKeyPressureEvent(c, b[1], b[2]);
        }
        if (l == 3 && b0u == 11 && b1h == 0 && b2h == 0) {
            if (b[1] < 120) {
                return new ControlChangeEvent(c, b[1], b[2]);
            }
            if (b[1] == 120 && b[2] == 0) {
                return new AllSoundOffEvent(c);
            }
            if (b[1] == 121) {
                return new ResetAllControllersEvent(c, b[2]);
            }
            if (b[1] == 122 && b[2] == 0) {
                return new LocalControlOffEvent(c);
            }
            if (b[1] == 122 && b[2] == 127) {
                return new LocalControlOnEvent(c);
            }
            if (b[1] == 123 && b[2] == 0) {
                return new AllNotesOffEvent(c);
            }
            if (b[1] == 124 && b[2] == 0) {
                return new OmniModeOffEvent(c);
            }
            if (b[1] == 125 && b[2] == 0) {
                return new OmniModeOnEvent(c);
            }
            if (b[1] == 126) {
                return new MonoModeOnEvent(c, b[2]);
            }
            if (b[1] == 127 && b[2] == 0) {
                return new PolyModeOnEvent(c);
            }
        }
        if (l == 2 && b0u == 12 && b1h == 0) {
            return new ProgramChangeEvent(c, b[1]);
        }
        if (l == 2 && b0u == 13 && b1h == 0) {
            return new ChannelPressureEvent(c, b[1]);
        }
        if (l == 3 && b0u == 14 && b1h == 0 && b2h == 0) {
            return new PitchBendChangeEvent(c, (((b[2] & 0x3f) << 7) | (b[1] & 0x7f)) + ((b[2] & 0x40) == 0x40 ? -8192 : 0)); 
        }
        if (l > 1 && b0u == 15 && c == 0) {
            return new SystemExclusiveEvent(b);
        }
        if (l == 2 && b0u == 15 && c == 1 && b1h == 0) {
            return new TimeCodeQuarterFrameEvent((byte) ((b[1] & 0x70) >> 4), (byte) (b[1] & 0x0f));
        }
        if (l == 3 && b0u == 15 && c == 2 && b1h == 0 && b2h == 0) {
            return new SongPositionPointerEvent(b[2] * 128 + b[1]);
        }
        if (l == 2 && b0u == 15 && c == 3 && b1h == 0) {
            return new SongSelectEvent(b[1]);
        }
        if (l == 1 && b0u == 15 && c == 6) {
            return new TuneRequestEvent();
        }
        if (l == 1 && b0u == 15 && c == 7) {
            return new EndOfExclusiveEvent();
        }
        return null;
    }

    public static String toString(MidiMessage e) {
        byte[] b = e.getMessage();
        MidiEvent me = parse(e);
        if (me == null) {
            return toString(b);
        }
        return me.toString();
    }

    public static String toString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(String.format(Locale.ROOT, "0x%02x", b[i]));
        }
        return sb.toString();
    }

    public static class MidiEvent {
        public static final String[] KEY_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

        public static Key getKey(byte note) {
            return Key.values()[note % KEY_NAMES.length];
        }

        public static int getOctave(byte note) {
            return (note / KEY_NAMES.length) - 1;
        }

        public static String getNote(byte note) {
            return getKey(note).toString() + getOctave(note);
        }
    }
    
    public static class EndOfExclusiveEvent extends MidiEvent {

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("EndOfExclusiveEvent []");
            return builder.toString();
        }

    }

    public static class TuneRequestEvent extends MidiEvent {

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TuneRequestEvent []");
            return builder.toString();
        }
        
    }

    public static class SongPositionPointerEvent extends MidiEvent {
        private final int midiBeats;

        public SongPositionPointerEvent(int midiBeats) {
            this.midiBeats = midiBeats;
        }

        public int getMidiBeats() {
            return midiBeats;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("SongPositionPointerEvent [midiBeats=");
            builder.append(midiBeats);
            builder.append("]");
            return builder.toString();
        }

    }

    public static class SongSelectEvent extends MidiEvent {
        private final byte song;

        public SongSelectEvent(byte song) {
            this.song = song;
        }

        public byte getSong() {
            return song;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("SongSelectEvent [song=");
            builder.append(song);
            builder.append("]");
            return builder.toString();
        }

    }

    public static class TimeCodeQuarterFrameEvent extends MidiEvent {
        private final byte messageType, value;

        public TimeCodeQuarterFrameEvent(byte messageType, byte value) {
            this.messageType = messageType;
            this.value = value;
        }

        public byte getMessageType() {
            return messageType;
        }

        public byte getValue() {
            return value;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TimeCodeQuarterFrame [messageType=");
            builder.append(messageType);
            builder.append(", value=");
            builder.append(value);
            builder.append("]");
            return builder.toString();
        }

    }

    public static class SystemExclusiveEvent extends MidiEvent {
        private final byte[] data;

        public SystemExclusiveEvent(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("SystemExclusiveEvent [data=");
            for (int i = 0; i < data.length; i++) {
                if (i > 0) {
                    builder.append(" ");
                }
                builder.append(String.format(Locale.ROOT, "0x%02x", data[i]));
            }
            builder.append("]");
            return builder.toString();
        }

    }

    public static class PitchBendChangeEvent extends MidiEvent {
        private final byte channel;
        private final int pitchChange;

        public PitchBendChangeEvent(byte channel, int pitchChange) {
            this.channel = channel;
            this.pitchChange = pitchChange;
        }

        public int getPitchChange() {
            return pitchChange;
        }

        public byte getChannel() {
            return channel;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("PitchBendChangeEvent [channel=");
            builder.append(channel);
            builder.append(", pitchChange=");
            builder.append(pitchChange);
            builder.append("]");
            return builder.toString();
        }

    }

    public static class NoteOnEvent extends MidiEvent {
        private final byte channel, note, velocity;

        public NoteOnEvent(byte channel, byte note, byte velocity) {
            this.channel = channel;
            this.note = note;
            this.velocity = velocity;
        }

        public byte getChannel() {
            return channel;
        }

        public byte getNote() {
            return note;
        }

        public byte getVelocity() {
            return velocity;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("NoteOnEvent [channel=");
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
    }

    public static class NoteOffEvent extends MidiEvent {
        private final byte channel, note, velocity;

        public NoteOffEvent(byte channel, byte note, byte velocity) {
            this.channel = channel;
            this.note = note;
            this.velocity = velocity;
        }

        public byte getChannel() {
            return channel;
        }

        public byte getNote() {
            return note;
        }

        public byte getVelocity() {
            return velocity;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("NoteOffEvent [note=");
            builder.append(note);
            builder.append("(");
            builder.append(getNote(note));
            builder.append("), velocity=");
            builder.append(velocity);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class PolyphonicKeyPressureEvent extends MidiEvent {
        private final byte channel, note, pressure;

        public PolyphonicKeyPressureEvent(byte channel, byte note, byte pressure) {
            this.channel = channel;
            this.note = note;
            this.pressure = pressure;
        }

        public byte getChannel() {
            return channel;
        }

        public byte getNote() {
            return note;
        }

        public byte getPressure() {
            return pressure;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("PolyphonicKeyPressureEvent [channel=");
            builder.append(channel);
            builder.append(", note=");
            builder.append(note);
            builder.append("(");
            builder.append(getNote(note));
            builder.append("), pressure=");
            builder.append(pressure);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class ChannelPressureEvent extends MidiEvent {
        private final byte channel, pressure;

        public ChannelPressureEvent(byte channel, byte pressure) {
            this.channel = channel;
            this.pressure = pressure;
        }

        public byte getChannel() {
            return channel;
        }

        public byte getPressure() {
            return pressure;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ChannelPressureEvent [channel=");
            builder.append(channel);
            builder.append(", pressure=");
            builder.append(pressure);
            builder.append("]");
            return builder.toString();
        }

    }

    public static class ControlChangeEvent extends MidiEvent {
        private final byte channel, controller, value;

        public ControlChangeEvent(byte channel, byte controller, byte value) {
            this.channel = channel;
            this.controller = controller;
            this.value = value;
        }

        public byte getChannel() {
            return channel;
        }

        public byte getController() {
            return controller;
        }

        public byte getValue() {
            return value;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ControlChangeEvent [channel=");
            builder.append(channel);
            builder.append(", controller=");
            builder.append(controller);
            builder.append(", value=");
            builder.append(value);
            builder.append("]");
            return builder.toString();
        }

    }

    public static class AllSoundOffEvent extends MidiEvent {
        private final byte channel;

        public AllSoundOffEvent(byte channel) {
            this.channel = channel;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("AllSoundOffEvent [channel=");
            builder.append(channel);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class ResetAllControllersEvent extends MidiEvent {
        private final byte channel, x;

        public ResetAllControllersEvent(byte channel, byte x) {
            this.channel = channel;
            this.x = x;
        }

        public byte getChannel() {
            return channel;
        }

        public byte getX() {
            return x;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ResetAllControllersEvent [channel=");
            builder.append(channel);
            builder.append(", x=");
            builder.append(x);
            builder.append("]");
            return builder.toString();
        }

    }

    public static class LocalControlOffEvent extends MidiEvent {
        private final byte channel;

        public LocalControlOffEvent(byte channel) {
            this.channel = channel;
        }

        public byte getChannel() {
            return channel;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("LocalControlOffEvent [channel=");
            builder.append(channel);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class LocalControlOnEvent extends MidiEvent {
        private final byte channel;

        public LocalControlOnEvent(byte channel) {
            this.channel = channel;
        }

        public byte getChannel() {
            return channel;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("LocalControlOnEvent [channel=");
            builder.append(channel);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class AllNotesOffEvent extends MidiEvent {
        private final byte channel;

        public AllNotesOffEvent(byte channel) {
            this.channel = channel;
        }

        public byte getChannel() {
            return channel;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("AllNotesOffEvent [channel=");
            builder.append(channel);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class OmniModeOffEvent extends MidiEvent {
        private final byte channel;

        public OmniModeOffEvent(byte channel) {
            this.channel = channel;
        }

        public byte getChannel() {
            return channel;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("OmniModeOffEvent [channel=");
            builder.append(channel);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class OmniModeOnEvent extends MidiEvent {
        private final byte channel;

        public OmniModeOnEvent(byte channel) {
            this.channel = channel;
        }

        public byte getChannel() {
            return channel;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("OmniModeOnEvent [channel=");
            builder.append(channel);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class PolyModeOnEvent extends MidiEvent {
        private final byte channel;

        public PolyModeOnEvent(byte channel) {
            this.channel = channel;
        }

        public byte getChannel() {
            return channel;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("PolyModeOnEvent [channel=");
            builder.append(channel);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class MonoModeOnEvent extends MidiEvent {
        private final byte channel, channels;

        public MonoModeOnEvent(byte channel, byte channels) {
            this.channel = channel;
            this.channels = channels;
        }

        public byte getChannel() {
            return channel;
        }

        public byte getChannels() {
            return channels;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("MonoModeOnEvent [channel=");
            builder.append(channel);
            builder.append(", channels=");
            builder.append(channels);
            builder.append("]");
            return builder.toString();
        }

    }

    public static class ProgramChangeEvent extends MidiEvent {
        private final byte channel, program;

        public ProgramChangeEvent(byte channel, byte program) {
            this.channel = channel;
            this.program = program;
        }

        public byte getChannel() {
            return channel;
        }

        public byte getProgram() {
            return program;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ProgramChangeEvent [channel=");
            builder.append(channel);
            builder.append(", program=");
            builder.append(program);
            builder.append("]");
            return builder.toString();
        }

    }

}
