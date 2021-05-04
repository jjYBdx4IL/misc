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
    This class creates a &lt;FORM&gt; tag.

    @version $Id: Form.java,v 1.5 2003/04/27 09:21:50 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class Form extends MultiPartElement implements Printable, FormEvents, MouseEvents, KeyEvents
{
    public static final String GET = "GET";
    public static final String get = "get";
    public static final String POST = "POST";
    public static final String post = "post";

    public static final String ENC_DEFAULT = "application/x-www-form-urlencoded";
    public static final String ENC_UPLOAD = "multipart/form-data";
    
    /**
        Private initialization routine.
    */
    {
        setElementType("form");
        setEncType ( ENC_DEFAULT );
        setAcceptCharset("UNKNOWN");
    }

    /**
        Basic constructor. You need to set the attributes using the
        set* methods.
    */
    public Form()
    {
    }

    /**
        Use the set* methods to set the values
        of the attributes.

        @param   element  set the value of &lt;FORM&gt;value&lt;/FORM&gt;
    */
    public Form(Element element)
    {
        addElement(element);
    }

    /**
        Use the set* methods to set the values
        of the attributes.

        @param   action  set the value of ACTION=""
    */
    public Form(String action)
    {
        setAction(action);
    }

    /**
        Use the set* methods to set the values
        of the attributes.

        @param   element  set the value of &lt;FORM&gt;value&lt;/FORM&gt;
        @param   action  set the value of ACTION=""
    */
    public Form(String action, Element element)
    {
        addElement(element);
        setAction(action);
    }

    /**
        Use the set* methods to set the values
        of the attributes.

        @param   action  set the value of ACTION=""
        @param   method  set the value of METHOD=""
        @param   element  set the value of &lt;FORM&gt;value&lt;/FORM&gt;
    */
    public Form(String action, String method, Element element)
    {
        addElement(element);
        setAction(action);
        setMethod(method);
    }

    /**
        Use the set* methods to set the values
        of the attributes.

        @param   action  set the value of ACTION=""
        @param   method  set the value of METHOD=""
    */
    public Form(String action, String method)
    {
        setAction(action);
        setMethod(method);
    }

    /**
        Use the set* methods to set the values
        of the attributes.

        @param   action  set the value of ACTION=""
        @param   method  set the value of METHOD=""
        @param   enctype  set the value of ENCTYPE=""
    */
    public Form(String action, String method, String enctype)
    {
        setAction(action);
        setMethod(method);
        setEncType(enctype);
    }

    /**
        Sets the ACTION="" attribute
        @param   action  the ACTION="" attribute
    */
    public Form setAction(String action)
    {
        addAttribute("action",action);
        return this;
    }

    /**
        Sets the METHOD="" attribute
        @param   method  the METHOD="" attribute
    */
    public Form setMethod(String method)
    {
        addAttribute("method",method);
        return this;
    }

    /**
        Sets the ENCTYPE="" attribute
        @param   enctype  the ENCTYPE="" attribute
    */
    public Form setEncType(String enctype)
    {
        addAttribute("enctype",enctype);
        return this;
    }

    /**
        Sets the ACCEPT="" attribute
        @param   accept  the ACCEPT="" attribute
    */
    public Form setAccept(String accept)
    {
        addAttribute("accept",accept);
        return this;
    }

    /**
        Sets the NAME="" attribute
        @param   name  the NAME="" attribute
    */
    public Form setName(String name)
    {
        addAttribute("name",name);
        return this;
    }

    /**
        Sets the TARGET="" attribute
        @param   target  the TARGET="" attribute
    */
    public Form setTarget(String target)
    {
        addAttribute("target",target);
        return this;
    }

    /**
        Sets the ACCEPT-CHARSET="" attribute
        @param   accept  the ACCEPT-CHARSET="" attribute
    */
    public Form setAcceptCharset(String acceptcharset)
    {
        addAttribute("accept-charset",acceptcharset);
        return this;
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public Form addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public Form addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public Form addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public Form addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public Form removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
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
}
