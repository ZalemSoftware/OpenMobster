/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.openmobster.core.common;

import java.nio.charset.Charset;

import org.openmobster.core.common.XMLUtilities;

import junit.framework.TestCase;
import org.w3c.dom.Document;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestXMLUtilities extends TestCase
{
	protected void setUp() throws Exception
	{
		super.setUp();
		
		System.out.println("***********************");
		System.out.println("Default Charset: "+Charset.defaultCharset().name());
		System.out.println("***********************");
	}

	
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testForeignCharacters() throws Exception
	{
		StringBuilder xml = new StringBuilder();
		
		xml.append("<spanish>\n");
		xml.append("<name>name</name>\n");
		xml.append("<value>espaÑñol</value>\n");
		xml.append("</spanish>\n");
		
		Document documnet = XMLUtilities.parse(xml.toString());
	}
}
