/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject.xml;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;

import com.thoughtworks.xstream.XStream;

/**
 * @author openmobster@gmail.com
 */
public class TestBlobsSerialization extends TestCase
{
	private static Logger log = Logger.getLogger(TestBlobsSerialization.class);
	
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
	public void testBlobs() throws Exception
	{		
		Blobs blobs = new Blobs();
		blobs.setName("blobTest");
		List<byte[]> list = new ArrayList<byte[]>();
		List<String[]> strings = new ArrayList<String[]>();
		List<Attachment> attachments = new ArrayList<Attachment>();
		for(int i=0; i<5; i++)
		{
			byte[] local = ("hello://"+i).getBytes();
			list.add(local);
			
			String[] stringArray = new String[]{"hello://"+i};
			strings.add(stringArray);
			
			Attachment attachment = new Attachment();
			attachment.setData(local);
			Attachment more = new Attachment();
			more.setData(("more://"+i).getBytes());
			attachment.setMore(more);
			attachments.add(attachment);
		}
		//blobs.setBlobs(list);
		//blobs.setStrings(strings);
		blobs.setAttachments(attachments);
		
		String xml = this.serializer.serialize(blobs);
		
		log.info("--------------------------------------------------");
		log.info(xml);
		
		blobs = (Blobs)this.serializer.deserialize(Blobs.class, xml);	
		
		log.info("----------------------------------------------------");
		log.info("Name: "+blobs.getName());
		assertEquals("blobTest",blobs.getName());
		attachments = blobs.getAttachments();
		for(int i=0; i<attachments.size(); i++)
		{
			Attachment attachment = attachments.get(i);
			log.info(new String(attachment.getData()));
			log.info(new String(attachment.getMore().getData()));
			assertEquals("hello://"+i,new String(attachment.getData()));
			assertEquals("more://"+i,new String(attachment.getMore().getData()));
		}
	}	
}
