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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

public class KeepAlive implements ActionListener {

    private final Robot robot;
    private final int keycode;
    private final Timer timer;

    /**
     * The constructor.
     * 
     * @throws AWTException
     *             on error
     */
    public KeepAlive() throws AWTException {
        robot = new Robot();
        keycode = KeyEvent.VK_F24;
        timer = new Timer(59000, this);
        timer.setInitialDelay(0);
    }

    public void ping() {
        robot.keyPress(keycode);
        robot.keyRelease(keycode);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ping();
    }
}
