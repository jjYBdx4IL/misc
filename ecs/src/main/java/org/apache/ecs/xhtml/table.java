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
    This class creates a &lt;table&gt; object.
    @version $Id: table.java,v 1.2 2003/04/27 09:36:17 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>
*/
public class table extends MultiPartElement implements Printable, MouseEvents, KeyEvents
{
    /**
        Private iniitialization routine
    */
    {
        setElementType("table");
        setCase(LOWERCASE);
        setAttributeQuote(true);
    }
    public table()
    {
    }

    /**
        Allows one to set the border size.
        
        @param border  sets the border="" attribute.
    */
    public table(int border)
    {
        setBorder(border);
    }

    /**
        Allows one to set the border size.
        
        @param border  sets the border="" attribute.
    */
    public table(String border)
    {
        setBorder(border);
    }

    /**
        Set the summary="" attribue.

        @param summary  sets the summary="" attribute.
    */
    public table setSummary(String summary)
    {
        addAttribute("summary",summary);
        return(this);
    }

    /**
        Sets the align="" attribute.

        @param  align sets the align="" attribute. You can
        use the AlignType.* variables for convience.
    */
    public table setAlign(String align)
    {
        addAttribute("align",align);
        return(this);
    }

    /**
        Sets the width="" attribute.

        @param  width sets the width="" attribute.
    */
    public table setWidth(String width)
    {
        addAttribute("width",width);
        return(this);
    }
    /**
        Sets the height="" attribute.

        @param  width sets the height="" attribute.
    */
    public table setHeight(String height)
    {
        addAttribute("height",height);
        return(this);
    }
    /**
        Sets the width="" attribute.

        @param  width sets the width="" attribute.
    */
    public table setWidth(int width)
    {
        addAttribute("width",Integer.toString(width));
        return(this);
    }
    /**
        Sets the height="" attribute.

        @param  width sets the height="" attribute.
    */
    public table setHeight(int height)
    {
        addAttribute("height",Integer.toString(height));
        return(this);
    }
    /**
        Sets the cols="" attribute.

        @param  width sets the cols="" attribute.
    */
    public table setCols(int cols)
    {
        addAttribute("cols",Integer.toString(cols));
        return(this);
    }
    /**
        Sets the cols="" attribute.

        @param  width sets the cols="" attribute.
    */
    public table setCols(String cols)
    {
        addAttribute("cols",cols);
        return(this);
    }
    /**
        Sets the cellpading="" attribute.
        @param  cellpadding sets the cellpading="" attribute.
    */
    public table setCellPadding(int cellpadding)
    {
        addAttribute("cellpadding",Integer.toString(cellpadding));
        return(this);
    }
    /**
        Sets the cellspacing="" attribute.
        @param  spacing sets the cellspacing="" attribute.
    */
    public table setCellSpacing(int cellspacing)
    {
        addAttribute("cellspacing",Integer.toString(cellspacing));
        return(this);
    }
    /**
        Sets the cellpading="" attribute.
        @param  cellpadding sets the cellpading="" attribute.
    */
    public table setCellPadding(String cellpadding)
    {
        addAttribute("cellpadding",cellpadding);
        return(this);
    }
    /**
        Sets the cellspacing="" attribute.
        @param  spacing sets the cellspacing="" attribute.
    */
    public table setCellSpacing(String cellspacing)
    {
        addAttribute("cellspacing",cellspacing);
        return(this);
    }
    /**
        Sets the border="" attribute.
        @param  border sets the border="" attribute.
    */
    public table setBorder(int border)
    {
        addAttribute("border",Integer.toString(border));
        return(this);
    }
    /**
        Sets the border="" attribute.
        @param  border sets the border="" attribute.
    */
    public table setBorder(String border)
    {
        addAttribute("border",border);
        return(this);
    }
    /**
        Sets the frame="" attribute.
        @param  frame sets the frame="" attribute.
    */
    public table setFrame(String frame)
    {
        addAttribute("frame",frame);
        return(this);
    }
    /**
        Sets the rules="" attribute.
        @param  rules sets the rules="" attribute.
    */
    public table setRules(String rules)
    {
        addAttribute("rules",rules);
        return(this);
    }

    /**
        Sets the bgcolor="" attribute
        @param   color  the bgcolor="" attribute
    */
    public table setBgColor(String color)
    {
        addAttribute("bgcolor",HtmlColor.convertColor(color));
        return this;
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
    public table addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public table addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public table addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public table addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public table removeElement(String hashcode)
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
