/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.security.plugin;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PluginContext
{	
	private String deviceId;
	private String principal;
	private String password;
	
	public PluginContext()
	{
		
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

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
}
