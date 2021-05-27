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

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

//CHECKSTYLE:OFF
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 *
 * @author jjYBdx4IL
 */
public class ImageUtils {

    public static double colorDist(int pixel1, int pixel2) {
        int p1r = (pixel1 >> 16) & 0xFF;
        int p1g = (pixel1 >> 8) & 0xFF;
        int p1b = (pixel1) & 0xFF;
        int p2r = (pixel2 >> 16) & 0xFF;
        int p2g = (pixel2 >> 8) & 0xFF;
        int p2b = (pixel2) & 0xFF;
        return Math.sqrt((double) ((p1r - p2r) * (p1r - p2r) + (p1g - p2g) * (p1g - p2g) + (p1b - p2b) * (p1b - p2b)));
    }

    /**
     * Works only as intended when used with an ordered palette, which implies a monochromaticity.
     *
     * @param pal the input palette
     * @param pixel the pixel
     * @return the closest color from the palette
     */
    public static int findClosestBinary(int[] pal, int pixel) {
        int imin = 0, imax = pal.length - 1;
        while (imax > imin) {
            int imidLeft = (imin + imax) / 2;
            int imidRight = imidLeft + 1;
            if (colorDist(pal[imidLeft], pixel) < colorDist(pal[imidRight], pixel)) {
                imax = imidLeft;
            } else {
                imin = imidRight;
            }
        }
        return pal[imax];
    }

    public static int findClosest(int[] pal, int pixel) {
        double minDist = colorDist(pal[0], pixel);
        int minIdx = 0;
        for (int i = 1; i < pal.length; i++) {
            double dist = colorDist(pal[i], pixel);
            if (dist < minDist) {
                minDist = dist;
                minIdx = i;
            }
        }
        return pal[minIdx];
    }

