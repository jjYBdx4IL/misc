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
package org.apache.ecs.filter;

import org.apache.ecs.Filter;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import java.util.StringTokenizer;

/**
    This filter uses StringTokenizer to create "words" and allow
    you to replace on those "words". A word is defined as anything
    between two spaces. This filter should be relatively fast and
    shows how easy it is to implement your own filters.

<pre><code>
    Filter filter = new WordFilter();
    filter.addAttribute("there","where");
    filter.addAttribute("it","is");
    filter.addAttribute("goes","it");
    P p = new P();
    p.setFilter(filter);
    p.addElement("there it goes");
    System.out.println(p.toString());
</code></pre>

    Produces: &lt;p&gt;where is it

    @version $Id: WordFilter.java,v 1.5 2003/04/27 09:28:56 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class WordFilter extends java.util.Hashtable implements Filter
{
    public WordFilter()
    {
		super(4);
    }

    /** Returns the name of the filter */
    public String getInfo()
    {
        return "WordFilter";
    }

    /**
        this method actually performs the filtering.
    */
    public String process(String to_process)
    {
        if ( to_process == null || to_process.length() == 0 )
            return "";

        String tmp = "";
        // the true at the end is the key to making it work
        StringTokenizer st = new StringTokenizer(to_process, " ", true);
        StringBuffer newValue = new StringBuffer(to_process.length() + 50);
        while ( st.hasMoreTokens() )
        {
            tmp = st.nextToken();
            if (hasAttribute(tmp))
                newValue.append((String)get(tmp));
            else
                newValue.append(tmp);
        }
        return newValue.toString();
    }

    /**
        Put a filter somewhere we can get to it.
    */
    public Filter addAttribute(String attribute,Object entity)
    {
        put(attribute,entity);
        return(this);
    }

    /**
        Get rid of a current filter.
    */
    public Filter removeAttribute(String attribute)
    {
        try
        {
            remove(attribute);
        }
        catch(NullPointerException exc)
        { // don't really care if this throws a null pointer exception
        }
        return(this);
    }

    /**
        Does the filter filter this?
    */
    public boolean hasAttribute(String attribute)
    {
        return(containsKey(attribute));
    }
}
