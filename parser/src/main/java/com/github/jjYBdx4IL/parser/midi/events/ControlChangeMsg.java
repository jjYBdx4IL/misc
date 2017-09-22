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

public class ControlChangeMsg extends ChannelModeMsg {
    private final int controller;
    private final int value;

    /**
     * ControlChangeEvent.
     * 
     * @param msg
     *            ShortMessage
     */
    public ControlChangeMsg(ShortMessage msg) {
        super(msg);
        this.controller = msg.getData1();
        this.value = msg.getData2();
    }

    public int getController() {
        return controller;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ControlChangeMsg [channel=");
        builder.append(channel);
        builder.append(", controller=");
        builder.append(controller);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public MidiMessage toMidiMessage() {
        try {
            return create(channel, controller, value);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static ShortMessage create(int channel, int controller, int value) throws InvalidMidiDataException {
        return new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, controller, value);
    }

}
