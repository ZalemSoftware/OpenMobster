/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject.email;

import org.openmobster.core.common.domain.BusinessObject;


/**
 * Represents an Email message 
 * 
 * @author openmobster@gmail.com
 *
 */
public class MockEmail extends BusinessObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7191190509680361917L;
	
	/**
	 * 
	 */
	private String messageID = null; //uniquely identifies this message	
	private String subject = null;	
	private String date	= null;	
	private String message = null;
	
	private MockEmailAddr[] from = null;
	private MockEmailAddr[] to = null;
	private MockEmailAddr[] cc = null;
	private MockEmailAddr[] bcc = null;
	private MockAttachment[] attachments = null;
	
	private MockFolder folder = null;	
	private String status = null;	
	private MockPriority priority = null;		
			
	/**
	 * 
	 *
	 */
	public MockEmail()
	{
		
	}

	public String getDate() 
	{
		return date;
	}

	public void setDate(String date) 
	{
		this.date = date;
	}

	public String getMessage() 
	{
		return message;
	}

	public void setMessage(String message) 
	{
		this.message = message;
	}

	public String getMessageID() 
	{
		return messageID;
	}

	public void setMessageID(String messageID) 
	{
		this.messageID = messageID;
	}

	public String getSubject() 
	{
		return subject;
	}

	public void setSubject(String subject) 
	{
		this.subject = subject;
	}

	public MockEmailAddr[] getFrom() 
	{
		return from;
	}

	public void setFrom(MockEmailAddr[] from) 
	{
		this.from = from;
	}

	public MockFolder getFolder() 
	{
		return folder;
	}

	public void setFolder(MockFolder folder) 
	{
		this.folder = folder;
	}

	public String getStatus() 
	{
		return status;
	}

	public void setStatus(String status) 
	{
		this.status = status;
	}

	public MockPriority getPriority() 
	{
		return priority;
	}

	public void setPriority(MockPriority priority) 
	{
		this.priority = priority;
	}

	public MockAttachment[] getAttachments() 
	{
		return attachments;
	}

	public void setAttachments(MockAttachment[] attachments) 
	{
		this.attachments = attachments;
	}

	public MockEmailAddr[] getBcc() 
	{
		return bcc;
	}

	public void setBcc(MockEmailAddr[] bcc) 
	{
		this.bcc = bcc;
	}

	public MockEmailAddr[] getCc() 
	{
		return cc;
	}

	public void setCc(MockEmailAddr[] cc) 
	{
		this.cc = cc;
	}

	public MockEmailAddr[] getTo() 
	{
		return to;
	}

	public void setTo(MockEmailAddr[] to) 
	{
		this.to = to;
	}		
}
