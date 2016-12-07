/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services;

import java.util.List;
import java.util.ArrayList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBeanId;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.common.bus.Bus;
import org.openmobster.core.common.bus.BusListener;
import org.openmobster.core.common.bus.BusMessage;
import org.openmobster.core.services.channel.ChannelManager;
import org.openmobster.core.services.channel.ChannelRegistration;
import org.openmobster.core.services.channel.ChannelBeanMetaData;
import org.openmobster.core.services.event.ChannelEvent;
import org.openmobster.core.services.event.NetworkEvent;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.cluster.ClusterService;

/**
 * MobileObjectMonitor provides infrastructure/management services for the Mobile Object Framework
 * 
 * @author openmobster@gmail.com
 */
public class MobileObjectMonitor implements BusListener
{	
	private static Logger log = Logger.getLogger(MobileObjectMonitor.class);
	
	/**
	 * 
	 */
	private Map<String, Channel> registry = null;
	private Map<String, ChannelManager> channelManagers;
	private HibernateManager hibernateManager;
	private DeviceController deviceController;
	private ClusterService clusterService;
	
	
	public MobileObjectMonitor()
	{
		this.registry = new HashMap<String, Channel>(); 
		this.channelManagers = new HashMap<String, ChannelManager>();
	}
	
	
	public void start()
	{		
		log.info("--------------------------------------------------");
		log.info("Mobile Object Monitor succesfully started.........");
		log.info("--------------------------------------------------");				
	}
		
	public void stop()
	{
		this.registry = null;
		
		//Stop the ChannelDaemons for cleanliness
		if(this.channelManagers != null && !this.channelManagers.isEmpty())
		{
			Collection<ChannelManager> channelManagers =  this.channelManagers.values();
			for(ChannelManager channelManager: channelManagers)
			{
				channelManager.stop();
			}
		}
	}
		
	public HibernateManager getHibernateManager() 
	{
		return hibernateManager;
	}

	public void setHibernateManager(HibernateManager hibernateManager) 
	{
		this.hibernateManager = hibernateManager;
	}
	
		
	public DeviceController getDeviceController() 
	{
		return deviceController;
	}

	public void setDeviceController(DeviceController deviceController) 
	{
		this.deviceController = deviceController;
	}
	
	

	public ClusterService getClusterService()
	{
		return clusterService;
	}


	public void setClusterService(ClusterService clusterService)
	{
		this.clusterService = clusterService;
	}


	public void notify(Channel mobileObjectConnector)
	{
		try
		{
			Class connectorClazz = mobileObjectConnector.getClass();
			
			ChannelInfo connectorInfo = (ChannelInfo)connectorClazz.
			getAnnotation(ChannelInfo.class);
			String channelId = connectorInfo.uri();
			
			//Validate channelId to make sure it does not have '/'...This causes issues
			//with table names on the device side with sqlite database (Android and iPhone)
			if(channelId.indexOf('/') != -1)
			{
				log.error("-----------------------------------------------------");
				log.error("ChannelUri: "+channelId+" is invalid!!");
				log.error("-----------------------------------------------------");
				throw new IllegalStateException("A ChannelUri should not contain the '/' character!!");
			}
			
			//Inspect the Id field of its Record Object and make sure its an instance of String
			String dataObjectStr = connectorInfo.mobileBeanClass();
			Class dataObjectClazz = Thread.currentThread().getContextClassLoader().
			loadClass(dataObjectStr);
			
			//Search for the Id field
			Field[] declaredFields = dataObjectClazz.getDeclaredFields();			
			for(Field field: declaredFields)
			{		
				Annotation id = field.getAnnotation(MobileBeanId.class);
				if(id != null)
				{
					Class type = field.getType();
					if(type != String.class)
					{
						log.error("Record Id must be of type <String> only!!!");
						log.error(mobileObjectConnector+" cannot be registered!!!!");
						throw new IllegalStateException("Record Id must be of type <String> only!!!");					
					}
				}
			}
			
			this.registry.put(channelId, mobileObjectConnector);	
			
			ChannelRegistration registration = new ChannelRegistration(channelId, mobileObjectConnector);
			
			//Get the Channel Update Interval
			registration.setUpdateCheckInterval(connectorInfo.updateCheckInterval());
			
			ChannelManager channelManager = ChannelManager.createInstance(this.hibernateManager,
			this.deviceController,registration,this.clusterService);			
			channelManager.start();
			Bus.addBusListener(channelId, this);
			this.channelManagers.put(channelId, channelManager);			
		}
		catch(ClassNotFoundException cne)
		{
			log.error(this, cne);
		}
	}
		
	public Channel lookup(String channelId)
	{
		return this.registry.get(channelId);
	}
	
	@Deprecated
	public Collection<Channel> getConnectors()
	{
		Collection<Channel> all = this.registry.values();
		return all;
	}
	//----BusListener implementation----------------------------------------------------------------------------
	public void messageIncoming(BusMessage busMessage) 
	{	
		String eventState = (String)busMessage.getAttribute(ChannelEvent.event);
		Object event = XMLUtilities.unmarshal(eventState);
		
		if(event instanceof ChannelEvent)
		{
			CometService cometService = CometService.getInstance();
			
			//Broadcast a Channel Event
			ChannelEvent channelEvent = (ChannelEvent)event;						
			cometService.broadcastChannelEvent(channelEvent);	
			
			//Broadcast a Network Event
			List<String> updatedChannels = new ArrayList<String>();
			updatedChannels.add(channelEvent.getChannel());
			
			NetworkEvent networkEvent = new NetworkEvent();			
			networkEvent.setUpdatedChannels(updatedChannels);
			
			List<ChannelBeanMetaData> channelMetaData = (List<ChannelBeanMetaData>)
			channelEvent.getAttribute(ChannelEvent.metadata);
			networkEvent.setAttribute(ChannelEvent.metadata, channelMetaData);
			
			cometService.broadcastNetworkEvent(networkEvent);
		}
		
		//If I get here go ahead and acknowledge the message
		busMessage.acknowledge();
	}
}
