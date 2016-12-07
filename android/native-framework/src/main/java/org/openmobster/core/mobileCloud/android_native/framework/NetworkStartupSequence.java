/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.BusException;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;

/**
 *
 * @author openmobster@gmail.com
 */
public final class NetworkStartupSequence
{
	private static NetworkStartupSequence singleton;
	
	private NetworkStartupSequence()
	{
		
	}
	
	public static NetworkStartupSequence getInstance()
	{
		if(NetworkStartupSequence.singleton == null)
		{
			NetworkStartupSequence.singleton = new NetworkStartupSequence();
		}
		return NetworkStartupSequence.singleton;
	}
	
	public void execute()
	{
		try
		{
			//Get the non-booted but active channels with a two-way sync
			List<String> channelsToSync = this.findTwoWaySyncChannels();
			if(channelsToSync != null && !channelsToSync.isEmpty())
			{
				for(String channel:channelsToSync)
				{
					//start the app session with a two way sync
					SyncInvocation syncInvocation = new SyncInvocation(
					"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
					SyncInvocation.twoWay, channel);
					syncInvocation.deactivateBackgroundSync(); //so that there are no push notifications...just a quiet sync
					Bus.getInstance().invokeService(syncInvocation);
				}
			}
			
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometRecycleHandler");
			Bus.getInstance().invokeService(invocation);
		}
		catch(BusException be)
		{
			throw new RuntimeException(be);
		}
	}
	
	private List<String> findTwoWaySyncChannels()
	{
		List<String> channelsToSync = new ArrayList<String>();
		
		AppSystemConfig appConfig = AppSystemConfig.getInstance();
		Set<String> appChannels = appConfig.getChannels();
		if(appChannels != null)
		{
			for(String channel:appChannels)
			{	
				if(MobileBean.isBooted(channel))
				{
					channelsToSync.add(channel);
				}
			}
		}
		
		return channelsToSync;
	}
}
