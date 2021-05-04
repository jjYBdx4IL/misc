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

import com.github.jjYBdx4IL.parser.midi.events.ChannelModeMsg;
import com.github.jjYBdx4IL.parser.midi.events.ChannelPressureMsg;
import com.github.jjYBdx4IL.parser.midi.events.EndOfExclusiveMsg;
import com.github.jjYBdx4IL.parser.midi.events.HasChannel;
import com.github.jjYBdx4IL.parser.midi.events.NoteOffMsg;
import com.github.jjYBdx4IL.parser.midi.events.NoteOnMsg;
import com.github.jjYBdx4IL.parser.midi.events.PMidiMessage;
import com.github.jjYBdx4IL.parser.midi.events.PitchBendChangeMsg;
import com.github.jjYBdx4IL.parser.midi.events.PolyphonicKeyPressureMsg;
import com.github.jjYBdx4IL.parser.midi.events.ProgramChangeMsg;
import com.github.jjYBdx4IL.parser.midi.events.SongPositionPointerMsg;
import com.github.jjYBdx4IL.parser.midi.events.SongSelectMsg;
import com.github.jjYBdx4IL.parser.midi.events.SystemExclusiveMsg;
import com.github.jjYBdx4IL.parser.midi.events.TimeCodeQuarterFrameMsg;
import com.github.jjYBdx4IL.parser.midi.events.TuneRequestMsg;

import java.util.Locale;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

//CHECKSTYLE:OFF
/**
 * https://www.midi.org/specifications/item/table-1-summary-of-midi-message
 * 
 * @author jjYBdx4IL
 */
public class MidiMessageParser {

    public static PMidiMessage parse(MidiMessage e) {

        if (!(e instanceof ShortMessage)) {
            if (e.getStatus() == SysexMessage.SYSTEM_EXCLUSIVE) {
                return new SystemExclusiveMsg(e);
            }
            return null;
        }

        ShortMessage m = (ShortMessage) e;

        if (m.getCommand() == 0xF0) { // status message
            switch (m.getStatus()) {
                case ShortMessage.MIDI_TIME_CODE:
                    return new TimeCodeQuarterFrameMsg(m);
                case ShortMessage.SONG_POSITION_POINTER:
                    return new SongPositionPointerMsg(m);
                case ShortMessage.SONG_SELECT:
                    return new SongSelectMsg(m);
                case ShortMessage.TUNE_REQUEST:
                    return new TuneRequestMsg();
                case ShortMessage.END_OF_EXCLUSIVE:
                    return new EndOfExclusiveMsg();
                default:
                    return null;
            }
        }

        // command message
        switch (m.getCommand()) {
            case ShortMessage.NOTE_OFF:
                return new NoteOffMsg(m);
            case ShortMessage.NOTE_ON:
                return new NoteOnMsg(m);
            case ShortMessage.POLY_PRESSURE:
                return new PolyphonicKeyPressureMsg(m);
            case ShortMessage.CONTROL_CHANGE:
                return ChannelModeMsg.createEvent(m);
            case ShortMessage.PROGRAM_CHANGE:
                return new ProgramChangeMsg(m);
            case ShortMessage.CHANNEL_PRESSURE:
                return new ChannelPressureMsg(m);
            case ShortMessage.PITCH_BEND:
                return new PitchBendChangeMsg(m);
            default:
                return null;
        }
    }

