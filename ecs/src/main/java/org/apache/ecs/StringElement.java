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
package org.apache.ecs;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
    This class is used to create a String element in ECS. A StringElement 
    has no tags wrapped around it, it is an Element without tags.
    
    @version $Id: StringElement.java,v 1.5 2003/04/27 09:42:40 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class StringElement extends ConcreteElement implements Printable
{
    /**
        Basic constructor
    */
    public StringElement()
    {
    }
    
    /** 
        Basic constructor
    */
    public StringElement(String string)
    {
	if (string != null)
	    setTagText(string);
	else
	    setTagText("");
    }

    /** 
        Basic constructor
    */
    public StringElement(Element element)
    {
        addElement(element);
    }

    private StringElement append(String string)
    {
        setTagText(getTagText()+string);
        return this;
    }

    /** 
        Resets the interal string to be empty.
    */
    public StringElement reset()
    {
        setTagText("");
        return this;
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public StringElement addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public StringElement addElement(String hashcode,String element)
    {
        // We do it this way so that filtering will work.
        // 1. create a new StringElement(element) - this is the only way that setTextTag will get called
        // 2. copy the filter state of this string element to this child.
        // 3. copy the filter for this string element to this child.

        StringElement se = new StringElement(element);
        se.setFilterState(getFilterState());
        se.setFilter(getFilter());
        addElementToRegistry(hashcode,se);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public StringElement addElement(String element)
    {
        addElement(Integer.toString(element.hashCode()),element);
        return(this);
    }
    
    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public StringElement addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }
    
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public StringElement removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }
    
    public String createStartTag()
    {
        return("");
    }
    public String createEndTag()
    {
        return("");
    }
}
