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
package com.github.jjYBdx4IL.utils.awt;

import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JInternalFrame;

public class MdiInternalImageFrameTest {

    @Test
    public void test() {
        MdiAutoScaleFrame frame = new MdiAutoScaleFrame(MdiAutoScaleFrame.class.getSimpleName() + " Test");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.add(create("unstretched", 320, 260, false, getImage()));
        frame.add(create("stretched", 240, 200, true, getImage()));
        frame.add(create("stretched", 740, 200, true, getImage()));

        AWTUtils.showFrameAndWaitForCloseByUserTest(frame);
    }

    @Test
    public void test2() {
        MdiAutoScaleFrame frame = new MdiAutoScaleFrame(MdiAutoScaleFrame.class.getSimpleName() + " Test");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.add(create("unstretched", 320, 260, false, getImage2()));
        frame.add(create("stretched", 240, 200, true, getImage2()));
        frame.add(create("stretched", 740, 200, true, getImage2()));

        AWTUtils.showFrameAndWaitForCloseByUserTest(frame);
    }

    JInternalFrame create(String title, int w, int h, boolean stretch, BufferedImage image) {
        MdiInternalImageFrame intFrame = new MdiInternalImageFrame(title, image, stretch);
        if (w >= 0 && h >= 0) {
            intFrame.setPreferredImageSize(new Dimension(w, h));
        }
        intFrame.pack();
        intFrame.setVisible(true);
        return intFrame;
    }

    BufferedImage getImage() {
        try (InputStream is = getClass()
            .getResourceAsStream("/org/openimaj/image/contour/aestheticode/aestheticode.jpg")) {
            return ImageIO.read(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    BufferedImage getImage2() {
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.BLACK);
        for (int i = 0; i < image.getWidth(); i++) {
            if (i % 2 != 0) {
                continue;
            }
            g.drawLine(i, 0, i, image.getHeight() - 1);
        }
        return image;
    }

}
