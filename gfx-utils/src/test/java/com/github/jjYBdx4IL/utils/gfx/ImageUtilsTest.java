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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.test.GraphicsResource;
import com.github.jjYBdx4IL.utils.junit4.ImageTesterTest;
import com.github.jjYBdx4IL.utils.junit4.InteractiveTestBase;
import com.github.jjYBdx4IL.utils.klass.ClassReloader;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.imageio.ImageIO;

// CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */

public class ImageUtilsTest extends InteractiveTestBase implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ImageUtilsTest.class);

    private static BufferedImage createImg1() {
        BufferedImage img = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 3, 3);
        g.setColor(Color.RED);
        g.fillRect(1, 1, 1, 1);
        assertEquals(Color.WHITE, new Color(img.getRGB(0, 0)));
        assertEquals(Color.RED, new Color(img.getRGB(1, 1)));
        assertEquals(Color.WHITE, new Color(img.getRGB(2, 2)));
        return img;
    }

    private static BufferedImage createImg2() {
        BufferedImage img = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 150, 150);
        g.setColor(Color.RED);
        g.fillRect(50, 50, 50, 50);
        return img;
    }

    @Test
    public void testAutoCrop() throws IOException, InterruptedException, InvocationTargetException {
        openWindow();

        BufferedImage img = ImageIO.read(ImageTesterTest.class.getResourceAsStream("greenish.png"));
        appendImage(img);
        assertEquals(800, img.getWidth());
        assertEquals(20, img.getHeight());
        BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        img = ImageUtils.autoCrop(img);
        Graphics2D g = (Graphics2D) img2.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img2.getWidth(), img2.getHeight());
        g.drawImage(img, (img2.getWidth() - img.getWidth()) / 2, (img2.getHeight() - img.getHeight()) / 2, null);
        appendImage(img2);
        assertEquals(424, img.getWidth());
        assertEquals(11, img.getHeight());

        saveWindowAsImage("testAutoCrop");
        waitForWindowClosing();
    }

    @Test
    public void testAutoCrop2() {
        BufferedImage img = ImageUtils.autoCrop(createImg1());
        assertEquals(1, img.getWidth());
        assertEquals(1, img.getHeight());
        assertEquals(Color.RED, new Color(img.getRGB(0, 0)));
    }

    @Test
    public void testAutoCrop2Border() throws InterruptedException, InvocationTargetException {
        BufferedImage img;

        img = ImageUtils.autoCrop(createImg1(), 1, 0, 0, 0);
        assertEquals(2, img.getWidth());
        assertEquals(1, img.getHeight());
        assertEquals(Color.WHITE, new Color(img.getRGB(0, 0)));
        assertEquals(Color.RED, new Color(img.getRGB(1, 0)));

        img = ImageUtils.autoCrop(createImg1(), 0, 1, 0, 0);
        assertEquals(2, img.getWidth());
        assertEquals(1, img.getHeight());
        assertEquals(Color.RED, new Color(img.getRGB(0, 0)));
        assertEquals(Color.WHITE, new Color(img.getRGB(1, 0)));

        img = ImageUtils.autoCrop(createImg1(), 0, 0, 1, 0);
        assertEquals(1, img.getWidth());
        assertEquals(2, img.getHeight());
        assertEquals(Color.WHITE, new Color(img.getRGB(0, 0)));
        assertEquals(Color.RED, new Color(img.getRGB(0, 1)));

        img = ImageUtils.autoCrop(createImg1(), 0, 0, 0, 1);
        assertEquals(1, img.getWidth());
        assertEquals(2, img.getHeight());
        assertEquals(Color.RED, new Color(img.getRGB(0, 0)));
        assertEquals(Color.WHITE, new Color(img.getRGB(0, 1)));

        img = ImageUtils.autoCrop(createImg2(), 50, 150, 100, 200);

        openWindow();
        appendImage(img);
        saveWindowAsImage("testAutoCrop2Border");
        waitForWindowClosing();
    }

    @Test
    public void testAutoCrop3() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1, 1);

        try {
            img = ImageUtils.autoCrop(img);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testAutoCrop4() {
        BufferedImage img = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 3, 3);

        try {
            img = ImageUtils.autoCrop(img);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testDeepCopy() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage img2 = ImageUtils.deepCopy(img);

        assertTrue(img.getColorModel().equals(img2.getColorModel()));
        assertEquals(img.isAlphaPremultiplied(), img2.isAlphaPremultiplied());
        assertEquals(img.getWidth(), img2.getWidth());
        assertEquals(img.getHeight(), img2.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                assertEquals(img.getRGB(x, y), img2.getRGB(x, y));
            }
        }
        img.getRaster().setPixel(1, 1, new int[]{120});
        assertNotEquals(img.getRGB(1, 1), img2.getRGB(1, 1));
    }

    @SuppressWarnings("unused")
	@Test
    public void testDeepCopySubImage() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage img2 = ImageUtils.deepCopy(img.getSubimage(2, 2, 5, 6));
    }

    @Test
    public void testDeducePaletteSimple() {
        BufferedImage img = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, Color.BLUE.getRGB());
        img.setRGB(1, 0, Color.CYAN.getRGB());
        img.setRGB(2, 0, Color.CYAN.getRGB());
        int[] pal = ImageUtils.deducePalette(img, 2);
        assertEquals(2, pal.length);
        Arrays.sort(pal);
        assertEquals(Color.BLUE.getRGB(), pal[0]);
        assertEquals(Color.CYAN.getRGB(), pal[1]);
    }

    @Ignore // out of memery problems
    @SuppressWarnings("unused")
	@Test
    public void testDeducePalettePerformance() {
        BufferedImage img = GraphicsResource.OPENIMAJ_TESTRES_AESTHETICODE.loadImage().getSubimage(0, 0, 200, 200);
        long start = System.currentTimeMillis();
        int[] pal = ImageUtils.deducePalette(img, 32);
        long duration = System.currentTimeMillis() - start;
        LOG.info(String.format("deducePalette performance: %dkpix/sec", img.getWidth() * img.getHeight() / duration));
    }

    @Test
    public void testFindClosest() {
        int[] pal = new int[]{0, 1, 10};
        assertEquals(0, ImageUtils.findClosest(pal, 0));
        assertEquals(1, ImageUtils.findClosest(pal, 1));
        assertEquals(1, ImageUtils.findClosest(pal, 5));
        assertEquals(10, ImageUtils.findClosest(pal, 6));
        assertEquals(10, ImageUtils.findClosest(pal, 255));

        assertEquals(0, ImageUtils.findClosest(pal, 0 + 0xFFFF00));
        assertEquals(1, ImageUtils.findClosest(pal, 1 + 0xFFFF00));
        assertEquals(1, ImageUtils.findClosest(pal, 5 + 0xFF0000));
        assertEquals(10, ImageUtils.findClosest(pal, 6 + 0xFFFF00));
        assertEquals(10, ImageUtils.findClosest(pal, 255 + 0x00FF00));
    }

    @Test
    public void testFindClosestBinary() {
        int[] pal = new int[]{0, 1, 10};
        assertEquals(0, ImageUtils.findClosestBinary(pal, 0));
        assertEquals(1, ImageUtils.findClosestBinary(pal, 1));
        assertEquals(1, ImageUtils.findClosestBinary(pal, 5));
        assertEquals(10, ImageUtils.findClosestBinary(pal, 6));
        assertEquals(10, ImageUtils.findClosestBinary(pal, 255));

        assertEquals(0, ImageUtils.findClosestBinary(pal, 0 + 0xFFFF00));
        assertEquals(1, ImageUtils.findClosestBinary(pal, 1 + 0xFFFF00));
        assertEquals(1, ImageUtils.findClosestBinary(pal, 5 + 0xFF0000));
        assertEquals(10, ImageUtils.findClosestBinary(pal, 6 + 0xFFFF00));
        assertEquals(10, ImageUtils.findClosestBinary(pal, 255 + 0x00FF00));
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        LOG.info("started");
        Thread t = ClassReloader.watchLoadAndRun("target/test-classes", ImageUtilsTest.class.getName());
        t.join();
    }

    @Override
    public void run() {
        LOG.info("running");
        try {
            testFindClosest();
        } catch (Throwable ex) {
            LOG.error("", ex);
        }
    }

    @Test
    public void testScale() {
        BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.red);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        BufferedImage result = ImageUtils.scale(image, 2, 5);
        
        assertEquals(2, result.getWidth());
        assertEquals(5, result.getHeight());
        assertEquals(BufferedImage.TYPE_3BYTE_BGR, result.getType());
        
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                assertEquals(Color.red, new Color(result.getRGB(x, y)));
            }
        }
    }
    
}
