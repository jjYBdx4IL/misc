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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JInternalFrame;

//CHECKSTYLE:OFF
@SuppressWarnings("serial")
public class MdiInternalImageFrame extends JInternalFrame {

    private static final Logger LOG = LoggerFactory.getLogger(MdiInternalImageFrame.class);

    protected final BufferedImage image;
    protected final boolean stretch;

    public MdiInternalImageFrame(BufferedImage image) {
        this(image, true);
    }

    public MdiInternalImageFrame(String title, BufferedImage image) {
        this(title, image, true);
    }

    public MdiInternalImageFrame(BufferedImage image, boolean stretch) {
        super();
        this.image = image;
        this.stretch = stretch;
        hookItUp();
    }

    public MdiInternalImageFrame(String title, BufferedImage image, boolean stretch) {
        super(title);
        this.image = image;
        this.stretch = stretch;
        hookItUp();
    }

    protected void hookItUp() {
        setResizable(true);
        setMaximizable(true);
        
        Container contentPane = new Container() {
            @Override
            public void paint(Graphics g) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("paint() " + this);
                }
                super.paint(g);

                Graphics2D g2 = (Graphics2D) g;

                Rectangle r = g.getClipBounds();
                LOG.trace("paint() " + r);

                if (stretch) {
                    // repainting only the clipping area does not work well with
                    // trivial, localized scaling,
                    // so we have to redraw the entire container for now (or
                    // introduce a buffered scaled instance of
                    // the image)
                    int dx1 = 0;
                    int dy1 = 0;
                    int dx2 = getSize().width - 1;
                    int dy2 = getSize().height - 1;

                    double xscale = 1d * getSize().width / image.getWidth();
                    double yscale = 1d * getSize().height / image.getHeight();
                    int sx1 = (int) Math.round(dx1 / xscale);
                    int sy1 = (int) Math.round(dy1 / yscale);
                    int sx2 = (int) Math.round(dx2 / xscale);
                    int sy2 = (int) Math.round(dy2 / yscale);
                    g2.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
                } else {
                    int dx1 = r.x;
                    int dy1 = r.y;
                    int dx2 = r.x + r.width;
                    int dy2 = r.y + r.height;
                    g2.drawImage(image, dx1, dy1, dx2, dy2, dx1, dy1, dx2, dy2, null);
                }
            }

            @Override
            public void repaint() {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("repaint()");
                }
                super.repaint();
            }
        };
        setContentPane(contentPane);
        contentPane.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    public void setPreferredImageSize(Dimension dimension) {
        getContentPane().setPreferredSize(dimension);
    }
}
