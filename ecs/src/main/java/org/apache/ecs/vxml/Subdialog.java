/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights 
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
package org.apache.ecs.vxml;

/**
    This class implements the subdialog element

    @author Written by <a href="mailto:jcarol@us.ibm.com">Carol Jones</a>
*/
public class Subdialog extends VXMLElement
{

    /**
	Basic constructor. You need to set the attributes using the
	set* methods.
    */
    public Subdialog()
    {
	super("subdialog");
    }

   
    /**
	This constructor creates a &lt;subdialog&gt; tag.
	@param name the name="" attribute
	@param expr the expr="" attribute
	@param cond the cond="" attribute
	@param modal the modal="" attribute
	@param namelist the namelist="" attribute
	@param src the src="" attribute
	@param method the method="" attribute
	@param enctype the enctype="" attribute
	@param caching the caching="" attribute
	@param fetchint the fetchint="" attribute
    @param fetchtimeout the fetchtimeout="" attribute
	@param fetchaudio the fetchaudio="" attribute
    */
    public Subdialog(String name, String expr, String cond, String modal,
	                 String namelist, String src, String method, String enctype,
					 String caching, String fetchint, String fetchtimeout, String fetchaudio)
    {
	this();
	setName(name);
	setExpr(expr);
	setCond(cond);
	setModal(modal);
	setNamelist(namelist);
	setSrc(src);
	setMethod(method);
	setEnctype(enctype);
	setCaching(caching);
	setFetchint(fetchint);
	setFetchtimeout(fetchtimeout);
	setFetchaudio(fetchaudio);
    }

    /**
	This constructor creates a &lt;subdialog&gt; tag.
	@param name the name="" attribute
	@param namelist the namelist="" attribute
	@param src the src="" attribute
	@param method the method="" attribute
    */
    public Subdialog(String name, String namelist, String src, String method)
    {
	this();
	setName(name);
	setNamelist(namelist);
	setSrc(src);
	setMethod(method);
    }


    /**
	Sets the name="" attribute
	@param name the name="" attribute
    */
    public Subdialog setName(String name)
    {
	addAttribute("name", name);
	return this;
    }


    /**
	Sets the expr="" attribute
	@param expr the expr="" attribute
    */
    public Subdialog setExpr(String expr)
    {
	addAttribute("expr", expr);
	return this;
    }

    /**
	Sets the cond="" attribute
	@param cond the cond="" attribute
    */
    public Subdialog setCond(String cond)
    {
	addAttribute("cond", cond);
	return this;
    }

    /**
	Sets the modal="" attribute
	@param modal the modal="" attribute
    */
    public Subdialog setModal(String modal)
    {
	addAttribute("modal", modal);
	return this;
    }

    /**
	Sets the namelist="" attribute
	@param namelist the namelist="" attribute
    */
    public Subdialog setNamelist(String namelist)
    {
	addAttribute("namelist", namelist);
	return this;
    }


    /**
	Sets the src="" attribute
	@param src the src="" method
    */
    public Subdialog setSrc(String src)
    {
	addAttribute("src", src);
	return this;
    }

    /**
	Sets the method="" attribute
	@param method the method="" method
    */
    public Subdialog setMethod(String method)
    {
	addAttribute("method", method);
	return this;
    }

    /**
	Sets the enctype="" attribute
	@param enctype the enctype="" method
    */
    public Subdialog setEnctype(String enctype)
    {
	addAttribute("enctype", enctype);
	return this;
    }


    /**
	Sets the caching="" attribute
	@param caching the caching="" attribute
    */
    public Subdialog setCaching(String caching)
    {
	addAttribute("caching", caching);
	return this;
    }

    /**
	Sets the fetchaudio="" attribute
	@param fetchaudio the fetchaudio="" attribute
    */
    public Subdialog setFetchaudio(String fetchaudio)
    {
	addAttribute("fetchaudio", fetchaudio);
	return this;
    }


    /**
	Sets the fetchint="" attribute
	@param fetchint the fetchint="" attribute
    */
    public Subdialog setFetchint(String fetchint)
    {
	addAttribute("fetchint", fetchint);
	return this;
    }

    /**
	Sets the fetchtimeout="" attribute
	@param fetchtimeout the fetchtimeout="" attribute
    */
    public Subdialog setFetchtimeout(String fetchtimeout)
    {
	addAttribute("fetchtimeout", fetchtimeout);
	return this;
    }    
    
    
} 
