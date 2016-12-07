/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.location;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.module.connection.NetSession;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.android.service.Registry;

import org.openmobster.api.service.location.PayloadHandler;

import android.content.Context;

/**
 *
 * @author openmobster@gmail.com
 */
public final class LocationService
{
	public static LocationContext invoke(Request request,LocationContext locationContext) 
		throws LocationServiceException
	{
		if(request == null || locationContext == null)
		{
			throw new IllegalStateException("LocationService Validation Failure");
		}
		
		//LocationContext Validation
		boolean coordinatesMissing = false;
		String latitude = locationContext.getLatitude();
		String longitude = locationContext.getLongitude();
		if(latitude == null || latitude.trim().length()==0 || longitude ==null || longitude.trim().length()==0)
		{
			coordinatesMissing = true;
		}
		
		boolean addressMissing = false;
		if(locationContext.getAddress() == null)
		{
			addressMissing = true;
		}
		
		boolean referenceMissing = false;
		String reference = locationContext.getPlaceReference();
		if(reference == null || reference.trim().length()==0)
		{
			referenceMissing = true;
		}
		
		if(coordinatesMissing && addressMissing && referenceMissing)
		{
			throw new IllegalStateException("LocationContext Validation Failure");
		}
		
		//Go ahead and make the invocation
		NetSession session = null;
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Configuration configuration = Configuration.getInstance(context);
			boolean secure = configuration.isSSLActivated();
			session = NetworkConnector.getInstance().openSession(secure);
			
			String sessionInitPayload = null;
			if(configuration.isActive())
			{
				String deviceId = configuration.getDeviceId();
				String authHash = configuration.getAuthenticationHash();
				sessionInitPayload = 
					"<request>" +
						"<header>" +
							"<name>device-id</name>"+
							"<value><![CDATA["+deviceId+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>nonce</name>"+
							"<value><![CDATA["+authHash+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>processor</name>"+
							"<value>org.openmobster.core.dataService.processor.LocationProcessor</value>"+
						"</header>";
							
						/*
						 * Estrutura adicionada na versão 2.4-M3.1
						 * Se há um token de autenticação sendo utilizado atualmente, envia-o para o servidor. 
						 */
						if (configuration.getAuthenticationToken() != null) {
							sessionInitPayload +=
								"<header>" +
									"<name>authToken</name>" +
									"<value><![CDATA[" + configuration.getAuthenticationToken() + "]]></value>" +
								"</header>";
						}
						
						sessionInitPayload +=
					"</request>";
			}
			else
			{
				sessionInitPayload = 
					"<request>" +
						"<header>" +
							"<name>processor</name>"+
							"<value>org.openmobster.core.dataService.processor.LocationProcessor</value>"+
						"</header>"+
					"</request>";
			}
			
			String response = session.sendTwoWay(sessionInitPayload);
			
			LocationContext responseContext = null;
			if(response.indexOf("status=200")!=-1)
			{
				PayloadHandler payloadHandler = PayloadHandler.getInstance();
				locationContext.setAttribute("request", request);
				
				String xml = payloadHandler.serializeRequest(locationContext);
				
				response = session.sendPayloadTwoWay(xml);
				
				responseContext = payloadHandler.deserializeResponse(response);
			}
			
			return responseContext;
		}	
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			LocationServiceException exception = new LocationServiceException(LocationService.class.getName(),"invoke", new Object[]{
				"Message: "+e.getMessage(),
				"ToString: "+e.toString()
			});
			throw exception;
		}
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}
	
	public static Place getPlaceDetails(String placeReference) 
	throws LocationServiceException
	{
		LocationContext locationContext = new LocationContext();
		locationContext.setPlaceReference(placeReference);
		
		Request request = new Request("placeDetails");
		
		LocationContext responseContext = LocationService.invoke(request, locationContext);
		
		Place placeDetails = responseContext.getPlaceDetails();
		
		return placeDetails;
	}
}
