/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common;

import org.apache.log4j.Logger;

/**
 * @author openmobster@gmail.com
 *
 */
public class CustomConfig 
{
	private static Logger log = Logger.getLogger(CustomConfig.class);
	
	private String serverName;
	private String serverIp;
	private String httpPort;
	
	public CustomConfig()
	{
		
	}
	
	public String getServerName() 
	{
		return serverName;
	}
	
	public void setServerName(String serverName) 
	{
		this.serverName = serverName;
	}
	
	public String getServerIp() 
	{
		return serverIp;
	}
	
	public void setServerIp(String serverIp) 
	{
		this.serverIp = serverIp;
	}	
		
	public String getHttpPort() 
	{
		return httpPort;
	}

	public void setHttpPort(String httpPort) 
	{
		this.httpPort = httpPort;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	public void start()
	{
		log.info("---------------------------------------------------------------------------");
		log.info("Server Name="+this.serverName);
		log.info("Server IP Address="+this.serverIp);
		log.info("Http Port="+this.httpPort);
		log.info("---------------------------------------------------------------------------");
	}
	
	public void stop()
	{
		
	}
}
