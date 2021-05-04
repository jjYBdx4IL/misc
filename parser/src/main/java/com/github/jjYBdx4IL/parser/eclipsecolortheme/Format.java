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

import java.awt.Color;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Format {

    @XmlAttribute
    private boolean bold = false;
    @XmlAttribute
    private boolean underline = false;
    @XmlAttribute
    private boolean strikethrough = false;
    @XmlAttribute
    @XmlJavaTypeAdapter(HtmlColorJaxbAdapter.class)
    private Color color = null;

    public boolean isBold() {
        return bold;
    }

    public boolean isUnderline() {
        return underline;
    }

    public boolean isStrikethrough() {
        return strikethrough;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Format [bold=").append(bold).append(", underline=").append(underline).append(", strikethrough=")
                .append(strikethrough).append(", color=").append(color).append("]");
        return builder.toString();
    }

}
