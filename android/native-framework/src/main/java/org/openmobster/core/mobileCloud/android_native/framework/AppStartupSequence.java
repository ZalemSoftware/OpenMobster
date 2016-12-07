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

import system.CometUtil;

/**
 *
 * @author openmobster@gmail.com
 */
public final class AppStartupSequence
{
	private static AppStartupSequence singleton;
	
	private AppStartupSequence()
	{
		
	}
	
	public static AppStartupSequence getInstance()
	{
		if(AppStartupSequence.singleton == null)
		{
			AppStartupSequence.singleton = new AppStartupSequence();
		}
		return AppStartupSequence.singleton;
	}
	
	public void execute()
	{
		try
		{
			//Subscribe to channels
	    	boolean wasBootsyncStarted = CometUtil.subscribeChannels();
	    	
			//Perfom boot-sync on the unbooted channels....CometUtil.subscribeChannels does this for you
	    	//....This results in duplicate bootsyncs
	    	if(!wasBootsyncStarted)
	    	{
				/*
				 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
				 * Faz com o bootup seja chamado através do método do próprio CometUtil.
				 */
	    		CometUtil.performChannelBootup(false);
	    		
//	    		Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.ChannelBootupHandler");
//	    		invocation.setValue("push-restart-cancel", ""+Boolean.FALSE);
//	    		Bus.getInstance().invokeService(invocation);
	    	}
			
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
			
			//Do a proxy sync on all the channels
			SyncInvocation syncInvocation = new SyncInvocation(
					"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
			SyncInvocation.proxySync);
			syncInvocation.deactivateBackgroundSync(); //so that there are no push notifications...just a quiet sync
			Bus.getInstance().invokeService(syncInvocation);
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
