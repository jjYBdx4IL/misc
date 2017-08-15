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

/**
    This interface describes the attributes within an element. It is 
    implemented by ElementAttributes.

    @version $Id: Attributes.java,v 1.5 2003/04/27 09:44:39 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public interface Attributes
{
    /**
        Does this element attribute value need a =""?
    */
    public static final String NO_ATTRIBUTE_VALUE = "ECS_NO_ATTRIBUTE_VALUE";

    /**
        Set the state of the attribute filter.
        @param filter_attribute_state do we need to filter attributes?
    */
    public Element setAttributeFilterState(boolean filter_attribute_state);

    /**
        Set the AttributeFilter that should be used.
        @param attribute_filter set the attribute filter to be used.
    */
    public Element setAttributeFilter(Filter attribute_filter);

    /**
        Get the AttributeFilter that is in use.
    */
    public Filter getAttributeFilter();

    /**
        Add an attribute to the Element.
        @param name name of the attribute
        @param element value of the attribute.
    */
    public Element addAttribute(String name,Object element);

    /**
        Add an attribute to the Element.
        @param name name of the attribute
        @param element value of the attribute.
    */
    public Element addAttribute(String name, int element);

    /**
        Add an attribute to the Element.
        @param name name of the attribute
        @param element value of the attribute.
    */
    public Element addAttribute(String name, String element);

    /**
        Add an attribute to the Element.
        @param name name of the attribute
        @param element value of the attribute.
    */
    public Element addAttribute(String name, Integer element);
    
    /**
        Remove an attribute from the element.
        @param name remove the attribute of this name
    */
    public Element removeAttribute(String name);

    /**
        Does the element have an attribute.
        @param name of the attribute to ask the element for.
    */
    public boolean hasAttribute(String name);

    /**
        Set the character used to quote attributes.
        @param  quote_char character used to quote attributes
    */
    public Element setAttributeQuoteChar(char quote_char);

    /**
        Get the character used to quote attributes.
    */
    public char getAttributeQuoteChar();

    /**
        Set the equality sign for an attribute.
        @param equality_sign The equality sign used for attributes.
    */
    public Element setAttributeEqualitySign(char equality_sign);

    /**
        Get the equality sign for an attribute.
    */
    public char getAttributeEqualitySign();

    /**
        Do we surround attributes with qoutes?
    */
    public boolean getAttributeQuote();

    /**
        Set wether or not we surround the attributes with quotes.
    */
    public Element setAttributeQuote(boolean attribute_quote);
} 
