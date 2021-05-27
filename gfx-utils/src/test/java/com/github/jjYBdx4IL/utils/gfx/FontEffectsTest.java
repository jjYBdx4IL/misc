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
package com.github.jjYBdx4IL.utils.gfx;

//CHECKSTYLE:OFF
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FontEffectsTest {

    private static final String testPngFileName = "test.png";

    public static void deleteTempFiles() {
        for (String fileName : new String[]{testPngFileName}) {
            File f = new File(fileName);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    @AfterClass
    public static void afterClass() {
        deleteTempFiles();
    }

    @Before
    public void before() {
        deleteTempFiles();
    }

    @Test
    public void testPaintGlassEffect() throws IOException {
        FontEffects effects = new FontEffects();
        effects.setText("Hello, World!");
        effects.setGlassBG(true);
        effects.setShadowOffset(15);
        effects.paint();
        ImageIO.write(effects.getImage(), "png", new File(testPngFileName));
    }

    @Test
    public void testBlurShadow() throws IOException {
        FontEffects effects = new FontEffects();
        effects.setText("Hello, World!");
        effects.setShadowType(FontEffects.ShadowType.BLUR);
        effects.setGlassBG(false);
        effects.setShadowOffset(15);
        effects.paint();
        ImageIO.write(effects.getImage(), "png", new File(testPngFileName));
    }
}
