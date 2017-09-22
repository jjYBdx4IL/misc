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
import javax.sound.midi.ShortMessage;

//CHECKSTYLE:OFF
public abstract class ChannelModeMsg extends PMidiMessage implements HasChannel {

    int channel;
    
    protected ChannelModeMsg(ShortMessage msg) {
        channel = msg.getChannel();
    }
    
    @Override
    public int getChannel() {
        return channel;
    }
    
    @Override
    public final void setChannel(int channel) throws InvalidMidiDataException {
        if (channel < 0 || channel > 15) {
            throw new InvalidMidiDataException("invalid channel index " + channel);
        }
        this.channel = channel;
    }

    /**
     * createEvent.
     * 
     * @param msg ShortMessage
     * @return the MidiEvent
     */
    public static ChannelModeMsg createEvent(ShortMessage msg) {
        if (msg.getData1() < 120) {
            return new ControlChangeMsg(msg);
        }

        switch (msg.getData1()) {
            case 120:
                return new AllSoundOffMsg(msg);
            case 121:
                return new ResetAllControllersMsg(msg);
            case 122:
                switch (msg.getData2()) {
                    case 0:
                        return new LocalControlOffMsg(msg);
                    case 127:
                        return new LocalControlOnMsg(msg);
                    default:
                        return null;
                }
            case 123:
                return new AllNotesOffMsg(msg);
            case 124:
                return new OmniModeOffMsg(msg);
            case 125:
                return new OmniModeOnMsg(msg);
            case 126:
                return new MonoModeOnMsg(msg);
            case 127:
                return new PolyModeOnMsg(msg);
            default:
                return null;
        }
    }

}
