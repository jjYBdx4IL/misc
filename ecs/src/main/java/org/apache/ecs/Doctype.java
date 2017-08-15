package org.apache.ecs;

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

import org.apache.ecs.*;

/**
 * This class creates a &lt;!DOCTYPE&gt; tag.<p>
 *
 * Format:<br>
     &lt;!DOCTYPE [name] [visibility] [identifier] [uri]&gt;<p>
    <p>
    usage:<br>
    Document d = new Document()<br>
        .setDoctype(new Doctype.Html40Strict())<br>
    <p> or <p>
    XMLDocument d = new XMLDocument()<br>
        .addToProlog( new Doctype( "foo", "\"--/bar/baz/en\"", "\"http://qoz.net/foo.dtd\"" );<br>
 *	.addElement( new XML( ...
 *
 *  @see Doctype.Html40Strict
 *  @see Doctype.Html40Transitional
 *  @see Doctype.Html40Frameset
 *  @see Doctype.XHtml10Strict
 *  @see Doctype.XHtml10Transitional
 *  @see Doctype.XHtml10Frameset
 *        
 *  @version $Id: Doctype.java,v 1.6 2003/04/27 09:43:30 rdonkin Exp $
 *  @author <a href="mailto:heuermh@shore.net">Michael Heuer</a>
 *  @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
 *  @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 *  @author <a href="mailto:brucedu@netscape.net">Bruce Durling</a>
 *  @author <a href="mailto:bojan@binarix.com">Bojan Smojver</a>
 *  @author Yuji Kumasaka
*/
public class Doctype extends SinglePartElement implements Printable 
{
    public static final String elementName = "!DOCTYPE";
    public static final String PUBLIC = "PUBLIC";
    
    protected String name;
    protected String visibility;
    protected String identifier;
    protected String uri;

    {
        setElementType(elementName);
        setCase(Element.UPPERCASE);
    }
    
    /**
     * Basic Constructor.
     *
     */
    public Doctype()
    {
        updateElementType();
    }


    /**
     * Constructor.
     *
     * @param name Root element of the XML document.
     * @param id Public identifier.
     * @param uri URI of the DTD.
     */
    public Doctype( String name, String id, String uri )
    {
        this.name = name;
        this.visibility = PUBLIC;
        this.identifier = id;
        this.uri = uri;
        updateElementType();
    }
    
    /**
     * Constructor.
     *
     * @param name Root element of the XML document.
     * @param id Public identifier.
     * @param uri URI of the DTD.
     */
    public Doctype( String name, String visibility, String id, String uri )
    {
        this.name = name;
        this.visibility = visibility;
        this.identifier = id;
        this.uri = uri;
        updateElementType();
    }

    /**
     * Should be called when any of the fields are changed.
     *
     */
    protected void updateElementType() 
    {
        setElementType( elementName
                        + " " + name 
                        + " " + visibility
                        + " " + identifier
                        + " " + uri );
    }

    /**
     * Updates the name of the root element.
     *
     * @param name Name of the root element.
     * @return a value of type 'Doctype'
     */
    public Doctype setName( String name )
    {
        this.name = name;
        updateElementType();
        return( this );
    }
    /**
     * Updates the name of the root element.
     *
     * @param name Name of the root element.
     * @return a value of type 'Doctype'
     */
    public Doctype setVisibility( String visibility )
    {
        this.visibility = visibility;
        updateElementType();
        return( this );
    }
    
    /**
     * Updates the name of the public identifier.
     *
     * @param identifier The public identifier.
     * @return a value of type 'Doctype'
     */
    public Doctype setIdentifier( String identifier )
    {
        this.identifier = identifier;
        updateElementType();
        return( this );
    }
    
    /**
     * Updates the URI of the dtd.
     *
     * @param uri URI of the dtd.
     * @return a value of type 'Doctype'
     */
    public Doctype setUri( String uri )
    {
        this.uri = uri;
        updateElementType();
        return(this);
    }

    /**
     * Adds and Element to the element.
     *
     * @param hashcode name of the element for hash table.
     * @param element Adds an Element to the element.
     * @return a value of type 'Doctype'
     */
    public Doctype addElement( String hashcode, Element element )
    {
        addElementToRegistry( hashcode, element );
        return(this);
    }

    /**
     * Adds an Element to the element.
     *
     * @param hashcode name of the element for the hash table.
     * @param element Adds an Element to the element.
     * @return a value of type 'Doctype'
     */
    public Doctype addElement( String hashcode, String element )
    {
        addElementToRegistry(hashcode,element);
        return(this);
    }
    
    /**
     * Adds an Element to the element.
     *
     * @param element Adds an Element to the element.
     * @return a value of type 'Doctype'
     */
    public Doctype addElement(Element element)
    {
        addElementToRegistry(element);
        return(this);
    }

    /**
     * Adds an Element to the element.
     *
     * @param  element Adds an Element to the element.
     * @return a value of type 'Doctype'
     */
    public Doctype addElement(String element)
    {
        addElementToRegistry(element);
        return(this);
    }
    
    /**
     * Removes an Element from the element.
     *
     * @param hashcode the name of the element to be removed.
     * @return a value of type 'Doctype'
     */
    public Doctype removeElement(String hashcode)
    {
        removeElementFromRegistry(hashcode);
        return(this);
    }

