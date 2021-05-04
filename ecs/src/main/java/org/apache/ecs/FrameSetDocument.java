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
package org.apache.ecs;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import org.apache.ecs.html.Html;
import org.apache.ecs.html.Body;
import org.apache.ecs.html.Title;
import org.apache.ecs.html.Head;
import org.apache.ecs.html.FrameSet;
import org.apache.ecs.html.NoFrames;

/**
    This class creates a FrameSetDocument container, for convience.

    @version $Id: FrameSetDocument.java,v 1.4 2003/04/27 09:43:57 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class FrameSetDocument implements Serializable,Cloneable
{
    /** @serial html html */
    private Html html; // this is the actual container for head and body
    /** @serial head head */
    private Head head;
    /** @serial body body */
    private Body body;
    /** @serial title title */
    private Title title;
    /** @serial frameset frameset */
    private FrameSet frameset;
    /** @serial noframes frameset */
    private NoFrames noframes;
    /** @serial doctype doctype */
    private Doctype doctype = null;

    /** @serial codeset codeset */
    private String codeset = null;
    
    {
        html = new Html();
        head = new Head();
        title = new Title();
        frameset = new FrameSet();
        noframes = new NoFrames();
        body = new Body();
        
        head.addElement("title",title);
        html.addElement("head",head);
        html.addElement("frameset",frameset);
        html.addElement("noframes",noframes);
        noframes.addElement("body",body);        
    }
    
    /**
        Basic constructor.
    */
    public FrameSetDocument()
    {
    }

    /**
        Basic constructor. Sets the codeset for the page output.
    */
    public FrameSetDocument(String codeset)
    {
        setCodeset(codeset);
    }

    /**
        Get the doctype element for this document container.
    */
    public Doctype getDoctype()
    {
        return(doctype);
    }
    
    /**
        Set the doctype element for this document container.
    */
    public FrameSetDocument setDoctype(Doctype set_doctype)
    {
        this.doctype = set_doctype;
        return(this);
    }

    /**
        Get the html element for this document container.
    */
    public Html getHtml()
    {
        return(html);
    }
    
    /**
        Set the html element for this FrameSetDocument container.
    */
    public FrameSetDocument setHtml(Html set_html)
    {
        this.html = set_html;
        return(this);
    }
    
    /**
        Get the head element for this FrameSetDocument container.
    */
    public Head getHead()
    {
        return(head);
    }

    /**
        Set the head element for this FrameSetDocument container.
    */
    public FrameSetDocument setHead(Head set_head)
    {
        html.addElement("head",set_head);
        this.head = set_head;
        return(this);
    }

    /**
        Append to the head element for this FrameSetDocument container.
        @param value adds to the value between the head tags
    */
    public FrameSetDocument appendHead(Element value)
    {
        head.addElement(value);
        return(this);
    }

    /**
        Append to the head element for this FrameSetDocument container.
        @param value adds to the value between the head tags
    */
    public FrameSetDocument appendHead(String value)
    {
        head.addElement(value);
        return(this);
    }

    /**
        Get the FrameSet element for this FrameSetDocument container.
    */
    public FrameSet getFrameSet()
    {
        return(frameset);
    }

    /**
        Set the FrameSet element for this FrameSetDocument container.
    */
    public FrameSetDocument setFrameSet(FrameSet set_frameset)
    {
        html.addElement("frameset",set_frameset);
        this.frameset = set_frameset;
        return(this);
    }

    /**
        Append to the head element for this FrameSetDocument container.
        @param value adds to the value between the head tags
    */
    public FrameSetDocument appendFrameSet(Element value)
    {
        frameset.addElement(value);
        return(this);
    }

    /**
        Append to the head element for this FrameSetDocument container.
        @param value adds to the value between the head tags
    */
    public FrameSetDocument appendFrameSet(String value)
    {
        frameset.addElement(value);
        return(this);
    }
    /**
        Get the body element for this FrameSetDocument container.
    */
    public Body getBody()
    {
        return(body);
    }

    /**
        Set the Body element for this FrameSetDocument container.
    */
    public FrameSetDocument setBody(Body set_body)
    {       
        noframes.addElement("body",set_body);
        this.body = set_body;
        return(this);
    }
    
    /**
        Append to the body element for this FrameSetDocument container.
        @param value adds to the value between the body tags
    */
    public FrameSetDocument appendBody(Element value)
    {
        body.addElement(value);
        return(this);
    }

    /**
        Append to the body element for this FrameSetDocument container.
        @param value adds to the value between the body tags
    */
    public FrameSetDocument appendBody(String value)
    {
        body.addElement(value);
        return(this);
    }

    /**
        Get the title element for this FrameSetDocument container.
    */
    public Title getTitle()
    {
        return(title);
    }

    /**
        Set the Title element for this FrameSetDocument container.
    */
    public FrameSetDocument setTitle(Title set_title)
    {
        head.addElement("title",set_title);
        this.title = set_title;
        return(this);
    }
    
    /**
        Append to the title element for this FrameSetDocument container.
        @param value adds to the value between the title tags
    */
    public FrameSetDocument appendTitle(Element value)
    {
        title.addElement(value);
        return(this);
    }

    /**
        Append to the title element for this FrameSetDocument container.
        @param value adds to the value between the title tags
    */
    public FrameSetDocument appendTitle(String value)
    {
        title.addElement(value);
        return(this);
    }

    /**
     * Sets the codeset for this FrameSetDocument
     */
    public void setCodeset ( String codeset )
    {
        this.codeset = codeset;
    }

    /**
     * Gets the codeset for this FrameSetDocument
     *
     * @return    the codeset 
     */
    public String getCodeset()
    {
        return this.codeset;
    }
    
    /**
        Write the container to the OutputStream
    */
    public void output(OutputStream out)
    {
        if (doctype != null)
        {
            doctype.output(out);
            try
            {
                out.write('\n');
            }
            catch ( Exception e)
            {}
        }
        // FrameSetDocument is just a convient wrapper for Html call Html.output
        html.output(out);
    }

    /**
        Write the container to the PrintWriter
    */
    public void output(PrintWriter out)
    {
        if (doctype != null)
        {
            doctype.output(out);
            try
            {
                out.write('\n');
            }
            catch ( Exception e)
            {}
        }
        // FrameSetDocument is just a convient wrapper for Html call Html.output
        html.output(out);
    }

    /**
        Override the toString() method so that it prints something meaningful.
    */
    public final String toString()
    {
        StringBuffer sb = new StringBuffer();
        if ( getCodeset() != null )
        {
            if (doctype != null)
                sb.append (doctype.toString(getCodeset()));
            sb.append (html.toString(getCodeset()));
            return (sb.toString());
        }
        else
        {
            if (doctype != null)
                sb.append (doctype.toString());
            sb.append (html.toString());
            return(sb.toString());
        }
    }

    /**
        Override the toString() method so that it prints something meaningful.
    */
    public final String toString(String codeset)
    {
        StringBuffer sb = new StringBuffer();
        if (doctype != null)
            sb.append (doctype.toString(getCodeset()));
        sb.append (html.toString(getCodeset()));
        return(sb.toString());
    }
   
    /**
        Allows the FrameSetDocument to be cloned.  Doesn't return an instanceof FrameSetDocument returns instance of Html.

    */

    public Object clone()
    {
        return(html.clone());
    }
}
