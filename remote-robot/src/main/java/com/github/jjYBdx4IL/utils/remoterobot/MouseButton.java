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
package com.github.jjYBdx4IL.utils.remoterobot;

import java.awt.event.InputEvent;

public enum MouseButton {

    LEFT(InputEvent.BUTTON1_DOWN_MASK, InputEvent.BUTTON1_MASK), MIDDLE(InputEvent.BUTTON2_DOWN_MASK,
        InputEvent.BUTTON2_MASK), RIGHT(InputEvent.BUTTON3_DOWN_MASK, InputEvent.BUTTON3_MASK);

    final int pressCode;
    final int releaseCode;

    MouseButton(int pressCode, int releaseCode) {
        this.pressCode = pressCode;
        this.releaseCode = releaseCode;
    }
}
