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
    This class creates a &lt;option&gt; tag.
    The option tag defaults to not having a closing &lt;/option&gt;
    because it is optional in the spec. This can be
    overridden by setNeedClosingTag(true)

    @version $Id: option.java,v 1.2 2003/04/27 09:37:58 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>
*/
public class option extends MultiPartElement implements Printable, FocusEvents, FormEvents, MouseEvents, KeyEvents

{

    /**
        Private initialization routine.
    */
    {
        setElementType("option");
        setCase(LOWERCASE);
        setAttributeQuote(true);
    }
    
    /**
        Basic constructor. Use the set* methods to set the values
        of the attributes.
    */
    public option()
    {
    }
    
    /**
        Basic constructor. Use the set* methods to set the values
        of the attributes.
        
        @param value sets the attribute value=""
    */
    public option(String value)
    {
        setValue(value);
    }

    /**
        Basic constructor. Use the set* methods to set the values
        of the attributes.
        
        @param label sets the attribute label=""
        @param value sets the attribute value=""
    */
    public option(String label, String value)
    {
        setLabel(label);
        setValue(value);
    }

    /**
        Basic constructor. Use the set* methods to set the values
        of the attributes.
        
        @param label sets the attribute label=""
        @param value sets the attribute value=""
    */
    public option(String label, int value)
    {
        setLabel(label);
        setValue(value);
    }

    /**
        Basic constructor. Use the set* methods to set the values
        of the attributes.
        
        @param label sets the attribute label=""
        @param value sets the attribute value=""
    */
    public option(String label, double value)
    {
        setLabel(label);
        setValue(value);
    }

    /**
        Sets the label="" attribute
        @param   label  the label="" attribute
    */
    public option setLabel(String label)
    {
        addAttribute("label",label);
        return this;
    }

    /**
        Sets the value="" attribute
        @param   value  the value="" attribute
    */
    public option setValue(String value)
    {
        addAttribute("value",value);
        return this;
    }

    /**
        Sets the value="" attribute
        @param   value  the value="" attribute
    */
    public option setValue(int value)
    {
        addAttribute("value",Integer.toString(value));
        return this;
    }

    /**
        Sets the value="" attribute
        @param   value  the value="" attribute
    */
    public option setValue(double value)
    {
        addAttribute("value",Double.toString(value));
        return this;
    }

    /**
        Sets the selected value
        @param   selected  true or false
    */
    public option setSelected(boolean selected)
    {
        if ( selected == true )
            addAttribute("selected", "selected");
        else
            removeAttribute("selected");
            
        return(this);
    }

    /**
        Sets the disabled value
        @param   disabled  true or false
    */
    public option setDisabled(boolean disabled)
    {
        if ( disabled == true )
            addAttribute("disabled", "disabled");
        else
            removeAttribute("disabled");
            
        return(this);
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
    public option addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public option addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public option addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }
    
    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public option addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Creates a group of options.
        @param  Creates a group of options.
     */
    public option[] addElement(String[] element)
    {
        option[] option = new option[element.length];
        for(int x = 0; x < element.length; x++)
        {
            option[x]= new option().addElement(element[x]);
        }
        return(option);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public option removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }

    /**
        The onfocus event occurs when an element receives focus either by the
        pointing device or by tabbing navigation. This attribute may be used
        with the following elements: label, input, select, textarea, and
        button.
        
        @param The script
    */
    public void setOnFocus(String script)
    {
        addAttribute ( "onfocus", script );
    }

    /**
        The onblur event occurs when an element loses focus either by the
        pointing device or by tabbing navigation. It may be used with the same
        elements as onfocus.
        
        @param The script
    */
    public void setOnBlur(String script)
    {
        addAttribute ( "onblur", script );
    }

    /**
        The onsubmit event occurs when a form is submitted. It only applies to
        the FORM element.
        
        @param The script
    */
    public void setOnSubmit(String script)
    {
        addAttribute ( "onsubmit", script );
    }

    /**
        The onreset event occurs when a form is reset. It only applies to the
        FORM element.
        
        @param The script
    */
    public void setOnReset(String script)
    {
        addAttribute ( "onreset", script );
    }

    /**
        The onselect event occurs when a user selects some text in a text
        field. This attribute may be used with the INPUT and TEXTAREA elements.
        
        @param The script
    */
    public void setOnSelect(String script)
    {
        addAttribute ( "onselect", script );
    }

    /**
        The onchange event occurs when a control loses the input focus and its
        value has been modified since gaining focus. This attribute applies to
        the following elements: input, select, and textarea.
        
        @param The script
    */
    public void setOnChange(String script)
    {
        addAttribute ( "onchange", script );
    }

    /**
        The onclick event occurs when the pointing device button is clicked
        over an element. This attribute may be used with most elements.
        
        @param The script
    */
    public void setOnClick(String script)
    {
        addAttribute ( "onclick", script );
    }
    /**
        The ondblclick event occurs when the pointing device button is double
        clicked over an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnDblClick(String script)
    {
        addAttribute ( "ondblclick", script );
    }
    /**
        The onmousedown event occurs when the pointing device button is pressed
        over an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseDown(String script)
    {
        addAttribute ( "onmousedown", script );
    }
    /**
        The onmouseup event occurs when the pointing device button is released
        over an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseUp(String script)
    {
        addAttribute ( "onmouseup", script );
    }
    /**
        The onmouseover event occurs when the pointing device is moved onto an
        element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseOver(String script)
    {
        addAttribute ( "onmouseover", script );
    }
    /**
        The onmousemove event occurs when the pointing device is moved while it
        is over an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseMove(String script)
    {
        addAttribute ( "onmousemove", script );
    }
    /**
        The onmouseout event occurs when the pointing device is moved away from
        an element. This attribute may be used with most elements.

        @param The script
    */
    public void setOnMouseOut(String script)
    {
        addAttribute ( "onmouseout", script );
    }

    /**
        The onkeypress event occurs when a key is pressed and released over an
        element. This attribute may be used with most elements.
        
        @param The script
    */
    public void setOnKeyPress(String script)
    {
        addAttribute ( "onkeypress", script );
    }

    /**
        The onkeydown event occurs when a key is pressed down over an element.
        This attribute may be used with most elements.
        
        @param The script
    */
    public void setOnKeyDown(String script)
    {
        addAttribute ( "onkeydown", script );
    }

    /**
        The onkeyup event occurs when a key is released over an element. This
        attribute may be used with most elements.
        
        @param The script
    */
    public void setOnKeyUp(String script)
    {
        addAttribute ( "onkeyup", script );
    }
}
