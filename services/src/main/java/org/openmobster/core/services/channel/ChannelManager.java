/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.channel;

import org.apache.log4j.Logger;

import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.cluster.ClusterService;

/**
 * This manages each registered Channel. There is one Channel Manager per instance of a registered Channel
 * 
 * @author openmobster@gmail.com
 */
public final class ChannelManager 
{
	private static Logger log = Logger.getLogger(ChannelManager.class);
			
	/**
	 * A background daemon that monitors the registered data channel for data updates
	 */
	private ChannelDaemon channelDaemon;
	
	private ChannelManager()
	{
		
	}
	
	public static ChannelManager createInstance(HibernateManager hibernateManager,
	DeviceController deviceController,
	ChannelRegistration channelRegistration,
	ClusterService clusterService)
	{
		ChannelManager manager = new ChannelManager();
		
		ChannelDaemon daemon = new ChannelDaemon(hibernateManager, deviceController,channelRegistration,clusterService);				
		manager.setChannelDaemon(daemon);
		
		return manager;
	}

	/**
	 * 
	 * @return
	 */
	public ChannelDaemon getChannelDaemon() 
	{
		return channelDaemon;
	}

	/**
	 * 
	 * @param channelDaemon
	 */
	public void setChannelDaemon(ChannelDaemon channelDaemon) 
	{
		this.channelDaemon = channelDaemon;
	}
			
	public void start()
	{
		this.channelDaemon.start();
	}
	
	public void stop()
	{
		this.channelDaemon.stop();
	}
}
