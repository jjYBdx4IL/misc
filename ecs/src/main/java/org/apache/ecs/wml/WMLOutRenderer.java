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
package org.apache.ecs.wml;

/**
    The class WMLOutRenderer contains static methods that takes
    unstructured strings of WML code and returns structured
    strings of WML code.
 
    @author Written by <a href="mailto:Anders.Samuelson@aspiro.com">Anders.Samuelson</a>
    @author Modifications by <a href="mailto:Orjan.Petersson@ehpt.com">Örjan Petersson</a>
 */
public class WMLOutRenderer
{
    
    /** The name of this class. */
    private static final String className = "WMLOutRenderer";

    private static final String[] indentArray = {"", "  ", "    ", "      ", "        ", "          "};

    private static final char EXCLAMATION_MARK = '!';
    private static final char LESS_THEN = '<';
    private static final char GREATER_THEN = '>';
    private static final char SLASH = '/';
    private static final char QUESTION_MARK = '?';
    private static final int MAX_INDENT_LEVEL = indentArray.length - 1;
    
    /**
	Adds structure to a 'raw' WML string.
	@param inString unstructured WML string.
	@return structured WML string
      */
    public static String parse(String inString)
    {
	boolean indentChanged = false;
	boolean proceed = true;
	char[] charArray = inString.toCharArray();
	int indentLevel = 0;
	int lastLTIndex = 0;
	int lastGTIndex = 0;
	StringBuffer out = new StringBuffer();
	
	while (proceed) {
	    indentChanged = false;
	    lastLTIndex = inString.indexOf(LESS_THEN, lastGTIndex);
	    
	    if (lastLTIndex != -1) {
		if ((lastLTIndex - lastGTIndex) > 1) {
		    out.append(indentArray[indentLevel]).append(
			inString.substring(lastGTIndex + 1, lastLTIndex)).append("\n");
		}
		    
		lastGTIndex = inString.indexOf(GREATER_THEN, lastLTIndex);
		
		if (charArray[lastLTIndex + 1] == SLASH) {
		    indentChanged = true;
		    indentLevel--;
		}
		    
		out.append(indentArray[indentLevel]).append(
		    inString.substring(lastLTIndex, lastGTIndex + 1)).append("\n");
		    
		if (charArray[lastGTIndex - 1] != SLASH && 
		    charArray[lastLTIndex + 1] != EXCLAMATION_MARK &&
		    charArray[lastLTIndex + 1] != QUESTION_MARK && !indentChanged) {
			indentLevel = (indentLevel < MAX_INDENT_LEVEL) ? 
						    indentLevel + 1 : MAX_INDENT_LEVEL;
		}
	    } else {
		proceed = false;
	    }
	}
	
	return out.toString();
    }
}
