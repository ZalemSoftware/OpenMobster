/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.push.notification;

import java.util.Map;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import org.openmobster.core.common.bus.Bus;
import org.openmobster.core.common.bus.BusMessage;

import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.XMLUtilities;

/**
 * This component processes all notifications/updates that must be sent to actively connected devices
 * 
 * @author openmobster@gmail.com
 */
public class Notifier 
{
	private static Logger log = Logger.getLogger(Notifier.class);
	
	private DeviceController deviceController;
	
	
	public Notifier()
	{
		
	}
	
	public static Notifier getInstance()
	{
		return (Notifier)ServiceManager.locate("org.openmobster.core.push.notification.Notifier");
	}
	
	public void start()
	{
		log.info("---------------------------------------");
		log.info("Notifier successfully started.........");
		log.info("---------------------------------------");
	}
	
	public void stop()
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

	public void process(Notification notification)
	{
		String deviceToNotify = null;
		String command = null;
		
		if(notification.getType() == NotificationType.SYNC)
		{
			log.debug("Sync Notification for "+notification.getMetaDataAsString(Constants.device));
			
			StringBuilder commandBuilder = new StringBuilder();
			
			try
			{
				commandBuilder.append(Constants.command+"="+Constants.sync+Constants.separator);
				commandBuilder.append(Constants.service+"="+URLEncoder.encode(notification.getMetaDataAsString(Constants.service)
						,"UTF-8"));
				Boolean isSilent = (Boolean)notification.getMetaData(Constants.silent);
				if(isSilent != null && isSilent)
				{
					commandBuilder.append(Constants.separator+Constants.silent+"=true");
				}
				
				command = commandBuilder.toString()+Constants.endOfCommand;
			}
			catch(Exception e)
			{
				log.error(this, e);
				throw new RuntimeException(e);
			}
			deviceToNotify = notification.getMetaDataAsString(Constants.device);
			
			if(deviceToNotify != null && command != null)
			{
				Device device = this.deviceController.read(deviceToNotify);
				String os = device.getOs();
				
				if(os != null)
				{
					BusMessage busMessage = new BusMessage();
					busMessage.setBusUri(deviceToNotify);
					busMessage.setSenderUri(notification.getMetaDataAsString(Constants.service));
					busMessage.setAttribute(Constants.command, command);
					busMessage.setAttribute(Constants.notification_type, Constants.channel);
					busMessage.setAttribute(Constants.os, os);
					
					Bus.sendMessage(busMessage);
				}
			}
		}
		//Deprecated....Used to be an experimental feature
		/*else if(notification.getType() == NotificationType.RPC)
		{
			log.debug("PushRPC Notification for "+notification.getMetaDataAsString(Constants.device));
			
			String rpc_request = notification.getMetaDataAsString("rpc-request");
			StringBuilder commandBuilder = new StringBuilder();
			commandBuilder.append(Constants.command+"="+Constants.pushrpc+Constants.separator);	
			commandBuilder.append(Constants.payload+"="+rpc_request);
			
			command = commandBuilder.toString()+Constants.endOfCommand;
			deviceToNotify = notification.getMetaDataAsString(Constants.device);
			
			if(deviceToNotify != null && command != null)
			{
				Device device = this.deviceController.read(deviceToNotify);
				String os = device.getOs();
				
				if(os != null)
				{
					BusMessage busMessage = new BusMessage();
					busMessage.setBusUri(deviceToNotify);
					busMessage.setSenderUri(notification.getMetaDataAsString(Constants.service));
					busMessage.setAttribute(Constants.command, command);
					busMessage.setAttribute(Constants.notification_type, "push-rpc");
					busMessage.setAttribute(Constants.os, os);
					
					Bus.sendMessage(busMessage);
				}
			}
		}*/
		else if(notification.getType() == NotificationType.PUSH)
		{
			log.debug("Push Notification for "+notification.getMetaDataAsString(Constants.device));
			
			StringBuilder commandBuilder = new StringBuilder();
			
			try
			{
				commandBuilder.append(Constants.command+"="+Constants.push+Constants.separator);
				commandBuilder.append(Constants.message+"="+URLEncoder.
						encode(notification.getMetaDataAsString(Constants.message),"UTF-8"));
				Map<String,String> extras = (Map<String,String>)notification.getMetaData(Constants.extras);
				String extrasStr = XMLUtilities.marshal(extras);
				commandBuilder.append(Constants.separator+Constants.extras+"="+URLEncoder.encode(extrasStr,"UTF-8"));
				
				command = commandBuilder.toString()+Constants.endOfCommand;
			}
			catch(Exception e)
			{
				log.error(this, e);
				throw new RuntimeException(e);
			}
			
			deviceToNotify = notification.getMetaDataAsString(Constants.device);
			
			if(deviceToNotify != null && command != null)
			{
				Device device = this.deviceController.read(deviceToNotify);
				String os = device.getOs();
				
				if(os != null)
				{
					BusMessage busMessage = new BusMessage();
					busMessage.setBusUri(deviceToNotify);
					busMessage.setSenderUri(Constants.push);
					busMessage.setAttribute(Constants.command, command);
					busMessage.setAttribute(Constants.notification_type, Constants.push);
					busMessage.setAttribute(Constants.os, os);
					busMessage.setAttribute(Constants.message, notification.getMetaDataAsString(Constants.message));
					busMessage.setAttribute(Constants.extras, notification.getMetaData(Constants.extras));
					
					Bus.sendMessage(busMessage);
				}
			}
		}
		else if(notification.getType() == NotificationType.DM)
		{
			log.debug("DeviceManagment Notification for "+notification.getMetaDataAsString(Constants.device));
			
			StringBuilder commandBuilder = new StringBuilder();
			commandBuilder.append(Constants.command+"="+Constants.deviceManagement+Constants.separator);
			commandBuilder.append(Constants.action+"="+notification.getMetaDataAsString(Constants.action));
			
			command = commandBuilder.toString()+Constants.endOfCommand;
			deviceToNotify = notification.getMetaDataAsString(Constants.device);
			
			if(deviceToNotify != null && command != null)
			{
				Device device = this.deviceController.read(deviceToNotify);
				String os = device.getOs();
				
				if(os != null)
				{
					BusMessage busMessage = new BusMessage();
					busMessage.setBusUri(deviceToNotify);
					busMessage.setSenderUri(Constants.deviceManagement);
					busMessage.setAttribute(Constants.command, command);
					busMessage.setAttribute(Constants.notification_type, Constants.deviceManagement);
					busMessage.setAttribute(Constants.os, os);
					busMessage.setAttribute(Constants.action, notification.getMetaDataAsString(Constants.action));
					
					Bus.sendMessage(busMessage);
				}
			}
		}
		else if(notification.getType() == NotificationType.D2D)
		{
			log.debug("D2D Notification for "+notification.getMetaDataAsString(Constants.device));
			
			Map<String,String> d2dMessage = (Map<String,String>)notification.getMetaData(Constants.d2dMessage);
			String from = d2dMessage.get(Constants.from);
			String to = d2dMessage.get(Constants.to);
			String message = d2dMessage.get(Constants.message);
			String source_deviceid = d2dMessage.get(Constants.source_deviceid);
			String destination_deviceid = d2dMessage.get(Constants.destination_deviceid);
			String timestamp = d2dMessage.get(Constants.timestamp);
			String app_id = d2dMessage.get(Constants.app_id);
			
			StringBuilder commandBuilder = new StringBuilder();
			try
			{
				commandBuilder.append(Constants.command+"="+Constants.d2d+Constants.separator);
				commandBuilder.append(Constants.from+"="+URLEncoder.encode(from, "UTF-8")+Constants.separator);
				commandBuilder.append(Constants.to+"="+URLEncoder.encode(to, "UTF-8")+Constants.separator);
				commandBuilder.append(Constants.message+"="+URLEncoder.encode(message, "UTF-8")+Constants.separator);
				commandBuilder.append(Constants.source_deviceid+"="+URLEncoder.encode(source_deviceid, "UTF-8")+Constants.separator);
				if(destination_deviceid != null)
				{
					commandBuilder.append(Constants.destination_deviceid+"="+URLEncoder.encode(destination_deviceid, "UTF-8")+Constants.separator);
				}
				commandBuilder.append(Constants.timestamp+"="+URLEncoder.encode(timestamp, "UTF-8")+Constants.separator);
				commandBuilder.append(Constants.app_id+"="+URLEncoder.encode(app_id, "UTF-8"));
				
				command = commandBuilder.toString()+Constants.endOfCommand;
			}
			catch(Exception e)
			{
				log.error(this, e);
				throw new RuntimeException(e);
			}
			
			deviceToNotify = notification.getMetaDataAsString(Constants.device);
			
			if(deviceToNotify != null && command != null)
			{
				Device device = this.deviceController.read(deviceToNotify);
				String os = device.getOs();
				
				if(os != null)
				{
					BusMessage busMessage = new BusMessage();
					busMessage.setBusUri(deviceToNotify);
					busMessage.setSenderUri(Constants.d2d);
					busMessage.setAttribute(Constants.command, command);
					busMessage.setAttribute(Constants.notification_type, Constants.d2d);
					busMessage.setAttribute(Constants.os, os);
					
					Bus.sendMessage(busMessage);
				}
			}
		}
	}
}
