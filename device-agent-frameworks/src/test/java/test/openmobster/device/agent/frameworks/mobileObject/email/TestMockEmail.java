/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject.email;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

import test.openmobster.device.agent.frameworks.mobileObject.AbstractSerialization;

import javax.mail.URLName;

/**
 * @author openmobster@gmail.com
 */
public class TestMockEmail extends AbstractSerialization 
{	
	private static Logger log = Logger.getLogger(TestMockEmail.class);
			
	public void testDeepEmailObject() throws Exception
	{
		MockEmail email = this.createMockEmail(123);
			
		String xml = this.serverSerialize(email);
		log.info("XML From Server-----------------------------------------");
		log.info(xml);
		
		MobileObject mobileObject = this.deviceDeserialize(xml);			
		
		String deserialized = DeviceSerializer.getInstance().serialize(mobileObject);
		
		//Process xml to produce the business object back
		log.info("XML From Device------------------------------------------");
		log.info(deserialized);
		
		MockEmail des = (MockEmail)this.serverDeserialize(MockEmail.class, deserialized);
		this.assertMockEmail(des);				
	}
	//-----------------------------------------------------------------------------------------------------
	private MockEmail createMockEmail(int id)
	{
		MockEmail email = new MockEmail();
		
		email.setId(new Long(id));
		email.setDate("2008-08-21");
		email.setMessage("<testXml name=\"blahName/'value'\">" +
		"Hello World......This is your message...http://www.xyz.com?name=blah&email=blah@blah.com......" +
		"</testXml>");
		email.setMessageID("id://unique-1");
		email.setSubject("This rocks!!!");
		
		MockEmailAddr[] from = new MockEmailAddr[]{new MockEmailAddr("from:blah@blah.com<tag attr='&1234'>this rocks</tag>"),
				new MockEmailAddr("from:blah2@blah2.com<tag attr='&5678'>this rocks</tag>")};
		email.setFrom(from);
		
		MockEmailAddr[] to = new MockEmailAddr[]{new MockEmailAddr("to:blah@blah.com<tag attr='&1234'>this rocks</tag>"),
				new MockEmailAddr("to:blah2@blah2.com<tag attr='&5678'>this rocks</tag>")};
		email.setTo(to);
		
		MockEmailAddr[] cc = new MockEmailAddr[]{new MockEmailAddr("cc:blah@blah.com<tag attr='&1234'>this rocks</tag>"),
				new MockEmailAddr("cc:blah2@blah2.com<tag attr='&5678'>this rocks</tag>")};
		email.setCc(cc);
		
		MockEmailAddr[] bcc = new MockEmailAddr[]{new MockEmailAddr("bcc:blah@blah.com<tag attr='&1234'>this rocks</tag>"),
				new MockEmailAddr("bcc:blah2@blah2.com<tag attr='&5678'>this rocks</tag>")};
		email.setBcc(bcc);
		
		email.setPriority(MockPriority.createInstance("HIGH"));
		
		MockAttachment[] attachments = new MockAttachment[]{new MockAttachment(
		"text/html", "hello.html", 1024, new String("<head><body><h1>Hello World!!!</h1></body></head>").getBytes())}; 		
		email.setAttachments(attachments);
		
		URLName folderUrl = new URLName("smtp", "localhost", 53, "mail.inbox", "blah", "blahpass");
		MockFolder folder = new MockFolder("INBOX", "full://INBOX", folderUrl.toString());	
		folder.setEmails(this.createFolderEmails(5));
		email.setFolder(folder);
			
		email.setStatus("DELETED");		
		
		return email;	
	}
	
	private MockEmail createSimpleMockEmail(int id)
	{
		MockEmail email = new MockEmail();
		
		email.setId(new Long(id));
		email.setDate("2008-08-21");
		email.setMessage("<testXml name=\"blahName/'value'\">" +
		"Hello World......This is your message...http://www.xyz.com?name=blah&email=blah@blah.com......" +
		"</testXml>");
		email.setMessageID("id://unique-1");
		email.setSubject("This rocks!!!");
		
		MockEmailAddr[] from = new MockEmailAddr[]{new MockEmailAddr("from:blah@blah.com<tag attr='&1234'>this rocks</tag>"),
				new MockEmailAddr("from:blah2@blah2.com<tag attr='&5678'>this rocks</tag>")};
		email.setFrom(from);
		
		MockEmailAddr[] to = new MockEmailAddr[]{new MockEmailAddr("to:blah@blah.com<tag attr='&1234'>this rocks</tag>"),
				new MockEmailAddr("to:blah2@blah2.com<tag attr='&5678'>this rocks</tag>")};
		email.setTo(to);
		
		MockEmailAddr[] cc = new MockEmailAddr[]{new MockEmailAddr("cc:blah@blah.com<tag attr='&1234'>this rocks</tag>"),
				new MockEmailAddr("cc:blah2@blah2.com<tag attr='&5678'>this rocks</tag>")};
		email.setCc(cc);
		
		MockEmailAddr[] bcc = new MockEmailAddr[]{new MockEmailAddr("bcc:blah@blah.com<tag attr='&1234'>this rocks</tag>"),
				new MockEmailAddr("bcc:blah2@blah2.com<tag attr='&5678'>this rocks</tag>")};
		email.setBcc(bcc);
		
		MockAttachment[] attachments = new MockAttachment[]{new MockAttachment(
		"text/html", "hello.html", 1024, new String("<head><body><h1>Hello World!!!</h1></body></head>").getBytes())}; 		
		email.setAttachments(attachments);
		
		email.setPriority(MockPriority.createInstance("HIGH"));
		
		URLName folderUrl = new URLName("smtp", "localhost", 53, "mail.inbox", "blah", "blahpass");
		MockFolder folder = new MockFolder("INBOX", "full://INBOX", folderUrl.toString());			
		email.setFolder(folder);
		
		
		email.setStatus("DELETED");		
		
		return email;	
	}
	
