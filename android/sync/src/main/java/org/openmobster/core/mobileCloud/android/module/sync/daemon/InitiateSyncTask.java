/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync.daemon;

import java.util.List;
import java.util.ArrayList;
import java.util.TimerTask;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.sync.SyncService;
import org.openmobster.core.mobileCloud.android.module.sync.engine.ChangeLogEntry;
import org.openmobster.core.mobileCloud.android.module.sync.engine.SyncDataSource;

/**
 * Initiate Sync Task is run by the Daemon to initiate sync session from the
 * device with the server based on appropriate environmental state. Such state
 * would be a modified device changelog indicating change in the state of data
 * on the device, etc
 * 
 * @author openmobster@gmail.com
 *
 */
final class InitiateSyncTask extends TimerTask
{
	/*
	 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
	 * Faz com que seja considerado em progresso mesmo antes de iniciar para garantir que não seja chamada novamente
	 * enquanto espera o delay do timer.
	 */
	boolean inProgress = true;

	public InitiateSyncTask()
	{

	}

	public void run()
	{
		try
		{
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Faz com que execute em loop até terminar todo o changelog.
			 */
			for (;;) {
				List<ChangeLogEntry> changelog = SyncDataSource.getInstance().readChangeLog();
				if (changelog == null || changelog.isEmpty()) {
					break;
				}
				
				List<String> servicesToSync = new ArrayList<String>(); 
				for (ChangeLogEntry entry : changelog) {
					String service = entry.getNodeId();	
					if (!servicesToSync.contains(service)) {
						servicesToSync.add(service);
					}
				}
				
				//Start syncing each service
				for (String service:servicesToSync) {
					//Initiate this as a background data sync
					SyncService.getInstance().performTwoWaySync(service, service, true);
				}
			}
		} catch(Exception e) {
			SystemException syse = new SystemException(this.getClass().getName(), "InitiateSyncTask", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
		} finally {
			inProgress = false;
		}
	}
}
