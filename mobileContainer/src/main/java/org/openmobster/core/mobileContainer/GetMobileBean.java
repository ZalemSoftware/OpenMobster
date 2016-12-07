/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileContainer;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.services.MobileObjectMonitor;


/**
 * @author openmobster@gmail.com
 */
public class GetMobileBean implements ContainerService
{
	private static Logger log = Logger.getLogger(GetMobileBean.class);
	
	private String id;
	private MobileObjectMonitor monitor;
	
	public GetMobileBean()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
	{
		this.id = id;
	}
	
	public MobileObjectMonitor getMonitor() 
	{
		return monitor;
	}

	public void setMonitor(MobileObjectMonitor monitor) 
	{
		this.monitor = monitor;
	}
	//---------------------------------------------------------------------------------------------------
	public InvocationResponse execute(Invocation invocation) throws InvocationException
	{
		InvocationResponse response = InvocationResponse.getInstance();
		MobileBean bean = null;
		
		String connectorId = invocation.getConnectorId();
		
		//Retrieve the correct MobileObjectConnector		
		Channel connector = monitor.lookup(connectorId);
		if(connector == null)
		{
			response.setStatus(InvocationResponse.STATUS_NOT_FOUND);
			return response;
		}
		
		bean = connector.read(invocation.getBeanId());
		if(bean != null)
		{
			response.setBean(bean);
		}
		else
		{
			response.setStatus(InvocationResponse.STATUS_NOT_FOUND);
		}
		
		return response;
	}
}
