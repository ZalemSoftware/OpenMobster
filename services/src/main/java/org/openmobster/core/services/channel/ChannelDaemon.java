/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.channel;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.text.DateFormat;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.common.transaction.TransactionHelper;
import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.bus.Bus;
import org.openmobster.core.common.bus.BusMessage;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.event.Event;
import org.openmobster.core.common.event.EventListener;
import org.openmobster.core.common.event.EventManager;

import org.openmobster.core.services.event.ChannelEvent;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.identity.Identity;

import org.openmobster.core.cluster.ClusterService;
import org.openmobster.core.cluster.ClusterEvent;
import org.openmobster.core.cluster.ClusterListener;

/**
 * The ChannelDaemon monitors any new data updates on its respective Channel. When an Update is detected, 
 * it sends an Event about this to interested Subscribers
 * 
 * @author openmobster@gmail.com
 */
public final class ChannelDaemon implements EventListener,ClusterListener 
{
	private static Logger log = Logger.getLogger(ChannelDaemon.class);
	
	private Timer timer;
	private HibernateManager hibernateManager;
	private DeviceController deviceController;
	private List<Device> allDevices;
	private boolean isRegisteredForCacheInvalidationEvent;
	private Map<String,LastScanTimestamp> lastScanTimestamps;
	private ClusterService clusterService;
	
	/**
	 * The channel being monitored
	 */
	private ChannelRegistration channelRegistration;
	
	public ChannelDaemon(HibernateManager hibernateManager,
	DeviceController deviceController,
	ChannelRegistration channelRegistration,
	ClusterService clusterService)
	{
		this.channelRegistration = channelRegistration;
		this.hibernateManager = hibernateManager;
		this.deviceController = deviceController;
		this.lastScanTimestamps = new HashMap<String,LastScanTimestamp>();
		this.clusterService = clusterService;
	}
	
	public ChannelRegistration getChannelRegistration()
	{
		return this.channelRegistration;
	}

	public void start()
	{
		String channel = this.channelRegistration.getUri();
		Bus.startBus(channel);
		
		this.clusterService.register(this);
	}
	
	@Override
	public void startService(ClusterEvent event) throws Exception
	{
		//load the device cache
		this.loadDevices();
		
		//Start a background daemon timer that scans the channel for updates and generates
		//Channel Update Events
		this.timer = new Timer(this.getClass().getName(), true); //sets it as a daemon thread
		TimerTask checkForUpdates = new CheckForUpdates(this.hibernateManager,
		this.deviceController,this.channelRegistration);
				
		long startDelay = 5000;
		long howOftenShouldICheck = channelRegistration.getUpdateCheckInterval();
		this.timer.schedule(checkForUpdates, startDelay, howOftenShouldICheck);
		
		log.info("-----------------------------------------------------");
		log.info("Channel Daemon ("+this.channelRegistration.getUri()+") started. Update Interval: "+howOftenShouldICheck+"(ms)");
		log.info("-----------------------------------------------------");
	}	
	
	public void stop()
	{
		String channel = this.channelRegistration.getUri();
		
		this.timer.cancel();
		this.timer.purge();
		Bus.stopBus(channel);
	}
	
	@Override
	public void onEvent(Event event)
	{
		Device device = (Device)event.getAttribute("new-device");
		if(device != null)
		{
			log.debug("***************************************************************");
			log.debug("Updating the device cache with a new device: "+this.channelRegistration.getUri());
			log.debug("***************************************************************");
			
			//add this device to the cache
			device = this.deviceController.read(device.getIdentifier());
			this.allDevices.add(device);
		}
		else
		{
			//Check for a device-cache-invalidation event
			Boolean invalidateDeviceCache = (Boolean)event.getAttribute("invalidate-device-cache");
			if(invalidateDeviceCache != null)
			{
				//A device cache invalidation event received
				log.debug("***************************************************************");
				log.debug("Invalidating the device cache: "+this.channelRegistration.getUri());
				log.debug("***************************************************************");
		
				this.loadDevices();
			}
		}
	}
	
