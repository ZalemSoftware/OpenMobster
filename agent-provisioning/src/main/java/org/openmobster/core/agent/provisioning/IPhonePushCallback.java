/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.agent.provisioning;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

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
import org.openmobster.core.security.device.PushApp;
import org.openmobster.core.security.device.PushAppController;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="iphone_push_callback")
public class IPhonePushCallback implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(IPhonePushCallback.class);
	
	private DeviceController deviceController;
	private PushAppController pushAppController;
	
	public IPhonePushCallback()
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
	
	public PushAppController getPushAppController()
	{
		return pushAppController;
	}

	public void setPushAppController(PushAppController pushAppController)
	{
		this.pushAppController = pushAppController;
	}

	public void start()
	{
		log.info("-----------------------------------------------------------");
		log.info("IPhone Push Callback successfully started............");
		log.info("-----------------------------------------------------------");				
	}
	
	public Response invoke(Request request) 
	{
		Response response = new Response();
		try
		{
			Device device = ExecutionContext.getInstance().getDevice();
			String os = request.getAttribute("os");
			String deviceToken = request.getAttribute("deviceToken");
			String appId = request.getAttribute("appId");
			List<String> channels = request.getListAttribute("channels");
			Set<String> storedChannels = new HashSet<String>();
			if(channels != null && !channels.isEmpty())
			{
				storedChannels.addAll(channels);
			}
			
			log.debug("IPhonePushCallback--------------------------");
			log.debug("OS: "+os);
			log.debug("DeviceToken: "+deviceToken);
			log.debug("AppId: "+appId);
			if(channels != null)
			{
				for(String channel:channels)
				{
					log.debug("Channel: "+channel);
				}
			}
			log.debug("--------------------------------------------");
			
			//Handle the PushApp
			PushApp pushApp = this.pushAppController.readPushApp(appId);
			if(pushApp == null)
			{
				pushApp = new PushApp();
				pushApp.setAppId(appId);
				pushApp.setChannels(storedChannels);
				pushApp.addDevice(device.getIdentifier());
				
				this.pushAppController.create(pushApp);
			}
			else
			{
				pushApp.setAppId(appId);
				
				pushApp.addDevice(device.getIdentifier());
				
				if(storedChannels != null && !storedChannels.isEmpty())
				{
					for(String channel:storedChannels)
					{
						pushApp.addChannel(channel);
					}
				}
				else
				{
					pushApp.setChannels(null);
				}
				
				this.pushAppController.update(pushApp);
			}
			
			
			//Handle DeviceToken
			DeviceAttribute tokenAttr = new DeviceAttribute("device-token",deviceToken);
			device.updateAttribute(tokenAttr);
			this.deviceController.update(device);
			
			return response;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
			throw new SystemException(e.getMessage(), e);
		}
	}
}
