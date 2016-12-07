/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileContainer;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.camera.CameraCommandContext;
import org.openmobster.cloud.api.camera.CameraCommandResponse;
import org.openmobster.cloud.api.camera.CloudCameraCommand;
import org.openmobster.cloud.api.camera.CloudCameraURI;
import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;


/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/cloud/camera")
public class CloudCameraService implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(CloudCameraService.class);
	private Map<String, CloudCameraCommand> registry;
	
	public CloudCameraService()
	{
		this.registry = new HashMap<String, CloudCameraCommand>();
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void register(CloudCameraCommand cameraCommand)
	{
		Class cameraCommandClazz = cameraCommand.getClass();
		
		CloudCameraURI annotation = (CloudCameraURI)cameraCommandClazz.getAnnotation(CloudCameraURI.class);
		String uri = annotation.uri();
		
		log.debug("**********************************");
		log.debug("Camera Command: "+uri+" regsitered");
		log.debug("**********************************");
		
		this.registry.put(uri, cameraCommand);
	}
	
	public Response invoke(Request request) 
	{
		Response response = new Response();
		
		String cameraCommandUri = request.getAttribute("command");
		if(cameraCommandUri == null || cameraCommandUri.trim().length()==0)
		{
			return response;
		}
		
		CloudCameraCommand cameraCommand = this.registry.get(cameraCommandUri);
		if(cameraCommand == null)
		{
			throw new RuntimeException("Camera Command Not Found for: "+cameraCommandUri);
		}
		
		CameraCommandContext commandContext = new CameraCommandContext(cameraCommandUri);
		
		//Populate the CommandContext using the request object
		String[] names = request.getNames();
		for(String name:names)
		{
			String value = request.getAttribute(name);
			commandContext.setAttribute(name, value);
		}
		
		CameraCommandResponse cameraResponse = cameraCommand.invoke(commandContext);
		
		//Populate the Response object using the one returned by the command
		if(cameraResponse != null)
		{
			names = cameraResponse.getNames();
			for(String name:names)
			{
				String value = cameraResponse.getAttribute(name);
				response.setAttribute(name, value);
			}
		}
		
		return response;
	}
}