	private void loadDevices()
	{
		boolean isStartedHere = TransactionHelper.startTx();
		try
		{
			this.allDevices = this.deviceController.readAll();
			
			if(allDevices != null && !allDevices.isEmpty())
			{
				List<Device> localCopy = new ArrayList<Device>();
				localCopy.addAll(allDevices);
				for(Device device:localCopy)
				{
					LastScanTimestamp lastScanTimestamp = this.findScanTimestamp(device);
					this.lastScanTimestamps.put(device.getIdentifier(), lastScanTimestamp);
				}
			}
			
			if(isStartedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(isStartedHere)
			{
				TransactionHelper.rollbackTx();
			}
							
			ErrorHandler.getInstance().handle(e);
		}
	}
	
	private LastScanTimestamp findScanTimestamp(Device device) throws Exception
	{
		LastScanTimestamp scanTimestamp = null;
		
		scanTimestamp = this.read(this.channelRegistration.getUri(),device.getIdentifier());
		if(scanTimestamp == null)
		{
			scanTimestamp = new LastScanTimestamp();
			scanTimestamp.setTimestamp(new Date());
			scanTimestamp.setChannel(this.channelRegistration.getUri());
			scanTimestamp.setClientId(device.getIdentifier());
		}
		
		return scanTimestamp;
	}
	
	private LastScanTimestamp read(String channel, String clientId) throws Exception
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			LastScanTimestamp lastScanTimestamp = null;
											
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			lastScanTimestamp = (LastScanTimestamp)session.
			createQuery("from LastScanTimestamp where channel=? and clientId=?").
			setString(0, channel).
			setString(1, clientId).
			uniqueResult();				
			
			tx.commit();
			return lastScanTimestamp;
		}
		catch(Exception e)
		{
			if(tx != null)
			{
				tx.rollback();
			}
							
			throw e;
		}
	}
	
	private long save(LastScanTimestamp lastScanTimestamp) throws Exception
	{
		Session session = null;
		Transaction tx = null;
		try
		{	
			long id = 0;
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			if(lastScanTimestamp.getId() ==0)
			{
				id = (Long)session.save(lastScanTimestamp);
			}
			else
			{
				session.update(lastScanTimestamp);
				id = lastScanTimestamp.getId();
			}
			
			tx.commit();
			
			return id;
		}
		catch(Exception e)
		{
			if(tx != null)
			{
				tx.rollback();
			}
							
			throw e;
		}		
	}
	//-----------------------------------------------------------------------------------------------------------
	private class CheckForUpdates extends TimerTask
	{
		private ChannelRegistration channelRegistration;
		private HibernateManager hibernateManager;
		private DeviceController deviceController;
		
		private CheckForUpdates(HibernateManager hibernateManager,
		DeviceController deviceController,ChannelRegistration channelRegistration)
		{
			this.channelRegistration = channelRegistration;
			this.hibernateManager = hibernateManager;
			this.deviceController = deviceController;
		}
		
		public void run()
		{
			boolean isStartedHere = TransactionHelper.startTx();
			try
			{
				Date timestamp = new Date();
				
				List<ChannelBeanMetaData> allUpdates = new ArrayList<ChannelBeanMetaData>();
				
				//Get all the registered devices
				//List<Device> allDevices = this.deviceController.readAll();
				if(!isRegisteredForCacheInvalidationEvent)
				{
					try
					{
						EventManager.getInstance().addListener(ChannelDaemon.this);
						isRegisteredForCacheInvalidationEvent = true;
					}
					catch(Throwable t)
					{
						//DO nothing...will try to register in the next go
					}
				}
				
				if(allDevices != null)
				{
					//Scan for channel updates for each device
					List<Device> localCopy = new ArrayList<Device>();
					localCopy.addAll(allDevices);
					for(Device device:localCopy)
					{
						if(!device.getIdentity().isActive())
						{
							continue;
						}
						
						this.scan(device, timestamp, allUpdates);
					}
				}
				
				//Send all the channel events
				if(allUpdates != null && !allUpdates.isEmpty())
				{
					this.sendChannelEvent(allUpdates);
				}
				
				if(isStartedHere)
				{
					TransactionHelper.commitTx();
				}
			}
			catch(Exception e)
			{
				log.error(this, e);
				
				if(isStartedHere)
				{
					TransactionHelper.rollbackTx();
				}
								
				ErrorHandler.getInstance().handle(e);
			}
		}
		
		private void scan(Device device, Date timestamp, 
		List<ChannelBeanMetaData> allUpdates)
		{			
			try
			{
				LastScanTimestamp lastScanTimestamp = ChannelDaemon.this.lastScanTimestamps.get(device.getIdentifier());
				if(lastScanTimestamp == null)
				{
					//create a new timestamp
					lastScanTimestamp = new LastScanTimestamp();
					lastScanTimestamp.setTimestamp(timestamp);
					lastScanTimestamp.setChannel(this.channelRegistration.getUri());
					lastScanTimestamp.setClientId(device.getIdentifier());
				}
				
				
				Identity identity = device.getIdentity();
				
				//Get any new beans
				String[] added = this.channelRegistration.getChannel().scanForNew(device, 
				lastScanTimestamp.getTimestamp());
				
				//Get updated beans
				String[] updated = this.channelRegistration.getChannel().scanForUpdates(device, 
				lastScanTimestamp.getTimestamp());
				
				//Get deleted beans
				String[] deleted = this.channelRegistration.getChannel().scanForDeletions(device, 
				lastScanTimestamp.getTimestamp());
				
				if(added != null)
				{
					for(String beanId : added)
					{
						ChannelBeanMetaData cour = new ChannelBeanMetaData();
						cour.setChannel(this.channelRegistration.getUri());
						cour.setBeanId(beanId);
						cour.setDeviceId(device.getIdentifier());
						cour.setUpdateType(ChannelUpdateType.ADD);
						cour.setPrincipal(identity.getPrincipal());
						allUpdates.add(cour);
					}
				}
				
				if(updated != null)
				{
					for(String beanId : updated)
					{
						ChannelBeanMetaData cour = new ChannelBeanMetaData();
						cour.setChannel(this.channelRegistration.getUri());
						cour.setBeanId(beanId);
						cour.setDeviceId(device.getIdentifier());
						cour.setUpdateType(ChannelUpdateType.REPLACE);
						cour.setPrincipal(identity.getPrincipal());
						allUpdates.add(cour);
					}
				}
				
				if(deleted != null)
				{
					for(String beanId : deleted)
					{
						ChannelBeanMetaData cour = new ChannelBeanMetaData();
						cour.setChannel(this.channelRegistration.getUri());
						cour.setBeanId(beanId);
						cour.setDeviceId(device.getIdentifier());
						cour.setUpdateType(ChannelUpdateType.DELETE);
						cour.setPrincipal(identity.getPrincipal());
						allUpdates.add(cour);
					}
				}
				
				lastScanTimestamp.setTimestamp(timestamp);
				
				//save the new timestamp
				long id = ChannelDaemon.this.save(lastScanTimestamp);
				lastScanTimestamp.setId(id);
				ChannelDaemon.this.lastScanTimestamps.put(device.getIdentifier(), lastScanTimestamp);
			}
			catch(Exception e)
			{
				ErrorHandler.getInstance().handle(e);
				
				DateFormat dateFormat = DateFormat.getDateTimeInstance();
				Exception ex = new Exception("Device:"+device.getIdentifier()+
				",Identity:"+device.getIdentity().getPrincipal()+"Channel: "+this.channelRegistration.getUri()+
				"Scan Time: "+dateFormat.format(new Date()));
				
				ErrorHandler.getInstance().handle(e);
			}
		}
										
		private void sendChannelEvent(List<ChannelBeanMetaData> allUpdates)
		{
			String channel = this.channelRegistration.getUri();
			
			BusMessage message = new BusMessage();
			message.setBusUri(channel);
			message.setSenderUri(channel);
																							
			ChannelEvent event = new ChannelEvent();
			event.setChannel(channel);					
			event.setAttribute(ChannelEvent.metadata, allUpdates);
			message.setAttribute(ChannelEvent.event, XMLUtilities.marshal(event));
									
			Bus.sendMessage(message);
		}
	}		
}
