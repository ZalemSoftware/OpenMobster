/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileContainer;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.core.services.MobileServiceMonitor;



/**
 * @author openmobster@gmail.com
 */
public class InvokeMobileService implements ContainerService
{
	private static Logger log = Logger.getLogger(InvokeMobileService.class);
	
	private String id;
	private MobileServiceMonitor monitor;
	
	public InvokeMobileService()
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
	
	public MobileServiceMonitor getMonitor() 
	{
		return monitor;
	}

	public void setMonitor(MobileServiceMonitor monitor) 
	{
		this.monitor = monitor;
	}
	//---------------------------------------------------------------------------------------------------
	public InvocationResponse execute(Invocation invocation) throws InvocationException
	{
		try
		{
			InvocationResponse response = InvocationResponse.getInstance();
			
			Request beanRequest = invocation.getServiceRequest();
			String serviceName = beanRequest.getService();			
			if(serviceName == null || serviceName.trim().length() == 0)
			{
				throw new RuntimeException("InvocationException: MobileBeanService not provided!!!");
			}
			
			MobileServiceBean mobileService = this.monitor.lookup(serviceName);
			if(mobileService != null)
			{
				Response beanResponse = null;
				try
				{
					beanResponse = mobileService.invoke(beanRequest);
					
					if(beanResponse == null)
					{
						beanResponse = new Response();
						beanResponse.setStatusCode("204");
						beanResponse.setStatusMsg("No Content");
					}
					else
					{
						beanResponse.setStatusCode("200");
						beanResponse.setStatusMsg("OK");
					}
					response.setServiceResponse(beanResponse);
				}
				catch(Throwable t)
				{
					beanResponse = new Response();
					
					beanResponse.setStatusCode("500");
					beanResponse.setStatusMsg("Internal Server Error: "+t.getMessage());
					
					response.setServiceResponse(beanResponse);
				}
			}
			else
			{
				throw new RuntimeException("InvocationException: "+serviceName+" MobileBeanService Not Found!!");
			}
			
			return response;
		}
		catch(Throwable t)
		{
			throw new InvocationException(t.getMessage(), t);
		}
	}
}
