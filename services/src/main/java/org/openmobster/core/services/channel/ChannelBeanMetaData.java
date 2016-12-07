/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.channel;

import java.io.Serializable;

/**
 * @author openmobster@gmail.com
 */
public class ChannelBeanMetaData implements Serializable 
{
	private String channel;
	private String beanId;
	private String deviceId;	
	private ChannelUpdateType updateType;
	private String principal;
	
	public ChannelBeanMetaData()
	{
		
	}

	public String getBeanId() 
	{
		return beanId;
	}

	public void setBeanId(String beanId) 
	{
		this.beanId = beanId;
	}

	public String getChannel() 
	{
		return channel;
	}

	public void setChannel(String channel) 
	{
		this.channel = channel;
	}

	public ChannelUpdateType getUpdateType() 
	{
		return updateType;
	}

	public void setUpdateType(ChannelUpdateType updateType) 
	{
		this.updateType = updateType;
	}

	public String getDeviceId() 
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId) 
	{
		this.deviceId = deviceId;
	}

	public String getPrincipal() 
	{
		return principal;
	}

	public void setPrincipal(String principal) 
	{
		this.principal = principal;
	}		
}
