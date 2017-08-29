/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.osdapp;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
class DeliverMessageUpdate implements Runnable {
    private static final Map<JLabel, DisplayLevel> LEVELS = new HashMap<>();

    /**
     * Avoid thread synchronization issues by using a fire-and-forget pattern. The
     * {@link #schedule(String,DisplayLevel,JLabel)} method instantiates an instance of
     * {@link DisplayMessageUpdate} that is only written to during initialization and only read once, ie.
     * inside the AWT event loop.
     *
     * @param msgPart
     * @param displayLvl
     * @param label
     */
    public static void deliver(String msgPart, DisplayLevel displayLvl, JLabel label) {
        EventQueue.invokeLater(new DeliverMessageUpdate(msgPart, displayLvl, label));
    }
    private final String messagePart;
    private final DisplayLevel displayLevel;
    private final JLabel labelToUpdate;

    private DeliverMessageUpdate(String msgPart, DisplayLevel displayLvl, JLabel label) {
        this.messagePart = msgPart;
        this.displayLevel = displayLvl;
        this.labelToUpdate = label;
    }

    private String getDisplayMessage() {
        return messagePart;
    }

    private Color getDisplayColor() {
        return displayLevel.getColor();
    }

    @Override
    public void run() {
        labelToUpdate.setText(getDisplayMessage());
        labelToUpdate.setForeground(getDisplayColor());

        LEVELS.put(labelToUpdate, displayLevel);
        OSDApp.setTrayLevel(DisplayLevel.max(LEVELS.values()));
    }
    
}
