/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.agent.provisioning;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.AndroidPushApp;
import org.openmobster.core.security.device.AndroidPushAppController;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="android_push_callback")
public class AndroidPushCallback implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(AndroidPushCallback.class);
	
	private DeviceController deviceController;
	private AndroidPushAppController pushAppController;
	
	public AndroidPushCallback()
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
	
	public AndroidPushAppController getPushAppController()
	{
		return pushAppController;
	}

	public void setPushAppController(AndroidPushAppController pushAppController)
	{
		this.pushAppController = pushAppController;
	}

	public void start()
	{
		log.info("-----------------------------------------------------------");
		log.info("Android Push Callback successfully started............");
		log.info("-----------------------------------------------------------");				
	}
	
	public Response invoke(Request request) 
	{
		Response response = new Response();
		try
		{
			String appId = request.getAttribute("app-id");
			
			log.debug("AndroidPushCallback--------------------------");
			log.debug("AppId: "+appId);
			log.debug("--------------------------------------------");
			
			//Handle the PushApp
			AndroidPushApp pushApp = this.pushAppController.readPushApp(appId);
			if(pushApp == null)
			{
				pushApp = new AndroidPushApp();
				pushApp.setAppId(appId);
				
				this.pushAppController.create(pushApp);
			}
			else
			{
				pushApp.setAppId(appId);
				this.pushAppController.update(pushApp);
			}
			
			return response;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
			throw new SystemException(e.getMessage(), e);
		}
	}
}
