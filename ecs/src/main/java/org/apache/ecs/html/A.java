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
    This class creates a &lt;A&gt; tag.
    <P>
    Please refer to the TestBed.java file for example code usage.

    @version $Id: A.java,v 1.6 2003/04/27 09:21:59 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class A extends MultiPartElement implements Printable, FocusEvents, MouseEvents, KeyEvents
{
    /**
          Private initialization routine.
     */
    {
        setElementType("a");
    }

    /**
        Basic constructor. You need to set the attributes using the
        set* methods.
    */
    public A()
    {
    }

    /**
        This constructor creates a &lt;A&gt; tag.

        @param   href  the URI that goes between double quotes
    */
    public A(String href)
    {
        setHref(href);
    }

    /**
        This constructor creates a &lt;A&gt; tag.
        
        @param   href  the URI that goes between double quotes
        @param   value what goes between &lt;start_tag&gt; &lt;end_tag&gt;
    */
    public A(String href, String value)
    {
        setHref(href);
        addElement(value);
    }

    /**
        This constructor creates a &lt;A&gt; tag.

        @param   href  the URI that goes between double quotes
        @param   value what goes between &lt;start_tag&gt; &lt;end_tag&gt;
    */
    public A(String href, Element value)
    {
        setHref(href);
        addElement(value);
    }

    /**
        This constructor creates a &lt;A&gt; tag.

        @param   href  the URI that goes between double quotes
        @param   name  the NAME="" attribute
        @param   value what goes between &lt;start_tag&gt; &lt;end_tag&gt;
    */
    public A(String href, String name, String value)
    {
        setHref(href);
        setName(name);
        addElement(value);
    }

    /**
        This constructor creates a &lt;A&gt; tag.

        @param   href  the URI that goes between double quotes
        @param   name  the NAME="" attribute
        @param   value what goes between &lt;start_tag&gt; &lt;end_tag&gt;
    */
    public A(String href, String name, Element value)
    {
        setHref(href);
        setName(name);
        addElement(value);
    }

    /**
        This constructor creates a &lt;A&gt; tag.

        @param   href  the URI that goes between double quotes
        @param   name  the NAME="" attribute
        @param   target  the TARGET="" attribute
        @param   value the value that goes between &lt;start_tag&gt; &lt;end_tag&gt;
    */
    public A(String href, String name, String target, Element value)
    {
        setHref(href);
        setName(name);
        setTarget(target);
        addElement(value);
    }

    /**
        This constructor creates a &lt;A&gt; tag.

        @param   href  the URI that goes between double quotes
        @param   name  the NAME="" attribute
        @param   target  the TARGET="" attribute
        @param   value the value that goes between &lt;start_tag&gt; &lt;end_tag&gt;
    */
    public A(String href, String name, String target, String value)
    {
        setHref(href);
        setName(name);
        setTarget(target);
        addElement(value);
    }

    /**
        This constructor creates a &lt;A&gt; tag.

        @param   href  the URI that goes between double quotes
        @param   name  the NAME="" attribute
        @param   target  the TARGET="" attribute
        @param   lang  the LANG="" attribute
        @param   value the value that goes between &lt;start_tag&gt; &lt;end_tag&gt;
    */
    public A(String href, String name, String target, String lang,
            String value)
    {
        setHref(href);
        setName(name);
        setTarget(target);
        setLang(lang);
        addElement(value);
    }

    /**
        This constructor creates a &lt;A&gt; tag.

        @param   href  the URI that goes between double quotes
        @param   name  the NAME="" attribute
        @param   target  the TARGET="" attribute
        @param   lang  the LANG="" attribute
        @param   value the value that goes between &lt;start_tag&gt; &lt;end_tag&gt;
    */
    public A(String href, String name, String target, String lang,
            Element value)
    {
        setHref(href);
        setName(name);
        setTarget(target);
        setLang(lang);
        addElement(value);
    }

    /**
        Sets the FOLDER="" attribute
        @param   folder  the FOLDER="" attribute
    */
    public A setFolder(String folder)
    {
        addAttribute("folder",folder);
        return this;
    }

    /**
        Sets the HREF="" attribute
        @param   href  the HREF="" attribute
    */
    public A setHref(String href)
    {
        addAttribute("href",href);
        return this;
    }

    /**
        Sets the NAME="" attribute
        @param   name  the NAME="" attribute
    */
    public A setName(String name)
    {
        addAttribute("name",name);
        return this;
    }

    /**
        Sets the TARGET="" attribute
        @param   target  the TARGET="" attribute
    */
    public A setTarget(String target)
    {
        addAttribute("target",target);
        return this;
    }

    /**
        Sets the REL="" attribute
        @param   rel  the REL="" attribute
    */
    public A setRel(String rel)
    {
        addAttribute("rel",rel);
        return this;
    }

    /**
        Sets the REV="" attribute
        @param   rev  the REV="" attribute
    */
    public A setRev(String rev)
    {
        addAttribute("rev",rev);
        return this;
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public A addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public A addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public A addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public A addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public A removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
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
        Determine if this element needs a line break, if pretty printing.
    */
    public boolean getNeedLineBreak()
    {
        java.util.Enumeration _enum = elements();
        int i=0;
        int j=0;
        while(_enum.hasMoreElements())
        {
            j++;
            Object obj = _enum.nextElement();
            if( obj instanceof IMG )
                i++;
        }
        if ( i==j) 
            return false;  
        return true;
    }

}
