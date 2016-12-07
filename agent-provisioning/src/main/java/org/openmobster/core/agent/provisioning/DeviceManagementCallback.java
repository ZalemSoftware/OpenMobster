/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.agent.provisioning;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.DeviceAttribute;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="dm_callback")
public class DeviceManagementCallback implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(DeviceManagementCallback.class);
	
	private DeviceController deviceController;
	
	public DeviceManagementCallback()
	{
		
	}
			
	public DeviceController getDeviceController()
	{
		return deviceController;
	}



	public void setDeviceController(DeviceController deviceController)
	{
		this.deviceController = deviceController;
	}


	public void start()
	{
		log.info("-----------------------------------------------------------");
		log.info("Device Management Callback successfully started............");
		log.info("-----------------------------------------------------------");				
	}
	
	public Response invoke(Request request) 
	{
		Response response = new Response();
		try
		{
			String os = request.getAttribute("os");
			String version = request.getAttribute("version");
			
			Device device = ExecutionContext.getInstance().getDevice();
			String deviceId = device.getIdentifier();
			
			log.debug("--------------------------------------------");
			log.debug("DeviceId : "+deviceId);
			log.debug("Operating System: "+os);
			log.debug("Version: "+version);
			log.debug("--------------------------------------------");
			
			Device toBeUpdated = this.deviceController.read(deviceId);
			DeviceAttribute osAttribute = new DeviceAttribute("os",os);
			DeviceAttribute versionAttribute = new DeviceAttribute("version",version);
			toBeUpdated.updateAttribute(osAttribute);
			toBeUpdated.updateAttribute(versionAttribute);
			
			this.deviceController.update(toBeUpdated);
			
			return response;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
			throw new SystemException(e.getMessage(), e);
		}
	}
}
