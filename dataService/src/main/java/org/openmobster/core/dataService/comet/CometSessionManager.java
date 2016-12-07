/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.comet;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import org.apache.mina.core.session.IoSession;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.transaction.TransactionHelper;
import org.openmobster.core.common.event.EventListener;
import org.openmobster.core.common.event.EventManager;
import org.openmobster.core.common.event.Event;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.services.subscription.Subscription;
import org.openmobster.core.cluster.ClusterService;
import org.openmobster.core.cluster.ClusterListener;
import org.openmobster.core.cluster.ClusterEvent;

/**
 * @author openmobster@gmail.com
 */
public final class CometSessionManager implements EventListener,ClusterListener
{
	private static Logger log = Logger.getLogger(CometSessionManager.class);
	
	private List<CometSession> cometSessions; //consists of all comet sessions corresponding to
	//all registered devices in the system
	
	private Timer timer; //sends keep-alive heartbeats active connections
	private long pulseInterval;
	
	private DeviceController deviceController;
	private EventManager eventManager;
	
	private ClusterService clusterService;
				
	public DeviceController getDeviceController() 
	{
		return deviceController;
	}

	public void setDeviceController(DeviceController deviceController) 
	{
		this.deviceController = deviceController;
	}

	public CometSessionManager()
	{
		this.cometSessions = new ArrayList<CometSession>();
	}
	
	public long getPulseInterval() 
	{
		return pulseInterval;
	}

	public void setPulseInterval(long pulseInterval) 
	{
		this.pulseInterval = pulseInterval;
	}
	
	public ClusterService getClusterService()
	{
		return clusterService;
	}

	public void setClusterService(ClusterService clusterService)
	{
		this.clusterService = clusterService;
	}

	public void start()
	{
		this.clusterService.register(this);
	}
	
	@Override
	public void startService(ClusterEvent event) throws Exception
	{
		boolean isStartedHere = TransactionHelper.startTx();
		try
		{
			//Load all cometSessions based on all the registered devices
			List<Device> allDevices = this.deviceController.readAll();
			if(allDevices != null)
			{
				for(Device device: allDevices)
				{
					this.addSession(device);
				}
			}
			
			this.eventManager.addListener(this);
			
			//Deprecated. The heartbeat will now be controlled by the device side component
			//This allows different heartbeat intervals to be set by the devices based on what
			//works out for them
			
			//For instance, on the BlackBerry device, a heartbeat of 55 seconds is optimal.
			//Hopefully for some devices it can be pushed even further to 2 minutes if the on device
			//TCP stack supports more robust timeout configuration.
			//Longer the heartbeat interval that keeps the push socket alive, the better it is for 
			//the battery life on the device
			this.timer = new Timer(this.getClass().getName(), true); //sets it as a daemon thread
			TimerTask heartBeatDaemon = new HeartBeatDaemon();
			long startDelay = 5000;
			this.timer.schedule(heartBeatDaemon, startDelay, this.pulseInterval*60*1000);			
			
			if(isStartedHere)
			{
				TransactionHelper.commitTx();
			}
			
			log.info("--------------------------------------------------------");
			log.info("Push Service successfully started. Pulse Interval set to: "+this.pulseInterval*60*1000+"(ms)");
			log.info("--------------------------------------------------------");
		}
		catch(Exception e)
		{
			log.error(this, e);
			if(isStartedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new RuntimeException(e);
		}	
	}
	
	public void stop()
	{
		if(this.cometSessions != null)
		{
			List<CometSession> sessions = sessions();
			for(CometSession deviceSession: sessions)
			{
				deviceSession.stop();
			}
		}
	}
	
	public static CometSessionManager getInstance()
	{
		return (CometSessionManager)ServiceManager.locate("dataService://CometSessionManager");
	}	
		
	public EventManager getEventManager() 
	{
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) 
	{
		this.eventManager = eventManager;
	}
	//------------------------------------------------------------------------------------------------------------
	public List<CometSession> getCometSessions()
	{
		return Collections.unmodifiableList(this.cometSessions); 
	}
	
	private List<CometSession> sessions()
	{
		List<CometSession> sessions = new ArrayList<CometSession>();
		sessions.addAll(this.cometSessions);
		return Collections.unmodifiableList(sessions);
	}
	
	public CometSession findCometSession(String deviceId)
	{
		List<CometSession> sessions = sessions();
		for(CometSession deviceSession: sessions)
		{
			if(deviceSession.getUri().equals(deviceId))
			{
				return deviceSession;
			}
		}
		return null;
	}
	
	public void reload()
	{
		this.cometSessions.clear();
		this.start();
	}
			
	public void activate(String deviceId, IoSession activeSession)
	{
		if(activeSession == null)
		{
			//Nothing to activate...this is a bogus call
			return;
		}
			
		if(deviceId == null)
		{
			throw new IllegalArgumentException("DeviceId must be specified!!");
		}		
			
		List<CometSession> sessions = sessions();
		for(CometSession deviceSession: sessions)
		{
			if(deviceSession.getUri().equals(deviceId))
			{
				deviceSession.activate(activeSession);
			}
		}
	}
	//------------------------------------------------------------------------------------------------------------
	public void newDeviceNotification(Device device)
	{
		if(device != null)
		{
			this.addSession(device);
		}
	}
	//-------------------------------------------------------------------------------------------------------------
	private void addSession(Device device)
	{
		CometSession session = this.findCometSession(device.getIdentifier());
		if(session != null)
		{
			//Checking to make sure multiple device sessions are not created
			//resulting in multiple Bus infrastructure for each device
			return;
		}
		
		Subscription subscription = new Subscription();
		subscription.setClientId(device.getIdentifier());
		subscription.setConfigValue("identity", device.getIdentity().getPrincipal());
		
		CometSession deviceSession = CometSession.createInstance(subscription);
		deviceSession.start();
		
		this.cometSessions.add(deviceSession);
	}
	//---------------------------------------------------------------------------------------------------------
	public void onEvent(Event event)
	{
		Device device = (Device)event.getAttribute("new-device");
		if(device != null)
		{
			this.newDeviceNotification(device);
		}
	}
	//---------------------------------------------------------------------------------------------------------
	private class HeartBeatDaemon extends TimerTask
	{
		public void run()
		{
			List<CometSession> sessions = CometSessionManager.this.sessions();
			for(CometSession session: sessions)
			{
				if(session.isActive())
				{
					log.debug("---------------------------------------------------------------");
					log.debug("Sender: "+this.hashCode());
					log.debug("Target Device: "+session.getUri());
					log.debug("Sending a KeepAlive HeartBeat Every: ("+CometSessionManager.this.pulseInterval*60*1000+" ms)");
					log.debug("---------------------------------------------------------------");
					session.sendHeartBeat();
				}
				else
				{
					//cleanup
					log.debug("---------------------------------------------------------------");
					log.debug("Sender: "+this.hashCode());
					log.debug("Target Device: "+session.getUri());
					log.debug("De-activating...................");
					log.debug("---------------------------------------------------------------");
					session.deactivate();
				}
			}
		}
	}
}
