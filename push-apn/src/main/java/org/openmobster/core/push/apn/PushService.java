/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.push.apn;

import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.PayLoad;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.bus.BusMessage;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.PushApp;
import org.openmobster.core.security.device.PushAppController;
import org.openmobster.core.push.notification.Constants;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class PushService
{
	private static Logger log = Logger.getLogger(PushService.class);
	
	private DeviceController deviceController;
	private PushAppController pushAppController;
	private Map<String, PushNotificationManager> connections;
	private String host;
	private int port;
	
	public PushService()
	{
		this.connections = new HashMap<String, PushNotificationManager>();
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
	

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void start()
	{
		log.info("*****************************************");
		log.info("iPhone Push Service successfully started");
		log.info("*****************************************");
	}
	
	public void stop()
	{
		try
		{
			Set<String> appIds = this.connections.keySet();
			for(String local:appIds)
			{
				PushNotificationManager connection = this.connections.get(local);
				connection.stopConnection();
			}
		}
		catch(Throwable t)
		{
			log.error(this, t);
		}
	}
	
	public static PushService getInstance()
	{
		return (PushService)ServiceManager.locate("org.openmobster.core.push.apn.PushService");
	}
	
	public void push(BusMessage busMessage)
	{
		try
		{
			String deviceId = busMessage.getBusUri();
			String notificationType = (String)busMessage.getAttribute("notification-type");
			
			if(notificationType.equals("channel"))
			{
				String channel = busMessage.getSenderUri();
				String command = (String)busMessage.getAttribute(Constants.command);
				
				//Make sure this not a silent event, in which do not notify
				if(command != null && command.contains(Constants.silent))
				{
					return;
				}
				
				this.sendSyncNotification(deviceId, channel);
			}
			else if(notificationType.equals("push"))
			{
				String message = (String)busMessage.getAttribute("message");
				Map<String,String> extras = (Map<String,String>)busMessage.getAttribute("extras");
				this.sendPushNotification(deviceId, message, extras);
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
	private void sendSyncNotification(String deviceId, String channel) throws Exception
	{
		//Get the Device Token of the device that should receive the push notification
		Device device = this.deviceController.read(deviceId);
		
		//Find PushApps on this device
		List<PushApp> pushApps = this.findDevicePushApps(deviceId);
		
		//Remove Apps that are not interested in this channel/notification
		this.disqualifyApps(pushApps, channel);
		
		//actually send the push
		this.push(device, pushApps, "You have new information", null);
	}
	
	private void sendPushNotification(String deviceId,String message, Map<String,String> extras) throws Exception
	{
		//Get the Device Token of the device that should receive the push notification
		Device device = this.deviceController.read(deviceId);
		
		List<PushApp> deviceApps = this.findDevicePushApps(deviceId);
		
		//Find the application where this message should be pushed
		String appId = extras.get("app-id");
		PushApp app = null;
		if(deviceApps != null && !deviceApps.isEmpty())
		{
			for(PushApp local:deviceApps)
			{
				if(local.getAppId().equals(appId))
				{
					app = local;
					break;
				}
			}
		}
		if(app == null)
		{
			return;
		}
		
		//Send a Push Message to the App
		List<PushApp> apps = new ArrayList<PushApp>();
		apps.add(app);
		
		this.push(device, apps, message, extras);
	}
	
	private List<PushApp> findDevicePushApps(String deviceId)
	{
		List<PushApp> apps = new ArrayList<PushApp>();
		
		List<PushApp> all = this.pushAppController.readAll();
		for(PushApp local:all)
		{
			boolean deviceFound = local.getDevices().contains(deviceId);
			if(deviceFound)
			{
				apps.add(local);
			}
		}
		
		return apps;
	}
	
	private void disqualifyApps(List<PushApp> apps, String channel)
	{
		List<PushApp> remove = new ArrayList<PushApp>();
		for(PushApp local:apps)
		{
			if(!local.getChannels().contains(channel))
			{
				remove.add(local);
			}
		}
		apps.removeAll(remove);
	}
	
	private void push(Device device,List<PushApp> apps, String message, Map<String,String> extras) throws Exception
	{
		if(apps != null && !apps.isEmpty())
		{
			for(PushApp local:apps)
			{
				//Setup Device for the notification
				PushNotificationManager pushNotificationManager = this.findConnection(local);
				javapns.data.Device pushDevice = null;
				try
				{
					pushDevice = pushNotificationManager.getDevice(device.getIdentifier());
				}
				catch(Throwable t)
				{
					pushNotificationManager.addDevice(device.getIdentifier(), device.getDeviceToken());
					pushDevice = pushNotificationManager.getDevice(device.getIdentifier());
				}
				
				// Setup up a simple message
	            PayLoad aPayload = new PayLoad();
	            aPayload.addBadge(0);
	            aPayload.addAlert(message);
	            aPayload.addSound("default");
	            if(extras != null && !extras.isEmpty())
	            {
	            	Set<String> names = extras.keySet();
	            	for(String name:names)
	            	{
	            		if(name.equals("app-id"))
	            		{
	            			continue;
	            		}
	            		String value = extras.get(name);
	            		aPayload.addCustomDictionary(name, value);
	            	}
	            }
	            
	            pushNotificationManager.sendNotification(pushDevice, aPayload);
			}
		}
	}
	
	private PushNotificationManager findConnection(PushApp app) throws Exception
	{
		PushNotificationManager connection = this.connections.get(app.getAppId());
		if(connection == null)
		{
			connection = PushNotificationManager.getInstance();
			this.connections.put(app.getAppId(), connection);
			
			//Connect
			ByteArrayInputStream certStream = null;
			try
			{
				certStream = new ByteArrayInputStream(app.getCertificate());
				String certPassword = app.getCertificatePassword();
				connection.initializeConnection(this.host, this.port, 
            		certStream, certPassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
			}
			finally
			{
				if(certStream != null)
				{
					certStream.close();
				}
			}
		}
		return connection;
	}
}
