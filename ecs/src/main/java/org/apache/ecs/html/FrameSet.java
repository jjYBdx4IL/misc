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
    This class creates a &lt;FrameSet&gt; tag.

    @version $Id: FrameSet.java,v 1.4 2003/04/27 09:21:50 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class FrameSet extends MultiPartElement implements Printable, PageEvents
{
    /**
        Private initialization routine.
    */
    {
        setElementType("frameset");
    }
    
    /**
        Basic constructor.
    */
    public FrameSet()
    {
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
    */
    public FrameSet(Element element)
    {
        addElement(element);
    }

    /**
        Basic constructor.
        @param  element Adds an Element to the element.
    */
    public FrameSet(String element)
    {
        addElement(element);
    }

    /**
        Basic constructor.
        @param  rows   Sets the ROWS="" attribute
        @param  cols   Sets the COLS="" attribute
    */
    public FrameSet(String rows, String cols)
    {
        setRows(rows);
        setCols(cols);
    }

    /**
        Basic constructor.
        @param  rows   Sets the ROWS="" attribute
        @param  cols   Sets the COLS="" attribute
        @param  element Adds an Element to the element.
    */
    public FrameSet(String rows, String cols, Element element)
    {
        addElement(element);
        setRows(rows);
        setCols(cols);
    }

    /**
        Basic constructor.
        @param  rows   Sets the ROWS="" attribute
        @param  cols   Sets the COLS="" attribute
        @param  element Adds an Element to the element.
    */
    public FrameSet(String rows, String cols, String element)
    {
        addElement(element);
        setRows(rows);
        setCols(cols);
    }

    /**
        Sets the ROWS="" attribute
        @param  rows   Sets the ROWS="" attribute
    */
    public FrameSet setRows(int rows)
    {
        setRows(Integer.toString(rows));
        return(this);
    }

    /**
        Sets the ROWS="" attribute
        @param  rows   Sets the ROWS="" attribute
    */
    public FrameSet setRows(String rows)
    {
        addAttribute("rows",rows);
        return(this);
    }

    /**
        Sets the COLS="" attribute
        @param  cols   Sets the COLS="" attribute
    */
    public FrameSet setCols(int cols)
    {
        setCols(Integer.toString(cols));
        return(this);
    }

    /**
        Sets the COLS="" attribute
        @param  cols   Sets the COLS="" attribute
    */
    public FrameSet setCols(String cols)
    {
        addAttribute("cols",cols);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public FrameSet addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public FrameSet addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }
    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public FrameSet addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public FrameSet addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public FrameSet removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }

    /**
        The onload event occurs when the user agent finishes loading a window
        or all frames within a FRAMESET. This attribute may be used with BODY
        and FRAMESET elements.
        
        @param The script
    */
    public void setOnLoad(String script)
    {
        addAttribute ( "onLoad", script );
    }

    /**
        The onunload event occurs when the user agent removes a document from a
        window or frame. This attribute may be used with BODY and FRAMESET
        elements.
        
        @param The script
    */
    public void setOnUnload(String script)
    {
        addAttribute ( "onUnload", script );
    }
}
