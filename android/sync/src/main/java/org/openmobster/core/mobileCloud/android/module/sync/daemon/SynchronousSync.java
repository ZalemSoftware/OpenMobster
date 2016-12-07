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

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.sync.SyncService;
import org.openmobster.core.mobileCloud.android.module.sync.engine.ChangeLogEntry;
import org.openmobster.core.mobileCloud.android.module.sync.engine.SyncDataSource;

/**
 * 
 *
 * @author openmobster@gmail.com
 */
public final class SynchronousSync 
{
	private static SynchronousSync singleton;
	
	boolean inProgress;
	
	private SynchronousSync()
	{
		
	}
	
	public static SynchronousSync getInstance()
	{
		if(singleton == null)
		{
			synchronized(SynchronousSync.class)
			{
				if(singleton == null)
				{
					singleton = new SynchronousSync();
				}
			}
		}
		return singleton;
	}
	
	public synchronized void sync()
	{
		inProgress = true;
		try
		{
			List<String> servicesToSync = new ArrayList<String>(); 
			List<ChangeLogEntry> changelog = SyncDataSource.getInstance().readChangeLog();
			
			if(changelog != null)
			{
				for(ChangeLogEntry entry:changelog)
				{
					String service = entry.getNodeId();					
					if(!servicesToSync.contains(service))
					{
						servicesToSync.add(service);
					}
				}
			}
			
			//Start syncing each service
			for(String service:servicesToSync)
			{
				//Initiate this as a background data sync
				SyncService.getInstance().performTwoWaySync(service, service, true);
			}
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(this.getClass().getName(), "InitiateSyncTask", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
		}
		finally
		{
			inProgress = false;
		}
	}
}
