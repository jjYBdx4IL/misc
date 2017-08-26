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
package com.github.jjYBdx4IL.utils.junit4;

import static org.junit.Assert.fail;

import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class ImageTester {

    public static void assertReddish(BufferedImage img) {
        assertRGBXor(img, true, false, false);
    }

    public static void assertGreenish(BufferedImage img) {
        assertRGBXor(img, false, true, false);
    }

    public static void assertBlueish(BufferedImage img) {
        assertRGBXor(img, false, false, true);
    }

    public static void assertNotReddish(BufferedImage img) {
        assertNotRGBXor(img, true, false, false);
    }

    public static void assertNotGreenish(BufferedImage img) {
        assertNotRGBXor(img, false, true, false);
    }

    public static void assertNotBlueish(BufferedImage img) {
        assertNotRGBXor(img, false, false, true);
    }

    public static void assertNotRGBXor(BufferedImage img, boolean r, boolean g, boolean b) {
        try {
            assertRGBXor(img, r, g, b);
            fail();
        } catch (AssertionError ex) {
        }
    }

    public static void assertRGBXor(BufferedImage img, boolean r, boolean g, boolean b) {
        List<String> ish = new ArrayList<>();
        if (r) {
            ish.add("reddish");
        }
        if (g) {
            ish.add("greenish");
        }
        if (b) {
            ish.add("blueish");
        }
        if (ish.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String ishDesc = (ish.size() > 1 ? "neither" : "not") + StringUtils.join(ish, " nor ");
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y));
                // reddish?
                if (r && c.getGreen() == c.getBlue() && c.getGreen() <= c.getRed()) {
                } else // greenish?
                if (g && c.getRed() == c.getBlue() && c.getRed() <= c.getGreen()) {
                } else // blueish?
                if (b && c.getRed() == c.getGreen() && c.getRed() <= c.getBlue()) {
                } else {
                    fail(String.format("pixel (%d,%d) is %s: %s", x, y, ishDesc, c.toString()));
                }
            }
        }
    }

    private ImageTester() {
    }
}
