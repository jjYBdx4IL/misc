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
import java.util.Collection;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
enum DisplayLevel {
    OK(0), WARNING(1), CRITICAL(2);

    private final int numericLevel;

    DisplayLevel(int level) {
        this.numericLevel = level;
    }

    public Color getColor() {
        switch (this) {
            case OK:
                return Color.green;
            case WARNING:
                return Color.yellow;
            case CRITICAL:
                return Color.red;
            default:
        }
        return null;
    }

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public boolean largerThan(DisplayLevel level) {
        return this.numericLevel > level.numericLevel;
    }

    public static DisplayLevel max(Collection<DisplayLevel> levels) {
        DisplayLevel maxLevel = DisplayLevel.OK;
        for (DisplayLevel lvl : levels) {
            maxLevel = lvl.largerThan(maxLevel) ? lvl : maxLevel;
        }
        return maxLevel;
    }
}
