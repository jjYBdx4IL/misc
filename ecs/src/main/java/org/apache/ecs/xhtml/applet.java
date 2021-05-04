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
    This class creates an &lt;applet&gt; tag.

    @version $Id: applet.java,v 1.2 2003/04/27 09:41:09 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>
*/
public class applet extends MultiPartElement implements Printable
{
    /**
        Private initializer.
    */
    {
        setElementType("applet");
        setCase(LOWERCASE);
        setAttributeQuote(true);
    }

    /**
        Default constructor.  Creates the &lt;applet/&gt; Element.<br>
        use set* methods.
    */
    public applet()
    {
    }

   /**
        Determines the base url for this applet.
        @param url base url for this applet.
    */
    public applet setCodeBase(String url)
    {
        addAttribute("codebase",url);
        return(this);
    }

    /**
        Comma seperated archive list.
        @param url Comma seperate archive list.
    */
    public applet setArchive(String url)
    {
        addAttribute("archive",url);
        return(this);
    }

    /**
        Applet class file.
        @param code applet class file.
    */
    public applet setCode(String code)
    {
        addAttribute("code",code);
        return(this);
    }

    /**
        Suggested height of applet.
        @param height suggested link height.
    */
    public applet setHeight(String height)
    {
        addAttribute("height",height);
        return(this);
    }

    /**
        Suggested height of applet.
        @param height suggested link height.
    */
    public applet setHeight(int height)
    {
        addAttribute("height",Integer.toString(height));
        return(this);
    }

    /**
        Suggested height of applet.
        @param height suggested link height.
    */
    public applet setHeight(double height)
    {
        addAttribute("height",Double.toString(height));
        return(this);
    }

    /**
        Suggested width of applet.
        @param height suggested link width.
    */
    public applet setWidth(String width)
    {
        addAttribute("width",width);
        return(this);
    }

    /**
        Suggested width of applet.
        @param height suggested link width.
    */
    public applet setWidth(int width)
    {
        addAttribute("width",Integer.toString(width));
        return(this);
    }

    /**
        Suggested width of object.
        @param height suggested link width.
    */
    public applet setWidth(double width)
    {
        addAttribute("width",Double.toString(width));
        return(this);
    }

    /**
        Suggested horizontal gutter.
        @param hspace suggested horizontal gutter.
    */
    public applet setHSpace(String hspace)
    {
        addAttribute("hspace",hspace);
        return(this);
    }

    /**
        Suggested horizontal gutter.
        @param hspace suggested horizontal gutter.
    */
    public applet setHSpace(int hspace)
    {
        addAttribute("hspace",Integer.toString(hspace));
        return(this);
    }

    /**
        Suggested horizontal gutter.
        @param hspace suggested horizontal gutter.
    */
    public applet setHSpace(double hspace)
    {
        addAttribute("hspace",Double.toString(hspace));
        return(this);
    }

    /**
        Suggested vertical gutter.
        @param hspace suggested vertical gutter.
    */
    public applet setVSpace(String vspace)
    {
        addAttribute("vspace",vspace);
        return(this);
    }

    /**
        Suggested vertical gutter.
        @param hspace suggested vertical gutter.
    */
    public applet setVSpace(int vspace)
    {
        addAttribute("vspace",Integer.toString(vspace));
        return(this);
    }

    /**
        Suggested vertical gutter.
        @param hspace suggested vertical gutter.
    */
    public applet setVSpace(double vspace)
    {
        addAttribute("vspace",Double.toString(vspace));
        return(this);
    }

    /**
        Set the horizontal or vertical alignment of this applet.<br>
        Convience variables are in the AlignTypes interface.
        @param alignment Set the horizontal or vertical alignment of this applet.<br>
        Convience variables are in the AlignTypes interface.
    */
    public applet setAlign(String alignment)
    {
        addAttribute("align",alignment);
        return(this);
    }

    /**
        Set the name of this applet.
        @param name set the name of this applet.
    */
    public applet setName(String name)
    {
        addAttribute("name",name);
        return(this);
    }

    /**
        Serialized applet file.
        @param object Serialized applet file.
    */
    // someone give me a better description of what this does.
    public applet setObject(String object)
    {
        addAttribute("object",object);
        return(this);
    }

    /**
        Breif description, alternate text for the applet.
        @param alt alternat text.
    */
    public applet setAlt(String alt)
    {
        addAttribute("alt",alt);
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
    public applet addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public applet addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Add an element to the element
        @param element a string representation of the element
    */
    public applet addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Add an element to the element
        @param element  an element to add
    */
    public applet addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public applet removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }
}
