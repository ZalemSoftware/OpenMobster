/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.processor;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.core.common.XMLUtilities;

import org.openmobster.core.mobileContainer.MobileContainer;
import org.openmobster.core.mobileContainer.Invocation;
import org.openmobster.core.mobileContainer.InvocationResponse;


/**
 * 
 * @author openmobster@gmail.com
 */
public class ServiceInvocationProcessor implements Processor 
{	
	private static Logger log = Logger.getLogger(ServiceInvocationProcessor.class);
		
	private String id = "mobileservice";
	private MobileContainer mobileContainer;
	
	
	public ServiceInvocationProcessor()
	{
		
	}
		
	public void start()
	{		
	}
	
	
	public void stop()
	{
		
	}
		
	public MobileContainer getMobileContainer() 
	{
		return mobileContainer;
	}

	public void setMobileContainer(MobileContainer mobileContainer) 
	{
		this.mobileContainer = mobileContainer;
	}
	
	public String getId() 
	{
		return this.id;
	}
	
	public String process(Input input) throws ProcessorException 
	{					
		try
		{
			String payload = input.getMessage().trim();
			
			//Make a ServiceInvocation into the RestFul Server			
			Request serviceRequest = this.parseServiceRequest(payload);
						
			Invocation invocation = Invocation.getInstance();
			invocation.setServiceUrl("/service/invoke");
			invocation.setServiceRequest(serviceRequest);
			
			InvocationResponse response = this.mobileContainer.invoke(invocation);
			Response serviceResponse = response.getServiceResponse();
			
			if(!response.getStatus().trim().equals(InvocationResponse.STATUS_SUCCESS))
			{
				throw new ProcessorException("MobileServiceBean Invocation Status="+response.getStatus());
			}
			
			//If an exception occurred during service invocation, make sure the root tx is rolledback
			if(serviceResponse.getStatusCode().equals("500"))
			{
				//an exception occurred during service invocation
				input.getSession().setAttribute("tx-rollback", Boolean.TRUE);
			}
			
			return this.prepareServiceResponse(serviceResponse);
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new ProcessorException(e);
		}		
	}	
	//---------------------------------------------------------------------------------------------------
	private Request parseServiceRequest(String payload)
	{
		Request beanRequest = null;
		
		Map<String, String> requestAttributes = (Map<String, String>)XMLUtilities.unmarshal(payload);
		String serviceName = requestAttributes.get("servicename");
		if(serviceName == null || serviceName.trim().length() == 0)
		{
			throw new RuntimeException("InvocationException: MobileBeanService not provided!!!");
		}
		requestAttributes.remove("servicename");
		
		beanRequest = new Request(serviceName);			
		Set<String> names = requestAttributes.keySet();
		for(String name: names)
		{
			beanRequest.setAttribute(name, requestAttributes.get(name));
		}
		
		return beanRequest;
	}
	
	private String prepareServiceResponse(Response beanResponse)
	{
		Map<String, String> responseAttributes = new HashMap<String, String>();
		
		if(beanResponse != null)
		{
			String[] cour = beanResponse.getNames();
			if(cour != null)
			{
				for(String name: cour)
				{
					responseAttributes.put(name, beanResponse.getAttribute(name));
				}
			}
		}
		
		return XMLUtilities.marshal(responseAttributes);
	}
}
