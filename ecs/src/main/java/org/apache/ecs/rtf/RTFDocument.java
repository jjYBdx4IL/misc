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
package org.apache.ecs.rtf;

import org.apache.ecs.ConcreteElement;

public class RTFDocument
{
    // static declerations.

    /** Ansi character set. ( the default ) */
    public static String ANSI = "\\ansi";
    /** Apple Macintosh character set. */
    public static String MAC = "\\mac";
    /** IBM PC code page 437 character set. */
    public static String PC = "\\pc";
    /** IBM PC code page 850 character set. */
    public static String PCA = "\\pca";

    private Info info = new Info();
    private Title title = new Title();
    private Subject subject = new Subject();
    private Comment comment = new Comment();
    private Version version = new Version();

    RTF rtf = new RTF();
    /* Default Initializer */
    {
        rtf.addElement("charSet",ANSI);
        info.addElement(title);
        info.addElement(subject);
        info.addElement(version);
        info.addElement(comment);
        rtf.addElement(info);
    }

    public RTFDocument()
    {
    }

    public Info getInfo()
    {
        return info;
    }

    public RTFDocument setTitle(String title)
    {
        this.title.addElement(title);
        return this;
    }

    public RTFDocument setVersion(int version)
    {
        this.version.setVersion(version);
        return this;
    }

    public RTFDocument setComment(String comment)
    {
        this.comment.addElement("comment",comment);
        return this;
    }

    public RTFDocument setSubject(String subject)
    {
        this.subject.addElement(subject);
        return this;
    }

    public RTFDocument setColorTable(ColorTbl tbl)
    {
        rtf.addElement(tbl);
        return this;
    }

    public RTFDocument setCharacterSet(String charSet)
    {
        rtf.addElement("charSet",charSet);
        return this;
    }

    public RTFDocument setCodeSet(String codePage)
    {
        rtf.addElement("\\ansicpg"+codePage);
        return this;
    }

    public org.apache.ecs.ConcreteElement getElement(String element)
    {
        return rtf.getElement(element);
    }

    public RTFDocument addElement(String element)
    {
        rtf.addElement(element);
        return this;
    }

    public RTFDocument addElement(String key,String element)
    {
        rtf.addElement(key,element);
        return this;
    }

    public RTFDocument addElement(RTFElement element)
    {
        rtf.addElement(element);
        return this;
    }

    public RTFDocument addElement(String key, RTFElement element)
    {
        rtf.addElement(key,element);
        return this;
    }

    public void output(java.io.OutputStream out)
    {
        rtf.output(out);
    }

    public void output(java.io.PrintWriter out)
    {
        rtf.output(out);
    }
}
