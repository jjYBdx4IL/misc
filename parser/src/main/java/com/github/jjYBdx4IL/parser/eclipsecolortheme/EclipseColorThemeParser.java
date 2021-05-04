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
package com.github.jjYBdx4IL.parser.eclipsecolortheme;

import com.github.jjYBdx4IL.parser.ParseException;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class EclipseColorThemeParser {

    /**
     * Load XML-serialized Eclipse [tm] color themes available on http://www.eclipsecolorthemes.org/. 
     * 
     * @param xmlInputStream the xml-serialized color theme from http://www.eclipsecolorthemes.org/
     * @return the {@link ColorTheme}
     * @throws ParseException thrown if there is an issue parsing the input XML
     */
    public static ColorTheme load(InputStream xmlInputStream) throws ParseException {

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(ColorTheme.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (ColorTheme) jaxbUnmarshaller.unmarshal(xmlInputStream);
        } catch (JAXBException e) {
            throw new ParseException(e);
        }
    }
}
