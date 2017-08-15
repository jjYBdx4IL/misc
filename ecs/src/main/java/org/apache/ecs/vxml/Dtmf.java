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
    This class implements the dtmf element

    @author Written by <a href="mailto:jcarol@us.ibm.com">Carol Jones</a>
*/
public class Dtmf extends VXMLElement
{

    /**
	Basic constructor. You need to set the attributes using the
	set* methods.
    */
    public Dtmf()
    {
	super("dtmf");
    }

    
    /**
	This constructor creates a &lt;dtmf&gt; tag.
	@param src the src="" attribute
	@param scope the scope="" attribute
	@param t the type="" attribute
	@param caching the caching="" attribute
	@param fetchint the fetchint="" attribute
	@param fetchtimeout the fetchtimeout="" attribute
    */
    public Dtmf(String src, String scope, String t,  
	              String caching, String fetchint, String fetchtimeout)
    {
	this();
	setSrc(src);
	setScope(scope);
	setType(t);
	setCaching(caching);
	setFetchint(fetchint);
	setFetchtimeout(fetchtimeout);
    }

    /**
	This constructor creates a &lt;dtmf&gt; tag.
	@param t the type="" attribute
    */
    public Dtmf(String t)
    {
	this();
	setType(t);
    }

    /**
	This constructor creates a &lt;dtmf&gt; tag.
	@param src the src="" attribute
	@param t the type="" attribute
    */
    public Dtmf(String src, String t)
    {
	this();
	setSrc(src);
	setType(t);
    }
        
    /**
	Sets the src="" attribute
	@param src the src="" attribute
    */
    public Dtmf setSrc(String src)
    {
	addAttribute("src", src);
	return this;
    }

    /**
	Sets the scope="" attribute
	@param scope the scope="" attribute
    */
    public Dtmf setScope(String scope)
    {
	addAttribute("scope", scope);
	return this;
    }


    /**
	Sets the type="" attribute
	@param t the type="" attribute
    */
    public Dtmf setType(String t)
    {
	addAttribute("type", t);
	return this;
    }

    /**
	Sets the caching="" attribute
	@param caching the caching="" attribute
    */
    public Dtmf setCaching(String caching)
    {
	addAttribute("caching", caching);
	return this;
    }

    /**
	Sets the fetchint="" attribute
	@param fetchint the fetchint="" attribute
    */
    public Dtmf setFetchint(String fetchint)
    {
	addAttribute("fetchint", fetchint);
	return this;
    }

    /**
	Sets the fetchtimeout="" attribute
	@param fetchtimeout the fetchtimeout="" attribute
    */
    public Dtmf setFetchtimeout(String fetchtimeout)
    {
	addAttribute("fetchtimeout", fetchtimeout);
	return this;
    }

    
} 
