/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.testsuite.server;


import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 * @author openmobster@gmail.com
 */
public class Email implements MobileBean
{
	@MobileBeanId
	private String uid;
	private String from;
	private String to;
	private String subject;
	private String message;
	
	
	public Email()
	{
		
	}
	
	public Email(String uid, String from, String to, String subject, String message)
	{
		this.uid = uid;
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.message = message;
	}
	
	public String getUid() 
	{
		return uid;
	}

	public void setUid(String uid) 
	{
		this.uid = uid;
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
	
	public void save(Email email)
	{
		this.uid = email.getUid();
		this.from = email.getFrom();
		this.to = email.getTo();
		this.subject = email.getSubject();
		this.message = email.getMessage();
	}
}
