/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud.api.push;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.openmobster.core.push.notification.Notification;
import org.openmobster.core.push.notification.Notifier;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;

import org.openmobster.core.common.transaction.TransactionHelper;

/**
 * PushService provides a cross platform API for pushing notifications to devices.
 *
 * @author openmobster@gmail.com
 */
public final class PushService
{
	private static PushService singleton;
	
	private PushService()
	{
		
	}
	
	/**
	 * Get an instance of the Push Service
	 * 
	 * @return an instance of the Push Service
	 */
	public static PushService getInstance()
	{
		if(PushService.singleton == null)
		{
			synchronized(PushService.class)
			{
				if(PushService.singleton == null)
				{
					PushService.singleton = new PushService();
				}
			}
		}
		return PushService.singleton;
	}
	
	/**
	 * A device agnostic Push method. Push is associated with the user and not his operating system
	 * 
	 * @param identity user that must receive this message
	 * @param appId unique application id this push is associated with
	 * @param message message to be sent
	 * @param title title of the message
	 * @param details any other details associated with the message
	 */
	public void push(String identity, String appId, String message, String title, String details)
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			//Validation
			if(message == null || message.trim().length()==0)
			{
				throw new IllegalArgumentException("Message is Required!!");
			}
			if(appId == null || appId.trim().length()==0)
			{
				throw new IllegalArgumentException("App Id is Required!!");
			}
			if(identity == null || identity.trim().length() == 0)
			{
				throw new IllegalArgumentException("Identity is Required!!");
			}
			
			//Detect the device that will receive the push
			DeviceController deviceController = DeviceController.getInstance();
			Set<Device> devices = deviceController.readByIdentity(identity);
			if(devices == null || devices.isEmpty())
			{
				return;
			}
			
			for(Device device:devices)
			{	
				//Prepare the extras
				Map<String,String> extras = new HashMap<String,String>();
				if(appId != null && appId.trim().length()>0)
				{
					extras.put("app-id", appId);
				}
				if(title != null && title.trim().length()>0)
				{
					extras.put("title", title);
				}
				if(details != null && details.trim().length()>0)
				{
					extras.put("detail", details);
				}
				
				//Prepare the Notification
				Notification notification = Notification.createPushNotification(device, message, extras);
				
				//Send the notification
				Notifier notifier = Notifier.getInstance();
				notifier.process(notification);
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
		}
	}
}
