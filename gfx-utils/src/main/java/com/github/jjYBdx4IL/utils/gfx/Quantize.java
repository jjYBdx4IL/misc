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
import java.awt.Color;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
*
* @author http://stackoverflow.com/questions/21472245/color-quantization-with-n-out-of-m-predefined-colors
* @author jjYBdx4IL update javadoc 
*/
public class Quantize {

public static class RGBTriple {
    public final int[] channels;
    public RGBTriple() { channels = new int[3]; }

    public RGBTriple(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        channels = new int[]{(int)r, (int)g, (int)b};
    }
    public RGBTriple(int R, int G, int B)
    { channels = new int[]{(int)R, (int)G, (int)B}; }
}

/* The authors of this work have released all rights to it and placed it
in the public domain under the Creative Commons CC0 1.0 waiver
(http://creativecommons.org/publicdomain/zero/1.0/).

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Floyd-Steinberg_dithering_(Java)?oldid=12476
 */
public static class FloydSteinbergDither
{
    private static int plus_truncate_uchar(int a, int b) {
        if ((a & 0xff) + b < 0)
            return 0;
        else if ((a & 0xff) + b > 255)
            return (int)255;
        else
            return (int)(a + b);
    }


    private static int findNearestColor(RGBTriple color, RGBTriple[] palette) {
        int minDistanceSquared = 255*255 + 255*255 + 255*255 + 1;
        int bestIndex = 0;
        for (int i = 0; i < palette.length; i++) {
            int Rdiff = (color.channels[0] & 0xff) - (palette[i].channels[0] & 0xff);
            int Gdiff = (color.channels[1] & 0xff) - (palette[i].channels[1] & 0xff);
            int Bdiff = (color.channels[2] & 0xff) - (palette[i].channels[2] & 0xff);
            int distanceSquared = Rdiff*Rdiff + Gdiff*Gdiff + Bdiff*Bdiff;
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public static int[][] floydSteinbergDither(RGBTriple[][] image, RGBTriple[] palette)
    {
        int[][] result = new int[image.length][image[0].length];

        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[y].length; x++) {
                RGBTriple currentPixel = image[y][x];
                int index = findNearestColor(currentPixel, palette);
                result[y][x] = index;

                for (int i = 0; i < 3; i++)
                {
                    int error = (currentPixel.channels[i] & 0xff) - (palette[index].channels[i] & 0xff);
                    if (x + 1 < image[0].length) {
                        image[y+0][x+1].channels[i] =
                                plus_truncate_uchar(image[y+0][x+1].channels[i], (error*7) >> 4);
                    }
                    if (y + 1 < image.length) {
                        if (x - 1 > 0) {
                            image[y+1][x-1].channels[i] =
                                    plus_truncate_uchar(image[y+1][x-1].channels[i], (error*3) >> 4);
                        }
                        image[y+1][x+0].channels[i] =
                                plus_truncate_uchar(image[y+1][x+0].channels[i], (error*5) >> 4);
                        if (x + 1 < image[0].length) {
                            image[y+1][x+1].channels[i] =
                                    plus_truncate_uchar(image[y+1][x+1].channels[i], (error*1) >> 4);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void generateDither(int[] pixels, int[] p, int w, int h){
        RGBTriple[] palette = new RGBTriple[p.length];
        for (int i = 0; i < palette.length; i++) {
            int color = p[i];
            palette[i] = new RGBTriple(color);
        }
        RGBTriple[][] image = new RGBTriple[w][h];
        for (int x = w; x-- > 0; ) {
            for (int y = h; y-- > 0; ) {
                int index = y * w + x;
                int color = pixels[index];
                image[x][y] = new RGBTriple(color);
            }
        }

        int[][] result = floydSteinbergDither(image, palette);
        convert(result, pixels, p, w, h);

    }

    public static void convert(int[][] result, int[] pixels, int[] p, int w, int h){
        for (int x = w; x-- > 0; ) {
            for (int y = h; y-- > 0; ) {
                int index = y * w + x;
                int index2 = result[x][y];
                pixels[index] = p[index2];
            }
        }
    }
}

private static class PaletteColor{
    final int color;
    public PaletteColor(int color) {
        super();
        this.color = color;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + color;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaletteColor other = (PaletteColor) obj;
        if (color != other.color)
            return false;
        return true;
    }

    public List<Integer> indices = new ArrayList<>();
}


public static int[] getPixels(Image image) throws IOException {
    int w = image.getWidth(null);
    int h = image.getHeight(null);
    int pix[] = new int[w * h];
    PixelGrabber grabber = new PixelGrabber(image, 0, 0, w, h, pix, 0, w);

    try {
        if (grabber.grabPixels() != true) {
            throw new IOException("Grabber returned false: " +
                    grabber.status());
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return pix;
}

/**
 * Returns the color distance between color1 and color2.
 * 
 * @param color1 first color
 * @param color2 second color
 * @return the color distance
 */
public static float getPixelDistance(PaletteColor color1, PaletteColor color2){
    int c1 = color1.color;
    int r1 = (c1 >> 16) & 0xFF;
    int g1 = (c1 >> 8) & 0xFF;
    int b1 = (c1 >> 0) & 0xFF;
    int c2 = color2.color;
    int r2 = (c2 >> 16) & 0xFF;
    int g2 = (c2 >> 8) & 0xFF;
    int b2 = (c2 >> 0) & 0xFF;
    return (float) getPixelDistance(r1, g1, b1, r2, g2, b2);
}

public static double getPixelDistance(int r1, int g1, int b1, int r2, int g2, int b2){
    return Math.sqrt(Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2));
}

/**
 * Fills the given fillColors palette with the nearest colors from the given colors palette until
 * it has the given max_cols size.
 * 
 * @param fillColors the colors to fill
 * @param colors the colors to use for filling
 * @param max_cols the target size for fillColors
 */
public static void fillPalette(List<PaletteColor> fillColors, List<PaletteColor> colors, int max_cols){
    while (fillColors.size() < max_cols) {
        int index = -1;
        float minDistance = -1;
        for (int i = 0; i < fillColors.size(); i++) {
            PaletteColor color1 = colors.get(i);
            for (int j = 0; j < colors.size(); j++) {
                PaletteColor color2 = colors.get(j);
                if (color1 == color2) {
                    continue;
                }
                float distance = getPixelDistance(color1, color2);
                if (index == -1 || distance < minDistance) {
                    index = j;
                    minDistance = distance;
                }
            }
        }
        PaletteColor color = colors.get(index);
        fillColors.add(color);
    }
}

public static void reducePaletteByAverageDistance(List<PaletteColor> colors, int max_cols, ReductionStrategy reductionStrategy){
    while (colors.size() > max_cols) {
        int index = -1;
        float minDistance = -1;
        for (int i = 0; i < colors.size(); i++) {
            PaletteColor color1 = colors.get(i);
            float averageDistance = 0;
            int count = 0;
            for (int j = 0; j < colors.size(); j++) {
                PaletteColor color2 = colors.get(j);
                if (color1 == color2) {
                    continue;
                }
                averageDistance += getPixelDistance(color1, color2);
                count++;
            }
            averageDistance/=count;
            if (minDistance == -1 || averageDistance < minDistance) {
                minDistance = averageDistance;
                index = i;
            }
        }
        PaletteColor removed = colors.remove(index);
        // find the color with the least distance:
        PaletteColor best = null;
        minDistance = -1;
        for (int i = 0; i < colors.size(); i++) {
            PaletteColor c = colors.get(i);
            float distance = getPixelDistance(c, removed);
            if (best == null || distance < minDistance) {
                best = c;
                minDistance = distance;
            }
        }
        best.indices.addAll(removed.indices);

    }
}
/**
 * Reduces the given color palette until it has the given max_cols size.
 * The colors that are closest in distance to other colors in the palette
 * get removed first.
 * 
 * @param colors the color palette
 * @param max_cols the maximum number of colors
 * @param reductionStrategy the reduction strategy
 */
public static void reducePalette(List<PaletteColor> colors, int max_cols, ReductionStrategy reductionStrategy){
    if (reductionStrategy == ReductionStrategy.AVERAGE_DISTANCE) {
        reducePaletteByAverageDistance(colors, max_cols, reductionStrategy);
        return;
    }
    while (colors.size() > max_cols) {
        int index1 = -1;
        int index2 = -1;
        float minDistance = -1;
        for (int i = 0; i < colors.size(); i++) {
            PaletteColor color1 = colors.get(i);
            for (int j = i+1; j < colors.size(); j++) {
                PaletteColor color2 = colors.get(j);
                if (color1 == color2) {
                    continue;
                }
                float distance = getPixelDistance(color1, color2);
                if (index1 == -1 || distance < minDistance) {
                    index1 = i;
                    index2 = j;
                    minDistance = distance;
                }
            }
        }
        PaletteColor color1 = colors.get(index1);
        PaletteColor color2 = colors.get(index2);

        switch (reductionStrategy) {
            case BETTER_CONTRAST:
                // remove the color with the lower average distance to the other palette colors
                int count = 0;
                float distance1 = 0;
                float distance2 = 0;
                for (PaletteColor c : colors) {
                    if (c != color1 && c != color2) {
                        count++;
                        distance1 += getPixelDistance(color1, c);
                        distance2 += getPixelDistance(color2, c);
                    }
                }
                if (count != 0 && distance1 != distance2) {
                    distance1 /= (float)count;
                    distance2 /= (float)count;
                    if (distance1 < distance2) {
                        // remove color 1;
                        colors.remove(index1);
                        color2.indices.addAll(color1.indices);
                    } else{
                        // remove color 2;
                        colors.remove(index2);
                        color1.indices.addAll(color2.indices);
                    }
                    break;
                }
                //$FALL-THROUGH$
            default:
                // remove the color with viewer mappings to the input pixels
                if (color1.indices.size() < color2.indices.size()) {
                    // remove color 1;
                    colors.remove(index1);
                    color2.indices.addAll(color1.indices);
                } else{
                    // remove color 2;
                    colors.remove(index2);
                    color1.indices.addAll(color2.indices);
                }
                break;
        }

    }
}

/**
 * Creates an initial color palette from the given pixels and the given palette by
 * selecting the colors with the nearest distance to the given pixels.
 * This method also stores the indices of the corresponding pixels inside the
 * returned PaletteColor instances.
 * 
 * @return the initial color palette
 * @param pixels the pixels to use
 * @param palette the palette to use 
 */
public static List<PaletteColor> createInitialPalette(int pixels[], int[] palette){
    Map<Integer, Integer> used = new HashMap<>();
    ArrayList<PaletteColor> result = new ArrayList<>();

    for (int i = 0, l = pixels.length; i < l; i++) {
        double bestDistance = Double.MAX_VALUE;
        int bestIndex = -1;

        int pixel = pixels[i];
        int r1 = (pixel >> 16) & 0xFF;
        int g1 = (pixel >> 8) & 0xFF;
        int b1 = (pixel >> 0) & 0xFF;
        for (int k = 0; k < palette.length; k++) {
            int pixel2 = palette[k];
            int r2 = (pixel2 >> 16) & 0xFF;
            int g2 = (pixel2 >> 8) & 0xFF;
            int b2 = (pixel2 >> 0) & 0xFF;
            double dist = getPixelDistance(r1, g1, b1, r2, g2, b2);
            if (dist < bestDistance) {
                bestDistance = dist;
                bestIndex = k;
            }
        }

        Integer index = used.get(bestIndex);
        PaletteColor c;
        if (index == null) {
            index = result.size();
            c = new PaletteColor(palette[bestIndex]);
            result.add(c);
            used.put(bestIndex, index);
        } else{
            c = result.get(index);
        }
        c.indices.add(i);
    }
    return result;
}

/**
 * Creates a simple random color palette
 * 
 * @param num_colors number of random colors to insert
 * @return the palette
 */
public static int[] createRandomColorPalette(int num_colors){
    Random random = new Random(101);

    int count = 0;
    int[] result = new int[num_colors];
    float add = 360f / (float)num_colors;
    for(float i = 0; i < 360f && count < num_colors; i += add) {
        float hue = i;
        float saturation = 90 +random.nextFloat() * 10;
        float brightness = 50 + random.nextFloat() * 10;
        result[count++] = Color.HSBtoRGB(hue, saturation, brightness);
    }
    return result;
}

public static int[] createGrayScalePalette(int count){
    float[] grays = new float[count];
    float step = 1f/(float)count;
    grays[0] = 0;
    for (int i = 1; i < count-1; i++) {
        grays[i]=i*step;
    }
    grays[count-1]=1;
    return createGrayScalePalette(grays);
}

/**
 * Returns a grayscale palette based on the given shades of gray
 * 
 * @param grays the sahdes of gray
 * @return the grayscale palette
 */
public static int[] createGrayScalePalette(float[] grays){
    int[] result = new int[grays.length];
    for (int i = 0; i < result.length; i++) {
        float f = grays[i];
        result[i] = Color.HSBtoRGB(0, 0, f);
    }
    return result;
}


private static int[] createResultingImage(int[] pixels,List<PaletteColor> paletteColors, boolean dither, int w, int h) {
    int[] palette = new int[paletteColors.size()];
    for (int i = 0; i < palette.length; i++) {
        palette[i] = paletteColors.get(i).color;
    }
    if (!dither) {
        for (PaletteColor c : paletteColors) {
            for (int i : c.indices) {
                pixels[i] = c.color;
            }
        }
    } else{
        FloydSteinbergDither.generateDither(pixels, palette, w, h);
    }
    return palette;
}

public static int[] quantize(int[] pixels, int widht, int heigth, int[] colorPalette, int max_cols, boolean dither, ReductionStrategy reductionStrategy) {

    // create the initial palette by finding the best match colors from the given color palette
    List<PaletteColor> paletteColors = createInitialPalette(pixels, colorPalette);

    // reduce the palette size to the given number of maximum colors
    reducePalette(paletteColors, max_cols, reductionStrategy);
    assert paletteColors.size() <= max_cols;

    if (paletteColors.size() < max_cols) {
        // fill the palette with the nearest remaining colors
        List<PaletteColor> remainingColors = new ArrayList<>();
        Set<PaletteColor> used = new HashSet<>(paletteColors);
        for (int i = 0; i < colorPalette.length; i++) {
            int color = colorPalette[i];
            PaletteColor c = new PaletteColor(color);
            if (!used.contains(c)) {
                remainingColors.add(c);
            }
        }
        fillPalette(paletteColors, remainingColors, max_cols);
    }
    assert paletteColors.size() == max_cols;

    // create the resulting image
    return createResultingImage(pixels,paletteColors, dither, widht, heigth);

}

static enum ReductionStrategy{
    ORIGINAL_COLORS,
    BETTER_CONTRAST,
    AVERAGE_DISTANCE,
}

}
