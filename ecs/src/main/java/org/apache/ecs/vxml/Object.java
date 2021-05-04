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
    This class implements the object element

    @author Written by <a href="mailto:jcarol@us.ibm.com">Carol Jones</a>
*/
public class Object extends VXMLElement
{

    /**
	Basic constructor. You need to set the attributes using the
	set* methods.
    */
    public Object()
    {
	super("object");
    }

    /**
	This constructor creates a &lt;object&gt; tag.
	@param name the name="" attribute
	@param expr the expr="" attribute
	@param cond the cond="" attribute
	@param classid the classid="" attribute
	@param codebase the codebase="" attribute
	@param codetype the codetype="" attribute
	@param data the data="" attribute
	@param t the type="" attribute
	@param archive the archive="" attribute
	@param caching the caching="" attribute
	@param fetchaudio the fetchaudio="" attribute
	@param fetchint the fetchint="" attribute
	@param fetchtimeout the fetchtimeout="" attribute
    */
    public Object(String name, String expr, String cond, String classid, String codebase,
	              String codetype, String data, String t, String archive, String caching,
				  String fetchaudio, String fetchint, String fetchtimeout)
    {
	this();
	setName(name);
	setExpr(expr);
	setCond(cond);
	setClassid(classid);
	setCodebase(codebase);
	setCodetype(codetype);
	setData(data);
	setType(t);
	setArchive(archive);
	setCaching(caching);
	setFetchaudio(fetchaudio);
	setFetchint(fetchint);
	setFetchtimeout(fetchtimeout);
    }
    
    /**
	This constructor creates a &lt;object&gt; tag.
	@param name the name="" attribute
	@param classid the classid="" attribute
	@param data the data="" attribute
    */
    public Object(String name, String classid, String data)
    {
	this();
	setName(name);
	setClassid(classid);
	setData(data);
    }

    /**
	This constructor creates a &lt;object&gt; tag.
	@param name the name="" attribute
	@param classid the classid="" attribute
    */
    public Object(String name, String classid)
    {
	this();
	setName(name);
	setClassid(classid);
    }
    
    /**
	Sets the name="" attribute
	@param name the name="" attribute
    */
    public org.apache.ecs.vxml.Object setName(String name)
    {
	addAttribute("name", name);
	return this;
    }

    /**
	Sets the expr="" attribute
	@param expr the expr="" attribute
    */
    public org.apache.ecs.vxml.Object setExpr(String expr)
    {
	addAttribute("expr", expr);
	return this;
    }

    /**
	Sets the cond="" attribute
	@param cond the cond="" attribute
    */
    public org.apache.ecs.vxml.Object setCond(String cond)
    {
	addAttribute("cond", cond);
	return this;
    }

    /**
	Sets the classid="" attribute
	@param classid the classid="" attribute
    */
    public org.apache.ecs.vxml.Object setClassid(String classid)
    {
	addAttribute("classid", classid);
	return this;
    }

    /**
	Sets the codebase="" attribute
	@param codebase the codebase="" attribute
    */
    public org.apache.ecs.vxml.Object setCodebase(String codebase)
    {
	addAttribute("codebase", codebase);
	return this;
    }

    /**
	Sets the codetype="" attribute
	@param codetype the codetype="" attribute
    */
    public org.apache.ecs.vxml.Object setCodetype(String codetype)
    {
	addAttribute("codetype", codetype);
	return this;
    }
    
    /**
	Sets the data="" attribute
	@param data the data="" attribute
    */
    public org.apache.ecs.vxml.Object setData(String data)
    {
	addAttribute("data", data);
	return this;
    }

    /**
	Sets the type="" attribute
	@param type the type="" attribute
    */
    public org.apache.ecs.vxml.Object setType(String type)
    {
	addAttribute("type", type);
	return this;
    }

    /**
	Sets the archive="" attribute
	@param archive the archive="" attribute
    */
    public org.apache.ecs.vxml.Object setArchive(String archive)
    {
	addAttribute("archive", archive);
	return this;
    }

    /**
	Sets the caching="" attribute
	@param caching the caching="" attribute
    */
    public org.apache.ecs.vxml.Object setCaching(String caching)
    {
	addAttribute("caching", caching);
	return this;
    }

    /**
	Sets the fetchaudio="" attribute
	@param fetchaudio the fetchaudio="" attribute
    */
    public org.apache.ecs.vxml.Object setFetchaudio(String fetchaudio)
    {
	addAttribute("fetchaudio", fetchaudio);
	return this;
    }

    /**
	Sets the fetchint="" attribute
	@param fetchint the fetchint="" attribute
    */
    public org.apache.ecs.vxml.Object setFetchint(String fetchint)
    {
	addAttribute("fetchint", fetchint);
	return this;
    }

    /**
	Sets the fetchtimeout="" attribute
	@param fetchtimeout the fetchtimeout="" attribute
    */
    public org.apache.ecs.vxml.Object setFetchtimeout(String fetchtimeout)
    {
	addAttribute("fetchtimeout", fetchtimeout);
	return this;
    }

} 
