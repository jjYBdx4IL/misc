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
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

// CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class Text2ImageAppMainTest {

    private static final Logger LOG = LoggerFactory.getLogger(Text2ImageAppMainTest.class);

    private static final File TARGET_DIR = Maven.getTempTestDir(Text2ImageAppMainTest.class);
    private static final File PNG1_FILE = new File(TARGET_DIR, "test.png");
    private static final File PNG2_FILE = new File(TARGET_DIR, "test2.png");
    private static final File PNG3_FILE = new File(TARGET_DIR, "test3.png");
    private static final File HTML_FILE = new File(TARGET_DIR, "test.html");

    private static final String ue = new String(new byte[]{252 - 256}, Charset.forName("ISO-8859-1"));
    private static final String manyUe = ue + ue + ue + ue + ue + ue + ue + ue + ue + ue;

    private static final String htmlPart1 = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "    <head>\n"
            + "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=";
    private static final String htmlPart2 = "\">\n"
            + "        <style>\n"
            + "            body {\n"
            + "                margin: 0px;\n"
            + "                padding: 0px;\n"
            + "                white-space: nowrap;\n"
            + "                font-family: \"Courier New\", courier, monospace;\n"
            + "            }\n"
            + "        </style>\n"
            + "    </head>\n"
            + "    <body>";
    private static final String htmlPart3 = "</body>\n"
            + "</html>";

