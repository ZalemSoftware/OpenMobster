/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.channel;

import org.openmobster.cloud.api.sync.Channel;

/**
 * @author openmobster@gmail
 *
 */
public final class ChannelRegistration 
{
	private String uri;
	private Channel channel;
	private long updateCheckInterval;
	
	public ChannelRegistration(String uri, Channel channel)
	{
		this.uri = uri;
		this.channel = channel;
		this.updateCheckInterval = 20000; //Default Value: checks for channel updates every 20 seconds
	}
	
	public String getUri()
	{
		return this.uri;
	}
	
	public Channel getChannel()
	{
		return this.channel;
	}

	public long getUpdateCheckInterval() 
	{
		return updateCheckInterval;
	}

	public void setUpdateCheckInterval(long updateCheckInterval) 
	{
		this.updateCheckInterval = updateCheckInterval;
	}
}
