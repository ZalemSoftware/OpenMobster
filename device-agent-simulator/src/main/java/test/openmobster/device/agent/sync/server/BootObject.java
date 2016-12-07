/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.openmobster.device.agent.sync.server;

import java.io.Serializable;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 *
 * @author openmobster@gmail.com
 */
public class BootObject implements MobileBean,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7124719905210522794L;

	@MobileBeanId
	private String syncId;
	
	private String from;
	private String to;
	
	private Message message;
	
	public BootObject()
	{
		
	}

	public String getSyncId()
	{
		return syncId;
	}

	public void setSyncId(String syncId)
	{
		this.syncId = syncId;
	}

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getTo()
	{
		return to;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

	public Message getMessage()
	{
		return message;
	}

	public void setMessage(Message message)
	{
		this.message = message;
	}
}
