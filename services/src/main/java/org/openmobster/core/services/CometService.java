/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;

import org.apache.log4j.Logger;

import org.openmobster.core.services.event.ChannelEvent;
import org.openmobster.core.services.event.ChannelEventListener;
import org.openmobster.core.services.event.NetworkEvent;
import org.openmobster.core.services.event.NetworkEventListener;
import org.openmobster.core.services.subscription.SubscriptionManager;
import org.openmobster.core.services.subscription.Subscription;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.errors.ErrorHandler;

/**
 * @author openmobster@gmail
 *
 */
public final class CometService
{
	private static Logger log = Logger.getLogger(CometService.class);
	
	private Map<String, SubscriptionManager> activeManagers;
	private List<ChannelEventListener> channelListeners;
	private List<NetworkEventListener> networkEventListeners;
	
	public CometService()
	{
		this.activeManagers = new HashMap<String, SubscriptionManager>();
		this.channelListeners = new ArrayList<ChannelEventListener>();
		this.networkEventListeners = new ArrayList<NetworkEventListener>();
	}
	
	public void start()
	{
	}
	
	public void stop()
	{
		
	}
	
	public static CometService getInstance()
	{
		return (CometService)ServiceManager.locate("services://CometService");
	}
	
	public void notifyChannelEventListener(ChannelEventListener channelEventListener)
	{
		this.channelListeners.add(channelEventListener);
	}
	
	public void notifyNetworkEventListener(NetworkEventListener networkEventListener)
	{
		this.networkEventListeners.add(networkEventListener);
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	public SubscriptionManager activateSubscription(Subscription subscription)
	{
		SubscriptionManager manager = SubscriptionManager.createInstance(subscription);
		this.activeManagers.put(subscription.getClientId(), manager);
		return manager;
	}
	
	public Map<String, SubscriptionManager> getActiveSubscriptions()
	{
		return Collections.unmodifiableMap(this.activeManagers);
	}
	
	public SubscriptionManager findByDeviceId(String deviceId)
	{
		Collection<SubscriptionManager> managers = this.activeManagers.values();
		for(SubscriptionManager cour: managers)
		{
			if(cour.getSubscription().getClientId().equals(deviceId))
			{
				return cour;
			}
		}
		return null;
	}
	
	public void broadcastChannelEvent(ChannelEvent channelEvent)
	{		
		if(this.channelListeners != null)
		{
			for(ChannelEventListener listener: this.channelListeners)
			{
				try
				{
					listener.channelUpdated(channelEvent);
				}
				catch(Exception e)
				{
					//so that if an error occurs on one listener, others don't suffer
					//listeners must be isolated of each other
					try{ErrorHandler.getInstance().handle(e);}catch(Exception ex){}
				}
			}
		}
	}
	
	public void broadcastNetworkEvent(NetworkEvent networkEvent)
	{
		if(this.networkEventListeners != null)
		{
			for(NetworkEventListener listener: this.networkEventListeners)
			{
				try
				{
					listener.serverPush(networkEvent);
				}
				catch(Exception e)
				{
					//so that if an error occurs on one listener, others don't suffer
					//listeners must be isolated of each other
					try{ErrorHandler.getInstance().handle(e);}catch(Exception ex){}
				}
			}
		}
	}		
}
