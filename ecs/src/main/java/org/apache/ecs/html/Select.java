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
import java.util.Enumeration;

/**
    <p>
    This class creates a &lt;SELECT&gt; tag.
    </p><p>
    <strong>Please note</strong> that the {@link Option} element now defaults
    to add a closing tag (as is now required by the specification).
    </p>
    @version $Id: Select.java,v 1.7 2003/04/27 09:03:51 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class Select extends MultiPartElement implements
    Printable, PageEvents, FormEvents, MouseEvents, KeyEvents, FocusEvents
{
    /**
        Private initializer
    */
    {
        setElementType("select");
    }
    /**
        Basic constructor.
        Use the set* methods to set attributes.
    */
    public Select()
    {
    }

    /**
        Constructor sets the name attribute.
        Use the set* methods to set the other attributes.

        @param name  set the NAME="" attribute
    */
    public Select(String name)
    {
        setName(name);
    }

    /**
        Constructor sets the name and size attribute.
        Use the set* methods to set the other attributes.

        @param name  set the NAME="" attribute
        @param name  set the SIZE="" attribute
    */
    public Select(String name, String size)
    {
        setName(name);
        setSize(size);
    }

    /**
        Constructor sets the name and size attribute.
        Use the set* methods to set the other attributes.

        @param name  set the NAME="" attribute
        @param name  set the SIZE="" attribute
    */
    public Select(String name, int size)
    {
        setName(name);
        setSize(size);
    }

    /**
        Constructor sets the name attribute and adds all the elements in the array.
        Use the set* methods to set the other attributes.

        @param name set the NAME="" attribute
        @param element provide a group of strings to be converted to options elements.
    */
    public Select(String name, String[] element)
    {
        setName(name);
        addElement(element);
    }

    /**
        Constructor sets the name attribute and adds all the option elements in the array.
        Use the set* methods to set the other attributes.

        @param name set the NAME="" attribute
        @param element provide a group of strings to be converted to options elements.
    */
    public Select(String name, Option[] element)
    {
        setName(name);
        addElement(element);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public Select addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public Select addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the Element.
        @param     element adds and Element to the Element.
    */
    public Select addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds a group of elements to the select element.
        @param     element adds a group of elements to the select element.
    */
    public Select addElement(Option[] element)
    {
        for(int x = 0 ; x < element.length; x++)
        {
            addElementToRegistry(element[x]);
        }
        return(this);
    }

    /**
        Adds an Element to the Element.
        @param     element adds and Element to the Element.
    */
    public Select addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Creates a group of option elements and adds them to this select.
        @param     element adds a group of option elements to this select.
    */
    public Select addElement(String[] element)
    {
        Option[] option = new Option().addElement(element);
        addElement(option);
        return(this);
    }

    public Select selectOption(int option)
    {
        Enumeration _enum = keys();
        for(int x = 0; _enum.hasMoreElements(); x++)
        {
            ConcreteElement element = (ConcreteElement)getElement((String)_enum.nextElement());
            if(x == option)
            {
                ((Option)element).setSelected(true);
                break;
            }
        }
        return this;
    }

    /**
        <p>
        Creates and appends an option element.
        </p><p>
        Same as addElement(new org.apache.ecs.html.Option(value)).
        </p>
        @param     value creates an option with value attribute set.
    */
    public Select appendOption(String value)
    {
        return addElement(new org.apache.ecs.html.Option(value));
    }

    /**
        <p>
        Creates and appends an option element.
        </p><p>
        Same as addElement(new org.apache.ecs.html.Option(label,value)).
        </p>
        @param     label creates an option with label attribute set.
        @param     value creates an option with value attribute set.
    */
    public Select appendOption(String label,String value)
    {
        return addElement(new org.apache.ecs.html.Option(label,value));
    }

    /**
        <p>
        Creates and appends an option element.
        </p><p>
        Same as addElement(new org.apache.ecs.html.Option(label,value)).
        </p>
        @param     label creates an option with label attribute set.
        @param     value creates an option with value attribute set.
    */
    public Select appendOption(String label,int value)
    {
        return addElement(new org.apache.ecs.html.Option(label,value));
    }

    /**
        <p>
        Creates and appends an option element.
        </p><p>
        Same as addElement(new org.apache.ecs.html.Option(label,value)).
        </p>
        @param     label creates an option with label attribute set.
        @param     value creates an option with value attribute set.
    */
    public Select appendOption(String label,double value)
    {
        return addElement(new org.apache.ecs.html.Option(label,value));
    }

    /**
        <p>
        Creates and appends an option element.
        </p><p>
        Same as addElement(new org.apache.ecs.html.Option(label,value,text)).
        </p>
        @param     label creates an option with label attribute set.
        @param     value creates an option with value attribute set.
        @param     text added to the option as a text element.
    */
    public Select appendOption(String label,String value,String text)
    {
        return addElement(new org.apache.ecs.html.Option(label,value,text));
    }

    /**
        <p>
        Creates and appends an option element.
        </p><p>
        Same as addElement(new org.apache.ecs.html.Option(label,value,text)).
        </p>
        @param     label creates an option with label attribute set.
        @param     value creates an option with value attribute set.
        @param     text added to the option as a text element.
    */
    public Select appendOption(String label,int value,String text)
    {
        return addElement(new org.apache.ecs.html.Option(label,value,text));
    }

    /**
        <p>
        Creates and appends an option element.
        </p><p>
        Same as addElement(new org.apache.ecs.html.Option(label,value,text)).
        </p>
        @param     label creates an option with label attribute set.
        @param     value creates an option with value attribute set.
        @param     text added to the option as a text element.
    */
    public Select appendOption(String label,double value,String text)
    {
        return addElement(new org.apache.ecs.html.Option(label,value,text));
    }

    /**
        Sets the NAME="" attribute
        @param   name  the NAME="" attribute
    */
    public Select setName(String name)
    {
        addAttribute("name",name);
        return this;
    }

    /**
        Sets the SIZE="" attribute
        @param   size  the SIZE="" attribute
    */
    public Select setSize(String size)
    {
        addAttribute("size",size);
        return this;
    }

    /**
        Sets the SIZE="" attribute
        @param   size  the SIZE="" attribute
    */
    public Select setSize(int size)
    {
        setSize(Integer.toString(size));
        return this;
    }

    /**
        Sets the multiple value
        @param   multiple  true or false
    */
    public Select setMultiple(boolean multiple)
    {
        if ( multiple == true )
            addAttribute("multiple", NO_ATTRIBUTE_VALUE);
        else
            removeAttribute("multiple");

        return(this);
    }

    /**
        Sets the TABINDEX="" attribute
        @param   alt  the TABINDEX="" attribute
    */
    public Select setTabindex(String index)
    {
        addAttribute("tabindex",index);
        return this;
    }

    /**
        Sets the TABINDEX="" attribute
        @param   alt  the TABINDEX="" attribute
    */
    public Select setTabindex(int index)
    {
        setTabindex(Integer.toString(index));
        return this;
    }

    /**
        Sets the disabled value
        @param   disabled  true or false
    */
    public Select setDisabled(boolean disabled)
    {
        if ( disabled == true )
            addAttribute("disabled", NO_ATTRIBUTE_VALUE);
        else
            removeAttribute("disabled");

        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public Select removeElement(String hashcode)
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
        The onfocus event occurs when an element receives focus either by the
        pointing device or by tabbing navigation. This attribute may be used
        with the following elements: LABEL, INPUT, SELECT, TEXTAREA, and
        BUTTON.

        @param The script
    */
    public void setOnFocus(String script)
    {
        addAttribute ( "onFocus", script );
    }

    /**
        The onblur event occurs when an element loses focus either by the
        pointing device or by tabbing navigation. It may be used with the same
        elements as onfocus.

        @param The script
    */
    public void setOnBlur(String script)
    {
        addAttribute ( "onBlur", script );
    }

}
