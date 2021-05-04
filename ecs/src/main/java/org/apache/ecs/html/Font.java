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
    This class creates a &lt;FONT&gt; object.
    @version $Id: Font.java,v 1.3 2003/04/27 09:21:50 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class Font extends MultiPartElement implements Printable
{

	/**
		Private initializer.
	*/
	{
		setElementType("font");
	}

    /**
		Basic Constructor. use set* methods.
	*/
    public Font()
    {
    }

    /**
        Basic constructor.
        @param face create new font object with this face.
    */
    public Font(String face)
    {
        setFace(face);
    }

    /**
        Basic constructor
        @param  face
        @param  color
        Create a new font object with the face abd color already set. Convience colors are defined in HtmlColor interface.
    */
    public Font(String face,String color)
    {
        setFace(face);
        setColor(color);
    }

    /**
        Basic constructor
        @param  face
        @param  color
        @param  size
        Create a new font object with the face,color and size already set. Convience colors are defined in HtmlColor interface.
    */
    public Font(String face,String color,int size)
    {
        setFace(face);
        setColor(color);
        setSize(size);
    }

    /**
        Basic constructor
        @param  size
        Create a new font object with the size already set.
    */
    public Font(int size)
    {
        setSize(size);
    }

    /**
        Basic constructor
        @param  size
        @param  face
        Create a new font object with the size and face already set.
    */
    public Font(int size,String face)
    {
        setSize(size);
        setFace(face);
    }

    /**
        Basic constructor
        @param  color
        @param  size
        Create a new font object with the size and color already set.
    */
    public Font(String color,int size)
    {
        setSize(size);
        setColor(color);
    }
    
    /**
        sets the FACE="" attribute.
        @param  face sets the FACE="" attribute.
    */
    public Font setFace(String face)
    {
        addAttribute("face",face);
        return(this);
    }

    /**
        sets the COLOR="" attribute.
        @param  color sets the COLOR="" attribute. Convience colors are defined in the HtmlColors interface.
    */
    public Font setColor(String color)
    {
        addAttribute("color",HtmlColor.convertColor(color));
        return(this);
    }
    
	/**
		sets the SIZE="" attribute.
		@param	size sets the SIZE="" attribute.
	*/
	public Font setSize(int size)
	{
		addAttribute("size",Integer.toString(size));
		return(this);
	}

	/**
		sets the SIZE="" attribute.
		@param	size sets the SIZE="" attribute.
	*/
	public Font setSize(String size)
	{
		addAttribute("size",size);
		return(this);
	}

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public Font addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public Font addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

	/**
		Adds an Element to the Element.
		@param 	element adds and Element to the Element.
	*/
	public Font addElement(Element element)
	{
		addElementToRegistry(element);
		return(this);
	}

	/**
		Adds an Element to the Element.
		@param 	element adds and Element to the Element.
	*/
	public Font addElement(String element)
	{
		addElementToRegistry(element);
		return(this);
	}
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public Font removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }
} 
