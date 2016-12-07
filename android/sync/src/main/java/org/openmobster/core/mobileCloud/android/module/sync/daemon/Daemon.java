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
 * Daemon service helps from running tasks in the background efficiently
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Daemon extends Service 
{
	private InitiateSyncTask syncTask;
	
	public Daemon()
	{
		
	}
	
	public void start()
	{
		//Upon startup...runs it one time to get all channels in sync
		//After that, App based channel updates will kick start this daemon
		//This runs in the background, so App execution/data access will not be affected
		this.scheduleSyncInitiation();
	}
	
	public void stop()
	{
	}
	
	public static Daemon getInstance()
	{
		return (Daemon)Registry.getActiveInstance().lookup(Daemon.class);
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Schedules a device initiated sync session at some later point in time
	 * 
	 * If a task is already scheduled, this request is ignored
	 * 
	 */
	public void scheduleSyncInitiation()
	{
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Substitui a verificação do "scheduledExecutionTime" com "inProgress" pois ele retorna 0 quando a task ainda
		 * não foi executada. Por exemplo, se a task vai ser executada daqui a 500 milisegundos, ela não vai retornar
		 * o tempo atual + 500 milisegundos, como provavelmente o programador havia pensado. Faltou ler o javadoc...
		 * 		this.syncTask.scheduledExecutionTime() < System.currentTimeMillis()
		 */
		if(this.syncTask == null || !this.syncTask.inProgress)
		{			
			//This task has executed, schedule a new time for its execution
			Timer timer = new Timer();
			this.syncTask = new InitiateSyncTask();
			
			//Schdeule it to execute in 2 minutes....why two minutes...hoping processing
			//that scheduled this should have completed whatever they were doing.
			//plus, even if they have not, it shouldn't interfere, but trying to execute at different times
			//will make the CPU utilization better
			//timer.schedule(this.syncTask, 120000);
			
			//timer.schedule(this.syncTask,10000);
			timer.schedule(this.syncTask, 500);
		}
	}
	
	public boolean areTasksPending()
	{
		return this.syncTask != null && this.syncTask.inProgress;
	}
}
