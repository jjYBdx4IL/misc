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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Partially based on work done by <a href="http://walkersoftware.net/articles/text-effects-in-java">Adam
 * Walker</a> &lt;adam@walkersoftware.net&gt; and <a
 * href="http://www.java-gaming.org/index.php?topic=15199.msg121236#msg121236">others</a>.
 *
 * @author Github jjYBdx4IL Projects
 */
public class FontEffects {

    private static final Logger LOG = LoggerFactory.getLogger(FontEffects.class);

    public static void paintGlassEffect(Graphics2D g, Shape s, Color c) {
        Paint oldPaint = g.getPaint();
        Rectangle r = s.getBounds();
        LOG.debug("shape bounds = " + r);
        int h = r.height / 5;
        Shape oldClip = g.getClip();
        GradientPaint bgp = new GradientPaint(r.x, r.y + h * 3, c,
                r.x, r.y + h * 5, c.darker());
        g.setClip(r.x, r.y + (h * 3), r.width, r.height - (h * 3));//h*2);
        g.setPaint(bgp);
        g.fill(s);
        bgp = new GradientPaint(r.x, r.y, c.brighter(),
                r.x, r.y + h * 2, c);
        g.setClip(r.x, r.y, r.width, h * 2);
        g.setPaint(bgp);
        g.fill(s);
        g.setPaint(oldPaint);
        g.setClip(oldClip);
    }

    public static void paintDropShadow(Graphics2D g, int ds, Shape s) {
        g.translate(ds, ds);
        Rectangle2D r = s.getBounds2D();
        float x = (float) r.getX();
        float y = (float) r.getY();
        float h = (float) r.getHeight();
        Color gradientStart = new Color(0, 0, 0, 120);
        Color gradientEnd = new Color(0, 0, 0, 40);
        GradientPaint gp = new GradientPaint(x, y, gradientStart,
                x, y + h, gradientEnd);
        Paint p = g.getPaint();
        g.setPaint(gp);
        g.fill(s);
        g.translate(-ds, -ds);
        g.setPaint(p);
    }

    public static void paintBevel(Graphics2D g, Shape shape, Shape outline, boolean inverted) {
        int jitter = 1;
        Rectangle r = shape.getBounds();

        Paint dark = new GradientPaint(r.x, r.y, new Color(0, 0, 0, 150),
                r.x, r.y + r.height, new Color(0, 0, 0, 210));
        Paint light = new GradientPaint(r.x, r.y, new Color(255, 255, 255, 230),
                r.x, r.y + r.height, new Color(255, 255, 255, 180));
        Paint oldPaint = g.getPaint();
        // Dark section of bevel
        g.setPaint(inverted ? light : dark);
        g.translate(jitter, jitter);
        g.setClip(shape);
        g.translate(-jitter, -jitter);
        g.fill(outline);
        // Light section of Bevel
        g.translate(-jitter, -jitter);
        g.setClip(shape);
        g.translate(jitter, jitter);
        g.setPaint(inverted ? dark : light);
        g.fill(outline);
        g.setClip(null);
        g.setPaint(oldPaint);
    }

    private Shape shape = null;
    private Shape outline = null;
    private String text = "Default Text";
    private boolean inverted = false;
    private boolean jiggle = true;
    private boolean drawBevel = true;
    private float fatten = 1.3f;
    private Color textColor = new Color(175, 175, 180);
    private int textSize = 72;//18;
    private boolean glassBG = true;
    private boolean glassText = true;
    private Font font = new Font("Arial", Font.PLAIN | Font.BOLD, 50);
    private Color background = Color.LIGHT_GRAY;
    private BufferedImage image;
    private int padding = 15;
    private boolean autoResize;
    private boolean antiAlias = true;
    private ShadowType shadowType = ShadowType.DROP;
    private int shadowOffset = 3;
    private long jiggleSeed = 0;

    public FontEffects() {
        this.image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        setAutoResize(true);
    }

    public FontEffects(BufferedImage image) {
        this.image = image;
        setAutoResize(false);
    }

