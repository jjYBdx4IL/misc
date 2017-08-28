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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ColorTheme {
    @XmlAttribute
    private Long id = null;
    @XmlAttribute
    private String name = null;
    @XmlAttribute
    private String modified = null;
    @XmlAttribute
    private String author = null;
    @XmlElement
    private Format searchResultIndication = null;
    @XmlElement
    private Format filteredSearchResultIndication = null;
    @XmlElement
    private Format occurrenceIndication = null;
    @XmlElement
    private Format writeOccurrenceIndication = null;
    @XmlElement
    private Format findScope = null;
    @XmlElement
    private Format deletionIndication = null;
    @XmlElement
    private Format sourceHoverBackground = null;
    @XmlElement
    private Format singleLineComment = null;
    @XmlElement
    private Format multiLineComment = null;
    @XmlElement
    private Format commentTaskTag = null;
    @XmlElement
    private Format javadoc = null;
    @XmlElement
    private Format javadocLink = null;
    @XmlElement
    private Format javadocTag = null;
    @XmlElement
    private Format javadocKeyword = null;
    @XmlElement(name = "class")
    private Format klass = null;
    @XmlElement(name = "interface")
    private Format iface = null;
    @XmlElement
    private Format method = null;
    @XmlElement
    private Format methodDeclaration = null;
    @XmlElement
    private Format bracket = null;
    @XmlElement
    private Format number = null;
    @XmlElement
    private Format string = null;
    @XmlElement
    private Format operator = null;
    @XmlElement
    private Format keyword = null;
    @XmlElement
    private Format annotation = null;
    @XmlElement
    private Format staticMethod = null;
    /**
     * Also sets the bracket color for xml tags.
     */
    @XmlElement
    private Format localVariable = null;
    /**
     * Also sets the color for xml tags.
     */
    @XmlElement
    private Format localVariableDeclaration = null;
    @XmlElement
    private Format field = null;
    @XmlElement
    private Format staticField = null;
    @XmlElement
    private Format staticFinalField = null;
    @XmlElement
    private Format deprecatedMember = null;
    @XmlElement(name = "enum")
    private Format enumeration = null;
    @XmlElement
    private Format inheritedMethod = null;
    @XmlElement
    private Format abstractMethod = null;
    @XmlElement
    private Format parameterVariable = null;
    @XmlElement
    private Format typeArgument = null;
    @XmlElement
    private Format typeParameter = null;
    @XmlElement
    private Format constant = null;
    @XmlElement
    private Format background = null;
    @XmlElement
    private Format currentLine = null;
    @XmlElement
    private Format foreground = null;
    @XmlElement
    private Format lineNumber = null;
    @XmlElement
    private Format selectionBackground = null;
    @XmlElement
    private Format selectionForeground = null;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModified() {
        return modified;
    }

    public String getAuthor() {
        return author;
    }

    public Format getSearchResultIndication() {
        return searchResultIndication;
    }

    public Format getFilteredSearchResultIndication() {
        return filteredSearchResultIndication;
    }

    public Format getOccurrenceIndication() {
        return occurrenceIndication;
    }

    public Format getWriteOccurrenceIndication() {
        return writeOccurrenceIndication;
    }

    public Format getFindScope() {
        return findScope;
    }

    public Format getDeletionIndication() {
        return deletionIndication;
    }

    public Format getSourceHoverBackground() {
        return sourceHoverBackground;
    }

    public Format getSingleLineComment() {
        return singleLineComment;
    }

    public Format getMultiLineComment() {
        return multiLineComment;
    }

    public Format getCommentTaskTag() {
        return commentTaskTag;
    }

    public Format getJavadoc() {
        return javadoc;
    }

    public Format getJavadocLink() {
        return javadocLink;
    }

    public Format getJavadocTag() {
        return javadocTag;
    }

    public Format getJavadocKeyword() {
        return javadocKeyword;
    }

    public Format getKlass() {
        return klass;
    }

    public Format getInterface() {
        return iface;
    }

    public Format getMethod() {
        return method;
    }

    public Format getMethodDeclaration() {
        return methodDeclaration;
    }

    public Format getBracket() {
        return bracket;
    }

    public Format getNumber() {
        return number;
    }

    public Format getString() {
        return string;
    }

    public Format getOperator() {
        return operator;
    }

    public Format getKeyword() {
        return keyword;
    }

    public Format getAnnotation() {
        return annotation;
    }

    public Format getStaticMethod() {
        return staticMethod;
    }

    public Format getLocalVariable() {
        return localVariable;
    }

    public Format getLocalVariableDeclaration() {
        return localVariableDeclaration;
    }

    public Format getField() {
        return field;
    }

    public Format getStaticField() {
        return staticField;
    }

    public Format getStaticFinalField() {
        return staticFinalField;
    }

    public Format getDeprecatedMember() {
        return deprecatedMember;
    }

    public Format getEnum() {
        return enumeration;
    }

    public Format getInheritedMethod() {
        return inheritedMethod;
    }

    public Format getAbstractMethod() {
        return abstractMethod;
    }

    public Format getParameterVariable() {
        return parameterVariable;
    }

    public Format getTypeArgument() {
        return typeArgument;
    }

    public Format getTypeParameter() {
        return typeParameter;
    }

    public Format getConstant() {
        return constant;
    }

    public Format getBackground() {
        return background;
    }

    public Format getCurrentLine() {
        return currentLine;
    }

    public Format getForeground() {
        return foreground;
    }

    public Format getLineNumber() {
        return lineNumber;
    }

    public Format getSelectionBackground() {
        return selectionBackground;
    }

    public Format getSelectionForeground() {
        return selectionForeground;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ColorTheme [id=").append(id).append(", name=").append(name).append(", modified=")
                .append(modified).append(", author=").append(author).append(", searchResultIndication=")
                .append(searchResultIndication).append(", filteredSearchResultIndication=")
                .append(filteredSearchResultIndication).append(", occurrenceIndication=").append(occurrenceIndication)
                .append(", writeOccurrenceIndication=").append(writeOccurrenceIndication).append(", findScope=")
                .append(findScope).append(", deletionIndication=").append(deletionIndication)
                .append(", sourceHoverBackground=").append(sourceHoverBackground).append(", singleLineComment=")
                .append(singleLineComment).append(", multiLineComment=").append(multiLineComment)
                .append(", commentTaskTag=").append(commentTaskTag).append(", javadoc=").append(javadoc)
                .append(", javadocLink=").append(javadocLink).append(", javadocTag=").append(javadocTag)
                .append(", javadocKeyword=").append(javadocKeyword).append(", _class=").append(klass)
                .append(", _interface=").append(iface).append(", method=").append(method)
                .append(", methodDeclaration=").append(methodDeclaration).append(", bracket=").append(bracket)
                .append(", number=").append(number).append(", string=").append(string).append(", operator=")
                .append(operator).append(", keyword=").append(keyword).append(", annotation=").append(annotation)
                .append(", staticMethod=").append(staticMethod).append(", localVariable=").append(localVariable)
                .append(", localVariableDeclaration=").append(localVariableDeclaration).append(", field=").append(field)
                .append(", staticField=").append(staticField).append(", staticFinalField=").append(staticFinalField)
                .append(", deprecatedMember=").append(deprecatedMember).append(", _enum=").append(enumeration)
                .append(", inheritedMethod=").append(inheritedMethod).append(", abstractMethod=").append(abstractMethod)
                .append(", parameterVariable=").append(parameterVariable).append(", typeArgument=").append(typeArgument)
                .append(", typeParameter=").append(typeParameter).append(", constant=").append(constant)
                .append(", background=").append(background).append(", currentLine=").append(currentLine)
                .append(", foreground=").append(foreground).append(", lineNumber=").append(lineNumber)
                .append(", selectionBackground=").append(selectionBackground).append(", selectionForeground=")
                .append(selectionForeground).append("]");
        return builder.toString();
    }

}