    public static String toString(MidiMessage e) {
        PMidiMessage me = parse(e);
        if (me == null) {
            return toString(e.getMessage());
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

    public static Sequence remapChannels(Sequence in, int targetChannel) throws InvalidMidiDataException {
        int[] channelMap = new int[16];
        for (int i = 0; i < channelMap.length; i++) {
            channelMap[i] = targetChannel;
        }
        return remapChannels(in, channelMap, false);
    }

    public static Sequence remapChannels(Sequence in, int[] channelMap, boolean isBitMask) throws InvalidMidiDataException {
        if (channelMap.length != 16) {
            throw new IllegalArgumentException();
        }
        Sequence out = new Sequence(in.getDivisionType(), in.getResolution());
        for (Track inTrack : in.getTracks()) {
            Track outTrack = out.createTrack();
            for (int i = 0; i < inTrack.size(); i++) {
                MidiEvent eventIn = inTrack.get(i);
                MidiMessage msgIn = eventIn.getMessage();
                if (msgIn instanceof MetaMessage) {
                    outTrack.add(new MidiEvent(msgIn, eventIn.getTick()));
                } else {
                    PMidiMessage parsedMsg = MidiMessageParser.parse(msgIn);
                    if (parsedMsg == null) {
                        throw new InvalidMidiDataException(
                            "failed to parse midi message: " + MidiMessageParser.toString(msgIn.getMessage()));
                    }
                    if (parsedMsg instanceof HasChannel) {
                        int channel = ((HasChannel) parsedMsg).getChannel();
                        if (!isBitMask) {
                            ((HasChannel) parsedMsg).setChannel(channelMap[channel]);
                            outTrack.add(new MidiEvent(parsedMsg.toMidiMessage(), eventIn.getTick()));
                        } else {
                            for (int j=0; j<16; j++) {
                                if ((channelMap[channel] & (1 << j))==0) {
                                    continue;
                                }
                                ((HasChannel) parsedMsg).setChannel(j);
                                outTrack.add(new MidiEvent(parsedMsg.toMidiMessage(), eventIn.getTick()));
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    public static Sequence compactChannels(Sequence in) throws InvalidMidiDataException {
        int[] channelMap = getChannelsInUse(in);
        int targetChannel = 0;
        for (int i = 0; i < channelMap.length; i++) {
            if (channelMap[i] != 0) {
                channelMap[i] = targetChannel;
                targetChannel++;
            }
        }
        return remapChannels(in, channelMap, false);
    }
    
    public static Sequence compactAndDuplicateChannels(Sequence in, int maxDuplFactor) throws InvalidMidiDataException {
        int[] channelMap = getChannelsInUse(in);
        int channelCount = 0;
        for (int i = 0; i < channelMap.length; i++) {
            if (channelMap[i] != 0) {
                channelCount++;
            }
        }
        int duplFactor = Math.min(16 / channelCount, maxDuplFactor > 0 ? maxDuplFactor : Integer.MAX_VALUE);
        int targetChannel = 0;
        for (int i = 0; i < channelMap.length; i++) {
            if (channelMap[i] != 0) {
                channelMap[i] = 0;
                for (int j = 0; j < duplFactor; j++) {
                    channelMap[i] |= (1 << targetChannel);
                    targetChannel++;
                }
            }
        }
        return remapChannels(in, channelMap, true);
    }

    public static int getMaxChannelInUse(Sequence in) throws InvalidMidiDataException {
        int[] channelMap = getChannelsInUse(in);
        int max = -1;
        for (int i = 0; i < channelMap.length; i++) {
            if (channelMap[i] != 0) {
                max = i;
            }
        }
        return max;
    }

    public static int[] getChannelsInUse(Sequence in) throws InvalidMidiDataException {
        int[] channels = new int[16];
        for (Track inTrack : in.getTracks()) {
            for (int i = 0; i < inTrack.size(); i++) {
                MidiEvent eventIn = inTrack.get(i);
                MidiMessage msgIn = eventIn.getMessage();
                if (msgIn instanceof MetaMessage) {
                } else {
                    PMidiMessage parsedMsg = MidiMessageParser.parse(msgIn);
                    if (parsedMsg == null) {
                        throw new InvalidMidiDataException(
                            "failed to parse midi message: " + MidiMessageParser.toString(msgIn.getMessage()));
                    }
                    if (parsedMsg instanceof HasChannel) {
                        int channel = ((HasChannel) parsedMsg).getChannel();
                        channels[channel]++;
                    }
                }
            }
        }
        return channels;
    }
}
