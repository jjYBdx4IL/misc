package org.apache.ecs.html2ecs;

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

import java.util.Hashtable;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.apache.ecs.filter.CharacterFilter;
import org.apache.ecs.Entities;
import org.xml.sax.SAXException;

/**
    The point of this class is to create an HTML file -> ECS code converter. 
    It is <strong>NOT</strong> required for ECS core execution. If it does not 
    compile, it is because you need to get the Xerces XML parser for Java from 
    <a href="http://xml.apache.org/">xml.apache.org</a>.
<p>
    This class is presently fairly broken and is really only shown as an example.

    Contributions towards making this class would be MOST appreciated. Please subscribe to 
    the <a href="http://java.apache.org/main/mail.html">ECS mailing list</a> and express
    your interest there.
*/
public class Html2Ecs extends Hashtable
{
    private static org.w3c.dom.Document doc;
    private static DOMParser parser;
    private static InputStream input;

    // Private Initializer
    // Map XML Elements to ECS Elements
    {
        put("A","org.apache.ecs.html.A");
        put("ABBR","org.apache.ecs.html.Abbr");
        put("ACRONYM","org.apache.ecs.html.Acronym");
        put("ADDRESS","org.apache.ecs.html.Address");
        put("APPLET","org.apache.ecs.html.Applet");
        put("AREA","org.apache.ecs.html.Area");
        put("B","org.apache.ecs.B");
        put("BASE","org.apache.ecs.html.Base");
        put("BASEFONT","org.apache.ecs.html.BaseFont");
        put("BDO","org.apache.ecs.html.Bdo");
        put("BIG","org.apache.ecs.html.Big");
        put("BLINK","org.apache.ecs.html.Blink");
        put("BLOCKQUOTE","org.apache.ecs.html.BlockQuote");
        put("BODY","org.apache.ecs.html.Body");
        put("BR","org.apache.ecs.html.BR");
        put("BUTTON","org.apache.ecs.html.Button");
        put("CAPTION","org.apache.ecs.html.Caption");
        put("CENTER","org.apache.ecs.html.Center");
        put("CITE","org.apache.ecs.html.Cite");
        put("CODE","org.apache.ecs.html.Code");
        put("COL","org.apache.ecs.html.Col");
        put("COLGROUP","org.apache.ecs.html.ColGroup");
        put("COMMENT","org.apache.ecs.html.Comment");
        put("DD","org.apache.ecs.html.DD");
        put("DEL","org.apache.ecs.html.Del");
        put("DFN","org.apache.ecs.html.Dfn");
        put("DIV","org.apache.ecs.html.Div");
        put("DL","org.apache.ecs.html.DL");
        put("EM","org.apache.ecs.html.EM");
        put("FILEDSET","org.apache.ecs.html.FieldSet");
        put("FONT","org.apache.ecs.html.Font");
        put("FORM","org.apache.ecs.html.Form");
        put("FRAME","org.apache.ecs.html.Frame");
        put("FRAMESET","org.apache.ecs.html.FrameSet");
        put("H1","org.apache.ecs.html.H1");
        put("H2","org.apache.ecs.html.H2");
        put("H3","org.apache.ecs.html.H3");
        put("H4","org.apache.ecs.html.H4");
        put("H5","org.apache.ecs.html.H5");
        put("H6","org.apache.ecs.html.H6");
        put("HEAD","org.apache.ecs.html.Head");
        put("HR","org.apache.ecs.html.HR");
        put("HTML","org.apache.ecs.html.Html");
        put("I","org.apache.ecs.html.I");
        put("IFRAME","org.apache.ecs.html.IFrame");
        put("IMG","org.apache.ecs.html.IMG");
        put("INPUT","org.apache.ecs.html.Input");
        put("INS","org.apache.ecs.html.Ins");
        put("KBD","org.apache.ecs.html.Kbd");
        put("LABEL","org.apache.ecs.html.Label");
        put("LEGEND","org.apache.ecs.html.Legend");
        put("LI","org.apache.ecs.html.LI");
        put("LINK","org.apache.ecs.html.Link");
        put("MAP","org.apache.ecs.html.Map");
        put("META","org.apache.ecs.html.Meta");
        put("NOBR","org.apache.ecs.html.NOBR");
        put("NOFRAMES","org.apache.ecs.html.NoFrames");
        put("NOSCRIPT","org.apache.ecs.html.NoScript");
        put("OBJECT","org.apache.ecs.html.ObjectElement");
        put("OL","org.apache.ecs.html.OL");
        put("OPTGROUP","org.apache.ecs.html.OptGroup");
        put("OPTION","org.apache.ecs.html.Option");
        put("P","org.apache.ecs.html.P");
        put("PARAM","org.apache.ecs.html.Param");
        put("PRE","org.apache.ecs.html.PRE");
        put("Q","org.apache.ecs.html.Q");
        put("S","org.apache.ecs.html.S");
        put("SAMP","org.apache.ecs.html.Samp");
        put("SCRIPT","org.apache.ecs.html.Script");
        put("SELECT","org.apache.ecs.html.Select");
        put("SMALL","org.apache.ecs.html.Small");
        put("SPAN","org.apache.ecs.html.Span");
        put("STRIKE","org.apache.ecs.html.Strike");
        put("STRONG","org.apache.ecs.html.Strong");
        put("STYLE","org.apache.ecs.html.Style");
        put("SUB","org.apache.ecs.html.Sub");
        put("SUP","org.apache.ecs.html.Sup");
        put("TABLE","org.apache.ecs.html.Table");
        put("TBODY","org.apache.ecs.html.TBody");
        put("TD","org.apache.ecs.html.TD");
        put("THEAD","org.apache.ecs.html.THead");
        put("TITLE","org.apache.ecs.html.Title");
        put("TR","org.apache.ecs.html.TR");
        put("TT","org.apache.ecs.html.TT");
        put("U","org.apache.ecs.html.U");
        put("UL","org.apache.ecs.html.UL");
        put("VAR","org.apache.ecs.html.Var");

    }

