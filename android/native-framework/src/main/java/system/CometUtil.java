/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package system;

import java.util.Set;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.BusException;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.service.Registry;

/**
 * @author openmobster@gmail
 *
 */
public final class CometUtil 
{
	public static boolean subscribeChannels() throws BusException
	{
		boolean wasChannelBootupStarted = false;
		Context context = Registry.getActiveInstance().getContext();
		
		Configuration configuration = Configuration.getInstance(context);
		if(!configuration.isActive())
		{
			return false;
		}
		
		AppSystemConfig appConfig = AppSystemConfig.getInstance();
		Set<String> channels = appConfig.getChannels();
		boolean newAdded = false;
		if(channels != null && !channels.isEmpty())
		{
			for(String channel:channels)
			{				
				boolean cour = configuration.addMyChannel(channel);
				if(!newAdded && cour)
				{
					newAdded = true;
				}
			}
			
			//refresh the channel list
			configuration.clearMyChannels();
			for(String channel:channels)
			{				
				configuration.addMyChannel(channel);
			}
			configuration.save(context);
			
			
			//If this channel is newly subscribed on this device, then recycle the comet system
			if(newAdded)
			{
				CometUtil.performChannelBootup(false);
				wasChannelBootupStarted = true;
			}
		}
		else
		{
			configuration.clearMyChannels();
			configuration.save(context);
		}
		
		return wasChannelBootupStarted;
	}
	
	public static synchronized void performChannelBootup(final boolean cancelPushRestart)
	{
		//Execute this in a background thread...holds up the App launch
		Thread thread = new Thread() {
			public void run()
			{
				try
				{					
					/*
					 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
					 * Faz com o bootup seja tentado novamente se algum canal não foi bootado com sucesso.
					 */
					for (int attempts = 0; ; attempts++) {
						Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.ChannelBootupHandler");
						if (cancelPushRestart) {
							invocation.setValue("push-restart-cancel", String.valueOf(false));
						}
						InvocationResponse response = Bus.getInstance().invokeService(invocation);
						String successStr = response.getValue("success");
						if (successStr != null && Boolean.parseBoolean(successStr)) {
							break;
						}
						
						//Aguarda 5 segundos mais um segundo para cada tentativa anterior.
						sleep((5 + attempts) * 1000);
					}
				}
				catch(Exception be)
				{
					//nothing you can do here
				}
			}
		};
		thread.start();
	}
}