    /**
     * The HTML 4.0 Strict DTD includes all elements and attributes
     * that have not been deprecated or do not appear in frameset
     * documents.
     * <p>
     * See: <a href="http://www.w3.org/TR/REC-html40/sgml/dtd.html">
     * http://www.w3.org/TR/REC-html40/sgml/dtd.html</a>
     */
    public static class Html40Strict extends Doctype {

        public Html40Strict() {
            this.name = "HTML";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD HTML 4.0//EN\"";
            this.uri = "\"http://www.w3.org/TR/REC-html40/strict.dtd\"";
            this.updateElementType();
        }
    }

    /**
     * The HTML 4.0 Transitional DTD includes everything in the
     * strict DTD plus deprecated elements and attributes (most of
     * which concern visual presentation).
     * <p>
     * See: <a href="http://www.w3.org/TR/REC-html40/sgml/loosedtd.html">
     * http://www.w3.org/TR/REC-html40/sgml/loosedtd.html</a>
     */
    public static class Html40Transitional extends Doctype {

        public Html40Transitional() {
            this.name = "HTML";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD HTML 4.0 Transitional//EN\"";
            this.uri = "\"http://www.w3.org/TR/REC-html40/loose.dtd\"";
            this.updateElementType();
        }
    }

    /**
     * The HTML 4.0 Frameset DTD includes everything in the transitional
     * DTD plus frames as well.
     * <p>
     * See: <a href="http://www.w3.org/TR/REC-html40/sgml/framesetdtd.html">
     * http://www.w3.org/TR/REC-html40/sgml/framesetdtd.html</a>
     */ 
    public static class Html40Frameset extends Doctype {

        public Html40Frameset() {
            this.name = "HTML";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD HTML 4.0 Frameset//EN\"";
            this.uri = "\"http://www.w3.org/TR/REC-html40/frameset.dtd\"";
            this.updateElementType();
        }
    }

    /**
     * The HTML 4.01 Strict DTD includes all elements and attributes
     * that have not been deprecated or do not appear in frameset
     * documents.
     * <p>
     * See: <a href="http://www.w3.org/TR/html4/">
     * http://www.w3.org/TR/html4/</a>
     */
    public static class Html401Strict extends Doctype {

        public Html401Strict() {
            this.name = "HTML";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD HTML 4.01//EN\"";
            this.uri = "\"http://www.w3.org/TR/html4/strict.dtd\"";
            this.updateElementType();
        }
    }

    /**
     * The HTML 4.01 Transitional DTD includes everything in the
     * strict DTD plus deprecated elements and attributes (most of
     * which concern visual presentation).
     * <p>
     * See: <a href="http://www.w3.org/TR/html4/">
     * http://www.w3.org/TR/html4/</a>
     */
    public static class Html401Transitional extends Doctype {

        public Html401Transitional() {
            this.name = "HTML";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD HTML 4.01 Transitional//EN\"";
            this.uri = "\"http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd\"";
            this.updateElementType();
        }
    }

    /**
     * The HTML 4.01 Frameset DTD includes everything in the transitional
     * DTD plus frames as well.
     * <p>
     * See: <a href="http://www.w3.org/TR/html4/">
     * http://www.w3.org/TR/html4/</a>
     */ 
    public static class Html401Frameset extends Doctype {

        public Html401Frameset() {
            this.name = "HTML";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD HTML 4.01 Frameset//EN\"";
            this.uri = "\"http://www.w3.org/TR/1999/REC-html401-19991224/frameset.dtd\"";
            this.updateElementType();
        }
    }

    /**
     * The XHTML 1.0 Strict DTD
     * This is the same as HTML 4.0 Strict except for changes due
     * to the differences between XML and SGML.
     * <p>
     * See: <a href="http://www.w3.org/TR/xhtml1">
     * http://www.w3.org/TR/xhtml1</a>
     */
    public static class XHtml10Strict extends Doctype {

        public XHtml10Strict() {
            this.name = "html";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD XHTML 1.0 Strict//EN\"";
            this.uri = "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"";
            this.updateElementType();
        }
    }

    /**
     * The XHTML 1.0 Transitional DTD
     * This is the same as HTML 4.0 Transitional except for changes due
     * to the differences between XML and SGML.
     * <p>
     * See: <a href="http://www.w3.org/TR/xhtml1">
     * http://www.w3.org/TR/xhtml1</a>
     */
    public static class XHtml10Transitional extends Doctype {

        public XHtml10Transitional() {
            this.name = "html";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD XHTML 1.0 Transitional//EN\"";
            this.uri =
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"";
            this.updateElementType();
        }
    }

    /**
     * The XHTML 1.0 Frameset DTD
     * This is the same as HTML 4.0 Frameset except for changes due
     * to the differences between XML and SGML.
     * <p>
     * See: <a href="http://www.w3.org/TR/xhtml1">
     * http://www.w3.org/TR/xhtml1</a>
     */
    public static class XHtml10Frameset extends Doctype {

        public XHtml10Frameset() {
            this.name = "html";
            this.visibility = PUBLIC;
            this.identifier = "\"-//W3C//DTD XHTML 1.0 Frameset//EN\"";
            this.uri =
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\"";
            this.updateElementType();
        }
    }
}
