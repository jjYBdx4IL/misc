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
    This class contains some simple tests of the vxml generation package

    @author Written by <a href="mailto:jcarol@us.ibm.com">Carol Jones</a>
*/
public class TestBed2
{
    	
	public void WeatherTest()
	{
	System.out.println("\nWeather.vxml");

	VXMLDocument doc = new VXMLDocument();
	Vxml vxml = new Vxml("1.0");
	Form form = new Form("weather_info");
	Block block = new Block();
	block.addElement("Welcome to the weather information service.");
	Field field = new Field("city");
	Prompt prompt = new Prompt("What city?");
	Grammar grammar = new Grammar("city.gram","application/x-jsgf");
	Catch catchtag = new Catch("help");
	catchtag.addElement("Please speak the city for which you want the weather.");
	field.addElement(prompt);
	field.addElement(grammar);
	field.addElement(catchtag);

	form.addElement(block);
	form.addElement(field);
	form.addElement(new Block().addElement(new Submit("/servlet/weather", "city")));
	vxml.addElement(form);

	doc.addElement(vxml);

	System.out.println(doc.toString());
	}
	
	public void CreditCard()
	{
	System.out.println("\nCreditCard.vxml");

	VXMLDocument doc = new VXMLDocument();
	Vxml vxml = new Vxml("1.0");
	Form form = new Form("get_card_info");
	Block block = new Block();
	block.addElement("We now need your credit card type, number, and expiration date.");
	form.addElement(block);
	
	Field field = new Field("card_type");
	Prompt prompt1 = new Prompt("What kind of credit card do you have?");
	Prompt prompt2 = new Prompt("Type of card?");
	prompt1.setBargein("false");
	prompt1.setCount("1");
	prompt2.setCount("2");
	
	Grammar grammar = new Grammar();
	grammar.addElement("visa {visa}");
	grammar.addElement("| master [card] {mastercard}");
	grammar.addElement("| amex {amex}");
	grammar.addElement("| american [express] {amex}");
	Help help = new Help("Please say Visa, Mastercard, or American Express.");
	field.addElement(prompt1);
	field.addElement(prompt2);
	field.addElement(grammar);
	field.addElement(help);
	form.addElement(field);


	vxml.addElement(form);

	doc.addElement(vxml);

	System.out.println(doc.toString());
	}
	
	public void MenuTest()
	{
	System.out.println("\nMenuTest.vxml");

	VXMLDocument doc = new VXMLDocument();
	Vxml vxml = new Vxml("1.0");
	Menu menu1 = new Menu();
	Property prop = new Property();
	prop.setInputmodes("dtmf");
	menu1.addElement(prop);
	Prompt prompt = new Prompt("For sports press 1, For weather press 2, For Stargazer astrophysics press 3.");
	menu1.addElement(prompt);
	menu1.addElement(new Choice("1","http://www.sports.example/vxml/start.vxml"));
	menu1.addElement(new Choice("2","http://www.weather.example/intro.vxml"));
	menu1.addElement(new Choice("3","http://www.stargazer.example/voice/astronews.vxml"));
	vxml.addElement(menu1);
	
	Menu menu2 = new Menu("true");
	menu2.addElement(prop);
	menu2.addElement(prompt);
	menu2.addElement(new Choice("http://www.sports.example/vxml/start.vxml"));
	menu2.addElement(new Choice("http://www.weather.example/intro.vxml"));
	menu2.addElement(new Choice("http://www.stargazer.example/voice/astronews.vxml"));
	vxml.addElement(menu2);
	
	Menu menu3 = new Menu("true");
	Prompt prompt2 = new Prompt("Welcome Home");
	Enumerate _enum = new Enumerate();
	_enum.addElement("For ");
	_enum.addElement(new Value("_prompt"));
	_enum.addElement(", press ");
	_enum.addElement(new Value("_dtmf"));
	prompt2.addElement(_enum);
	menu3.addElement(prompt2);
	Choice choice1 = new Choice("http://www.sports.example/vxml/start.vxml");
	Choice choice2 = new Choice("http://www.weather.example/intro.vxml");
	Choice choice3 = new Choice("http://www.stargazer.example/voice/astronews.vxml");
	choice1.addElement("sports");
	choice2.addElement("weather");
	choice3.addElement("Stargazer astrophysics news");
	menu3.addElement(choice1);
	menu3.addElement(choice2);
	menu3.addElement(choice3);

	vxml.addElement(menu3);

	System.out.println(vxml.toString());	
	}

	public void RepromptTest()
	{
	System.out.println("\nRepromptTest.vxml");

	VXMLDocument doc = new VXMLDocument();
	Vxml vxml = new Vxml("1.0");	

	vxml.addElement(new Reprompt());

	doc.addElement(vxml);

	System.out.println(doc.toString());
	}

	
    public static void main(String[] args)
    {
	TestBed2 tb = new TestBed2();
	
	tb.WeatherTest();
	tb.CreditCard();
	tb.MenuTest();
	tb.RepromptTest();
    }
}
