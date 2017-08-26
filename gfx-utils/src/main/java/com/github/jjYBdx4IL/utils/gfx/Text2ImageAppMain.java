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

import com.github.jjYBdx4IL.utils.gfx.FontEffects.ShadowType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.io.StreamDocumentSource;
import org.fit.cssbox.layout.BrowserCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

// CHECKSTYLE:OFF
/**
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("deprecation")
public class Text2ImageAppMain {

    private static final Logger LOG = LoggerFactory.getLogger(Text2ImageAppMain.class);

    // defaults
    private static final int DEFAULT_FONTSIZE = 18;
    private static final boolean DEFAULT_TRANSPARENT = false;
    private static final int DEFAULT_MARGIN = 10;
    private static final Color DEFAULT_FONTCOLOR = Color.GRAY;
    private static final boolean DEFAULT_INPUT_IS_HTML = false;
    private static final int DEFAULT_HTML_WIDTH = 800;
    private static final ShadowType DEFAULT_SHADOWTYPE = ShadowType.NONE;
    private static final int DEFAULT_SHADOW_OFFSET = 5;
    private static final boolean DEFAULT_JIGGLE = false;
    private static final long DEFAULT_JIGGLE_SEED = 0;

    private static final boolean DEFAULT_MERGE = false;
    private static final int DEFAULT_MERGE_GAP = 8;
    private static final int DEFAULT_MERGE_PADDING = 8;

    private static final boolean DEFAULT_SCALE = false;
    private static final int DEFAULT_SCALE_HEIGHT = -1;
    private static final int DEFAULT_SCALE_WIDTH = -1;

    // NOT configurable via command line:
    private int imageType = BufferedImage.TYPE_INT_RGB;
    private String fontName = "Arial"; // "Times New Roman";
    private int fontStyle = Font.PLAIN | Font.BOLD;
    private int marginLeft = DEFAULT_MARGIN;
    private int marginRight = DEFAULT_MARGIN;
    private int marginTop = DEFAULT_MARGIN;
    private int marginBottom = DEFAULT_MARGIN;

    // command line options
    private static final String PROGNAME = "text2image";
    private static final String OPTNAME_H = "h";
    private static final String OPTNAME_HELP = "help";
    private static final String OPTNAME_I = "i";
    private static final String OPTNAME_INPUT_FILENAME = "input";
    private String optInputFileName = null;
    private static final String OPTNAME_O = "o";
    private static final String OPTNAME_OUTPUT_FILENAME = "output";
    private String optOutputFileName = null;
    private static final String OPTNAME_FONTSIZE = "fontSize";
    private int optFontSize = DEFAULT_FONTSIZE;
    private static final String OPTNAME_TRANSPARENT = "transparent";
    private boolean optTransparent = DEFAULT_TRANSPARENT;
    private static final String OPTNAME_FONTCOLOR = "fontColor";
    private Color optFontColor = DEFAULT_FONTCOLOR;
    private static final String OPTNAME_INPUT_IS_HTML = "html";
    private boolean optInputIsHtml = DEFAULT_INPUT_IS_HTML;
    private static final String OPTNAME_HTML_WIDTH = "htmlWidth";
    private int optHtmlWidth = DEFAULT_HTML_WIDTH;
    private static final String OPTNAME_SHADOW_BLUR = "shadowBlur";
    private static final String OPTNAME_SHADOW_DROP = "shadowDrop";
    private ShadowType optShadowType = DEFAULT_SHADOWTYPE;
    private static final String OPTNAME_SHADOW_OFFSET = "shadowOffset";
    private int optShadowOffset = DEFAULT_SHADOW_OFFSET;
    private static final String OPTNAME_TEXT = "text";
    private String optText = null;
    private static final String OPTNAME_JIGGLE = "jiggle";
    private boolean optJiggle = DEFAULT_JIGGLE;
    private static final String OPTNAME_JIGGLE_SEED = "jiggleSeed";
    private long optJiggleSeed = DEFAULT_JIGGLE_SEED;

    private static final String OPTNAME_MERGE = "merge";
    private boolean optMerge = DEFAULT_MERGE;
    private static final String OPTNAME_MERGE_INPUT1_FILENAME = "mergeInput1";
    private String optMergeInput1FileName = null;
    private static final String OPTNAME_MERGE_INPUT2_FILENAME = "mergeInput2";
    private String optMergeInput2FileName = null;
    private static final String OPTNAME_MERGE_GAP = "mergeGap";
    private int optMergeGap = DEFAULT_MERGE_GAP;
    private static final String OPTNAME_MERGE_PADDING = "mergePadding";
    private int optMergePadding = DEFAULT_MERGE_PADDING;

    private static final String OPTNAME_SCALE = "scale";
    private static final String OPTNAME_SCALE_HEIGHT = "scaleHeight";
    private static final String OPTNAME_SCALE_WIDTH = "scaleWidth";
    private static final String OPTNAME_SCALE_INPUT_FILENAME = "scaleInput";
    private boolean optScale = DEFAULT_SCALE;
    private int optScaleHeight = DEFAULT_SCALE_HEIGHT;
    private int optScaleWidth = DEFAULT_SCALE_WIDTH;
    private String optScaleInputFileName = null;

    private static final String OPTNAME_STOP = "stop";

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (String arg : args) {
            if ("--stop".equalsIgnoreCase(arg) || "-stop".equalsIgnoreCase(arg)) {
                new Text2ImageAppMain().run(list.toArray(new String[]{}));
                list.clear();
            } else {
                list.add(arg);
            }
        }
        if (list.size() > 0) {
            new Text2ImageAppMain().run(list.toArray(new String[]{}));
            list.clear();
        }
    }

    public void run(String[] args) {
        // create the command line parser
        CommandLineParser parser = new GnuParser();

        // create the Options
        Options options = new Options();
        options.addOption(OPTNAME_I, OPTNAME_INPUT_FILENAME, true, "name of input text file (replaces --text)");
        options.addOption(OPTNAME_O, OPTNAME_OUTPUT_FILENAME, true, "name of output image, ending determines type");
        options.addOption(OPTNAME_FONTSIZE, true, String.format("font size, default: %d", DEFAULT_FONTSIZE));
        options.addOption(OPTNAME_FONTCOLOR, true, "font color, default Gray, example: 0xFF0096");
        options.addOption(OPTNAME_TRANSPARENT, false,
                String.format("transparent background, default: %s", Boolean.toString(DEFAULT_TRANSPARENT)));
        options.addOption(OPTNAME_INPUT_IS_HTML, false,
                String.format("input is html (ignored by --text), default: %s", Boolean.toString(DEFAULT_INPUT_IS_HTML)));
        options.addOption(OPTNAME_HTML_WIDTH, true,
                String.format("desired output width for html input (ignored by --text), default: %d", DEFAULT_HTML_WIDTH));
        options.addOption(OPTNAME_SHADOW_BLUR, false, "blurred shadow (use with --text only)");
        options.addOption(OPTNAME_SHADOW_DROP, false, "simple drop shadow (use with --text only)");
        options.addOption(OPTNAME_SHADOW_OFFSET, true,
                String.format("shadow offset (use with --text only), default: %d", DEFAULT_SHADOW_OFFSET));
        options.addOption(OPTNAME_TEXT, true, "input text");
        options.addOption(OPTNAME_JIGGLE, false, "jiggle (use with --text only)");
        options.addOption(OPTNAME_JIGGLE_SEED, true,
                String.format("jiggle seed (use with --text only), default: %s", DEFAULT_JIGGLE_SEED));

        options.addOption(OPTNAME_MERGE, false, "merge the two input pics");
        options.addOption(OPTNAME_MERGE_INPUT1_FILENAME, true, "name of first input file to merge");
        options.addOption(OPTNAME_MERGE_INPUT2_FILENAME, true, "name of second input file to merge");
        options.addOption(OPTNAME_MERGE_GAP, true,
                String.format("merge gap, default: %d", DEFAULT_MERGE_GAP));
        options.addOption(OPTNAME_MERGE_PADDING, true,
                String.format("merge padding, default: %d", DEFAULT_MERGE_PADDING));

        options.addOption(OPTNAME_SCALE, false, "merge the two input pics");
        options.addOption(OPTNAME_SCALE_INPUT_FILENAME, true, "name of input file to scale");
        options.addOption(OPTNAME_SCALE_WIDTH, true,
                String.format("scale target width, default: %d", DEFAULT_SCALE_WIDTH));
        options.addOption(OPTNAME_SCALE_HEIGHT, true,
                String.format("scale target height, default: %d", DEFAULT_SCALE_HEIGHT));

        options.addOption(OPTNAME_STOP, false,
                "separate output definitions to allow for multiple image defs on one command line");
        options.addOption(OPTNAME_H, OPTNAME_HELP, false, "show help (this page)");

        // parse the command line arguments
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption(OPTNAME_H)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(PROGNAME, options);
                return;
            }
            if (line.hasOption(OPTNAME_INPUT_FILENAME)) {
                setOptInputFileName(line.getOptionValue(OPTNAME_INPUT_FILENAME));
            }
            if (line.hasOption(OPTNAME_OUTPUT_FILENAME)) {
                setOptOutputFileName(line.getOptionValue(OPTNAME_OUTPUT_FILENAME));
            }
            if (line.hasOption(OPTNAME_FONTSIZE)) {
                setOptFontSize(Integer.parseInt(line.getOptionValue(OPTNAME_FONTSIZE)));
            }
            if (line.hasOption(OPTNAME_FONTCOLOR)) {
                setOptFontColor(Color.decode(line.getOptionValue(OPTNAME_FONTCOLOR)));
            }
            if (line.hasOption(OPTNAME_TRANSPARENT)) {
                setOptTransparent(true);
            }
            if (line.hasOption(OPTNAME_INPUT_IS_HTML)) {
                setOptInputIsHtml(true);
            }
            if (line.hasOption(OPTNAME_HTML_WIDTH)) {
                setOptHtmlWidth(Integer.parseInt(line.getOptionValue(OPTNAME_HTML_WIDTH)));
            }
            if (line.hasOption(OPTNAME_SHADOW_BLUR)) {
                setOptShadowType(ShadowType.BLUR);
            }
            if (line.hasOption(OPTNAME_SHADOW_DROP)) {
                setOptShadowType(ShadowType.DROP);
            }
            if (line.hasOption(OPTNAME_SHADOW_OFFSET)) {
                setOptShadowOffset(Integer.parseInt(line.getOptionValue(OPTNAME_SHADOW_OFFSET)));
            }
            if (line.hasOption(OPTNAME_TEXT)) {
                setOptText(line.getOptionValue(OPTNAME_TEXT));
            }
            if (line.hasOption(OPTNAME_JIGGLE)) {
                setOptJiggle(true);
            }
            if (line.hasOption(OPTNAME_JIGGLE_SEED)) {
                setOptJiggleSeed(Integer.parseInt(line.getOptionValue(OPTNAME_JIGGLE_SEED)));
            }

            if (line.hasOption(OPTNAME_MERGE)) {
                setOptMerge(true);
            }
            if (line.hasOption(OPTNAME_MERGE_INPUT1_FILENAME)) {
                setOptMergeInput1FileName(line.getOptionValue(OPTNAME_MERGE_INPUT1_FILENAME));
            }
            if (line.hasOption(OPTNAME_MERGE_INPUT2_FILENAME)) {
                setOptMergeInput2FileName(line.getOptionValue(OPTNAME_MERGE_INPUT2_FILENAME));
            }
            if (line.hasOption(OPTNAME_MERGE_GAP)) {
                setOptMergeGap(Integer.parseInt(line.getOptionValue(OPTNAME_MERGE_GAP)));
            }
            if (line.hasOption(OPTNAME_MERGE_PADDING)) {
                setOptMergePadding(Integer.parseInt(line.getOptionValue(OPTNAME_MERGE_PADDING)));
            }

            if (line.hasOption(OPTNAME_SCALE)) {
                setOptScale(true);
            }
            if (line.hasOption(OPTNAME_SCALE_INPUT_FILENAME)) {
                setOptScaleInputFileName(line.getOptionValue(OPTNAME_SCALE_INPUT_FILENAME));
            }
            if (line.hasOption(OPTNAME_SCALE_WIDTH)) {
                setOptScaleWidth(Integer.parseInt(line.getOptionValue(OPTNAME_SCALE_WIDTH)));
            }
            if (line.hasOption(OPTNAME_SCALE_HEIGHT)) {
                setOptScaleHeight(Integer.parseInt(line.getOptionValue(OPTNAME_SCALE_HEIGHT)));
            }
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }

        if (!isOptScale() && !isOptMerge() && getOptInputFileName() == null && getOptText() == null) {
            throw new RuntimeException("no input");
        }
        if (getOptOutputFileName() == null) {
            throw new RuntimeException("no output file name");
        }

        if (getOptFontColor() == null) {
            throw new RuntimeException("failed to set font color");
        }

        if (isOptTransparent()) {
            setImageType(BufferedImage.TYPE_INT_ARGB);
        }

        if (getOptText() != null && getOptInputFileName() != null) {
            throw new RuntimeException("thou shall not specify --text and --input at the same time!");
        }

        try {
            if (isOptScale()) {
                runScale();
            } else if (isOptMerge()) {
                runMerge();
            } else if (getOptText() != null) {
                runTextConversion();
            } else if (isOptInputIsHtml()) {
                runHtmlConversion();
            } else {
                runConversion();
            }
        } catch (IOException | SAXException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void runScale() throws IOException {
        BufferedImage image = ImageIO.read(new File(getOptScaleInputFileName()));
        int w = 0, h = 0;
        if (getOptScaleWidth() > -1 && getOptScaleHeight() > -1) { // fit into box
            if (image.getWidth() / getOptScaleWidth() > image.getHeight() / getOptScaleHeight()) {
                w = getOptScaleWidth();
                h = image.getHeight() * getOptScaleWidth() / image.getWidth();
            } else {
                w = image.getWidth() * getOptScaleHeight() / image.getHeight();
                h = getOptScaleHeight();
            }
        } else if (getOptScaleWidth() > -1) { // adjust width
            w = getOptScaleWidth();
            h = image.getHeight() * getOptScaleWidth() / image.getWidth();
        } else if (getOptScaleHeight() > -1) { // adjust height
            w = image.getWidth() * getOptScaleHeight() / image.getHeight();
            h = getOptScaleHeight();
        }
        Image tkImage = image.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
        BufferedImage scaledImage = new BufferedImage(w, h, image.getType());
        scaledImage.getGraphics().drawImage(tkImage, 0, 0, null);
        LOG.info("writing: " + getOptOutputFileName());
        ImageIO.write(scaledImage, getOutputFileNameSuffix(), new File(getOptOutputFileName()));
    }

    public void runMerge() throws IOException {
        BufferedImage image1 = ImageIO.read(new File(getOptMergeInput1FileName()));
        BufferedImage image2 = ImageIO.read(new File(getOptMergeInput2FileName()));
        LOG.info("writing: " + getOptOutputFileName());
        ImageIO.write(Merge.merge(image1, image2, getOptMergePadding(), getOptMergeGap()),
                getOutputFileNameSuffix(), new File(getOptOutputFileName()));
    }

    public void runTextConversion() throws IOException {
        FontEffects effects = new FontEffects();
        effects.setFont(new Font(getFontName(), getFontStyle(), getOptFontSize()));
        effects.setTextColor(getOptFontColor());
        effects.setTextSize(getOptFontSize());
        effects.setText(getOptText());
        effects.setShadowType(getOptShadowType());
        effects.setGlassBG(!isOptTransparent());
        effects.setShadowOffset(getOptShadowOffset());
        effects.setJiggle(isOptJiggle());
        effects.setJiggleSeed(getOptJiggleSeed());
        effects.setAntiAlias(true);
        effects.paint();
        LOG.info("writing: " + getOptOutputFileName());
        ImageIO.write(effects.getImage(), getOutputFileNameSuffix(), new File(getOptOutputFileName()));
    }

    public void runHtmlConversion() throws MalformedURLException, IOException, SAXException {
        URL url = new File(getOptInputFileName()).toURI().toURL();
        LOG.debug("reading: " + url.toString());
        DocumentSource src = new StreamDocumentSource(new ByteArrayInputStream(IOUtils.toByteArray(url)), url, "text/html");
        //Parse the input document (replace this with your own parser if desired)
        DOMSource parser = new DefaultDOMSource(src);
        Document doc = parser.parse(); //doc represents the obtained DOM

        DOMAnalyzer da = new DOMAnalyzer(doc, url);
        da.attributesToStyles(); //convert the HTML presentation attributes to inline styles
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the standard style sheet
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the additional style sheet
        da.getStyleSheets(); //load the author style sheets

        BrowserCanvas browser
                = new BrowserCanvas(da.getRoot(),
                        da,
                        new java.awt.Dimension(getOptHtmlWidth(), 1),
                        url);
        // we need to change the image type so we can support transparent backgrounds. However, that also
        // destroys the auto-resize capability, so we need to do two passes: the first one the determine
        // the optimal image height, the second one to draw on the changed image type.
        int height = browser.getImage().getHeight();
        int width = browser.getImage().getWidth();
        browser.setImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        browser.createLayout(new java.awt.Dimension(width, height));
        LOG.info("writing: " + getOptOutputFileName());
        ImageIO.write(browser.getImage(), getOutputFileNameSuffix(), new File(getOptOutputFileName()));
    }

    public void runConversion() throws IOException {
        Font font = new Font(getFontName(), getFontStyle(), getOptFontSize());

        LOG.debug("reading: " + getOptInputFileName());
        String text = IOUtils.toString(new FileInputStream(getOptInputFileName()), Charset.forName("UTF-8"));
        String[] lines = text.split("\r?\n");

        // first do a test run to determine the exact image dimensions
        BufferedImage image = new BufferedImage(100, 100, getImageType());
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setFont(font);
        FontMetrics fm = graphics.getFontMetrics(font);
        int fontHeight = fm.getHeight();

        int height = fontHeight * lines.length;
        int maxWidth = 1;

        for (String line : lines) {
            int lineWidth = 0;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                int charWidth = fm.charWidth(c);
                lineWidth += charWidth;
            }
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        // now produce the image:
        image = new BufferedImage(maxWidth + marginLeft + marginRight,
                height + marginTop + marginBottom, getImageType());
        graphics = (Graphics2D) image.getGraphics();
        graphics.setFont(font);
        graphics.setColor(getOptFontColor());
        fm = graphics.getFontMetrics(font);

        int y = fm.getAscent() + marginTop;
        for (String line : lines) {
            int x = marginLeft;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                int charWidth = fm.charWidth(c);
                graphics.drawString(String.valueOf(c), x, y);
                x += charWidth;
            }
            y += fontHeight;
        }

        LOG.info("writing: " + getOptOutputFileName());
        ImageIO.write(image, getOutputFileNameSuffix(), new File(getOptOutputFileName()));
    }

    private String getOutputFileNameSuffix() {
        String[] parts = getOptOutputFileName().split("[.]");
        return parts[parts.length - 1];
    }

    /**
     * @return the optInputFileName
     */
    public String getOptInputFileName() {
        return optInputFileName;
    }

    /**
     * @param aOptInputFileName the optInputFileName to set
     */
    public void setOptInputFileName(String aOptInputFileName) {
        optInputFileName = aOptInputFileName;
    }

    /**
     * @return the optOutputFileName
     */
    public String getOptOutputFileName() {
        return optOutputFileName;
    }

    /**
     * @param aOptOutputFileName the optOutputFileName to set
     */
    public void setOptOutputFileName(String aOptOutputFileName) {
        optOutputFileName = aOptOutputFileName;
    }

    /**
     * @return the optFontSize
     */
    public int getOptFontSize() {
        return optFontSize;
    }

    /**
     * @param aFontSize the optFontSize to set
     */
    public void setOptFontSize(int aFontSize) {
        optFontSize = aFontSize;
    }

    /**
     * @return the imageType
     */
    public int getImageType() {
        return imageType;
    }

    /**
     * @param aImageType the imageType to set
     */
    public void setImageType(int aImageType) {
        imageType = aImageType;
    }

    /**
     * @return the fontName
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * @param aFontName the fontName to set
     */
    public void setFontName(String aFontName) {
        fontName = aFontName;
    }

    /**
     * @return the fontStyle
     */
    public int getFontStyle() {
        return fontStyle;
    }

    /**
     * @param aFontStyle the fontStyle to set
     */
    public void setFontStyle(int aFontStyle) {
        fontStyle = aFontStyle;
    }

    /**
     * @return the optTransparent
     */
    public boolean isOptTransparent() {
        return optTransparent;
    }

    /**
     * @param aOptTransparent the optTransparent to set
     */
    public void setOptTransparent(boolean aOptTransparent) {
        optTransparent = aOptTransparent;
    }

    /**
     * @return the optFontColor
     */
    public Color getOptFontColor() {
        return optFontColor;
    }

    /**
     * @param aOptFontColor the optFontColor to set
     */
    public void setOptFontColor(Color aOptFontColor) {
        optFontColor = aOptFontColor;
    }

    /**
     * @return the optInputIsHtml
     */
    public boolean isOptInputIsHtml() {
        return optInputIsHtml;
    }

    /**
     * @param aOptInputIsHtml the optInputIsHtml to set
     */
    public void setOptInputIsHtml(boolean aOptInputIsHtml) {
        optInputIsHtml = aOptInputIsHtml;
    }

    /**
     * @return the optHtmlWidth
     */
    public int getOptHtmlWidth() {
        return optHtmlWidth;
    }

    /**
     * @param aOptHtmlWidth the optHtmlWidth to set
     */
    public void setOptHtmlWidth(int aOptHtmlWidth) {
        optHtmlWidth = aOptHtmlWidth;
    }

    /**
     * @return the optShadowType
     */
    public ShadowType getOptShadowType() {
        return optShadowType;
    }

    /**
     * @param optShadowType the optShadowType to set
     */
    public void setOptShadowType(ShadowType optShadowType) {
        this.optShadowType = optShadowType;
    }

    /**
     * @return the optShadowOffset
     */
    public int getOptShadowOffset() {
        return optShadowOffset;
    }

    /**
     * @param optShadowOffset the optShadowOffset to set
     */
    public void setOptShadowOffset(int optShadowOffset) {
        this.optShadowOffset = optShadowOffset;
    }

    /**
     * @return the optText
     */
    public String getOptText() {
        return optText;
    }

    /**
     * @param optText the optText to set
     */
    public void setOptText(String optText) {
        this.optText = optText;
    }

    /**
     * @return the optJiggle
     */
    public boolean isOptJiggle() {
        return optJiggle;
    }

    /**
     * @param optJiggle the optJiggle to set
     */
    public void setOptJiggle(boolean optJiggle) {
        this.optJiggle = optJiggle;
    }

    /**
     * @return the optJiggleSeed
     */
    public long getOptJiggleSeed() {
        return optJiggleSeed;
    }

    /**
     * @param optJiggleSeed the optJiggleSeed to set
     */
    public void setOptJiggleSeed(long optJiggleSeed) {
        this.optJiggleSeed = optJiggleSeed;
    }

    /**
     * @return the optMerge
     */
    public boolean isOptMerge() {
        return optMerge;
    }

    /**
     * @param optMerge the optMerge to set
     */
    public void setOptMerge(boolean optMerge) {
        this.optMerge = optMerge;
    }

    /**
     * @return the optMergeGap
     */
    public int getOptMergeGap() {
        return optMergeGap;
    }

    /**
     * @param optMergeGap the optMergeGap to set
     */
    public void setOptMergeGap(int optMergeGap) {
        this.optMergeGap = optMergeGap;
    }

    /**
     * @return the optMergePadding
     */
    public int getOptMergePadding() {
        return optMergePadding;
    }

    /**
     * @param optMergePadding the optMergePadding to set
     */
    public void setOptMergePadding(int optMergePadding) {
        this.optMergePadding = optMergePadding;
    }

    /**
     * @return the optMergeInput1FileName
     */
    public String getOptMergeInput1FileName() {
        return optMergeInput1FileName;
    }

    /**
     * @param optMergeInput1FileName the optMergeInput1FileName to set
     */
    public void setOptMergeInput1FileName(String optMergeInput1FileName) {
        this.optMergeInput1FileName = optMergeInput1FileName;
    }

    /**
     * @return the optMergeInput2FileName
     */
    public String getOptMergeInput2FileName() {
        return optMergeInput2FileName;
    }

    /**
     * @param optMergeInput2FileName the optMergeInput2FileName to set
     */
    public void setOptMergeInput2FileName(String optMergeInput2FileName) {
        this.optMergeInput2FileName = optMergeInput2FileName;
    }

    /**
     * @return the optScaleHeight
     */
    public int getOptScaleHeight() {
        return optScaleHeight;
    }

    /**
     * @param optScaleHeight the optScaleHeight to set
     */
    public void setOptScaleHeight(int optScaleHeight) {
        this.optScaleHeight = optScaleHeight;
    }

    /**
     * @return the optScaleWidth
     */
    public int getOptScaleWidth() {
        return optScaleWidth;
    }

    /**
     * @param optScaleWidth the optScaleWidth to set
     */
    public void setOptScaleWidth(int optScaleWidth) {
        this.optScaleWidth = optScaleWidth;
    }

    /**
     * @return the optScaleInputFileName
     */
    public String getOptScaleInputFileName() {
        return optScaleInputFileName;
    }

    /**
     * @param optScaleInputFileName the optScaleInputFileName to set
     */
    public void setOptScaleInputFileName(String optScaleInputFileName) {
        this.optScaleInputFileName = optScaleInputFileName;
    }

    /**
     * @return the optScale
     */
    public boolean isOptScale() {
        return optScale;
    }

    /**
     * @param optScale the optScale to set
     */
    public void setOptScale(boolean optScale) {
        this.optScale = optScale;
    }
}
