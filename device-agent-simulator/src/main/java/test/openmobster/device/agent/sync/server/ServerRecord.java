/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync.server;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;
import org.openmobster.cloud.api.sync.MobileBeanStreamable;

/**
 * @author openmobster@gmail.com
 */
public class ServerRecord implements MobileBeanStreamable
{
	/**
	 * database uid, not to be mistaken with domain level unique identity
	 */
	private Long uid = null;
	
	@MobileBeanId
	private String objectId = null;
			
	private String from = null;
		
	private String to = null;
		
	private String subject = null;
		
	private String message = null;
	
	private byte[] attachment = null;
	
	private List<Email> emails;
	private List<String> fruits;
				
	
	public ServerRecord()
	{		
	}
	
	
	
	public Long getUid() 
	{
		return uid;
	}



	public void setUid(Long uid) 
	{
		this.uid = uid;
	}

	
	public String getObjectId()
	{
		return objectId;
	}

	
	public void setObjectId(String objectId)
	{
		this.objectId = objectId;
	}

	
	public String getFrom()
	{
		return from;
	}

	
	public void setFrom(String from)
	{
		this.from = from;
	}

	
	public String getMessage()
	{
		return message;
	}

	
	public void setMessage(String message)
	{
		this.message = message;
	}

	
	public String getSubject()
	{
		return subject;
	}

	
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	
	public String getTo()
	{
		return to;
	}

	
	public void setTo(String to)
	{
		this.to = to;
	}

	
	public byte[] getAttachment() 
	{
		return attachment;
	}

	
	public void setAttachment(byte[] attachment) 
	{
		this.attachment = attachment;
	}	
	
	
	public List<Email> getEmails() 
	{
		return emails;
	}

	public void setEmails(List<Email> emails) 
	{
		this.emails = emails;
	}
		
	public List<String> getFruits() 
	{
		return fruits;
	}

	public void setFruits(List<String> fruits) 
	{
		this.fruits = fruits;
	}
	//-------Streamable implementation----------------------------------------------------------------
	public MobileBean getFull()
	{	
		ServerRecord fullRecord = new ServerRecord();
		
		fullRecord.setUid(this.uid);
		fullRecord.setObjectId(this.objectId);
		fullRecord.setFrom(this.from);
		fullRecord.setTo(this.to);
		fullRecord.setSubject(this.subject);
		fullRecord.setMessage(this.message);
		fullRecord.setAttachment(this.attachment);
		fullRecord.setEmails(this.generateEmails());
		fullRecord.setFruits(this.generateFruits());
		
		return fullRecord;
	}

	public MobileBean getPartial()
	{	
		ServerRecord partialRecord = new ServerRecord();
		
		partialRecord.setUid(this.uid);
		partialRecord.setObjectId(this.objectId);
		partialRecord.setFrom(this.from);
		partialRecord.setTo(this.to);
		partialRecord.setSubject(this.subject);
		partialRecord.setMessage(this.message);
		partialRecord.setAttachment(null);
		partialRecord.setEmails(this.generateEmails());
		partialRecord.setFruits(this.generateFruits());
		
		return partialRecord;
	}	
	//----------------------------------------------------------------------------------------------------
	private List<String> generateFruits()
	{
		List<String> fruits = new ArrayList<String>();
		
		for(int i=0; i<5; i++)
		{
			fruits.add(i+"://fruit");
		}
		return fruits;
	}
	
	private List<Email> generateEmails()
    {
	   List<Email> emails = new ArrayList<Email>();
	   
	   for(int i=0; i<5; i++)
	   {
		   Email email = new Email();
		   
		   String uid = String.valueOf(i);
		   email.setUid(uid);
		   email.setFrom(uid+"://from");
		   email.setTo(uid+"://to");
		   email.setSubject(uid+"://subject");
		   email.setMessage(uid+"://message");
		   
		   emails.add(email);
	   }
	   
	   return emails;
    }
}
