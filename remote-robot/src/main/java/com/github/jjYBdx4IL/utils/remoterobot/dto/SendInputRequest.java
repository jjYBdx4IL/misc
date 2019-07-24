/*
 * Copyright © 2019 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.remoterobot.dto;

import com.github.jjYBdx4IL.utils.remoterobot.Utils;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SendInputRequest extends Request implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final Set<Integer> MOD_KEYS = new HashSet<>();

    static {
        MOD_KEYS.add(KeyEvent.VK_ALT);
        MOD_KEYS.add(KeyEvent.VK_SHIFT);
        MOD_KEYS.add(KeyEvent.VK_CONTROL);
    }

    public long delayMs;
    public int[] keycodes;

    public SendInputRequest() {
    }

    /**
     * Create an instance.
     * 
     * @param seq
     *            the request sequence id
     * @param delayMs
     *            delay (in ms) between key events
     * @param keycodes
     *            the keys to press
     * @return the instance
     */
    public static SendInputRequest create(long seq, long delayMs, int... keycodes) {
        SendInputRequest request = new SendInputRequest();
        request.seq = seq;
        request.delayMs = delayMs;
        request.keycodes = keycodes;
        return request;
    }

    /**
     * Handle this request.
     * 
     * @see com.github.jjYBdx4IL.utils.remoterobot.RobotClient#sendInput(long,int...)
     * 
     * @param robot
     *            the robot
     */
    public void handle(Robot robot) {
        Set<Integer> pressed = new HashSet<>();
        boolean first = true;
        for (int keycode : keycodes) {
            if (!first) {
                Utils.delay(delayMs);
            }
            first = false;

            if (pressed.remove(keycode)) {
                robot.keyRelease(keycode);
            } else {
                pressed.add(keycode);
                robot.keyPress(keycode);
            }
        }

        // add missing key releases for regular keys
        for (int keycode : pressed) {
            if (MOD_KEYS.contains(keycode)) {
                continue;
            }
            Utils.delay(delayMs);
            robot.keyRelease(keycode);
        }

        // add missing key releases for mod keys
        for (int keycode : pressed) {
            if (!MOD_KEYS.contains(keycode)) {
                continue;
            }
            Utils.delay(delayMs);
            robot.keyRelease(keycode);
        }
    }
}
