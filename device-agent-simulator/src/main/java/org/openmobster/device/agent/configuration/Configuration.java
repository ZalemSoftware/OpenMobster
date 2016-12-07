/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.configuration;

import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;


/**
 * @author openmobster@gmail.com
 *
 */
public final class Configuration
{
	private static Configuration singleton;
	
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
	
	private Configuration()
	{							
	}
	
		
	public static Configuration getInstance()
	{
		if(Configuration.singleton == null)
		{
			Configuration.singleton = new Configuration();
		}
		return Configuration.singleton;
	}
	
	public static void cleanup()
	{
		Configuration.singleton = null;
	}
	
	public void swapState(org.openmobster.device.agent.test.framework.Configuration config)
	{
		this.deviceId = config.getDeviceId();
		this.serverId = config.getServerId();
		this.plainServerPort = config.getServerPort();
		this.secureServerPort = config.getSecureServerPort();
		this.isSSLActive = config.isSSLActivated();
		this.maxPacketSize = config.getMaxPacketSize();
		this.authenticationHash = config.getAuthenticationHash();
		this.authenticationNonce = config.getAuthenticationNonce();
		this.email = config.getEmail();
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
	
	public String decidePort()
	{
		if(this.isSSLActivated())
		{
			return this.getSecureServerPort();
		}
		else
		{
			return this.getPlainServerPort();
		}
	}
	
	public void bootup()
	{
		this.deActivateSSL();
		Request request = new Request("provisioning");
		request.setAttribute("action", "metadata");
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		//Setup the Cloud Connector Configuration
		this.setServerIp(response.getAttribute("serverIp"));
		this.setServerId(response.getAttribute("serverId"));
		this.setPlainServerPort(response.getAttribute("plainServerPort"));
		
		if(response.getAttribute("isSSLActive").equals(Boolean.TRUE.toString()))
		{
			this.setSecureServerPort(response.getAttribute("secureServerPort"));
			this.activateSSL();
		}
		else
		{
			this.deActivateSSL();
		}
		
		this.setMaxPacketSize(Integer.parseInt(response.getAttribute("maxPacketSize")));
	}
}
