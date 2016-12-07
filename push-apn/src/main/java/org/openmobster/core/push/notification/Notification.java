/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.push.notification;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

import org.openmobster.core.security.device.Device;

/**
 * Encapsulates the Notification meta data to be processed by the Notifier. Notification Object is used by
 * System components that would like to send Notifications to devices via the Notifier Service
 * 
 * @author openmobster@gmail.com
 */
public class Notification implements Serializable 
{
	private static final long serialVersionUID = -8754021735988478805L;
	
	/**
	 * Type of Notification 
	 */
	private NotificationType type;
	
	private Map<String, Object> metaData;
	
	public Notification(NotificationType type)
	{
		this.type = type;
		this.metaData = new HashMap<String, Object>();
	}
	
	public NotificationType getType()
	{
		return this.type;
	}
	
	public void setMetaData(String name, Object value)
	{
		if(name == null || name.trim().length() == 0)
		{
			throw new IllegalArgumentException("Name cannot be empty");
		}
		if(value == null)
		{
			throw new IllegalArgumentException("Value cannot be Null");
		}
		
		this.metaData.put(name, value);
	}
	
	public Object getMetaData(String name)
	{
		return this.metaData.get(name);
	}
	
	public String getMetaDataAsString(String name)
	{
		return (String)this.metaData.get(name);
	}
	
	public static Notification createSyncNotification(Device device, String service)
	{
		Notification syncNotification = new Notification(NotificationType.SYNC);
		
		syncNotification.setMetaData(Constants.device, device.getIdentifier());
		syncNotification.setMetaData(Constants.service, service);		
		
		return syncNotification;
	}
	
	public static Notification createSyncNotification(String deviceIdentifier, String service)
	{
		Notification syncNotification = new Notification(NotificationType.SYNC);
		
		syncNotification.setMetaData(Constants.device, deviceIdentifier);
		syncNotification.setMetaData(Constants.service, service);		
		
		return syncNotification;
	}
	
	public static Notification createSilentSyncNotification(String deviceIdentifier, String service)
	{
		Notification syncNotification = new Notification(NotificationType.SYNC);
		
		syncNotification.setMetaData(Constants.device, deviceIdentifier);
		syncNotification.setMetaData(Constants.service, service);
		syncNotification.setMetaData(Constants.silent, new Boolean(true));
		
		return syncNotification;
	}
	
	public static Notification createPushRPCNotification(Device device, String service)
	{
		Notification notification = new Notification(NotificationType.RPC);
		
		notification.setMetaData(Constants.device, device.getIdentifier());	
		notification.setMetaData(Constants.service, service);
		
		return notification;
	}
	
	public static Notification createPushNotification(Device device, String message, Map<String,String> extras)
	{
		if(extras == null || extras.isEmpty())
		{
			new IllegalArgumentException("Extras cannot be null");
		}
		Notification notification = new Notification(NotificationType.PUSH);
		
		notification.setMetaData(Constants.device, device.getIdentifier());
		notification.setMetaData(Constants.message,message);
		notification.setMetaData(Constants.extras,extras);
		
		return notification;
	}
	
	public static Notification createDeviceManagementNotification(Device device, String action)
	{
		if(action == null || action.trim().length()==0)
		{
			new IllegalArgumentException("Action cannot be null");
		}
		Notification notification = new Notification(NotificationType.DM);
		
		notification.setMetaData(Constants.device, device.getIdentifier());
		notification.setMetaData(Constants.action,action);
		
		return notification;
	}
	
	public static Notification createD2DNotification(Device device, Map<String,String> d2dMessage)
	{
		String from = d2dMessage.get(Constants.from);
		String to = d2dMessage.get(Constants.to);
		String message = d2dMessage.get(Constants.message);
		String source_deviceid = d2dMessage.get(Constants.source_deviceid);
		String timestamp = d2dMessage.get(Constants.timestamp);
		String app_id = d2dMessage.get(Constants.app_id);
		
		//Validate
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
		if(app_id == null || app_id.trim().length()==0)
		{
			throw new IllegalArgumentException("App Id is required");
		}
		
		Notification notification = new Notification(NotificationType.D2D);
		
		notification.setMetaData(Constants.device, device.getIdentifier());
		notification.setMetaData(Constants.d2dMessage, d2dMessage);
		
		return notification;
	}
}
