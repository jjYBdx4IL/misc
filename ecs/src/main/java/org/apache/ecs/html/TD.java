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
    This class creates a &lt;TD&gt; object.
    @version $Id: TD.java,v 1.8 2003/04/27 09:03:39 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class TD extends MultiPartElement implements Printable, MouseEvents, KeyEvents
{
    /**
        private initializer.
    */
    {
        setElementType("td");
    }

    /**
        Basic Constructor.
        Use set* methods to change attributes.
    */
    public TD()
    {
    }

    /**
        Constructor adds element.
        Use set* methods to change attributes.
        @param element  element to add.
    */
    public TD(Element element)
    {
        addElement(element);
    }

    /**
        Constructor adds element.
        Use set* methods to change attributes.
        @param element  element to add.
    */
    public TD(String element)
    {
        addElement(element);
    }

    /**
        Constructor sets closing tag.
        Use set* methods to change attributes.
        @param close  print the closing tag?
    */
    public TD(boolean close)
    {
        setNeedClosingTag(close);
    }
    
    /**
        Sets the ABBR="" attribute.
        @param cdata    sets the ABBR="" attribute.
    */
    public TD setAbbr(String cdata)
    {
        addAttribute("abbr",cdata);
        return(this);
    }

    /**
        Sets the AXIS="" attribute
        @param  cdata   sets the AXIS="" attribute
    */
    public TD setAxis(String cdata)
    {
        addAttribute("axis",cdata);
        return(this);
    }

    /**
        Sets the AXES="" attribute
        @param  id_refs list of id's for header cells
    */
    public TD setAxes(String id_refs)
    {
        addAttribute("axes",id_refs);
        return(this);
    }

    /**
        Sets the ROWSPAN="" attribute
        @param  rowspan    Number of rows spaned by cell
    */
    public TD setRowSpan(int rowspan)
    {
        addAttribute("rowspan",Integer.toString(rowspan));
        return(this);
    }

    /**
        Sets the ROWSPAN="" attribute
        @param  rowspan    Number of rows spaned by cell
    */
    public TD setRowSpan(String rowspan)
    {
        addAttribute("rowspan",rowspan);
        return(this);
    }

    /**
        Sets the COLSPAN="" attribute
        @param  colspan    Number of columns spanned by cell
    */
    public TD setColSpan(int colspan)
    {
        addAttribute("colspan",Integer.toString(colspan));
        return(this);
    }

    /**
        Sets the COLSPAN="" attribute
        @param  colspan    Number of columns spanned by cell
    */
    public TD setColSpan(String colspan)
    {
        addAttribute("colspan",colspan);
        return(this);
    }

    /**
        Sets word wrap on or off.
        @param wrap turn word wrap on or off.
    */
    public TD setNoWrap(boolean wrap)
    {
        if ( wrap == true )
            addAttribute("nowrap", NO_ATTRIBUTE_VALUE);
        else
            removeAttribute("nowrap");

        return(this);
    }

    /**
        Supplies user agents with a recommended cell width.  (Pixel Values)
        @param width    how many pixels to make cell
    */
    public TD setWidth(int width)
    {
        addAttribute("width",Integer.toString(width));
        return(this);
    }
    
    /**
        Supplies user agents with a recommended cell width.  (Pixel Values)
        @param width    how many pixels to make cell
    */
    public TD setWidth(String width)
    {
        addAttribute("width",width);
        return(this);
    }

    /**
        Supplies user agents with a recommended cell height.  (Pixel Values)
        @param height    how many pixels to make cell
    */
    public TD setHeight(int height)
    {
        addAttribute("height",Integer.toString(height));
        return(this);
    }

    /**
        Supplies user agents with a recommended cell height.  (Pixel Values)
        @param height    how many pixels to make cell
    */
    public TD setHeight(String height)
    {
        addAttribute("height",height);
        return(this);
    }

    /**
        Sets the ALIGN="" attribute convience variables are provided in the AlignType interface
        @param  align   Sets the ALIGN="" attribute
    */
    public TD setAlign(String align)
    {
        addAttribute("align",align);
        return(this);
    }

    /**
        Sets the VALIGN="" attribute convience variables are provided in the AlignType interface
        @param  valign   Sets the ALIGN="" attribute
    */
    public TD setVAlign(String valign)
    {
        addAttribute("valign",valign);
        return(this);
    }

    /**
        Sets the CHAR="" attribute.
        @param character    the character to use for alignment.
    */
    public TD setChar(String character)
    {
        addAttribute("char",character);
        return(this);
    }

    /**
        Sets the CHAROFF="" attribute.
        @param char_off When present this attribute specifies the offset
        of the first occurrence of the alignment character on each line.
    */
    public TD setCharOff(int char_off)
    {
        addAttribute("charoff",Integer.toString(char_off));
        return(this);
    }

    /**
        Sets the CHAROFF="" attribute.
        @param char_off When present this attribute specifies the offset
        of the first occurrence of the alignment character on each line.
    */
    public TD setCharOff(String char_off)
    {
        addAttribute("charoff",char_off);
        return(this);
    }

    /**
        Sets the BGCOLOR="" attribute
        @param color    sets the background color of the cell.
    */
    public TD setBgColor(String color)
    {
        addAttribute("bgcolor",HtmlColor.convertColor(color));
        return(this);
    }

    /**
        Sets the BACKGROUND="" attribute
        @param url    sets the background to some image specified by url.
    */
    public TD setBackground(String url)
    {
        addAttribute("background",url);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public TD addElement(String hashcode,Element element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  hashcode name of element for hash table
        @param  element Adds an Element to the element.
     */
    public TD addElement(String hashcode,String element)
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }

     /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public TD addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
        Adds an Element to the element.
        @param  element Adds an Element to the element.
     */
    public TD addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    /**
        Removes an Element from the element.
        @param hashcode the name of the element to be removed.
    */
    public TD removeElement(String hashcode)
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
            if( obj instanceof IMG || obj instanceof A )
                i++;
        }
        if ( i==j) 
            return false;  
        return true;
    }

} 
