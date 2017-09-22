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

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class NoteOnEventTest {

    @Test
    public void testCreate() throws InvalidMidiDataException {
        ShortMessage msg = NoteOnMsg.create(1, "C4", 120);
        assertArrayEquals(new byte[] {(byte) 0x91, 60, 120}, msg.getMessage());
    }
}
