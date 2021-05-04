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

/**
    Stupid implementation of Filter interface to demonstrate how easy  <br>
    it is to create your own filters. <b>This should NOT be used</b> in/for<br>
    anything real. Anyone want to implement a regex filter?

    @version $Id: StringFilter.java,v 1.5 2003/04/27 09:28:56 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class StringFilter extends java.util.Hashtable implements Filter
{

    public StringFilter()
    {
		super(4);
    }

    /** Returns the name of the filter */
    public String getInfo()
    {
        return "StringFilter";
    }

    /**
        this method actually performs the filtering.
    */
    public String process(String to_process)
    {   System.out.println("\nString to Process in StringFilter = "+to_process);
        String[] value = split(to_process);
        StringBuffer new_value = new StringBuffer();
        for(int x = 0; x < value.length; x++)
        {
            if(hasAttribute(value[x]))
                new_value.append((String)get(value[x]));
            else
                new_value.append(value[x]);
            if(x != value.length - 1)
                new_value.append(" ");
        }
        return(new_value.toString());
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

    /**
        Need a way to parse the stream so we can do string comparisons instead
        of character comparisons.
    */
    private String[] split(String to_split)
    {

        if ( to_split == null || to_split.length() == 0 )
        {
            String[] array = new String[0];
            return array;
        }

        StringBuffer sb = new StringBuffer(to_split.length()+50);
        StringCharacterIterator sci = new StringCharacterIterator(to_split);
        int length = 0;

        for (char c = sci.first(); c != CharacterIterator.DONE; c = sci.next())
        {
            if(String.valueOf(c).equals(" "))
                length++;
            else if(sci.getEndIndex()-1 == sci.getIndex())
                length++;
        }

        String[] array = new String[length];
        length = 0;
        String tmp = new String();
        for (char c = sci.first(); c!= CharacterIterator.DONE; c = sci.next())
        {
            if(String.valueOf(c).equals(" "))
            {
                array[length] = tmp;
                tmp = new String();
                length++;
            }
            else if(sci.getEndIndex()-1 == sci.getIndex())
            {
                tmp = tmp+String.valueOf(sci.last());
                array[length] = tmp;
                tmp = new String();
                length++;
            }
            else
                tmp += String.valueOf(c);
        }
        return(array);
    }
}
