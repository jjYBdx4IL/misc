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

import com.github.jjYBdx4IL.parser.midi.Key;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiMessage;

//CHECKSTYLE:OFF
/**
 * Parsed Midi Message.
 * 
 */
public abstract class PMidiMessage {
    public static final Pattern NOTE_PATTERN = Pattern.compile("([a-zA-Z]#?)(-?[0-9]+)"); 
    
    public static Key getKey(int note) {
        return Key.values()[note % Key.values().length];
    }

    public static int getOctave(int note) {
        return (note / 12) - 1;
    }

    public static String getNote(int note) {
        return getKey(note).toString() + getOctave(note);
    }

    public static int toIntNote(String note) {
        Matcher m = NOTE_PATTERN.matcher(note);
        if (!m.find()) {
            throw new IllegalArgumentException();
        }
        int octave = Integer.parseInt(m.group(2));
        Key key = Key.byName(m.group(1));
        return (octave + 1) * 12 + key.ordinal();
    }

    public abstract MidiMessage toMidiMessage();

}
