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
    This class implements the property element

    @author Written by <a href="mailto:jcarol@us.ibm.com">Carol Jones</a>
*/
public class Property extends VXMLElement
{

    /**
	Basic constructor. You need to set the attributes using the
	set* methods.
    */
    public Property()
    {
	super("property");
    }

     
	/**
	Sets the confidencelevel="" attribute
	@param confidencelevel the confidencelevel="" attribute
	*/
	public Property setConfidencelevel(String confidencelevel)
	{
	addAttribute("name", "confidencelevel");
	addAttribute("value", confidencelevel);
	return this;
	}

	
    /**
    Sets the sensitivity="" attribute
    @param sensitivity the sensitivity="" attribute
    */
    public Property setSensitivity(String sensitivity)
    {
	addAttribute("name", "sensitivity");
	addAttribute("value", sensitivity);
    return this;
    }

    /**
    Sets the speedvsaccuracy="" attribute
    @param speedvsaccuracy the speedvsaccuracy="" attribute
    */
    public Property setSpeedvsaccuracy(String speedvsaccuracy)
    {
	addAttribute("name", "speedvsaccuracy");
	addAttribute("value", speedvsaccuracy);
    return this;
    }
    
    /**
    Sets the completetimeout="" attribute
    @param completetimeout the completetimeout="" attribute
    */
    public Property setCompletetimeout(String completetimeout)
    {
	addAttribute("name", "completetimeout");
	addAttribute("value", completetimeout);
	return this;
	}

	/**
	Sets the incompletetimeout="" attribute
	@param incompletetimeout the incompletetimeout="" attribute
	*/
	public Property setIncompletetimeout(String incompletetimeout)
	{
	addAttribute("name", "incompletetimeout");
	addAttribute("value", incompletetimeout);
	return this;
	}

	/**
	Sets the interdigittimeout="" attribute
	@param interdigittimeout the interdigittimeout="" attribute
	*/
	public Property setInterdigittimeout(String interdigittimeout)
	{
	addAttribute("name", "interdigittimeout");
	addAttribute("value", interdigittimeout);
	return this;
	}

	/**
	Sets the termtimeout="" attribute
	@param termtimeout the termtimeout="" attribute
	*/
	public Property setTermtimeout(String termtimeout)
	{
	addAttribute("name", "termtimeout");
	addAttribute("value", termtimeout);
	return this;
	}

	/**
	Sets the termchar="" attribute
	@param termchar the termchar="" attribute
	*/
	public Property setTermchar(String termchar)
	{
	addAttribute("name", "termchar");
	addAttribute("value", termchar);
	return this;
	}

	/**
	Sets the bargein="" attribute
	@param bargein the bargein="" attribute
	*/
	public Property setBargein(String bargein)
	{
	addAttribute("name", "bargein");
	addAttribute("value", bargein);
	return this;
	}

	/**
	Sets the timeout="" attribute
	@param timeout the timeout="" attribute
	*/
	public Property setTimeout(String timeout)
	{
	addAttribute("name", "timeout");
	addAttribute("value", timeout);
	return this;
	}

	/**
	Sets the caching="" attribute
	@param caching the caching="" attribute
	*/
	public Property setCaching(String caching)
	{
	addAttribute("name", "caching");
	addAttribute("value", caching);
	return this;
	}

	/**
	Sets the audiofetchhint="" attribute
	@param audiofetchhint the audiofetchhint="" attribute
	*/
	public Property setAudiofetchhint(String audiofetchhint)
	{
	addAttribute("name", "audiofetchhint");
	addAttribute("value", audiofetchhint);
	return this;
	}

	/**
	Sets the documentfetchhint="" attribute
	@param documentfetchhint the documentfetchhint="" attribute
	*/
	public Property setDocumentfetchhint(String documentfetchhint)
	{
	addAttribute("name", "documentfetchhint");
	addAttribute("value", documentfetchhint);
	return this;
	}

	/**
	Sets the grammarfetchint="" attribute
	@param grammarfetchint the grammarfetchint="" attribute
	*/
	public Property setGrammarfetchint(String grammarfetchint)
	{
	addAttribute("name", "grammarfetchint");
	addAttribute("value", grammarfetchint);
	return this;
	}

	/**
	Sets the objectfetchint="" attribute
	@param objectfetchint the objectfetchint="" attribute
	*/
	public Property setObjectfetchint(String objectfetchint)
	{
	addAttribute("name", "objectfetchint");
	addAttribute("value", objectfetchint);
	return this;
	}

	/**
	Sets the scriptfetchhint="" attribute
	@param scriptfetchhint the scriptfetchhint="" attribute
	*/
	public Property setScriptfetchhint(String scriptfetchhint)
	{
	addAttribute("name", "scriptfetchhint");
	addAttribute("value", scriptfetchhint);
	return this;
	}

	/**
	Sets the fetchaudio="" attribute
	@param fetchaudio the fetchaudio="" attribute
	*/
	public Property setFetchaudio(String fetchaudio)
	{
	addAttribute("name", "fetchaudio");
	addAttribute("value", fetchaudio);
	return this;
	}

	/**
	Sets the fetchtimeout="" attribute
	@param fetchtimeout the fetchtimeout="" attribute
	*/
	public Property setFetchtimeout(String fetchtimeout)
	{
	addAttribute("name", "fetchtimeout");
	addAttribute("value", fetchtimeout);
	return this;
	}

	/**
	Sets the inputmodes="" attribute
	@param inputmodes the inputmodes="" attribute
	*/
	public Property setInputmodes(String inputmodes)
	{
	addAttribute("name", "inputmodes");
	addAttribute("value", inputmodes);
	return this;
	}
        
    
} 
