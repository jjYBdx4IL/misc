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
    This class implements the record element

    @author Written by <a href="mailto:jcarol@us.ibm.com">Carol Jones</a>
*/
public class Record extends VXMLElement
{

    /**
	Basic constructor. You need to set the attributes using the
	set* methods.
    */
    public Record()
    {
	super("record");
    }

    
    /**
	This constructor creates a &lt;record&gt; tag.
	@param name the name="" attribute
	@param expr the expr="" attribute
    */
    public Record(String name, String expr)
    {
	this();
	setName(name);
	setExpr(expr);
    }
    
    /**
	This constructor creates a &lt;record&gt; tag.
	@param name the name="" attribute
    */
    public Record(String name)
    {
	this();
	setName(name);
    }
    
    /**
	Sets the name="" attribute
	@param name the name="" attribute
    */
    public Record setName(String name)
    {
	addAttribute("name", name);
	return this;
    }

    /**
	Sets the expr="" attribute
	@param expr the expr="" attribute
    */
    public Record setExpr(String expr)
    {
	addAttribute("expr", expr);
	return this;
    }

	/**
	Sets the cond="" attribute
	@param cond the cond="" attribute
	*/
	public Record setCond(String cond)
	{
	addAttribute("cond", cond);
	return this;
	}

	/**
	Sets the modal="" attribute
	@param modal the modal="" attribute
	*/
	public Record setModal(String modal)
	{
	addAttribute("modal", modal);
	return this;
	}

	/**
	Sets the beep="" attribute
	@param beep the beep="" attribute
	*/
	public Record setBeep(String beep)
	{
	addAttribute("beep", beep);
	return this;
	}

	/**
	Sets the maxtime="" attribute
	@param maxtime the maxtime="" attribute
	*/
	public Record setMaxtime(String maxtime)
	{
	addAttribute("maxtime", maxtime);
	return this;
	}

	/**
	Sets the finalsilence="" attribute
	@param finalsilence the finalsilence="" attribute
	*/
	public Record setFinalsilence(String finalsilence)
	{
	addAttribute("finalsilence", finalsilence);
	return this;
	}

	/**
	Sets the dtmfterm="" attribute
	@param dtmfterm the dtmfterm="" attribute
	*/
	public Record setDtmfterm(String dtmfterm)
	{
	addAttribute("dtmfterm", dtmfterm);
	return this;
	}

	/**
	Sets the type="" attribute
	@param type the type="" attribute
	*/
	public Record setType(String t)
	{
	addAttribute("type", t);
	return this;
	}

   
} 
