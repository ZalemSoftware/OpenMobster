/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.common;

import java.text.MessageFormat;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import org.openmobster.core.common.XMLUtilities;

/**
 * @author openmobster@gmail.com
 *
 */
public class TestObjectSerialization extends TestCase 
{	
	private static Logger log = Logger.getLogger(TestObjectSerialization.class);
	
	public void testXMLGeneration() throws Exception
	{
		Bean3 orig = new Bean3();
		String message = "<tag apos=''apos'' quote=\"quote\" ampersand=''&''>{0}/Message</tag>";
		String formattedMessage = MessageFormat.format(message, new Object[]{"blah"});
		
		log.info("------------------------------------------------------");
		log.info("Message="+message);
		log.info("FormattedMessage="+formattedMessage);
		
		orig.setMessage(formattedMessage);
		
		String xml = XMLUtilities.marshal(orig);
		log.info(xml);
		
		
	}
}
