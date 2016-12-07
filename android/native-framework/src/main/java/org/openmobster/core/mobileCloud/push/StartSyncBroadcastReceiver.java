/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.push;

import java.util.Set;

import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 *
 * @author openmobster@gmail.com
 */
public class StartSyncBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		try
		{
			/*
			 * Alteração feita na versão 2.4-M3.1
			 * Só inicia o serviço se estiver o dispositivo estiver ativado na nuvem.
			 */
			if (!CloudService.getInstance().isDeviceActivated()) {
				return;
			}
			
			//Some bootstrapping
			DeviceContainer.getInstance(context).startupWithoutPush();
			if(!AppConfig.getInstance().isActive())
			{
				AppConfig.getInstance().init();
			}
			if(!AppSystemConfig.getInstance().isActive())
			{
				AppSystemConfig.getInstance().start();
			}
			
			Bundle shared = intent.getBundleExtra("bundle");
			final String channel = shared.getString("channel");
			final String silent = shared.getString("silent");
			
			if(!this.isMyChannel(channel))
			{
				//My App does not use this channel, so no need to sync
				return;
			}
			
			if(channel == null)
			{
				SystemException se = new SystemException(this.getClass().getName(),"run", 
				new Object[]{
					"Error=Channel to synchronize with is missing!!!"
				});
				ErrorHandler.getInstance().handle(se);
				return;
			}
			
			if(!this.isBooted(channel))
			{
				//channel must be booted first before it can respond to sync notifications
				return;
			}
			
			//Grab a WakeLock
			StartSync.acquireWakeLock(context);
			
			//Now invoke the service and leave
			Intent serviceIntent = new Intent(context,StartSync.class);
			serviceIntent.putExtra("channel", channel);
			serviceIntent.putExtra("silent", silent);
			context.startService(serviceIntent);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "onReceive", new Object[]{						
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
	}

	
	private boolean isMyChannel(String channel)
	{
		AppSystemConfig appConfig = AppSystemConfig.getInstance();
		Set<String> myChannels = appConfig.getChannels();
		
		if(myChannels != null && !myChannels.isEmpty())
		{
			return myChannels.contains(channel);
		}
		
		return false;
	}
	
	private boolean isBooted(String channel)
	{
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		return deviceDB.isChannelBooted(channel);
	}
}
