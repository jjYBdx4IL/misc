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
package org.apache.ecs.html;

import org.apache.ecs.*;

/**
    This class creates a &lt;IFrame&gt; tag.

    @version $Id: IFrame.java,v 1.3 2003/04/27 09:20:29 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class IFrame extends MultiPartElement implements Printable
{
    public final static String YES = "YES";
    public final static String NO = "NO";
    public final static String AUTO = "AUTO";
    public final static String yes = "yes";
    public final static String no = "no";
    public final static String auto = "auto";
    
    /**
        Private initialization routine.
    */
    {
        setElementType("iframe");
    }
    /**
        Basic constructor.
    */
    public IFrame()
    {
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
    */
    public IFrame(Element element)
    {
        addElement(element);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
    */
    public IFrame(String element)
    {
        addElement(element);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param   name  the NAME="" attribute
    */
    public IFrame(Element element, String name)
    {
        addElement(element);
        setName(name);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param   name  the NAME="" attribute
    */
    public IFrame(String element, String name)
    {
        addElement(element);
        setName(name);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param   name  the NAME="" attribute
        @param   src  the SRC="" attribute
    */
    public IFrame(Element element, String name, String src)
    {
        addElement(element);
        setName(name);
        setSrc(src);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param   name  the NAME="" attribute
        @param   src  the SRC="" attribute
    */
    public IFrame(String element, String name, String src)
    {
        addElement(element);
        setName(name);
        setSrc(src);
    }

    /**
        Sets the LONGDESC="" attribute
        @param   longdesc  the LONGDESC="" attribute
    */
    public IFrame setLongDesc(String longdesc)
    {
        addAttribute("longdesc",longdesc);
        return this;
    }

    /**
        Sets the NAME="" attribute
        @param   name  the NAME="" attribute
    */
    public IFrame setName(String name)
    {
        addAttribute("name",name);
        return this;
    }

    /**
        Sets the SRC="" attribute
        @param   src  the SRC="" attribute
    */
    public IFrame setSrc(String src)
    {
        addAttribute("src",src);
        return this;
    }

    /**
        Sets the FRAMEBORDER="" attribute
        @param   frameborder  the FRAMEBORDER="" attribute
    */
    public IFrame setFrameBorder(boolean frameborder)
    {
        if (frameborder)
            addAttribute("frameborder",Integer.toString(1));
        else
            addAttribute("frameborder",Integer.toString(0));            
        return this;
    }

    /**
        Sets the MARGINWIDTH="" attribute
        @param   marginwidth  the MARGINWIDTH="" attribute
    */
    public IFrame setMarginWidth(int marginwidth)
    {
        setMarginWidth(Integer.toString(marginwidth));
        return this;
    }

    /**
        Sets the MARGINWIDTH="" attribute
        @param   marginwidth  the MARGINWIDTH="" attribute
    */
    public IFrame setMarginWidth(String marginwidth)
    {
        addAttribute("marginwidth",marginwidth);
        return this;
    }


    /**
        Sets the HEIGHT="" attribute
        @param   height  the HEIGHT="" attribute
    */
    public IFrame setHeight(String height)
    {
        addAttribute("height",height);
        return this;
    }

    /**
        Sets the HEIGHT="" attribute
        @param   height  the HEIGHT="" attribute
    */
    public IFrame setHeight(int height)
    {
        addAttribute("height",Integer.toString(height));
        return this;
    }

    /**
        Sets the WIDTH="" attribute
        @param   width  the WIDTH="" attribute
    */
    public IFrame setWidth(String width)
    {
        addAttribute("width",width);
        return this;
    }

    /**
        Sets the WIDTH="" attribute
        @param   width  the WIDTH="" attribute
    */
    public IFrame setWidth(int width)
    {
        addAttribute("width",Integer.toString(width));
        return this;
    }

    /**
        Sets the MARGINHEIGHT="" attribute
        @param   marginheight  the MARGINHEIGHT="" attribute
    */
    public IFrame setMarginHeight(int marginheight)
    {
        setMarginHeight(Integer.toString(marginheight));
        return this;
    }

    /**
        Sets the MARGINHEIGHT="" attribute
        @param   marginheight  the MARGINHEIGHT="" attribute
    */
    public IFrame setMarginHeight(String marginheight)
    {
        addAttribute("marginheight",marginheight);
        return this;
    }

    /**
        Sets the SCROLLING="" attribute
        @param   scrolling  the SCROLLING="" attribute
    */
    public IFrame setScrolling(String scrolling)
    {
        addAttribute("scrolling",scrolling);
        return this;
    }

    /**
        Sets the ALIGN="" attribute.

        @param  align sets the ALIGN="" attribute. You can
        use the AlignType.* variables for convience.
    */
    public IFrame setAlign(String align)
    {
        addAttribute("align",align);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public IFrame addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public IFrame addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public IFrame addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public IFrame addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public IFrame removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }
}
