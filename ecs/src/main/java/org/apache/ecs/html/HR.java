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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
    This class creates an &lt;HR&gt; tag.

    @version $Id: HR.java,v 1.5 2003/04/27 09:04:02 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class HR extends SinglePartElement implements Printable, MouseEvents, KeyEvents
{
    /**
            Private initialization routine.
    */
    {
        setElementType("hr");
    }

    /**
        Basic constructor. Use the set* methods to set the attibutes.
    */
    public HR()
    {
    }

    /**
     * Basic constructor
     *
     * @param   width  sets the WIDTH="" attribute
     */
    public HR(String width)
    {
        setWidth(width);
    }

    /**
     * Basic constructor
     *
     * @param   width  sets the WIDTH="" attribute
     */
    public HR(int width)
    {
        setWidth(width);
    }

    /**
     * Basic constructor
     *
     * @param   width  sets the WIDTH="" attribute
     * @param   align  sets the ALIGN="" attribute
     */
    public HR(String width, String align)
    {
        setWidth(width);
        setAlign(align);
    }

    /**
     * Basic constructor
     *
     * @param   width  sets the WIDTH="" attribute
     * @param   align  sets the ALIGN="" attribute
     */
    public HR(int width, String align)
    {
        setWidth(width);
        setAlign(align);
    }

    /**
     * Basic constructor
     *
     * @param   width  sets the WIDTH="" attribute
     * @param   align  sets the ALIGN="" attribute
     * @param   size   sets the SIZE="" attribute
     */
    public HR(String width, String align, String size)
    {
        setWidth(width);
        setAlign(align);
        setSize(size);
    }

    /**
     * Basic constructor
     *
     * @param   width  sets the WIDTH="" attribute
     * @param   align  sets the ALIGN="" attribute
     * @param   size   sets the SIZE="" attribute
     */
    public HR(String width, String align, int size)
    {
        setWidth(width);
        setAlign(align);
        setSize(size);
    }

    /**
     * Basic constructor
     *
     * @param   width  sets the WIDTH="" attribute
     * @param   align  sets the ALIGN="" attribute
     * @param   size   sets the SIZE="" attribute
     */
    public HR(int width, String align, int size)
    {
        setWidth(width);
        setAlign(align);
        setSize(size);
    }

    /**
        Sets the WIDTH="" attribute
        @param   width  the WIDTH="" attribute
    */
    public HR setWidth(String width)
    {
        addAttribute("width",width);
        return this;
    }

    /**
        Sets the WIDTH="" attribute
        @param   width  the WIDTH="" attribute
    */
    public HR setWidth(int width)
    {
        addAttribute("width",Integer.toString(width));
        return this;
    }

    /**
        Sets the ALIGN="" attribute
        @param   align  the ALIGN="" attribute
    */
    public HR setAlign(String align)
    {
        addAttribute("align",align);
        return this;
    }

    /**
        Sets the SIZE="" attribute
        @param   hspace  the SIZE="" attribute
    */
    public HR setSize(String size)
    {
        addAttribute("size",size);
        return this;
    }

    /**
        Sets the SIZE="" attribute
        @param   hspace  the SIZE="" attribute
    */
    public HR setSize(int size)
    {
        addAttribute("size",Integer.toString(size));
        return this;
    }

    /**
        Sets the noshade
        @param   shade  true or false
    */
    public HR setNoShade(boolean shade)
    {
        if ( shade == true )
            addAttribute("noshade", NO_ATTRIBUTE_VALUE);
        else
            removeAttribute("noshade");
            
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public HR addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public HR addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public HR addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public HR addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public HR removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }

    /**
        The onclick event occurs when the pointing device button is clicked
        over an element. This attribute may be used with most elements.
        
        @param The script
    */
    public void setOnClick(String script)
    {
        addAttribute ( "onClick", script );
    }
    /**
        The ondblclick event occurs when the pointing device button is double
        clicked over an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnDblClick(String script)
    {
        addAttribute ( "onDblClick", script );
    }
    /**
        The onmousedown event occurs when the pointing device button is pressed
        over an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseDown(String script)
    {
        addAttribute ( "onMouseDown", script );
    }
    /**
        The onmouseup event occurs when the pointing device button is released
        over an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseUp(String script)
    {
        addAttribute ( "onMouseUp", script );
    }
    /**
        The onmouseover event occurs when the pointing device is moved onto an
        element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseOver(String script)
    {
        addAttribute ( "onMouseOver", script );
    }
    /**
        The onmousemove event occurs when the pointing device is moved while it
        is over an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseMove(String script)
    {
        addAttribute ( "onMouseMove", script );
    }
    /**
        The onmouseout event occurs when the pointing device is moved away from
        an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseOut(String script)
    {
        addAttribute ( "onMouseOut", script );
    }

    /**
        The onkeypress event occurs when a key is pressed and released over an
        element. This attribute may be used with most elements.
        
        @param The script
    */
    public void setOnKeyPress(String script)
    {
        addAttribute ( "onKeyPress", script );
    }

    /**
        The onkeydown event occurs when a key is pressed down over an element.
        This attribute may be used with most elements.
        
        @param The script
    */
    public void setOnKeyDown(String script)
    {
        addAttribute ( "onKeyDown", script );
    }

    /**
        The onkeyup event occurs when a key is released over an element. This
        attribute may be used with most elements.
        
        @param The script
    */
    public void setOnKeyUp(String script)
    {
        addAttribute ( "onKeyUp", script );
    }
}
