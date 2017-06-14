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
package com.github.jjYBdx4IL.parser.eclipsecolortheme;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.InputStream;

import org.junit.Test;

public class EclipseColorThemeParserTest {

    @Test
    public void test() throws Exception {
        ColorTheme colorTheme = null;
        try (InputStream xmlInputStream = getClass().getResourceAsStream("EclipseColorTheme1.xml")) {
            colorTheme = EclipseColorThemeParser.load(xmlInputStream);
        }
        assertNotNull(colorTheme);
        assertEquals(Color.black, colorTheme.getBackground().getColor());
        assertEquals(Color.yellow, colorTheme.getFindScope().getColor());
        assertTrue(colorTheme.getKlass().isBold());
        assertFalse(colorTheme.getFindScope().isBold());
    }

}