	private List createFolderEmails(int count)
	{
		List emails = new ArrayList();
		
		for(int i=0; i<count; i++)
		{
			MockEmail folderMail = this.createSimpleMockEmail(i);
			emails.add(folderMail);
		}
		
		return emails;
	}
	
	private void assertMockEmail(MockEmail email)
	{			
		this.assertSimpleMockEmail(email);	
		
		assertEquals("Failure!!!", email.getFolder().getEmails().size(), 5);
		for(int i=0; i<email.getFolder().getEmails().size(); i++)
		{
			this.assertSimpleMockEmail((MockEmail)email.getFolder().getEmails().get(i));
		}
	}
	
	private void assertSimpleMockEmail(MockEmail email)
	{			
		log.info("Id="+email.getId());
		assertNotNull("Id Must Not be Null", email.getId());
		
		log.info("Date="+email.getDate());
		assertEquals("Failure!!!", email.getDate(), "2008-08-21");
		
		log.info("Message="+email.getMessage());
		assertEquals("Failure!!!", email.getMessage(), "<testXml name=\"blahName/'value'\">" +
		"Hello World......This is your message...http://www.xyz.com?name=blah&email=blah@blah.com......" +
		"</testXml>");
		
		log.info("MessageId="+email.getMessageID());
		assertEquals("Failure!!!", email.getMessageID(), "id://unique-1");
		
		log.info("Subject="+email.getSubject());
		assertEquals("Failure!!!", email.getSubject(), "This rocks!!!");
		
		MockEmailAddr[] from = email.getFrom();
		assertEquals("Failure!!!", from.length, 2);
		assertEquals("Failure!!!", from[0].getAddress(), "from:blah@blah.com<tag attr='&1234'>this rocks</tag>");
		assertEquals("Failure!!!", from[1].getAddress(), "from:blah2@blah2.com<tag attr='&5678'>this rocks</tag>");
		
		MockEmailAddr[] to = email.getTo();
		assertEquals("Failure!!!", to.length, 2);
		assertEquals("Failure!!!", to[0].getAddress(), "to:blah@blah.com<tag attr='&1234'>this rocks</tag>");
		assertEquals("Failure!!!", to[1].getAddress(), "to:blah2@blah2.com<tag attr='&5678'>this rocks</tag>");
		
		MockEmailAddr[] cc = email.getCc();
		assertEquals("Failure!!!", cc.length, 2);
		assertEquals("Failure!!!", cc[0].getAddress(), "cc:blah@blah.com<tag attr='&1234'>this rocks</tag>");
		assertEquals("Failure!!!", cc[1].getAddress(), "cc:blah2@blah2.com<tag attr='&5678'>this rocks</tag>");
		
		MockEmailAddr[] bcc = email.getBcc();
		assertEquals("Failure!!!", bcc.length, 2);
		assertEquals("Failure!!!", bcc[0].getAddress(), "bcc:blah@blah.com<tag attr='&1234'>this rocks</tag>");
		assertEquals("Failure!!!", bcc[1].getAddress(), "bcc:blah2@blah2.com<tag attr='&5678'>this rocks</tag>");
		
		MockAttachment[] attachments = email.getAttachments();
		log.info("Attachment="+new String(attachments[0].getData()));
		assertEquals("Failure!!!", attachments.length, 1);
		assertEquals("Failure!!!", new String(attachments[0].getData()), 
		"<head><body><h1>Hello World!!!</h1></body></head>");
		
		log.info("Flags="+email.getStatus());
		assertTrue("Failure!!!", email.getStatus().equals("DELETED"));
		
		log.info("Priority="+email.getPriority());
		assertEquals("Failure!!!", email.getPriority().toString(), "HIGH");
		
		log.info("Folder Name="+email.getFolder().getName());
		log.info("Folder FullName="+email.getFolder().getFullName());
		log.info("Folder URL="+email.getFolder().getUrlName());				
		assertEquals("Failure!!!", email.getFolder().getName(), "INBOX");
		assertEquals("Failure!!!", email.getFolder().getFullName(), "full://INBOX");
		assertEquals("Failure!!!", email.getFolder().getUrlName(), "smtp://blah:blahpass@localhost:53/mail.inbox");				
	}
}
