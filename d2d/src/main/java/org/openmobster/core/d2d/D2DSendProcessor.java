/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.d2d;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.push.notification.Constants;
import org.openmobster.core.push.notification.Notification;
import org.openmobster.core.push.notification.Notifier;

/**
 *
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/d2d/send")
public final class D2DSendProcessor implements MobileServiceBean
{
	public static Logger log = Logger.getLogger(D2DSendProcessor.class);
	
	public void start()
	{
		log.info("********************************************************");
		log.info("Device-To-Device Send Processor successfully started....");
		log.info("********************************************************");
	}
	
	public void stop()
	{
		
	}
	
	@Override
	public Response invoke(Request request)
	{
		String from = request.getAttribute(Constants.from);
		String to = request.getAttribute(Constants.to);
		String message = request.getAttribute(Constants.message);
		String source_deviceid = request.getAttribute(Constants.source_deviceid);
		String destination_deviceid = request.getAttribute(Constants.destination_deviceid);
		String appId = request.getAttribute(Constants.app_id);
		String timestamp = ""+System.currentTimeMillis();
		
		//Validation
		if(from == null || from.trim().length()==0)
		{
			throw new IllegalArgumentException("From is required");
		}
		if(to == null || to.trim().length()==0)
		{
			throw new IllegalArgumentException("To is required");
		}
		if(message == null || message.trim().length()==0)
		{
			throw new IllegalArgumentException("Message is required");
		}
		if(source_deviceid == null || source_deviceid.trim().length()==0)
		{
			throw new IllegalArgumentException("Source DeviceId is required");
		}
		if(timestamp == null || timestamp.trim().length()==0)
		{
			throw new IllegalArgumentException("Timestamp is required");
		}
		if(appId == null || appId.trim().length()==0)
		{
			throw new IllegalArgumentException("App Id is required");
		}
		
		//Find the Device and send it to the device
		DeviceController deviceController = DeviceController.getInstance();
		Set<Device> devices = deviceController.readByIdentity(to);
		if(devices == null || devices.isEmpty())
		{
			return null;
		}
		
		for(Device device:devices)
		{
			if(destination_deviceid != null)
			{
				if(!device.getIdentifier().equals(destination_deviceid))
				{
					continue;
				}
			}
			
			Map<String,String> d2dMessage = new HashMap<String,String>();
			d2dMessage.put(Constants.from, from);
			d2dMessage.put(Constants.to, to);
			d2dMessage.put(Constants.message, message);
			d2dMessage.put(Constants.source_deviceid, source_deviceid);
			d2dMessage.put(Constants.destination_deviceid, destination_deviceid);
			d2dMessage.put(Constants.app_id, appId);
			d2dMessage.put(Constants.timestamp, timestamp);
			
			//Prepare the notification
			Notification notification = Notification.createD2DNotification(device, d2dMessage);
			
			//Send the notification
			Notifier.getInstance().process(notification);
		}
		
		return null;
	}
}
