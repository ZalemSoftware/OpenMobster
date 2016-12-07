/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.subscription;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;

/**
 * A SubscriptionManager manages the Comet Subscription for each client/device that is "actively" connected to the system
 * 
 * @author openmobster@gmail.com
 */
public class SubscriptionManager
{
	private static Logger log = Logger.getLogger(SubscriptionManager.class);
	
	private Subscription subscription;
	private List<String> myChannels;
	
	private SubscriptionManager()
	{	
		this.myChannels = new ArrayList<String>();
	}
	
	public static SubscriptionManager createInstance(Subscription subscription)
	{
		SubscriptionManager manager = new SubscriptionManager();
		manager.subscription = subscription;		
		return manager;
	}
	
	public Subscription getSubscription()
	{
		return this.subscription;
	}			
	//-----------------------------------------------------------------------------------------------------------
	public List<String> getMyChannels() 
	{
		return Collections.unmodifiableList(this.myChannels);
	}
		
	public void clearMyChannels()
	{
		this.myChannels.clear();
	}
	
	public void addMyChannel(String channel)
	{		
		this.myChannels.add(channel);
	}
	
	public Device getDevice()
	{
		if(this.subscription == null || this.subscription.getClientId() == null)
		{
			return null;
		}
		return DeviceController.getInstance().read(this.subscription.getClientId());
	}
	
	public boolean isMyChannel(String channel)
	{
		return this.myChannels.contains(channel);
	}
}
