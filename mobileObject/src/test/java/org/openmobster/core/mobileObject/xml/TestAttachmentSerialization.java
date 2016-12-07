/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject.xml;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;

import com.thoughtworks.xstream.XStream;

/**
 * @author openmobster@gmail.com
 */
public class TestAttachmentSerialization extends TestCase
{
	private static Logger log = Logger.getLogger(TestAttachmentSerialization.class);
	
	private MobileObjectSerializer serializer;
			
	protected void setUp() throws Exception 
	{		
		ServiceManager.bootstrap();
		
		this.serializer = (MobileObjectSerializer)ServiceManager.
		locate("mobileObject://MobileObjectSerializer");
	}	
		
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}
	//-------------------------------------------------------------------------------------------------------------------
	public void testAttachment() throws Exception
	{		
		Attachment attachment = new Attachment();
		attachment.setData("blahblahblah".getBytes());
		Attachment more = new Attachment();
		more.setData("more://blahblahblah".getBytes());
		attachment.setMore(more);
		
		XStream coreXStream = new XStream();
		String coreXml = coreXStream.toXML(attachment);
		log.info("Core Xml-----------------------------------------");
		log.info(coreXml);
		
		String xml = this.serializer.serialize(attachment);
		
		log.info("--------------------------------------------------");
		log.info(xml);
		
		attachment = (Attachment)this.serializer.deserialize(Attachment.class, xml);	
		
		log.info("----------------------------------------------------");
		log.info("Data="+new String(attachment.getData()));
		log.info("More Data="+new String(attachment.getMore().getData()));
		assertEquals("blahblahblah", new String(attachment.getData()));
		assertEquals("more://blahblahblah", new String(attachment.getMore().getData()));
		
		attachment = (Attachment)coreXStream.fromXML(coreXml);
		
		log.info("Core Deserialization----------------------------------------------------");
		log.info("Data="+new String(attachment.getData()));
		log.info("More Data="+new String(attachment.getMore().getData()));
	}	
}