    public static void replaceColors(BufferedImage img, int[] pal) {
        int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = findClosest(pal, pixels[i]);
        }
        img.setRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
    }

    private static class Point implements Clusterable {

        final int value;

        Point(int value) {
            this.value = value;
        }

        @Override
        public double[] getPoint() {
            return new double[]{
                (value >> 24) & 0xFF,
                (value >> 16) & 0xFF,
                (value >> 8) & 0xFF,
                value & 0xFF
            };
        }
    }

    public static int[] deducePalette(BufferedImage img, int numColors) {
        int[] colors = new int[numColors];
        KMeansPlusPlusClusterer<Point> clusterer = new KMeansPlusPlusClusterer<>(numColors);
        List<Point> pts = new ArrayList<>();
        int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        for (int pixel : pixels) {
            pts.add(new Point(pixel));
        }
        List<CentroidCluster<Point>> res = clusterer.cluster(pts);
        for (int i = 0; i < numColors; i++) {
            double[] pt = res.get(i).getCenter().getPoint();
            colors[i] = (int) (((Math.round(pt[0]) & 0xFF) << 24)
                    | ((Math.round(pt[1]) & 0xFF) << 16)
                    | ((Math.round(pt[2]) & 0xFF) << 8)
                    | (Math.round(pt[3]) & 0xFF));
        }
        return colors;
    }

    public static BufferedImage addBorder(BufferedImage img, Color c, int xl, int xr, int yt, int yb) {
        BufferedImage img2 = new BufferedImage(img.getWidth() + xl + xr, img.getHeight() + yt + yb, img.getType());
        Graphics2D g = (Graphics2D) img2.getGraphics();
        g.setColor(c);
        g.fillRect(0, 0, img2.getWidth(), img2.getHeight());
        g.drawImage(img, xl, yt, img.getWidth(), img.getHeight(), null);
        return img2;
    }

    public static BufferedImage autoCrop(BufferedImage img, int borderSize) {
        return autoCrop(img, borderSize, borderSize);
    }

    public static BufferedImage autoCrop(BufferedImage img, int horizontalBorderSize, int verticalBorderSize) {
        return autoCrop(img, horizontalBorderSize, horizontalBorderSize, verticalBorderSize, verticalBorderSize);
    }

    public static BufferedImage autoCrop(BufferedImage img, int xl, int xr, int yt, int yb) {
        BufferedImage img2 = autoCrop(img);
        if (xl == 0 && xr == 0 && yt == 0 && yb == 0) {
            return img2;
        }
        return addBorder(img2, new Color(img.getRGB(0, 0)), xl, xr, yt, yb);
    }

    public static BufferedImage autoCrop(BufferedImage img) {
        Rectangle r = getBoundingBox(img);
        return deepCopy(img.getSubimage(r.x, r.y, r.width, r.height));
    }
    
    public static Rectangle getBoundingBox(Path imgFile) throws IOException {
        BufferedImage img = ImageIO.read(imgFile.toFile());
        return getBoundingBox(img);
    }
    
    public static Rectangle getBoundingBox(BufferedImage img) {
        final int bkgd = img.getRGB(0, 0);
        int x, y;

        outer:
        for (x = 0; x < img.getWidth(); x++) {
            for (y = 0; y < img.getHeight(); y++) {
                if (img.getRGB(x, y) != bkgd) {
                    break outer;
                }
            }
        }
        int xLeft = x;

        if (xLeft == img.getWidth()) {
            throw new IllegalArgumentException("no content");
        }

        outer:
        for (x = img.getWidth() - 1; x >= 0; x--) {
            for (y = 0; y < img.getHeight(); y++) {
                if (img.getRGB(x, y) != bkgd) {
                    break outer;
                }
            }
        }
        int xRight = x;

        outer:
        for (y = 0; y < img.getHeight(); y++) {
            for (x = 0; x < img.getWidth(); x++) {
                if (img.getRGB(x, y) != bkgd) {
                    break outer;
                }
            }
        }
        int yTop = y;

        outer:
        for (y = img.getHeight() - 1; y >= 0; y--) {
            for (x = 0; x < img.getWidth(); x++) {
                if (img.getRGB(x, y) != bkgd) {
                    break outer;
                }
            }
        }
        int yBottom = y;
        
        return new Rectangle(xLeft, yTop, xRight - xLeft + 1, yBottom - yTop + 1);
    }

    // https://stackoverflow.com/questions/4156518/rotate-an-image-in-java/56752539
    //@meta:keywords:rotate,bufferedimage@
    public static BufferedImage rotate(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int) Math.floor(h * cos + w * sin);
        BufferedImage result = deepCopy(image, false);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }

    public static BufferedImage deepCopy(BufferedImage bi, boolean copyPixels) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.getRaster().createCompatibleWritableRaster();
        if (copyPixels) {
            bi.copyData(raster);
        }
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }   
    
    public static BufferedImage deepCopy(BufferedImage bi) {
        return deepCopy(bi, true);
    }
        
    public final static int DEFAULT_CHECKERED_BACKGROUND_SQUARE_SIZE = 8;

    public static void paintCheckeredBackground(BufferedImage img) {
        paintCheckeredBackground(img, DEFAULT_CHECKERED_BACKGROUND_SQUARE_SIZE);
    }

    public static void paintCheckeredBackground(BufferedImage img, int squareSize) {
        WritableRaster raster = img.getRaster();
        int[] gray = new int[]{127, 127, 127, 255};
        int[] white = new int[]{192, 192, 192, 255};
        for (int x = raster.getMinX(); x < raster.getMinX() + raster.getWidth(); x++) {
            for (int y = raster.getMinY(); y < raster.getMinY() + raster.getHeight(); y++) {
                raster.setPixel(x, y,
                        (x / squareSize - y / squareSize) % 2 == 0 ? gray
                                : white);
            }
        }
    }

    public static BufferedImage alphaMerge(BufferedImage background, BufferedImage layer) {
        BufferedImage bkgd = ImageUtils.deepCopy(background);
        if (bkgd.getColorModel().hasAlpha() && !bkgd.isAlphaPremultiplied()) {
            bkgd.coerceData(true);
        }
        BufferedImage layr = ImageUtils.deepCopy(layer);
        if (layr.getColorModel().hasAlpha() && !layr.isAlphaPremultiplied()) {
            layr.coerceData(true);
        }

        BufferedImage newImage = new BufferedImage(bkgd.getWidth(), bkgd.getHeight(), bkgd.getType());
        Graphics2D g = newImage.createGraphics();
        g.drawImage(bkgd, 0, 0, null);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.drawImage(layr, 0, 0, null);

        g.dispose();
        newImage.flush();

        return newImage;
    }
            
    /**
     * Quality, bicubic scaling. Width/height ratio not preserved.
     * 
     * @param sbi the image to be scaled
     * @param dWidth desired target width
     * @param dHeight desired target height
     * @return the scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int dWidth, int dHeight) {
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, sbi.getType());
            Graphics2D g = dbi.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            AffineTransform at = AffineTransform.getScaleInstance(1d * dWidth / sbi.getWidth(), 1d * dHeight / sbi.getHeight());
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }

    /**
     * Quality, bicubic scaling. Width/height ratio not preserved.
     * 
     * @param input the image to be scaled
     * @param dWidth desired target width
     * @param dHeight desired target height
     * @return the scaled image
     */
    public static BufferedImage scale(Path input, int dWidth, int dHeight) throws IOException {
        BufferedImage img = ImageIO.read(input.toFile());
        return scale(img, dWidth, dHeight);
    }

    /**
     * Quality, bicubic scaling. Width/height ratio not preserved.
     * 
     * @param input the image to be scaled
     * @param output the scaled image. target file will be overwritten if it exists.
     * @param dWidth desired target width
     * @param dHeight desired target height
     */
    public static void scale(Path input, Path output, int dWidth, int dHeight) throws IOException {
        BufferedImage img = ImageIO.read(input.toFile());
        BufferedImage out = scale(img, dWidth, dHeight);
        String outname = output.toFile().getName();
        int i = outname.lastIndexOf(".");
        if (i == -1) {
            throw new IOException("failed to determine image format form file extension");
        }
        String ext = outname.substring(i+1);
        if (!ImageIO.write(out, ext, output.toFile())) {
            throw new IOException("failed to determine image format form file extension: " + ext);
        }
    }
    
    private ImageUtils() {}
}
