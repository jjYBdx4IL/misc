/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Jakarta Element Construction Set", 
 *    "Jakarta ECS" , and "Apache Software Foundation" must not be used 
 *    to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Jakarta Element Construction Set" nor "Jakarta ECS" nor may "Apache" 
 *    appear in their names without prior written permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */package org.apache.ecs.xml;

import org.apache.ecs.*;

/**
    This class creates a generic &lt;&gt; tag.

    @version $Id: XML.java,v 1.4 2003/04/27 09:28:14 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class XML extends MultiPartElement implements Printable
{

    /**
        Default constructor use set* Methods. With this name.
        @param element_type The name of this element.
    */
    public XML(String element_type)
    {
        setElementType(element_type);
    }

    /**
        Construct a new XML element with this name, <br>and specify if it needs
        the element tag closed.
        @param element_type The name of this element.
        @param close    Should it have a closing tag
    */
    public XML(String element_type,boolean close)
    {
        setElementType(element_type);
        setNeedClosingTag(close);
    }

    /**
        Construct a new XML element with this name, and specify a filter for it.
        @param element_type The name of this element.
        @param filter    a new Filter for this element override the default.
    */
    public XML(String element_type,Filter filter)
    {
        setElementType(element_type);
        setFilter(filter);
    }
    
    /**
        Construct a new XML element with this name, and specify a filter for it.
        @param element_type The name of this element.
        @param close    Should it have a closing tag
        @param filter   Should this element be filtered?
    */
    public XML(String element_type,boolean close,boolean filter)
    {
        setElementType(element_type);
        setNeedClosingTag(close);
        setFilterState(filter);
    }

    /**
        Construct a new XML element with this name, and specify a filter for it.
        @param element_type The name of this element.
        @param close    Should it have a closing tag
        @param filter    a new Filter for this element override the default.
    */
    public XML(String element_type,boolean close,Filter filter)
    {
        setElementType(element_type);
        setNeedClosingTag(close);
        setFilter(filter);
    }

    /**
        Add a new attribute to this XML tag.
        @param attribute the attribute name
        @param attribute_value the value of the attribute set this to<BR>
        <code>"ECS_NO_ATTRIBUTE_VALUE"</code> if this attribute <BR>
        doesn't take a value.
    */
    public XML addXMLAttribute(String attribute, String attribute_value)
    {
        addAttribute(attribute,attribute_value);
        return(this);
    }

    /**
        Add an element to the valuie of &lt;&gt;VALUE&lt;/&gt;
        @param element the value of &lt;&gt;VALUE&lt;/&gt;
    */
    public XML addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public XML addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public XML addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Add an element to the valuie of &lt;&gt;VALUE&lt;/&gt;
        @param element the value of &lt;&gt;VALUE&lt;/&gt;
    */
    public XML addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public XML removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }

    public boolean getNeedLineBreak() {
        boolean linebreak = true;

        java.util.Enumeration _enum = elements();

        // if this tag has one child, and it's a String, then don't
        // do any linebreaks to preserve whitespace

        while (_enum.hasMoreElements()) {
            Object obj = _enum.nextElement();
            if (obj instanceof StringElement) {
                 linebreak = false;
                 break;
            }

        }

        return linebreak;
    }

    public boolean getBeginEndModifierDefined() {
        boolean answer = false;

        if (! this.getNeedClosingTag())
            answer = true;

        return answer;
    }

    public char getBeginEndModifier() {
        return '/';
    }
}
