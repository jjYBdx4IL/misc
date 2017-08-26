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
package com.github.jjYBdx4IL.test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public enum GraphicsResource {

    OPENIMAJ_TESTRES_SIGNTEXT("/org/openimaj/image/text/extraction/signtext.jpg"),
    OPENIMAJ_TESTRES_AESTHETICODE("/org/openimaj/image/contour/aestheticode/aestheticode.jpg");
    private final String value;

    GraphicsResource(String value) {
        this.value = value;
    }

    public BufferedImage loadImage() {
        try (InputStream is = getClass().getResourceAsStream(value)) {
            return ImageIO.read(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
