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
    This class creates a &lt;script&gt; tag.
    <P>
    Note that XHTML script tag doesn't hide the script text withing comments
    like its HTML counterpart does. This difference is caused by the fact that
    XHTML is XML and XML parsers can throw the comments out. Use this tag with
    browsers that support scripting language(s).

    @version $Id: script.java,v 1.2 2003/04/27 09:36:30 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>
*/
public class script extends MultiPartElement implements Printable
{
    /**
        Private initialization routine.
    */
    {
        setElementType("script");
        setCase(LOWERCASE);
        setAttributeQuote(true);
        setLanguage("Javascript");
    }
    /**
        Basic constructor.
    */
    public script()
    {
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
    */
    public script(Element element)
    {
        addElement(element);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param  src sets the src="" attribute
    */
    public script(Element element, String src)
    {
        addElement(element);
        setSrc(src);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param  src sets the src="" attribute
        @param  type sets the type="" attribute
    */
    public script(Element element, String src, String type)
    {
        addElement(element);
        setSrc(src);
        setType(type);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param  src sets the src="" attribute
        @param  type sets the type="" attribute
        @param  lang sets the language="" attribute
    */
    public script(Element element, String src, String type, String lang)
    {
        addElement(element);
        setSrc(src);
        setType(type);
        setLanguage(lang);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
    */
    public script(String element)
    {
        addElement(element);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param  src sets the src="" attribute
    */
    public script(String element, String src)
    {
        addElement(element);
        setSrc(src);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param  src sets the src="" attribute
        @param  type sets the type="" attribute
    */
    public script(String element, String src, String type)
    {
        addElement(element);
        setSrc(src);
        setType(type);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
        @param  src sets the src="" attribute
        @param  type sets the type="" attribute
        @param  lang sets the language="" attribute
    */
    public script(String element, String src, String type, String lang)
    {
        addElement(element);
        setSrc(src);
        setType(type);
        setLanguage(lang);
    }

    /**
        Sets the src="" attribute
        @param   src  the src="" attribute
    */
    public script setSrc(String src)
    {
        addAttribute("src",src);
        return this;
    }

    /**
        Sets the type="" attribute
        @param   type  the type="" attribute
    */
    public script setType(String type)
    {
        addAttribute("type", type);
        return this;
    }

    /**
        Sets the language="" attribute
        @param   language  the language="" attribute
    */
    public script setLanguage(String language)
    {
        addAttribute("language", language);
        return this;
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
    public script addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public script addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public script addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public script addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public script removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }
}
