/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync.daemon;

import java.util.Timer;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;

/**
 * A Daemon task that loads a handful of proxies (unpopulated mobilebeans) at a time in the background
 * 
 * @author openmobster@gmail.com
 *
 */
public final class LoadProxyDaemon extends Service 
{
	private InitiateProxyTask proxyTask;
	
	public LoadProxyDaemon()
	{
		
	}
	
	public void start()
	{
	}
	
	public void stop()
	{
	}
	
	public static LoadProxyDaemon getInstance()
	{
		return (LoadProxyDaemon)Registry.getActiveInstance().lookup(LoadProxyDaemon.class);
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	public void scheduleProxyTask()
	{
		if(this.proxyTask != null)
		{
			if(this.proxyTask.inProgress)
			{
				return;
			}
			else
			{
				//It could be scheduled to execute at a later time or finished execution
				if(!this.proxyTask.executionFinished)
				{
					//its scheduled to execute later
					return;
				}
			}
		}
		
		//This task has executed, schedule a new time for its execution
		Timer timer = new Timer();
		this.proxyTask = new InitiateProxyTask();
		timer.schedule(this.proxyTask, 20000);
	}
}