//    public static void deleteTempFiles() {
//        for (File file : new File[]{PNG1_FILE, PNG2_FILE, PNG2_FILE,
//            HTML_FILE}) {
//            if (file.exists()) {
//                file.delete();
//            }
//        }
//    }

    @Before
    public void before() throws IOException {
        TARGET_DIR.mkdirs();
        FileUtils.cleanDirectory(TARGET_DIR);
    }

    @Test
    public void testRunMerge() throws IOException {
        Graphics2D g;

        BufferedImage image1 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) image1.getGraphics();
        g.setColor(Color.black);
        g.drawRect(0, 0, 0, 0);
        ImageIO.write(image1, "png", PNG1_FILE);

        BufferedImage image2 = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) image2.getGraphics();
        g.setColor(Color.black);
        g.drawRect(1, 1, 0, 0);
        ImageIO.write(image2, "png", PNG2_FILE);

        new Text2ImageAppMain().run(new String[]{"-merge", "-mergeInput1", PNG1_FILE.getPath(),
            "-mergeInput2", PNG2_FILE.getPath(),
            "--output", PNG3_FILE.getPath(),
            "-mergeGap", "8", "-mergePadding", "8"});
        BufferedImage mergedImage = ImageIO.read(PNG3_FILE);
        assertEquals(1 + 2 * 8, mergedImage.getWidth());
        assertEquals(2 + 8 + 2 * 8, mergedImage.getHeight());

    }

    @Test
    public void testRunScale() throws IOException {
        BufferedImage scaledImage;

        BufferedImage image1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(image1, "png", PNG1_FILE);

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleWidth", "8", "-scaleHeight", "8"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(8, scaledImage.getWidth());
        assertEquals(8, scaledImage.getHeight());

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleWidth", "8", "-scaleHeight", "80"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(8, scaledImage.getWidth());
        assertEquals(8, scaledImage.getHeight());

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleWidth", "80", "-scaleHeight", "8"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(8, scaledImage.getWidth());
        assertEquals(8, scaledImage.getHeight());

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleWidth", "8"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(8, scaledImage.getWidth());
        assertEquals(8, scaledImage.getHeight());

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleHeight", "8"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(8, scaledImage.getWidth());
        assertEquals(8, scaledImage.getHeight());

        BufferedImage image2 = new BufferedImage(20, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(image2, "png", PNG1_FILE);

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleWidth", "8", "-scaleHeight", "8"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(8, scaledImage.getWidth());
        assertEquals(4, scaledImage.getHeight());

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleWidth", "8", "-scaleHeight", "80"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(8, scaledImage.getWidth());
        assertEquals(4, scaledImage.getHeight());

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleWidth", "80", "-scaleHeight", "8"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(16, scaledImage.getWidth());
        assertEquals(8, scaledImage.getHeight());

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleWidth", "8"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(8, scaledImage.getWidth());
        assertEquals(4, scaledImage.getHeight());

        new Text2ImageAppMain().run(new String[]{"-scale", "-scaleInput", PNG1_FILE.getPath(),
            "--output", PNG2_FILE.getPath(), "-scaleHeight", "8"});
        scaledImage = ImageIO.read(PNG2_FILE);
        assertEquals(16, scaledImage.getWidth());
        assertEquals(8, scaledImage.getHeight());
    }

    @Test
    public void testRunConversion() throws Exception {
        LOG.info(Text2ImageAppMainTest.class.getResource("input1.txt").toString());
        Text2ImageAppMain app = new Text2ImageAppMain();
        app.setFontName("Courier New");
        app.setOptInputFileName(Text2ImageAppMainTest.class.getResource("input1.txt").getPath());
        app.setOptOutputFileName(PNG1_FILE.getPath());
        app.runConversion();

        @SuppressWarnings("unused")
        BufferedImage img = ImageIO.read(PNG1_FILE);
//        assertEquals(130, img.getWidth());
//        assertEquals(83, img.getHeight());
    }

    @Test
    public void testRunHtmlConversion() throws Exception {
        LOG.info(Text2ImageAppMainTest.class.getResource("dimensions.html").toString());
        Text2ImageAppMain app = new Text2ImageAppMain();
        app.setOptInputFileName(Text2ImageAppMainTest.class.getResource("dimensions.html").getPath());
        app.setOptOutputFileName(PNG1_FILE.getPath());
        app.runHtmlConversion();

        @SuppressWarnings("unused")
        BufferedImage img = ImageIO.read(PNG1_FILE);
//        assertEquals(800, img.getWidth());
//        assertEquals(799, img.getHeight());
    }

    @Test
    public void testHtmlConversionLatin1HeadLatin1Content() throws Exception {
        try (OutputStream os = new FileOutputStream(HTML_FILE)) {
            IOUtils.write(htmlPart1 + "ISO-8859-1" + htmlPart2 + manyUe + htmlPart3, os, Charset.forName("ISO-8859-1"));
        }
        new Text2ImageAppMain().run(new String[]{"-i", HTML_FILE.getPath(), "-o", PNG1_FILE.getPath(),
            "--html", "--htmlWidth", "1"});
        @SuppressWarnings("unused")
        BufferedImage img = ImageIO.read(PNG1_FILE);
//        assertEquals(100, img.getWidth());
//        assertEquals(20, img.getHeight());
    }

    @Test
    public void testHtmlConversionUTF8HeadUTF8Content() throws Exception {
        try (OutputStream os = new FileOutputStream(HTML_FILE)) {
            IOUtils.write(htmlPart1 + "UTF-8" + htmlPart2 + manyUe + htmlPart3, os, Charset.forName("UTF-8"));
        }
        new Text2ImageAppMain().run(new String[]{"-i", HTML_FILE.getPath(), "-o", PNG1_FILE.getPath(),
            "--html", "--htmlWidth", "1"});
        @SuppressWarnings("unused")
        BufferedImage img = ImageIO.read(PNG1_FILE);
//        assertEquals(100, img.getWidth());
//        assertEquals(20, img.getHeight());

    }

    @Test
    public void testHtmlConversionLatin1HeadUTF8Content() throws Exception {
        try (OutputStream os = new FileOutputStream(HTML_FILE)) {
            IOUtils.write(htmlPart1 + "latin1" + htmlPart2 + manyUe + htmlPart3, os, Charset.forName("UTF-8"));
        }
        new Text2ImageAppMain().run(new String[]{"-i", HTML_FILE.getPath(), "-o", PNG1_FILE.getPath(),
            "--html", "--htmlWidth", "1"});
        @SuppressWarnings("unused")
        BufferedImage img = ImageIO.read(PNG1_FILE);
//        assertEquals(200, img.getWidth());
//        assertEquals(20, img.getHeight());
    }

    @Test
    public void testHtmlConversionUTF8HeadLatin1Content() throws Exception {
        try (OutputStream os = new FileOutputStream(HTML_FILE)) {
            IOUtils.write(htmlPart1 + "UTF-8" + htmlPart2 + manyUe + htmlPart3, os, Charset.forName("latin1"));
        }
        new Text2ImageAppMain().run(new String[]{"-i", HTML_FILE.getPath(), "-o", PNG1_FILE.getPath(),
            "--html", "--htmlWidth", "1"});
        @SuppressWarnings("unused")
        BufferedImage img = ImageIO.read(PNG1_FILE);
//        assertEquals(100, img.getWidth());
//        assertEquals(20, img.getHeight());
    }
}