    /**
     * @return the shadowType
     */
    public ShadowType getShadowType() {
        return shadowType;
    }

    /**
     * @param shadowType the shadowType to set
     */
    public void setShadowType(ShadowType shadowType) {
        this.shadowType = shadowType;
    }

    /**
     * @return the shadowOffset
     */
    public int getShadowOffset() {
        return shadowOffset;
    }

    /**
     * @param shadowOffset the shadowOffset to set
     */
    public void setShadowOffset(int shadowOffset) {
        this.shadowOffset = shadowOffset;
    }


    /**
     * @return the jiggleSeed
     */
    public long getJiggleSeed() {
        return jiggleSeed;
    }

    /**
     * @param jiggleSeed the jiggleSeed to set
     */
    public void setJiggleSeed(long jiggleSeed) {
        this.jiggleSeed = jiggleSeed;
    }

    public void setText(String s) {
        shape = null;
        outline = null;
        text = s;
    }

    private Graphics2D getGraphics2D() {
        Graphics2D g = (Graphics2D) getImage().getGraphics();
        RenderingHints hints = new RenderingHints(null);
        if (isAntiAlias()) {
            hints.put(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        if (isAntiAlias()) {
            hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        hints.put(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        hints.put(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
        hints.put(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHints(hints);
        return g;
    }

    public void paint() {
        Graphics2D g = getGraphics2D();
        if (shape == null) {
            Font font = getFont();
            font = font.deriveFont(Font.BOLD, textSize);
            Stroke bs = new BasicStroke(fatten);
            GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(),
                    text);
            int maxTilt = 20;
            GeneralPath path = new GeneralPath();
            Random random = new Random(getJiggleSeed());

            if (jiggle) {
                for (int i = 0; i < gv.getNumGlyphs(); i++) {
                    double r = (random.nextInt(2 * maxTilt) - maxTilt) * Math.toRadians(1.0);
                    AffineTransform trans = gv.getGlyphTransform(i);
                    Shape sh = gv.getGlyphOutline(i);
                    Rectangle shr = sh.getBounds();
                    double cx = shr.getCenterX();
                    double cy = shr.getCenterY();
                    if (trans == null) {
                        trans = new AffineTransform();
                    }
                    trans.rotate(r, cx, cy);
                    path.transform(trans);
                    path.append(sh, false);
                    try {
                        path.transform(trans.createInverse());
                    } catch (Exception e) {
                        System.out.println("foo");
                    }
                }
            } else {
                path.append(gv.getOutline(), false);
            }
            if (fatten == 0) {
                shape = path;
            } else {
                // What we need is something like photoshop's expand selection.

                // The following is very slow...
                Area area = new Area(path);
                area.add(new Area(bs.createStrokedShape(path)));
                shape = area;

                // This creates a disconnected outline, which isn't right,
                // but much faster ;)
                /*GeneralPath p2 = new GeneralPath(path);
                 p2.append(bs.createStrokedShape(path), false);
                 shape = p2;*/
            }

            /*if(ringed){
             Area area = null;
             Area comp = new Area();;
             //if(shape instanceof Area)
             //    area = (Area)shape;
             //else
             area = new Area(shape);
             Stroke empty = new BasicStroke(10);
             comp.add(new Area(shape));
             comp.add(new Area(empty.createStrokedShape(shape)));
             Stroke ring = new BasicStroke(3);
             area.add(new Area(ring.createStrokedShape(comp)));
             shape = area;
             }*/
            Stroke stroke = new BasicStroke(2);
            outline = stroke.createStrokedShape(shape);
        }
        Rectangle r = shape.getBounds();
        LOG.debug("glyph shape bounds = " + r);

        if (isAutoResize()) {
            setImage(new BufferedImage(r.getBounds().width + 2 * getPadding() + getShadowOffset(),
                    r.getBounds().height + 2 * getPadding() + getShadowOffset(),
                    getImage().getType()));
            g = getGraphics2D();
        }

        //g.clearRect(0,0,getWidth(),getHeight());
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        Rectangle bg = new Rectangle(0, 0, getWidth(), getHeight());
        if (glassBG) {
            paintGlassEffect(g, bg, getBackground());
        }

        int dx = (getWidth() - r.width - getShadowOffset()) / 2;
        int dy = (getHeight() - r.height - getShadowOffset()) / 2;
        g.setColor(Color.BLACK);
        g.translate(dx - r.x, dy - r.y);
        // Show bounding box
        //g.draw(r);
        paintText(g);
        g.translate(r.x - dx, r.y - dy);
        g.setColor(getTextColor());
    }

    public void paintText(Graphics2D g) {
        //Drop shadow
        if (ShadowType.DROP.equals(getShadowType())) {
            paintDropShadow(g, getShadowOffset(), shape);
        } else if (ShadowType.BLUR.equals(getShadowType())) {
            paintBlurShadow(g, getShadowOffset(), shape);
        }

        // Main Text
        g.setColor(textColor);
        g.fill(shape);
        if (glassText) {
            paintGlassEffect(g, shape, textColor);
        }

        if (drawBevel) {
            paintBevel(g, shape, outline, inverted);
        }
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public boolean isJiggle() {
        return jiggle;
    }

    public void setJiggle(boolean jiggle) {
        this.jiggle = jiggle;
    }

    public boolean isDrawBevel() {
        return drawBevel;
    }

    public void setDrawBevel(boolean drawBevel) {
        this.drawBevel = drawBevel;
    }

    public float getFatten() {
        return fatten;
    }

    public void setFatten(float fatten) {
        this.fatten = fatten;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public boolean isGlassBG() {
        return glassBG;
    }

    public void setGlassBG(boolean glassBG) {
        this.glassBG = glassBG;
    }

    public boolean isGlassText() {
        return glassText;
    }

    public void setGlassText(boolean glassText) {
        this.glassText = glassText;
    }

    public void paintBlurShadow(Graphics2D g, float ds, Shape shadow) {
        Kernel kernel = new Kernel(5, 5,
                new float[]{
                    0.05f, 0.1f, 0.1f, 0.1f, 0.05f,
                    0.1f, 0.2f, 0.2f, 0.2f, 0.1f,
                    0.1f, 0.2f, 0.2f, 0.2f, 0.1f,
                    0.1f, 0.2f, 0.2f, 0.2f, 0.1f,
                    0.05f, 0.1f, 0.1f, 0.1f, 0.05f,});
        ConvolveOp blur = new ConvolveOp(kernel);

        BufferedImage backgrd = new BufferedImage(getWidth(), getHeight(), getImage().getType());

        Graphics2D gBack = backgrd.createGraphics();
        gBack.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBack.setColor(new Color(0f, 0f, 0f, 0.3f));
        gBack.translate((int) g.getTransform().getTranslateX() + ds, (int) g.getTransform().getTranslateY() + ds);
        gBack.fill(shadow);
        blur.filter(backgrd, getImage());
    }

    /**
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * @return the background
     */
    public Color getBackground() {
        return background;
    }

    /**
     * @param background the background to set
     */
    public void setBackground(Color background) {
        this.background = background;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return getImage().getWidth();
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return getImage().getHeight();
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * @return the padding
     */
    public int getPadding() {
        return padding;
    }

    /**
     * @param padding the padding to set
     */
    public void setPadding(int padding) {
        this.padding = padding;
    }

    /**
     * @return the autoResize
     */
    public boolean isAutoResize() {
        return autoResize;
    }

    /**
     * @param autoResize the autoResize to set
     */
    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    /**
     * @return the antiAlias
     */
    public boolean isAntiAlias() {
        return antiAlias;
    }

    /**
     * @param antiAlias the antiAlias to set
     */
    public void setAntiAlias(boolean antiAlias) {
        this.antiAlias = antiAlias;
    }

    public enum ShadowType {

        DROP, BLUR, NONE
    }

}
