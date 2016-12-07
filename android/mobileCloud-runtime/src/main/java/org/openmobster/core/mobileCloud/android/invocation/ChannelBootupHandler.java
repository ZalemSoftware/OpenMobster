/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.invocation;

import java.util.List;
import java.util.Set;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.module.connection.NotificationListener;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.module.sync.SyncException;
import org.openmobster.core.mobileCloud.android.module.sync.SyncService;


import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.storage.DBException;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

/**
 * @author openmobster@gmail
 *
 */
public final class ChannelBootupHandler extends Service implements InvocationHandler 
{
	public ChannelBootupHandler()
	{
		
	}
	
	public void start()
	{
		try
		{
			Bus.getInstance().register(this);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop()
	{
		
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public String getUri()
	{
		return this.getClass().getName();
	}
	
	public synchronized InvocationResponse handleInvocation(Invocation invocation)
	{
		InvocationResponse response = new InvocationResponse();
		try
		{			
			//Bootup any nonn-booted registered channels
			boolean success = this.bootupChannels();
			response.setValue("success", String.valueOf(success));
			
			String pushRestart = invocation.getValue("push-restart-cancel");
			if(pushRestart != null && pushRestart.trim().length()>0)
			{
				return response;
			}
			
			//Recycle the Comet Daemon so the newly added channels will start receiving comet push
			//Restart the NotificationListener
			NotificationListener notify = NotificationListener.getInstance();
			if(notify != null)
			{
				notify.restart();
			}
		
			return response;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "handleInvocation", new Object[]{
						"Comet Mode to Switch To="+invocation.getValue("mode"),												
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
			return response;
		}
	}
	
	private boolean bootupChannels() throws SyncException, DBException
	{
		boolean success = true;
		
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		List<String> myChannels = conf.getMyChannels();
		
		if(myChannels != null && !myChannels.isEmpty())
		{
			for(String channel:myChannels)
			{
				if(!this.isBooted(channel))
				{
					//false because it does not need to send any push related notifications
					//just load the data silently and be done with it
					try {
						SyncService.getInstance().performBootSync(channel, channel, false);
					} catch (SyncException e) {
						/*
						 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
						 * Faz com que um erro no bootup evite que o canal seja marcado como bootado, além de avisar para o invocador que a operação não
						 * foi completada com sucesso.
						 */
						success = false;
						continue;
					}
					
					
					/*
					 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
					 * Define que determinado canal terminou de ser bootado. É obrigado a fazer o "checkStorage" pois o OpenMobster
					 * só cria tabelas na inserção de dados. Desta forma, se o canal n possui dados, terá a tabela criada mesmo assim.
					 */
					MobileObjectDatabase.getInstance().checkStorage(context, channel); 
					conf.setBooted(channel);
					conf.save(context);
				}
			}
		}
		
		return success;
	}
	
	private boolean isBooted(String channel)
	{
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		return deviceDB.isChannelBooted(channel);
	}
}
