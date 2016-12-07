/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.invocation;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.module.connection.NotificationListener;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import android.content.Context;

/**
 * @author openmobster@gmail
 *
 */
public final class CometConfigHandler extends Service implements InvocationHandler 
{
	public CometConfigHandler()
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
	
	public InvocationResponse handleInvocation(Invocation invocation)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			InvocationResponse response = new InvocationResponse();
			
			Configuration configuration = Configuration.getInstance(context);
			
			String cometMode = invocation.getValue("mode");
			String poll_interval = invocation.getValue("poll_interval");
			
			if(cometMode.equalsIgnoreCase("push"))
			{
				//Push Mode
				configuration.setCometMode("push");
			}
			else if(cometMode.equalsIgnoreCase("poll"))
			{
				//Poll Mode
				configuration.setCometMode("poll");
				
				if(poll_interval != null && poll_interval.trim().length()>0)
				{
					configuration.setCometPollInterval(Long.parseLong(poll_interval));
				}
				else
				{
					configuration.setCometPollInterval(5000); //some system default value should be used
					//for now, just use every 15 minutes
				}
			}
			configuration.save(context);
			
			//Restart the NotificationListener			
			NotificationListener notify = NotificationListener.getInstance();
			if(notify != null)
			{
				notify.restart();
			}
			
			//Get the status...A new NotificationListener should be started with respect to this invocation
			boolean status = false;
			NotificationListener newNotify = NotificationListener.getInstance();
			if(newNotify != null)
			{
				//The regular isActive call to check status will not work, since when this is invoked,
				//chances are the NetSession is still in the process of being established in the background.
				//So in this case if a new NotificationListener instance is available in the kernel, 
				//we can assume, it will be eventually
				//activated in the background
				if(notify != null)
				{
					if(newNotify.hashCode() != notify.hashCode())
					{
						status = true;
					}
				}
				else
				{
					status = true;
				}
			}
			
			response.setValue("status", ""+status);
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
			return null;
		}
	}
}
