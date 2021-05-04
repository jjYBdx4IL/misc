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

import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.Hashtable;
import java.util.Enumeration;

import org.apache.ecs.Element;
import org.apache.ecs.ConcreteElement;

public class Alignment extends RTFElement
{
    private RTFElement type = new Justified();
    private static Hashtable _lookup = new Hashtable();
    private static final int CENTERED = 1;
    private static final int JUSTIFIED = 2;
    private static final int RIGHT = 3;
    private static final int LEFT = 4;

    public Alignment()
    {
    }

    /*
        Registers an element in the head element list
        @param   element element to be added to the registry.
    */
    public Element addElementToRegistry(Element element)
    {
        type.addElementToRegistry(element);
        return type;
    }

    /**
        Registers an element in the head element list
        @param   hashcode internal name of element
        @param   element element to be added to the registry.
    */
    public Element addElementToRegistry(String hashcode,Element element)
    {
        type.addElementToRegistry(hashcode,element);
        return type;
    }

    /**
        Registers an element in the head element list
        @hashcode named element for hashcode
        @param   element element to be added to the registry.
        @param   filter does this need to be filtered?
    */
    public Element addElementToRegistry(Element element,boolean filter)
    {
        type.addElementToRegistry(element,filter);
        return type;
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
        @param   filter  should we filter this element?
    */
    public Element addElementToRegistry(String hashcode, Element element,boolean filter)
    {
        type.addElementToRegistry(hashcode,element,filter);
        return type;
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
        @param   filter does this need to be filtered?
    */
    public Element addElementToRegistry(String value,boolean filter)
    {
        type.addElementToRegistry(value,filter);
        return type;
    }

    /**
        Registers an element in the head element list
        @hashcode named element for hashcode
        @param   element element to be added to the registry.
        @param   filter does this need to be filtered?
    */
    public Element addElementToRegistry(String hashcode, String value,boolean filter)
    {
        type.addElementToRegistry(hashcode,value,filter);
        return type;
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
    */
    public Element addElementToRegistry(String value)
    {
        type.addElementToRegistry(value);
        return type;
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
    */
    public Element addElementToRegistry(String hashcode,String value)
    {
        type.addElementToRegistry(hashcode,value);
        return type;
    }

    /**
        Removes an element from the element registry
        @param   element element to be added to the registry.
    */
    public Element removeElementFromRegistry(Element element)
    {
        type.removeElementFromRegistry(Integer.toString(element.hashCode()));
        return(type);
    }

    /**
        Removes an element from the head element registry
        @param   hashcode element to be added to the registry.
    */
    public Element removeElementFromRegistry(String hashcode)
    {
        type.removeElementFromRegistry(hashcode);
        return(type);
    }

    /**
        Find out if this element is in the element registry.
        @param element find out if this element is in the registry
    */
    public boolean registryHasElement(Element element)
    {
        return(type.registryHasElement(element));
    }

    /**
        Get an enumeration of the elements that this element contains.
    */
    public Enumeration elements()
    {
        return(type.elements());
    }

    /**
        Find out if this element is in the element registry.
        @param element find out if this element is in the registry
    */
    public boolean registryHasElement(String hashcode)
    {
        return(type.registryHasElement(hashcode));
    }

    public Alignment setType(String type)
    {
        if(type.equals("centered"))
        {
            this.type = new Centered();
        }
        else if(type.equals("justified"))
        {
            this.type = new Justified();
        }
        else if(type.equals("right"))
        {
            this.type = new Right();
        }
        else if(type.equals("left"))
        {
            this.type = new Left();
        }
        return this;
    }

    public void output(OutputStream out)
    {
        type.output(out);
    }

    public void output(Writer out)
    {
        type.output(out);
    }

    public void output(PrintWriter out)
    {
        type.output(out);
    }

    // Static initializer
    {
        _lookup.put("centered",Integer.toString(CENTERED));
        _lookup.put("justified",Integer.toString(JUSTIFIED));
        _lookup.put("right",Integer.toString(RIGHT));
        _lookup.put("left",Integer.toString(LEFT));
    }
}
