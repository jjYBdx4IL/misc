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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class Merge {

    public static Rectangle computeBounds(BufferedImage image) {
        Raster r = image.getAlphaRaster();

        int yMin = r.getMinY() - 1;
        outerloop:
        for (int y = r.getMinY(); y < r.getMinY() + r.getHeight(); y++) {
            for (int x = r.getMinX(); x < r.getMinX() + r.getWidth(); x++) {
                if (r.getPixel(x, y, (int[]) null)[0] > 0) {
                    yMin = y;
                    break outerloop;
                }
            }
        }
        // image without content?
        if (yMin < r.getMinY()) {
            return new Rectangle(0, 0, 0, 0);
        }

        int yMax = r.getMinY() + r.getHeight() - 1;
        outerloop:
        for (int y = r.getMinY() + r.getHeight() - 1; y >= r.getMinY(); y--) {
            for (int x = r.getMinX(); x < r.getMinX() + r.getWidth(); x++) {
                if (r.getPixel(x, y, (int[]) null)[0] > 0) {
                    yMax = y;
                    break outerloop;
                }
            }
        }

        int xMin = r.getMinX();
        outerloop:
        for (int x = r.getMinX(); x < r.getMinX() + r.getWidth(); x++) {
            for (int y = r.getMinY(); y < r.getMinY() + r.getHeight(); y++) {
                if (r.getPixel(x, y, (int[]) null)[0] > 0) {
                    xMin = x;
                    break outerloop;
                }
            }
        }

        int xMax = r.getMinX() + r.getWidth() - 1;
        outerloop:
        for (int x = r.getMinX() + r.getWidth() - 1; x >= r.getMinX(); x--) {
            for (int y = r.getMinY(); y < r.getMinY() + r.getHeight(); y++) {
                if (r.getPixel(x, y, (int[]) null)[0] > 0) {
                    xMax = x;
                    break outerloop;
                }
            }
        }

        return new Rectangle(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1);
    }

    public static BufferedImage merge(BufferedImage image1, BufferedImage image2, int padding, int gap) {
        BufferedImage dest;
        Rectangle r1 = computeBounds(image1);
        Rectangle r2 = computeBounds(image2);

        dest = new BufferedImage(
                (int) (r1.getWidth() > r2.getWidth() ? r1.getWidth() : r2.getWidth()) + 2 * padding,
                (int) r1.getHeight() + (int) r2.getHeight() + gap + 2 * padding,
                image1.getType());

        Graphics2D g = (Graphics2D) dest.getGraphics();
        AffineTransform origXform = g.getTransform();
        try {
            g.translate(padding - r1.getX(), padding - r1.getY());
            g.drawImage(image1, null, null);
            g.translate(r1.getX() - r2.getX(), 0);
            g.translate(0, r1.getHeight());
            g.translate(0, gap);
            g.drawImage(image2, null, null);
        } finally {
            g.setTransform(origXform);
        }

        return dest;
    }

    private Merge() {
    }
}
