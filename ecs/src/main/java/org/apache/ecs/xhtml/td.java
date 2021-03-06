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
    This class creates a &lt;td&gt; object.
    @version $Id: td.java,v 1.2 2003/04/27 09:36:17 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
    @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>
*/
public class td extends MultiPartElement implements Printable, MouseEvents, KeyEvents
{
    /**
        private initializer.
    */
    {
        setElementType("td");
        setCase(LOWERCASE);
        setAttributeQuote(true);
    }

    /**
        Basic Constructor use set* methods.
    */
    public td()
    {
    }

    /**
        Basic Constructor use set* methods.
    */
    public td(Element element)
    {
        addElement(element);
    }

    /**
        Basic Constructor use set* methods.
    */
    public td(String element)
    {
        addElement(element);
    }

    /*
        Basic Constructor use set* methods.
        @param close.  Print the closing tag or not.
     *
    public td(boolean close)
    {
        setNeedClosingTag(close);
    }*/
    
    /**
        Sets the abbr="" attribute.
        @param cdata    sets the abbr="" attribute.
    */
    public td setAbbr(String cdata)
    {
        addAttribute("abbr",cdata);
        return(this);
    }

    /**
        Sets the axis="" attribute
        @param  cdata   sets the axis="" attribute
    */
    public td setAxis(String cdata)
    {
        addAttribute("axis",cdata);
        return(this);
    }

    /**
        Sets the axes="" attribute
        @param  id_refs list of id's for header cells
    */
    public td setAxes(String id_refs)
    {
        addAttribute("axes",id_refs);
        return(this);
    }

    /**
        Sets the rowspan="" attribute
        @param  rowspan    number of rows spaned by cell
    */
    public td setRowSpan(int rowspan)
    {
        addAttribute("rowspan",Integer.toString(rowspan));
        return(this);
    }

    /**
        Sets the rowspan="" attribute
        @param  rowspan    number of rows spaned by cell
    */
    public td setRowSpan(String rowspan)
    {
        addAttribute("rowspan",rowspan);
        return(this);
    }

    /**
        Sets the colspan="" attribute
        @param  colspan    number of columns spanned by cell
    */
    public td setColSpan(int colspan)
    {
        addAttribute("colspan",Integer.toString(colspan));
        return(this);
    }

    /**
        Sets the colspan="" attribute
        @param  colspan    number of columns spanned by cell
    */
    public td setColSpan(String colspan)
    {
        addAttribute("colspan",colspan);
        return(this);
    }

    /**
        Sets word wrap on or off.
        @param wrap turn word wrap on or off.
    */
    public td setNoWrap(boolean wrap)
    {
        if ( wrap == true )
            addAttribute("nowrap", "nowrap");
        else
            removeAttribute("nowrap");

        return(this);
    }

    /**
        Supplies user agents with a recommended cell width.  (Pixel Values)
        @param width    how many pixels to make cell
    */
    public td setWidth(int width)
    {
        addAttribute("width",Integer.toString(width));
        return(this);
    }
    
    /**
        Supplies user agents with a recommended cell width.  (Pixel Values)
        @param width    how many pixels to make cell
    */
    public td setWidth(String width)
    {
        addAttribute("width",width);
        return(this);
    }

    /**
        Supplies user agents with a recommended cell height.  (Pixel Values)
        @param height    how many pixels to make cell
    */
    public td setHeight(int height)
    {
        addAttribute("height",Integer.toString(height));
        return(this);
    }

    /**
        Supplies user agents with a recommended cell height.  (Pixel Values)
        @param height    how many pixels to make cell
    */
    public td setHeight(String height)
    {
        addAttribute("height",height);
        return(this);
    }

    /**
        Sets the align="" attribute convience variables are provided in the AlignType interface
        @param  align   Sets the align="" attribute
    */
    public td setAlign(String align)
    {
        addAttribute("align",align);
        return(this);
    }

    /**
        Sets the valign="" attribute convience variables are provided in the AlignType interface
        @param  valign   Sets the valign="" attribute
    */
    public td setVAlign(String valign)
    {
        addAttribute("valign",valign);
        return(this);
    }

    /**
        Sets the char="" attribute.
        @param character    the character to use for alignment.
    */
    public td setChar(String character)
    {
        addAttribute("char",character);
        return(this);
    }

    /**
        Sets the charoff="" attribute.
        @param char_off When present this attribute specifies the offset
        of the first occurrence of the alignment character on each line.
    */
    public td setCharOff(int char_off)
    {
        addAttribute("charoff",Integer.toString(char_off));
        return(this);
    }

    /**
        Sets the charoff="" attribute.
        @param char_off When present this attribute specifies the offset
        of the first occurrence of the alignment character on each line.
    */
    public td setCharOff(String char_off)
    {
        addAttribute("charoff",char_off);
        return(this);
    }

    /**
        Sets the bgcolor="" attribute
        @param color    sets the background color of the cell.
    */
    public td setBgColor(String color)
    {
        addAttribute("bgcolor",HtmlColor.convertColor(color));
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
    public td addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public td addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

     /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public td addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public td addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public td removeElement(String hashcode)
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
            if( obj instanceof img || obj instanceof a )
                i++;
        }
        if ( i==j) 
            return false;  
        return true;
    }

} 
