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

import java.util.ResourceBundle;

/**
    This class is responsible for loading the ecs.properties file and 
    getting the default settings for ECS. This allows you to edit a 
    simple text file instead of having to edit the .java files and 
    recompile.
    The property file can be specified via the 'ecs.properties' system property. 
    For example, java -Decs.properties="my.ecs.properties". If ecs.properties is null
    then the standard ecs.properties resource in the ECS jar is used. 
    If the property file cannot be loaded, a message is printed to standard 
    error and hard-coded defaults are used instead.

    @version $Id: ECSDefaults.java,v 1.5 2003/04/27 09:43:24 rdonkin Exp $
*/
public final class ECSDefaults
{
    /**
    This singleton allows the properties to gracefully default in the 
    case of an error.
    */
    private static ECSDefaults defaults = new ECSDefaults();

    //  now follows the private methods called by the 
    private ResourceBundle resource;
    
    // assign original default values in case the props can't be loaded.
    private boolean filter_state =false;
    private boolean filter_attribute_state = false;
    private char attribute_equality_sign = '=';
    private char begin_start_modifier = ' ';
    private char end_start_modifier = ' ';
    private char begin_end_modifier = ' ';
    private char end_end_modifier = ' ';
    private char attribute_quote_char = '\"';
    private boolean attribute_quote = true;
    private boolean end_element = true;
    private String codeset = "UTF-8";
    private int position = 4;
    private int case_type = 3;
    private char start_tag = '<';
    private char end_tag = '>';
    private boolean pretty_print = false;

    /**
        Should we filter the value of &lt;&gt;VALUE&lt;/&gt;
    */
    public static boolean getDefaultFilterState()
    {
        return defaults.filter_state;
    }

    /**
        Should we filter the value of the element attributes
    */
    public static boolean getDefaultFilterAttributeState()
    {
        return defaults.filter_attribute_state;
    }

    /**
        What is the equality character for an attribute.
    */
    public static char getDefaultAttributeEqualitySign()
    {
        return defaults.attribute_equality_sign;
    }

    /**
        What the start modifier should be
    */
    public static char getDefaultBeginStartModifier()
    {
        return defaults.begin_start_modifier;
    }

    /**
        What the start modifier should be
    */
    public static char getDefaultEndStartModifier()
    {
        return defaults.end_start_modifier;
    }

    /**
        What the end modifier should be
    */
    public static char getDefaultBeginEndModifier()
    {
        return defaults.begin_end_modifier;
    }

    /**
        What the end modifier should be
    */
    public static char getDefaultEndEndModifier()
    {
        return defaults.end_end_modifier;
    }

    /*
        What character should we use for quoting attributes.
    */
    public static char getDefaultAttributeQuoteChar()
    {
        return defaults.attribute_quote_char;
    }

    /*
        Should we wrap quotes around an attribute?
    */
    public static boolean getDefaultAttributeQuote()
    {
        return defaults.attribute_quote;
    }

    /**
        Does this element need a closing tag?
    */
    public static boolean getDefaultEndElement()
    {
        return defaults.end_element;
    }

    /**
        What codeset are we going to use the default is UTF-8.
    */
    public static String getDefaultCodeset()
    {
        return defaults.codeset;
    }

    /**
        Position of tag relative to start and end.
    */
    public static int getDefaultPosition()
    {
        return defaults.position;
    }
    
    /**
        Default value to set case type
    */
    public static int getDefaultCaseType()
    {
        return defaults.case_type;
    }

    /**
        Default start-of-tag character.  
    */
    public static char getDefaultStartTag()
    {
        return defaults.start_tag;
    }

    /**
        Default end-of-tag character. 
    */
    public static char getDefaultEndTag()
    {
        return defaults.end_tag;
    }

    /**
        Should we print html in a more readable format?
    */
    public static boolean getDefaultPrettyPrint()
    {
        return defaults.pretty_print;
    }

    /**
        This private constructor is used to create the singleton used in the public static methods. 
    */
    private ECSDefaults ()
    {
        try
        {   
            // if the ecs.properties system property is set, use that
            String props=System.getProperty("ecs.properties");
            if (props==null)
            {
                resource = ResourceBundle.getBundle("org.apache.ecs.ecs");
            } else {
                resource = new java.util.PropertyResourceBundle(new java.io.FileInputStream(props));
            }

            // set up variables
            filter_state = new Boolean(resource.getString("filter_state")).booleanValue();
            filter_attribute_state = new Boolean(resource.getString("filter_attribute_state")).booleanValue();
            attribute_equality_sign = resource.getString("attribute_equality_sign").charAt(1);
            begin_start_modifier = resource.getString("begin_start_modifier").charAt(1);
            end_start_modifier = resource.getString("end_start_modifier").charAt(1);
            begin_end_modifier = resource.getString("begin_end_modifier").charAt(1);
            end_end_modifier = resource.getString("end_end_modifier").charAt(1);
            attribute_quote_char = resource.getString("attribute_quote_char").charAt(0);
            attribute_quote = new Boolean(resource.getString("attribute_quote")).booleanValue();
            end_element = new Boolean(resource.getString("end_element")).booleanValue();
            codeset = resource.getString("codeset");
            position = Integer.parseInt(resource.getString("position"));
            case_type = Integer.parseInt(resource.getString("case_type"));
            start_tag = resource.getString("start_tag").charAt(0);
            end_tag = resource.getString("end_tag").charAt(0);
            pretty_print = new Boolean(resource.getString("pretty_print")).booleanValue();

        }
        catch(Exception e)
        {
            System.err.println("The following error preventing " + 
                "ecs.properties being loaded:");
            System.err.println(e.toString());
        }
    }
    
    /**
        This method returns a string showing the current values.
    */
    public static String debugString()
    {
        return
            "ECSDefaults:" + '\n'
            + '\t' + "DefaultFilterState=" + getDefaultFilterState() +'\n'
            + '\t' + "DefaultFilterAttributeState=" + getDefaultFilterAttributeState() +'\n'
            + '\t' + "DefaultAttributeEqualitySign='" + getDefaultAttributeEqualitySign() +"'\n"
            + '\t' + "DefaultBeginStartModifier='" + getDefaultBeginStartModifier() + "'\n"
            + '\t' + "DefaultEndStartModifier='" + getDefaultEndStartModifier() + "'\n"
            + '\t' + "DefaultBeginEndModifier='" + getDefaultBeginEndModifier() + "'\n"
            + '\t' + "DefaultEndEndModifier='" + getDefaultEndEndModifier() + "'\n"
            + '\t' + "DefaultAttributeQuoteChar=" + getDefaultAttributeQuoteChar() + '\n'
            + '\t' + "DefaultAttributeQuote=" + getDefaultAttributeQuote() +'\n'
            + '\t' + "DefaultEndElement=" + getDefaultEndElement() +'\n'
            + '\t' + "DefaultCodeset='" + getDefaultCodeset() + "'\n"
            + '\t' + "DefaultPosition=" + getDefaultPosition() +'\n'
            + '\t' + "DefaultCaseType=" + getDefaultCaseType() +'\n'
            + '\t' + "DefaultStartTag='" + getDefaultStartTag() + "'\n"
            + '\t' + "DefaultEndTag='" + getDefaultEndTag() + "'\n"
            + '\t' + "DefaultPrettyPrint=" + getDefaultPrettyPrint();
    }
}
