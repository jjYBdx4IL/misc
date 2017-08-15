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
import java.util.Enumeration;
import java.util.Vector;

/**
    This class is a Element container class. You can place elements into 
    this class and then you can place this class into other elements in order 
    to combine elements together.

<code><pre>
    P p = new P().addElement("foo");
    P p1 = new P().addElement("bar");
    ElementContainer ec = new ElementContainer(p).addElement(p1);
    System.out.println(ec.toString());
</pre></code>

    @version $Id: ElementContainer.java,v 1.7 2003/04/27 09:43:24 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class ElementContainer extends ConcreteElement implements Printable
{
    /** 
        internal use only
        @serial ec ec
    */
    private Vector ec = new Vector(2);
    
    /** 
        Basic constructor
    */
    public ElementContainer()
    {
    }

    /** 
        Basic constructor
    */
    public ElementContainer(Element element)
    {
        addElement(element);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public ElementContainer addElement(Element element)
    {
        ec.addElement(element);
        return(this);
    }
    
    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public ElementContainer addElement(String element)
    {
        ec.addElement(new StringElement(element));
        return(this);
    }

    /**
        Implements the output method in Element
    */
    public void output(OutputStream out)
    {
        Element element = null;
        Enumeration data = ec.elements();
        while ( data.hasMoreElements() )
        {
            element = (Element) data.nextElement();
            element.output(out);
        }
    }
    
    /**
        Implements the output method in Element
    */
    public void output(PrintWriter out)
    {
        Element element = null;
        Enumeration data = ec.elements();
        while ( data.hasMoreElements() )
        {
            element = (Element) data.nextElement();
            element.output(out);
        }
    }
    /**
        returns an enumeration of the elements in this container
    */
    public Enumeration elements()
    {
        return ec.elements();
    }
}
