/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileContainer;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.HashMap;

/**
 * @author openmobster@gmail.com
 */
public class MobileContainer 
{
	private static Logger log = Logger.getLogger(MobileContainer.class);
	
	private Map<String, ContainerService> services;
	
	public MobileContainer()
	{
		this.services = new HashMap<String, ContainerService>();
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void register(ContainerService service)
	{
		this.services.put(service.getId(), service);
	}
	//-------------------------------------------------------------------------------------------------
	public InvocationResponse invoke(Invocation invocation) throws InvocationException
	{
		InvocationResponse response = null;
		
		//Route invocation to the appropriate container service
		String serviceUrl = invocation.getServiceUrl();
		ContainerService service = this.services.get(serviceUrl);		
		if(service != null)
		{
			response = service.execute(invocation);
			if(response != null && response.getStatus() == null)
			{
				response.setStatus(InvocationResponse.STATUS_SUCCESS);
			}
		}
		
		return response;
	}
}
