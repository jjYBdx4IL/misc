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
package org.apache.ecs;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

/**
    This class is to be subclassed by those elements that are made up of
    other elements. i.e. BODY,HEAD,etc.

    @version $Id: ConcreteElement.java,v 1.31 2003/05/13 12:23:18 rdonkin Exp $
    @author <a href="mailto:snagy@servletapi.com">Stephan Nagy</a>
    @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
*/
public class ConcreteElement extends ElementAttributes implements Cloneable
{
	/** The line separator to use for pretty printing */
	private static String lineSeparator = System.getProperty("line.separator");

    /** @serial registry registry */
    private Hashtable registry = new Hashtable(4); // keep a list of elements that need to be added to the element
    /** Maintain an ordered list of elements */
    private Vector registryList = new Vector(2);

    public ConcreteElement()
    {
    }

    /**
        If the object is in the registry return otherwise return null.
        @param element the name of the object to locate.
    */
    public ConcreteElement getElement(String element)
    {
        if(registry.containsKey(element))
        {
            return (ConcreteElement)registry.get(element);
        }
        return null;
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
    */
    public Element addElementToRegistry(Element element)
    {
        if ( element == null )
            return(this);
        addElementToRegistry(Integer.toString(element.hashCode()),element);
        return(this);
    }

    /**
        Registers an element in the head element list
        @param   hashcode internal name of element
        @param   element element to be added to the registry.
    */
    public Element addElementToRegistry(String hashcode,Element element)
    {
        if ( hashcode == null || element == null )
            return(this);

         element.setFilterState(getFilterState());
         if(ECSDefaults.getDefaultPrettyPrint() != element.getPrettyPrint())
              element.setPrettyPrint(getPrettyPrint());
         registry.put(hashcode,element);
         if(!registryList.contains(hashcode))
            registryList.addElement(hashcode);
         return(this);
    }

    /**
        Registers an element in the head element list
        @hashcode named element for hashcode
        @param   element element to be added to the registry.
        @param   filter does this need to be filtered?
    */
    public Element addElementToRegistry(Element element,boolean filter)
    {
        if ( element == null )
            return(this);
        setFilterState(filter);
        addElementToRegistry(Integer.toString(element.hashCode()),element);
        return(this);
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
        @param   filter  should we filter this element?
    */
    public Element addElementToRegistry(String hashcode, Element element,boolean filter)
    {
        if ( hashcode == null )
            return(this);
        setFilterState(filter);
        addElementToRegistry(hashcode,element);
        return(this);
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
        @param   filter does this need to be filtered?
    */
    public Element addElementToRegistry(String value,boolean filter)
    {
        if ( value == null )
            return(this);
        setFilterState(filter);
        addElementToRegistry(Integer.toString(value.hashCode()),value);
        return(this);
    }

    /**
        Registers an element in the head element list
        @hashcode named element for hashcode
        @param   element element to be added to the registry.
        @param   filter does this need to be filtered?
    */
    public Element addElementToRegistry(String hashcode, String value,boolean filter)
    {
        if ( hashcode == null )
            return(this);
        setFilterState(filter);
        addElementToRegistry(hashcode,value);
        return(this);
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
    */
    public Element addElementToRegistry(String value)
    {
        if ( value == null )
            return(this);
        addElementToRegistry(new StringElement(value));
        return(this);
    }

    /**
        Registers an element in the head element list
        @param   element element to be added to the registry.
    */
    public Element addElementToRegistry(String hashcode,String value)
    {
        if ( hashcode == null )
            return(this);

        // We do it this way so that filtering will work.
        // 1. create a new StringElement(element) - this is the only way that setTextTag will get called
        // 2. copy the filter state of this string element to this child.
        // 3. copy the prettyPrint state of the element to this child
        // 4. copy the filter for this string element to this child.

        StringElement se = new StringElement(value);
        se.setFilterState(getFilterState());
        se.setFilter(getFilter());
        se.setPrettyPrint(getPrettyPrint());
        addElementToRegistry(hashcode,se);
        return(this);
    }

    /**
        Removes an element from the element registry
        @param   element element to be added to the registry.
    */
    public Element removeElementFromRegistry(Element element)
    {
        removeElementFromRegistry(Integer.toString(element.hashCode()));
        return(this);
    }

    /**
        Removes an element from the head element registry
        @param   hashcode element to be added to the registry.
    */
    public Element removeElementFromRegistry(String hashcode)
    {
        registry.remove(hashcode);
        registryList.removeElement(hashcode);
        return(this);
    }

    /**
        Find out if this element is in the element registry.
        @param element find out if this element is in the registry
    */
    public boolean registryHasElement(Element element)
    {
        return(registry.contains(element));
    }

	/**
		Get the keys of this element.
	*/
	public Enumeration keys()
	{
		return(registryList.elements());
	}

    /**
        Get an enumeration of the elements that this element contains.
    */
    public Enumeration elements()
    {
        return(registry.elements());
    }

    /**
        Find out if this element is in the element registry.
        @param element find out if this element is in the registry
    */
    public boolean registryHasElement(String hashcode)
    {
        return(registry.containsKey(hashcode));
    }

    /**
        Overload output(OutputStream).
        @param output OutputStream to write to.
		@param ConcreteElement	Instance of ConcreteElement
    */
    public static void output(OutputStream out, ConcreteElement ce) 
    {
        // use the encoding for the given element
        String encoding = ce.getCodeSet();
        if ( encoding == null ) 
        {
           // By default use Big Endian Unicode.
           // In this way we will not loose any information.
           encoding = "UTF-16BE"; 
        }    
        
        boolean prettyPrint = ce.getPrettyPrint();
        int tabLevel = ce.getTabLevel();
        try
        {
            if (ce.registry.size() == 0)
            {	
                ce.output(out);
            }
            else
            {
                if ((prettyPrint && ce instanceof Printable) && (tabLevel > 0))
                    ce.putTabs(tabLevel, out);

                out.write(ce.createStartTag().getBytes(encoding));

                // If this is a StringElement that has ChildElements still print the TagText
                if(ce.getTagText() != null)
                    out.write(ce.getTagText().getBytes(encoding));

                Enumeration _enum = ce.registryList.elements();

                while(_enum.hasMoreElements())
                {
                    Object obj = ce.registry.get((String)_enum.nextElement());
                    if(obj instanceof GenericElement)
                    {
                        Element e = (Element)obj;
                        if (prettyPrint && ce instanceof Printable)
                        {
                            if ( ce.getNeedLineBreak() )
                            {
                                out.write(ce.lineSeparator.getBytes(encoding));
                                e.setTabLevel(tabLevel + 1);
                            }
                        }
                        e.output(out);
                    }
                    else
                    {
                        if (prettyPrint && ce instanceof Printable)
                        {
                            if ( ce.getNeedLineBreak() )
                            {
                                out.write(ce.lineSeparator.getBytes(encoding));
                                ce.putTabs(tabLevel + 1, out);
                            }
                        }
                        String string = obj.toString();
                        out.write(string.getBytes(encoding));
                    }
                }
                if (ce.getNeedClosingTag())
                {
                    if (prettyPrint && ce instanceof Printable)
                    {
                        if ( ce.getNeedLineBreak() )
                        {
                            out.write(ce.lineSeparator.getBytes(encoding));
                            if (tabLevel > 0)
                                ce.putTabs(tabLevel, out);
                        }
                    }
                out.write(ce.createEndTag().getBytes(encoding));
                }
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace(new PrintWriter(out));
        }
    }
	
    /**
        Override output(OutputStream) incase any elements are in the registry.
        @param output OutputStream to write to.
    */
    public void output(OutputStream out)
	{
		if (this.registry.size() == 0)
		{
				int tabLevel = getTabLevel();
				if ((getPrettyPrint() && this instanceof Printable) && (tabLevel > 0))  
				{
					try 
					{
						this.putTabs(tabLevel, out);
					}
					catch(IOException ioe) 
					{
						ioe.printStackTrace(new PrintWriter(out));					
					}
				}
                super.output(out);
		} 
		else  
		{
			output(out,this);
		}
	}

    /**
        Writer version of this method.
    */
    public void output(Writer out)
    {
        PrintWriter pw = new PrintWriter(out);
        output ( pw );
        pw.flush();
    }
    
    /**
        Override output(BufferedWriter) incase any elements are in the registry.
        @param output OutputStream to write to.
    */
    public void output(PrintWriter out)
	{
		boolean prettyPrint = getPrettyPrint();
		int tabLevel = getTabLevel();
		if (registry.size() == 0)
		{
			if ((prettyPrint && this instanceof Printable) && (tabLevel > 0))
				putTabs(tabLevel, out);

			super.output(out);
		}
		else
		{
			if ((prettyPrint && this instanceof Printable) && (tabLevel > 0))
				putTabs(tabLevel, out);

			out.write(createStartTag());
            // If this is a StringElement that has ChildElements still print the TagText
            if(getTagText() != null)
                out.write(getTagText());

            Enumeration _enum = registryList.elements();
			while(_enum.hasMoreElements())
			{
				Object obj = registry.get((String)_enum.nextElement());
				if(obj instanceof GenericElement)
				{
					Element e = (Element)obj;
					if (prettyPrint && this instanceof Printable)
					{
                        if (getNeedLineBreak()) {
							out.write(lineSeparator);
							e.setTabLevel(tabLevel + 1);
						}
					}
					e.output(out);
				}
				else
				{
					if (prettyPrint && this instanceof Printable)
					{
                        if (getNeedLineBreak()) {
							out.write(lineSeparator);
							putTabs(tabLevel + 1, out);
						}
					}
					String string = obj.toString();
					if(getFilterState())
						out.write(getFilter().process(string));
					else
						out.write(string);
				}
			}
			if (getNeedClosingTag())
			{
				if (prettyPrint && this instanceof Printable)
				{
                    if (getNeedLineBreak()) {
						out.write(lineSeparator);
						if (tabLevel > 0)
							putTabs(tabLevel, out);
					}
				}
			   out.write(createEndTag());
			}
		}
	}

    /**
        Allows all Elements the ability to be cloned.
    */
    public Object clone()
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(this);
            out.close();
            ByteArrayInputStream bin = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bin);
            Object clone =  in.readObject();
            in.close();
            return(clone);
        }
        catch(ClassNotFoundException cnfe)
        {
            throw new InternalError(cnfe.toString());
        }
        catch(StreamCorruptedException sce)
        {
            throw new InternalError(sce.toString());
        }
        catch(IOException ioe)
        {
            throw new InternalError(ioe.toString());
        }
    }
    
    public boolean isEmpty()
    {
        return registryList.isEmpty();
    }
}
