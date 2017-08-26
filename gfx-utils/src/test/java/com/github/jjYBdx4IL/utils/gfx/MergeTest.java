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

import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class MergeTest {

    private static final File TARGET_DIR = Maven.getTempTestDir(MergeTest.class);

    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(TARGET_DIR);
    }

    @Test
    public void testComputeBounds() throws IOException {
        Rectangle bounds;
        BufferedImage image;
        Graphics2D g;

        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        bounds = Merge.computeBounds(image);
        assertEquals(0, (int) bounds.getWidth());
        assertEquals(0, (int) bounds.getHeight());

        g = (Graphics2D) image.getGraphics();
        g.setColor(Color.black);
        g.drawRect(0, 0, 0, 0);
        bounds = Merge.computeBounds(image);
        assertEquals(0, (int) bounds.getX());
        assertEquals(0, (int) bounds.getY());
        assertEquals(1, (int) bounds.getWidth());
        assertEquals(1, (int) bounds.getHeight());

        image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) image.getGraphics();
        g.setColor(Color.black);
        g.drawRect(1, 1, 0, 0);
        bounds = Merge.computeBounds(image);
        assertEquals(1, (int) bounds.getX());
        assertEquals(1, (int) bounds.getY());
        assertEquals(1, (int) bounds.getWidth());
        assertEquals(1, (int) bounds.getHeight());
    }

    @Test
    public void testMerge() throws IOException {
        Graphics2D g;

        BufferedImage image1 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) image1.getGraphics();
        g.setColor(Color.black);
        g.drawRect(0, 0, 0, 0);

        BufferedImage image2 = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) image2.getGraphics();
        g.setColor(Color.black);
        g.drawRect(1, 1, 0, 0);

        BufferedImage mergedImage = Merge.merge(image1, image2, 0, 0);
        assertEquals(1, mergedImage.getWidth());
        assertEquals(2, mergedImage.getHeight());

        mergedImage = Merge.merge(image1, image2, 0, 3);
        assertEquals(1, mergedImage.getWidth());
        assertEquals(5, mergedImage.getHeight());

        mergedImage = Merge.merge(image1, image2, 2, 3);
        assertEquals(5, mergedImage.getWidth());
        assertEquals(9, mergedImage.getHeight());
    }
}
