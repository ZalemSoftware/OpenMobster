/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.channel;

import java.io.Serializable;
import java.util.Date;


/**
 * @author openmobster@gmail.com
 */
public class LastScanTimestamp implements Serializable 
{
	private static final long serialVersionUID = 2882738599502882093L;
	
	private long id;
	private Date timestamp;
	private String channel;
	private String clientId;
	
	public LastScanTimestamp()
	{
		
	}
	
	public LastScanTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public long getId()
	{
		return this.id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public Date getTimestamp()
	{
		return this.timestamp;
	}
	
	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getChannel() 
	{
		return channel;
	}

	public void setChannel(String channel) 
	{
		this.channel = channel;
	}

	public String getClientId() 
	{
		return clientId;
	}

	public void setClientId(String clientId) 
	{
		this.clientId = clientId;
	}		
}
