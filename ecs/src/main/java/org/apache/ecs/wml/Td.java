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
package org.apache.ecs.wml;

import org.apache.ecs.*;

/**
  This class implements the &lt;td&gt; element.
  It is a container to hold a single table cell data within a table 
  row. Table cell data may be empty. Empty cells are significant, 
  and must not be ignored. The user agent should do a best effort 
  to deal with multiple line data cells that may result from using 
  images or line breaks.
 	
   @version $Id: Td.java,v 1.2 2003/04/27 09:26:18 rdonkin Exp $
   @author Written by <a href="mailto:Krzysztof.Zelazowski@cern.ch">Krzysztof Zelazowski</a>
 */
public class Td extends org.apache.ecs.MultiPartElement 
{
    /**
      Basic constructor.
     */
    public Td() 
	{
        setElementType("td");
    }

    /**
      Basic Constructor 
	  @param element an element to be added to this cell.
     */
    public Td(Element element)
    {
        this();
        addElement(element);
    }

    /**
      Basic Constructor.
	  @param element a String element to be added to this cell
     */
    public Td(String element)
    {
        this();
        addElement(element);
    }

    /**
      Adds an Element to the element.
	  @param  element an element to be added.
     */
    public Td addElement(Element element)
    {
        addElementToRegistry(element,getFilterState());
        return(this);
    }

    /**
      Adds an Element to the element.
	  @param element a String element to be added
     */
    public Td addElement(String element)
    {
        addElementToRegistry(element,getFilterState());
        return(this);
    }

}
