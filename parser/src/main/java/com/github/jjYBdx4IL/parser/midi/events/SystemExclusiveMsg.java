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

import java.util.Locale;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

public class SystemExclusiveMsg extends SystemCommonMsg {
    
    private final byte[] data;

    public SystemExclusiveMsg(MidiMessage msg) {
        this.data = msg.getMessage();
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SystemExclusiveMsg [data=");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                builder.append(" ");
            }
            builder.append(String.format(Locale.ROOT, "0x%02x", data[i]));
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public MidiMessage toMidiMessage() {
        try {
            return create(data);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static SysexMessage create(byte[] data) throws InvalidMidiDataException {
        return new SysexMessage(data, data.length);
    }

}
