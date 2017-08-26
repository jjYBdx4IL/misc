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
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AnimatedGifOutputStream implements Closeable {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(AnimatedGifOutputStream.class);

    /**
     * Returns an existing child node, or creates and returns a new child node
     * (if the requested node does not exist).
     *
     * @param rootNode
     *            the <tt>IIOMetadataNode</tt> to search for the child node.
     * @param nodeName
     *            the name of the child node.
     *
     * @return the child node, if found or a new node created with the given
     *         name.
     */
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return (node);
    }

    protected final ImageOutputStream ios;
    public final static int DEFAULT_TIME_BETWEEN_FRAMES_MILLIS = 40;
    protected final int timeBetweenFramesMillis;
    protected ImageWriter writer = null;
    protected ImageWriteParam param = null;
    protected boolean loopContinuously = true;
    protected IIOMetadata imageMetaData = null;

    public AnimatedGifOutputStream(File output) throws IOException {
        this(output, DEFAULT_TIME_BETWEEN_FRAMES_MILLIS);
    }

    public AnimatedGifOutputStream(File output, int timeBetweenFramesMillis) throws IOException {
        ios = new FileImageOutputStream(output);
        this.timeBetweenFramesMillis = timeBetweenFramesMillis;
    }

    protected void init(BufferedImage sampleImage) throws IOException {

        writer = ImageIO.getImageWritersBySuffix("gif").next();
        param = writer.getDefaultWriteParam();
        // param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        // log.info(param.getCompressionType());

        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(sampleImage.getType());

        if (sampleImage.getColorModel() instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel) sampleImage.getColorModel();
            if (icm.getNumColorComponents() != 3) {
                throw new IllegalArgumentException(
                        AnimatedGifOutputStream.class.getSimpleName() + " requires 3 color components");
            }
            int mapSize = icm.getMapSize();
            byte[] r = new byte[mapSize];
            byte[] g = new byte[mapSize];
            byte[] b = new byte[mapSize];
            byte[] a = null;
            icm.getReds(r);
            icm.getGreens(g);
            icm.getBlues(b);
            if (icm.hasAlpha()) {
                a = new byte[mapSize];
                icm.getAlphas(a);
            }
            imageTypeSpecifier = ImageTypeSpecifier.createIndexed(r, g, b, a, 8, DataBuffer.TYPE_BYTE);
        }

        imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, param);
        // imageMetaData = writer.getDefaultImageMetadata(
        // ImageTypeSpecifier.createFromBufferedImageType(sampleImage.getType()),
        // param);
        //
        //
        // sampleImage.
        // ImageTypeSpecifier.createIndexed(redLUT, greenLUT, blueLUT, alphaLUT,
        // timeBetweenFramesMillis, timeBetweenFramesMillis);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMillis / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "No comment.");

        IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");

        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop = loopContinuously ? 0 : 1;

        child.setUserObject(new byte[] { 0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF) });
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        writer.setOutput(ios);

        writer.prepareWriteSequence(null);

    }

    public void append(BufferedImage img) throws IOException {
        if (writer == null) {
            init(img);
        }

        writer.writeToSequence(new IIOImage(img, null, imageMetaData), param);
    }

    @Override
    public void close() throws IOException {
        writer.endWriteSequence();
        ios.close();
    }

}
