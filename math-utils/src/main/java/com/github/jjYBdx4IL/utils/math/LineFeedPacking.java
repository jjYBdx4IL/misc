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
package com.github.jjYBdx4IL.utils.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

//CHECKSTYLE:OFF
public class LineFeedPacking {

    private static final Logger LOG = LoggerFactory.getLogger(LineFeedPacking.class);

    private final int[] widths;
    private final int[] heights;
    private final float ratio;
    private long bestSolution = 0;

    private List<Point> layoutOffsets = null;
    private Dimension layoutTotalSize = null;

    /**
     * Tries to pack rectangles along lines like text (ltr). The order of the
     * childs is not changed. The returned solution is a bitmask determining
     * where line-feeds should happen: <code>soution &amp; n^2</code> is 1 if
     * there is a line-feed after the n-th rectangle (starting at 0).
     *
     * <p>
     * The algorithm used is very simple and has complexity n*2^n. It simply
     * tries all possibilities to insert a line-feed and checks the resulting
     * dimensions of the enclosing rectangle.
     * </p>
     * 
     * @param ratio
     *            x-y-ratio
     * @param widths
     *            the widths of the rectangles
     * @param heights
     *            the heights of the rectangles
     */
    public LineFeedPacking(int[] widths, int[] heights, float ratio) {
        if (widths.length == 0) {
            throw new IllegalArgumentException();
        }
        if (widths.length != heights.length) {
            throw new IllegalArgumentException();
        }
        if (widths.length > 64) {
            throw new IllegalArgumentException("too many internal frames, max 64");
        }
        this.widths = widths;
        this.heights = heights;
        this.ratio = ratio;
    }

    public long fit() {
        long limit = 1 << (widths.length - 1);
        float bestSize = Float.MAX_VALUE;
        for (long state = 0L; state < limit; state++) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("state = " + state);
            }
            int maxLineWidth = widths[0];
            int currentLineWidth = widths[0];
            int currentLineMaxHeight = heights[0];
            int totalHeight = 0;
            for (int i = 1; i < widths.length; i++) {
                // line-feed before i-th child?
                if ((state & (1 << (i - 1))) != 0) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("  lf after " + (i - 1));
                    }
                    currentLineWidth = 0;
                    totalHeight += currentLineMaxHeight;
                    currentLineMaxHeight = 0;
                }
                currentLineWidth += widths[i];
                if (currentLineWidth > maxLineWidth) {
                    maxLineWidth = currentLineWidth;
                }
                if (heights[i] > currentLineMaxHeight) {
                    currentLineMaxHeight = heights[i];
                }
            }
            totalHeight += currentLineMaxHeight;

            if (LOG.isTraceEnabled()) {
                LOG.trace(state + " " + maxLineWidth + " " + totalHeight);
            }

            float size = maxLineWidth / ratio;
            if (totalHeight > size) {
                size = totalHeight;
            }
            if (size < bestSize) {
                bestSize = size;
                bestSolution = state;
            }
        }
        return bestSolution;
    }

    void toLayout(long state) {
        if (layoutOffsets == null) {
            layoutOffsets = new ArrayList<>();
        } else {
            layoutOffsets.clear();
        }
        int maxLineWidth = widths[0];
        int currentLineWidth = widths[0];
        int currentLineMaxHeight = heights[0];
        int totalHeight = 0;
        layoutOffsets.add(new Point(0, 0));
        for (int i = 1; i < widths.length; i++) {
            // line-feed before i-th child?
            if ((state & (1 << (i - 1))) != 0) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("  lf after " + (i - 1));
                }
                currentLineWidth = 0;
                totalHeight += currentLineMaxHeight;
                currentLineMaxHeight = 0;
            }
            layoutOffsets.add(new Point(currentLineWidth, totalHeight));
            currentLineWidth += widths[i];
            if (currentLineWidth > maxLineWidth) {
                maxLineWidth = currentLineWidth;
            }
            if (heights[i] > currentLineMaxHeight) {
                currentLineMaxHeight = heights[i];
            }
        }
        totalHeight += currentLineMaxHeight;
        layoutTotalSize = new Dimension(maxLineWidth, totalHeight);
    }

    public List<Point> getLayoutOffsets() {
        if (layoutOffsets == null) {
            toLayout(bestSolution);
        }
        return layoutOffsets;
    }

    public Dimension getLayoutTotalSize() {
        if (layoutTotalSize == null) {
            toLayout(bestSolution);
        }
        return layoutTotalSize;
    }

    public long getBestSolution() {
        return bestSolution;
    }

    /**
     * Determine a scale factor to fit {@link #getLayoutTotalSize()} into
     * maxSize.
     * 
     * @param maxSize
     *            the maximum size allowed
     * @return a scale factor &lt;= 1f
     */
    public float getOptimalSizeReductionFactor(Dimension maxSize) {
        Dimension totalSize = getLayoutTotalSize();
        float scaleX = 1f * maxSize.width / totalSize.width;
        float scaleY = 1f * maxSize.height / totalSize.height;
        return Math.min(Math.min(scaleX, scaleY), 1f);
    }
}