    public Html2Ecs()
    {
		super(4);
    }

    private void process( Node node )
    {
        while( node != null)
        {
            constructElement(node);

            // If it has children loop through them
            if ( node.hasChildNodes() &&
                !node.getNodeName().equals("#document") &&
                get(node.getNodeName()) == null )
            {
                System.out.print(".addElement(");
                process ( node.getFirstChild() );
                System.out.println(")");
            }
            else
            {
                process ( node.getFirstChild() );
            }
            // move to the next node.
            node = node.getNextSibling();
            System.out.print("\n");
        }
    }

    private void constructElement( Node node )
    {
        if( get(node.getNodeName().toUpperCase()) != null )
        {
            System.out.print("new ");
            System.out.print(get(node.getNodeName().toUpperCase()));
            System.out.print("()");
        }
        constructAttributes( node );
        constructValue( node );
    }

    private void constructValue( Node node )
    {
        CharacterFilter cf = new CharacterFilter();
        cf.addAttribute("\"",Entities.QUOT);

        if( node.getNodeName().equals("#text")
            && node.getNodeValue().length() > 1 )
        {
            System.out.print(".addElement(\"");
            System.out.print(node.getNodeValue());
            System.out.print("\")");
        }
    }

    private void constructAttributes( Node node )
    {
        NamedNodeMap attrList = node.getAttributes();
        Attr attr;
        Class c = null;

        try
        {
            c = Class.forName( (String) get(node.getNodeName().toUpperCase()) );
        }
        catch(Exception e)
        {
        }

        if( attrList != null && c != null)
        {
            for(int x = 0; x < attrList.getLength(); x++)
            {
                attr = (Attr) attrList.item(x);

                Method[] m = c.getMethods();
                for(int y = 0; y < m.length; ++y)
                {
                    if(m[y].getName().toLowerCase().endsWith(attr.getName().toLowerCase()))
                    {
                        System.out.print(".");
                        System.out.print(m[y].getName());
                        System.out.print("(\"");
                        System.out.print(attr.getValue());
                        System.out.print("\")");
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args)
    {
        Html2Ecs html2ecs = new Html2Ecs();
        try
        {
            input = new FileInputStream( args[0] );
            parser = new DOMParser();
            parser.parse(new InputSource(input));
            doc = parser.getDocument();
        }
        catch(SAXException se)
        {
            System.out.println(se.toString());
        }
        catch(FileNotFoundException fnfe)
        {
            System.out.println(fnfe.toString());
        }
        catch(IOException ioe)
        {
            System.out.println(ioe.toString());
        }
        html2ecs.process( doc );
    }
}
