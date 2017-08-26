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

import com.github.jjYBdx4IL.utils.env.Maven;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AnimatedGifOutputStreamTest {

    private final static File tempDir = Maven.getTempTestDir(AnimatedGifOutputStreamTest.class);

    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(tempDir);
    }

    @Test
    public void test() throws IOException {
        final int w = 100;
        final int h = 100;
        final int frames = 100;
        final int lastIndex = frames - 1;
        try (AnimatedGifOutputStream animGifOs = new AnimatedGifOutputStream(new File(tempDir, "animated.gif"))) {
            for (int i = 0; i <= lastIndex; i++) {
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D g = (Graphics2D) img.getGraphics();
                int componentValue = (i * 255 / lastIndex);
                g.setColor(new Color(0xff000000 | componentValue | (componentValue << 8) | (componentValue << 16)));
                g.fillRect(0, 0, w, h);
                
                animGifOs.append(img);
            }
        }
    }
}
