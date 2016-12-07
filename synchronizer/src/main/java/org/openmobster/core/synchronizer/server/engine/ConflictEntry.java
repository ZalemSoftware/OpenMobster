/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.server.engine;

import java.io.Serializable;

/**
 * 
 * @author openmobster@gmail.com
 */
public class ConflictEntry implements Serializable
{
	private long id; //instance oid
	
	private String deviceId;
	private String app;
	private String channel;
	private String oid;
	private byte[] state;
	
	public ConflictEntry()
	{
		
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getOid()
	{
		return oid;
	}

	public void setOid(String oid)
	{
		this.oid = oid;
	}

	public byte[] getState()
	{
		return state;
	}

	public void setState(byte[] state)
	{
		this.state = state;
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	public String getApp()
	{
		return app;
	}

	public void setApp(String app)
	{
		this.app = app;
	}
	
	public String getStateAsString()
	{
		String stateAsString = "";
		if(this.state != null)
		{
			stateAsString = new String(this.state);
		}
		return stateAsString;
	}
}
