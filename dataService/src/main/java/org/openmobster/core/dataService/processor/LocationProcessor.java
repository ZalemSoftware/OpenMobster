/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.processor;

import org.apache.log4j.Logger;

import org.openmobster.core.mobileContainer.Invocation;
import org.openmobster.core.mobileContainer.InvocationResponse;
import org.openmobster.core.mobileContainer.MobileContainer;

import org.openmobster.cloud.api.location.LocationContext;
import org.openmobster.cloud.api.location.Response;
import org.openmobster.cloud.api.location.Request;
import org.openmobster.cloud.api.ExecutionContext;

import org.openmobster.core.location.PayloadHandler;

/**
 *
 * @author openmobster@gmail.com
 */
public class LocationProcessor implements Processor
{
	private static Logger log = Logger.getLogger(LocationProcessor.class);
	
	private String id;
	private MobileContainer mobileContainer;
	private PayloadHandler payloadHandler;
	
	public LocationProcessor()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	
	public MobileContainer getMobileContainer()
	{
		return mobileContainer;
	}

	public void setMobileContainer(MobileContainer mobileContainer)
	{
		this.mobileContainer = mobileContainer;
	}
	
	
	public PayloadHandler getPayloadHandler()
	{
		return payloadHandler;
	}

	public void setPayloadHandler(PayloadHandler payloadHandler)
	{
		this.payloadHandler = payloadHandler;
	}
	//----------------------------------------------------------------------------------------------------------
	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public String process(Input input) throws ProcessorException
	{
		try
		{
			String payload = input.getMessage().trim();
			Request locationRequest = this.parseRequest(payload);
			
			Invocation invocation = Invocation.getInstance();
			invocation.setServiceUrl("/service/location/invoke");
			invocation.setLocationRequest(locationRequest);
			
			InvocationResponse response = this.mobileContainer.invoke(invocation);
			if(response == null)
			{
				throw new ProcessorException("LocationServiceBean Invocation Failure");
			}
			if(!response.getStatus().trim().equals(InvocationResponse.STATUS_SUCCESS))
			{
				throw new ProcessorException("LocationServiceBean Invocation Status="+response.getStatus());
			}
			
			//JSON-ify the response
			Response locationResponse = response.getLocationResponse();
			String jsonResponse = this.prepareResponse(locationResponse);
			
			return jsonResponse;
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new ProcessorException(e);
		}
	}
	
	private Request parseRequest(String payload)
	{
		LocationContext locationContext = payloadHandler.deserializeRequest(payload);
		Request request = (Request)locationContext.getAttribute("request");
		
		//Start the LocationContext for this request
		ExecutionContext exe = ExecutionContext.getInstance();
		exe.setLocationContext(locationContext);
		
		return request;
	}
	
	private String prepareResponse(Response response)
	{
		LocationContext locationContext = ExecutionContext.getInstance().getLocationContext();
		locationContext.setAttribute("response", response);
		
		//serialize
		String xml = this.payloadHandler.serializeResponse(locationContext);
		
		return xml;
	}
	//----------------------------------------------------------------------------------------------
}
