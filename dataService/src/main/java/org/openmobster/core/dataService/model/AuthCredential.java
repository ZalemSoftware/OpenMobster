/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.model;

import java.io.Serializable;

/**
 * @author openmobster@gmail.com
 */
public class AuthCredential implements Serializable 
{
	private static final long serialVersionUID = -4601649505873566320L;

	private String deviceId;
	private String nonce;
	
	public AuthCredential()
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

	public String getNonce() 
	{
		return nonce;
	}

	public void setNonce(String nonce) 
	{
		this.nonce = nonce;
	}	
}
