/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloudConnector.api;

/**
 * @author openmobster@gmail.com
 */
public final class SecurityConfig
{
	private String keyStoreLocation;
	private String keyStorePassword;
	
	public SecurityConfig()
	{
		
	}

	public String getKeyStoreLocation()
	{
		return keyStoreLocation;
	}

	public void setKeyStoreLocation(String keyStoreLocation)
	{
		this.keyStoreLocation = keyStoreLocation;
	}

	public String getKeyStorePassword()
	{
		if(this.keyStorePassword == null)
		{
			return "";
		}
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword)
	{
		this.keyStorePassword = keyStorePassword;
	}
}
