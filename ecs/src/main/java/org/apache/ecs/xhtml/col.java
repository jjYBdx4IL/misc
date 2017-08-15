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
 */
package org.apache.ecs.xhtml;

import org.apache.ecs.*;

/**
    This class creates a &lt;col&gt; object.

    @version $Id: col.java,v 1.2 2003/04/27 09:40:47 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>
*/
public class col extends SinglePartElement implements Printable
{
    /**
        private initializer.
    */
    {
        setElementType("col");
        setCase(LOWERCASE);
        setAttributeQuote(true);
        setBeginEndModifier('/');
    }
    public col()
    {
    }

    /**
        Sets the span="" attribute.
        @param span    sets the span="" attribute.
    */
    public col setSpan(String span)
    {
        addAttribute("span",span);
        return(this);
    }

    /**
        Sets the span="" attribute.
        @param span    sets the span="" attribute.
    */
    public col setSpan(int span)
    {
        addAttribute("span",Integer.toString(span));
        return(this);
    }

    /**
        Supplies user agents with a recommended cell width.  (Pixel Values)
        @param width    how many pixels to make cell
    */
    public col setWidth(int width)
    {
        addAttribute("width",Integer.toString(width));
        return(this);
    }
    
    /**
        Supplies user agents with a recommended cell width.  (Pixel Values)
        @param width    how many pixels to make cell
    */
    public col setWidth(String width)
    {
        addAttribute("width",width);
        return(this);
    }

    /**
        Sets the align="" attribute convience variables are provided in the AlignType interface
        @param  align   Sets the align="" attribute
    */
    public col setAlign(String align)
    {
        addAttribute("align",align);
        return(this);
    }

    /**
        Sets the valign="" attribute convience variables are provided in the AlignType interface
        @param  valign   Sets the valign="" attribute
    */
    public col setVAlign(String valign)
    {
        addAttribute("valign",valign);
        return(this);
    }

    /**
        Sets the char="" attribute.
        @param character    the character to use for alignment.
    */
    public col setChar(String character)
    {
        addAttribute("char",character);
        return(this);
    }

    /**
        Sets the charoff="" attribute.
        @param char_off When present this attribute specifies the offset
        of the first occurrence of the alignment character on each line.
    */
    public col setCharOff(int char_off)
    {
        addAttribute("charoff",Integer.toString(char_off));
        return(this);
    }

    /**
        Sets the charoff="" attribute.
        @param char_off When present this attribute specifies the offset
        of the first occurrence of the alignment character on each line.
    */
    public col setCharOff(String char_off)
    {
        addAttribute("charoff",char_off);
        return(this);
    }

    /**
        Sets the lang="" and xml:lang="" attributes
        @param   lang  the lang="" and xml:lang="" attributes
    */
    public Element setLang(String lang)
    {
        addAttribute("lang",lang);
        addAttribute("xml:lang",lang);
        return this;
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public col addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public col addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }
    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public col addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public col addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public col removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }
}
