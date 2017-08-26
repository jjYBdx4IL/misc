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

import com.github.jjYBdx4IL.utils.env.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class Screenshot {

    private static final Logger log = LoggerFactory.getLogger(Screenshot.class);
    public final static String DEFAULT_EXTENSION = ".png";
    private final static Pattern allowedExtensions = Pattern.compile("\\.(png|jpg|jpeg)$", Pattern.CASE_INSENSITIVE);

    /**
     * Local screenshots take a screenshot of the entire desktop. They are
     * required to catch alerts.
     *
     * @param outputFilePath the output directory
     * @return the screenshot
     */
    public static File takeDesktopScreenshot(String outputFilePath) {
        return takeDesktopScreenshot(outputFilePath, false);
    }

    public static File getMavenScreenshotOutputDir() {
        return getMavenScreenshotOutputDir((String) null);
    }

    public static File getMavenScreenshotOutputDir(Class<?> classType) {
        return getMavenScreenshotOutputDir(classType.getName());
    }

    public static File getMavenScreenshotOutputDir(String subDirName) {
        File outputDir = new File(Maven.getMavenTargetDir(), "screenshots");
        if (subDirName != null) {
            outputDir = new File(outputDir, subDirName);
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return outputDir;
    }

    public static File takeDesktopScreenshot(String outputFilePath, boolean placeIntoMavenTargetScreenshotDir) {
        return takeDesktopScreenshot(null, outputFilePath, placeIntoMavenTargetScreenshotDir);
    }

    public static File takeDesktopScreenshot(GraphicsDevice screen, String outputFilePath,
            boolean placeIntoMavenTargetScreenshotDir) {
        try {
            InteractiveTestBase.waitForSwing();

            File output;

            if (placeIntoMavenTargetScreenshotDir) {
                output = new File(getMavenScreenshotOutputDir(), outputFilePath);
            } else {
                output = new File(outputFilePath);
            }

            File parentDir = output.getParentFile();
            if (parentDir != null) {
                if (!parentDir.exists()) {
                    if (!parentDir.mkdirs()) {
                        throw new RuntimeException("failed to create " + parentDir.getAbsolutePath());
                    }
                }
            }
            if (!allowedExtensions.matcher(output.getName()).find()) {
                output = new File(output.getPath() + DEFAULT_EXTENSION);
            }
            String ext = output.getName().substring(output.getName().lastIndexOf(".") + 1);

            log.info("writing local screenshot: " + output.getPath());

            if (output.exists()) {
                log.warn("screenshot output file exists, overwriting it");
                if (!output.delete()) {
                    log.error("failed to remove previous screenshot file");
                }
            }

            final BufferedImage exportImage;
            if (screen != null) {
                exportImage = createDesktopScreenshot(screen);
            } else {
                exportImage = createDesktopScreenshot();
            }
            if (!ImageIO.write(exportImage, ext.toLowerCase(), output)) {
                throw new IOException("failed to write " + output.getPath());
            }
            return output;
        } catch (IOException | HeadlessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static File takeDesktopScreenshot(File file) {
        return takeDesktopScreenshot(file.getAbsolutePath(), false);
    }

    public static File takeDesktopScreenshot(GraphicsDevice screen, File file) {
        return takeDesktopScreenshot(screen, file.getAbsolutePath(), false);
    }

    public static BufferedImage createDesktopScreenshot() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        return createDesktopScreenshot(0, 0, (int) screenDim.getWidth(), (int) screenDim.getHeight());
    }

    public static BufferedImage createDesktopScreenshot(GraphicsDevice screen) {
        GraphicsConfiguration gc = screen.getDefaultConfiguration();
        Rectangle rect = gc.getBounds();
        return createDesktopScreenshot(screen, rect.x, rect.y, rect.width, rect.height);
    }

    public static BufferedImage createDesktopScreenshot(int x, int y, int w, int h) {
        return createDesktopScreenshot(X_OFFSET.LEFT, Y_OFFSET.TOP, x, y, w, h);
    }

    public static BufferedImage createDesktopScreenshot(GraphicsDevice screen, int x, int y, int w, int h) {
        return createDesktopScreenshot(screen, X_OFFSET.LEFT, Y_OFFSET.TOP, x, y, w, h);
    }

    public static BufferedImage createDesktopScreenshot(X_OFFSET x_offset, Y_OFFSET y_offset, int x, int y, int w,
            int h) {
        return createDesktopScreenshot(null, x_offset, y_offset, x, y, w, h);
    }

    public static BufferedImage createDesktopScreenshot(GraphicsDevice screen, X_OFFSET x_offset, Y_OFFSET y_offset,
            int x, int y, int w, int h) {
        try {
            final Dimension screenDim;
            final Robot objRobot;
            if (screen != null) {
                Rectangle rect = screen.getDefaultConfiguration().getBounds();
                screenDim = new Dimension(rect.width, rect.height);
                objRobot = new Robot(screen);
            } else {
                screenDim = Toolkit.getDefaultToolkit().getScreenSize();
                objRobot = new Robot();
            }

            int _x = x;
            switch (x_offset) {
            case LEFT:
                break;
            case CENTER:
                _x += screenDim.width / 2;
                break;
            case RIGHT:
                _x += screenDim.width;
                break;
            }
            int _y = y;
            switch (y_offset) {
            case TOP:
                break;
            case MIDDLE:
                _y += screenDim.height / 2;
                break;
            case BOTTOM:
                _y += screenDim.height;
                break;
            }

            return objRobot.createScreenCapture(new Rectangle(_x, _y, w, h));
        } catch (AWTException ex) {
            throw new RuntimeException(ex);
        }
    }

    public enum X_OFFSET {

        LEFT, CENTER, RIGHT
    };

    public enum Y_OFFSET {

        TOP, MIDDLE, BOTTOM
    };

    private Screenshot() {
    }
}
