/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.test.framework;

import org.openmobster.core.common.ServiceManager;


/**
 * @author openmobster@gmail.com
 *
 */
public final class Configuration
{	
	private String deviceId;
	private String serverId;
	private String serverIp;
	private String plainServerPort;
	private String secureServerPort;
	private boolean isSSLActive;
	private int maxPacketSize;
	private String authenticationHash;
	private String authenticationNonce;
	private String email;
	
	public Configuration()
	{							
	}						
	//--------------------------------------------------------------------------------------------------------------------------------------
	public String getDeviceId()
	{
		return this.deviceId;
	}
	
	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}
			
	public int getMaxPacketSize()
	{
		return this.maxPacketSize;
	}
	
	public void setMaxPacketSize(int maxPacketSize)
	{
		this.maxPacketSize = maxPacketSize;
	}
	
	public String getAuthenticationHash()
	{
		String nonce = this.getAuthenticationNonce();
		if(nonce == null || nonce.trim().length()==0)
		{
			//This should be the provisioned username, password based hash
			nonce = this.authenticationHash;
		}
		return nonce;
	}
	
	public void setAuthenticationHash(String authenticationHash)
	{
		this.authenticationHash = authenticationHash;
	}
	
	public String getAuthenticationNonce()
	{
		return this.authenticationNonce;
	}
	
	public void setAuthenticationNonce(String authenticationNonce)
	{
		this.authenticationNonce = authenticationNonce;
	}
	
	public String getServerId()
	{
		return this.serverId;
	}
	
	public void setServerId(String serverId)
	{
		this.serverId = serverId;
	}
	
	public String getServerIp()
	{
		return this.serverIp;
	}
	
	public void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}
	
	public String getServerPort()
	{
		if(this.isSSLActivated())
		{
			return this.secureServerPort;
		}
		else
		{
			return this.plainServerPort;
		}
	}
	
	public String getPlainServerPort()
	{
		return this.plainServerPort;
	}
	
	public void setPlainServerPort(String plainServerPort)
	{
		this.plainServerPort = plainServerPort;
	}
	
	public String getSecureServerPort()
	{
		return this.secureServerPort;
	}
	
	public void setSecureServerPort(String secureServerPort)
	{
		this.secureServerPort = secureServerPort;
	}
	
	public boolean isSSLActivated()
	{
		return this.isSSLActive;
	}
	
	public void activateSSL()
	{
		this.isSSLActive = true;
	}
	
	public void deActivateSSL()
	{
		this.isSSLActive = false;
	}


	public String getEmail() 
	{
		return email;
	}


	public void setEmail(String email) 
	{
		this.email = email;
	}	
}
