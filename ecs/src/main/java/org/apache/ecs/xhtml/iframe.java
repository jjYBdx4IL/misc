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
    This class creates a &lt;iframe&gt; tag.

    @version $Id: iframe.java,v 1.2 2003/04/27 09:39:23 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>
*/
public class iframe extends MultiPartElement implements Printable
{
    public final static String yes = "yes";
    public final static String no = "no";
    public final static String auto = "auto";
    
    /**
        Private initialization routine.
    */
    {
        setElementType("iframe");
        setCase(LOWERCASE);
        setAttributeQuote(true);
    }
    /**
        Basic constructor.
    */
    public iframe()
    {
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
    */
    public iframe(Element element)
    {
        addElement(element);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
    */
    public iframe(String element)
    {
        addElement(element);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param   name  the name="" attribute
    */
    public iframe(Element element, String name)
    {
        addElement(element);
        setName(name);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param   name  the name="" attribute
    */
    public iframe(String element, String name)
    {
        addElement(element);
        setName(name);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param   name  the name="" attribute
        @param   src  the src="" attribute
    */
    public iframe(Element element, String name, String src)
    {
        addElement(element);
        setName(name);
        setSrc(src);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param   name  the name="" attribute
        @param   src  the src="" attribute
    */
    public iframe(String element, String name, String src)
    {
        addElement(element);
        setName(name);
        setSrc(src);
    }

    /**
        Sets the longdesc="" attribute
        @param   longdesc  the longdesc="" attribute
    */
    public iframe setLongDesc(String longdesc)
    {
        addAttribute("longdesc",longdesc);
        return this;
    }

    /**
        Sets the name="" attribute
        @param   name  the name="" attribute
    */
    public iframe setName(String name)
    {
        addAttribute("name",name);
        return this;
    }

    /**
        Sets the src="" attribute
        @param   src  the src="" attribute
    */
    public iframe setSrc(String src)
    {
        addAttribute("src",src);
        return this;
    }

    /**
        Sets the frameborder="" attribute
        @param   frameborder  the frameborder="" attribute
    */
    public iframe setFrameBorder(boolean frameborder)
    {
        if (frameborder)
            addAttribute("frameborder",Integer.toString(1));
        else
            addAttribute("frameborder",Integer.toString(0));            
        return this;
    }

    /**
        Sets the marginwidth="" attribute
        @param   marginwidth  the marginwidth="" attribute
    */
    public iframe setMarginWidth(int marginwidth)
    {
        setMarginWidth(Integer.toString(marginwidth));
        return this;
    }

    /**
        Sets the marginwidth="" attribute
        @param   marginwidth  the marginwidth="" attribute
    */
    public iframe setMarginWidth(String marginwidth)
    {
        addAttribute("marginwidth",marginwidth);
        return this;
    }


    /**
        Sets the height="" attribute
        @param   height  the height="" attribute
    */
    public iframe setHeight(String height)
    {
        addAttribute("height",height);
        return this;
    }

    /**
        Sets the height="" attribute
        @param   height  the height="" attribute
    */
    public iframe setHeight(int height)
    {
        addAttribute("height",Integer.toString(height));
        return this;
    }

    /**
        Sets the width="" attribute
        @param   width  the width="" attribute
    */
    public iframe setWidth(String width)
    {
        addAttribute("width",width);
        return this;
    }

    /**
        Sets the width="" attribute
        @param   width  the width="" attribute
    */
    public iframe setWidth(int width)
    {
        addAttribute("width",Integer.toString(width));
        return this;
    }

    /**
        Sets the marginheight="" attribute
        @param   marginheight  the marginheight="" attribute
    */
    public iframe setMarginHeight(int marginheight)
    {
        setMarginHeight(Integer.toString(marginheight));
        return this;
    }

    /**
        Sets the marginheight="" attribute
        @param   marginheight  the marginheight="" attribute
    */
    public iframe setMarginHeight(String marginheight)
    {
        addAttribute("marginheight",marginheight);
        return this;
    }

    /**
        Sets the scrolling="" attribute
        @param   scrolling  the scrolling="" attribute
    */
    public iframe setScrolling(String scrolling)
    {
        addAttribute("scrolling",scrolling);
        return this;
    }

    /**
        Sets the align="" attribute.

        @param  align sets the align="" attribute. You can
        use the AlignType.* variables for convience.
    */
    public iframe setAlign(String align)
    {
        addAttribute("align",align);
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
    public iframe addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public iframe addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public iframe addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public iframe addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public iframe removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }
}
