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
    This class creates a &lt;TEXTAREA&gt;&lt;/TEXTAREA&gt; tag.

    @version $Id: TextArea.java,v 1.7 2003/04/27 09:03:39 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class TextArea extends MultiPartElement implements PageEvents, FormEvents, MouseEvents, KeyEvents, FocusEvents
{
    public final static String off = "off";
    public final static String physical = "physical";
    public final static String virtual = "virtual";
    public final static String OFF = "OFF";
    public final static String PHYSICAL = "PHYSICAL";
    public final static String VIRTUAL = "VIRTUAL";
    
    /**
        Private initializer.
    */
    {
        setElementType("textarea");
    }
    /**
        Basic Constructor use set* methods.
    */
    public TextArea()
    {
    }

    /**
        Basic Constructor use set* methods.
        @param rows  the ROWS="" attribute
        @param cols  the COLS="" attribute
    */
    public TextArea(int rows, int cols)
    {
        setRows(rows);
        setCols(cols);
    }
    
    /**
        Basic Constructor use set* methods.
        @param rows  the ROWS="" attribute
        @param cols  the COLS="" attribute
    */
    public TextArea(String rows, String cols)
    {
        setRows(rows);
        setCols(cols);
    }
    
    /**
        Basic Constructor use set* methods.
        @param name  the NAME="" attribute
        @param rows  the ROWS="" attribute
        @param cols  the COLS="" attribute
    */
    public TextArea(String name, int rows, int cols)
    {
        setName(name);
        setRows(rows);
        setCols(cols);
    }
    
    /**
        Basic Constructor use set* methods.
        @param name  the NAME="" attribute
        @param rows  the ROWS="" attribute
        @param cols  the COLS="" attribute
    */
    public TextArea(String name, String rows, String cols)
    {
        setName(name);
        setRows(rows);
        setCols(cols);
    }
    
    /**
        Sets the ROWS="" attribute
        @param  rows   Sets the ROWS="" attribute
    */
    public TextArea setRows(int rows)
    {
        setRows(Integer.toString(rows));
        return(this);
    }

    /**
        Sets the ROWS="" attribute
        @param  rows   Sets the ROWS="" attribute
    */
    public TextArea setRows(String rows)
    {
        addAttribute("rows",rows);
        return(this);
    }

    /**
        Sets the WRAP="" attribute
        @param  wrap   Sets the WRAP="" attribute
    */
    public TextArea setWrap(String wrap)
    {
        addAttribute("wrap",wrap);
        return(this);
    }

    /**
        Sets the COLS="" attribute
        @param  cols   Sets the COLS="" attribute
    */
    public TextArea setCols(int cols)
    {
        setCols(Integer.toString(cols));
        return(this);
    }

    /**
        Sets the COLS="" attribute
        @param  cols   Sets the COLS="" attribute
    */
    public TextArea setCols(String cols)
    {
        addAttribute("cols",cols);
        return(this);
    }

    /**
        Sets the NAME="" attribute
        @param  name   Sets the NAME="" attribute
    */
    public TextArea setName(String name)
    {
        addAttribute("name",name);
        return(this);
    }

    /**
        Sets the TABINDEX="" attribute
        @param   alt  the TABINDEX="" attribute
    */
    public TextArea setTabindex(String index)
    {
        addAttribute("tabindex",index);
        return this;
    }
    
    /**
        Sets the TABINDEX="" attribute
        @param   alt  the TABINDEX="" attribute
    */
    public TextArea setTabindex(int index)
    {
        setTabindex(Integer.toString(index));
        return this;
    }
    
    /**
        Sets the readonly value
        @param   readonly  true or false
    */
    public TextArea setReadOnly(boolean readonly)
    {
        if ( readonly == true )
            addAttribute("readonly", NO_ATTRIBUTE_VALUE);
        else
            removeAttribute("readonly");
            
        return(this);
    }

    /**
        Sets the disabled value
        @param   disabled  true or false
    */
    public TextArea setDisabled(boolean disabled)
    {
        if ( disabled == true )
            addAttribute("disabled", NO_ATTRIBUTE_VALUE);
        else
            removeAttribute("disabled");
            
        return(this);
    }
    
    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public TextArea addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public TextArea addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public TextArea addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public TextArea addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public TextArea removeElement(String hashcode)
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

    /**
        The onsubmit event occurs when a form is submitted. It only applies to
        the FORM element.
        
        @param The script
    */
    public void setOnSubmit(String script)
    {
        addAttribute ( "onSubmit", script );
    }

    /**
        The onreset event occurs when a form is reset. It only applies to the
        FORM element.
        
        @param The script
    */
    public void setOnReset(String script)
    {
        addAttribute ( "onReset", script );
    }

    /**
        The onselect event occurs when a user selects some text in a text
        field. This attribute may be used with the INPUT and TEXTAREA elements.
        
        @param The script
    */
    public void setOnSelect(String script)
    {
        addAttribute ( "onSelect", script );
    }

    /**
        The onchange event occurs when a control loses the input focus and its
        value has been modified since gaining focus. This attribute applies to
        the following elements: INPUT, SELECT, and TEXTAREA.
        
        @param The script
    */
    public void setOnChange(String script)
    {
        addAttribute ( "onChange", script );
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

    /**
        The onFocus event occurs when a element is focussed. This
        attribute may be used with most elements.

        @param The script
    */
    public void setOnFocus(String script)
    {
        addAttribute ( "onFocus", script );
    }

    /**
        The onBlur event occurs when a element is blurred. This
        attribute may be used with most elements.

        @param The script
    */
    public void setOnBlur(String script)
    {
        addAttribute ( "onBlur", script );
    }
} 
