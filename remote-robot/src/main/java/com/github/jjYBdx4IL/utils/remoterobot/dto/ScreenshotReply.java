/*
 * Copyright © 2019 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.remoterobot.dto;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.Serializable;

public class ScreenshotReply extends Ack implements Serializable {

    private static final long serialVersionUID = 1L;

    public int width;
    public int height;
    public int type;
    public int[] data;

    public ScreenshotReply() {
    }

    /**
     * Create an instance.
     * 
     * @param seq
     *            the request sequence id
     * @param image
     *            the image to return
     * @return the instance
     */
    public static ScreenshotReply createFrom(long seq, BufferedImage image) {
        ScreenshotReply reply = new ScreenshotReply();
        reply.seq = seq;
        reply.width = image.getWidth();
        reply.height = image.getHeight();
        reply.type = image.getType();
        reply.data = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();
        return reply;
    }

    /**
     * Extract image from reply.
     * 
     * @return the image
     */
    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, type);
        int[] imageData = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();
        System.arraycopy(data, 0, imageData, 0, data.length);
        return image;
    }

}
