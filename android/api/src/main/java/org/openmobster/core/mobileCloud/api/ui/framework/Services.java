/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

import android.app.Activity;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;


/**
 * @author openmobster@gmail.com
 *
 */
public final class Services 
{
	private static Services singleton;
	
	private AppResources resources;	
	private NavigationContext navigationContext;
	private CommandService commandService;
	private Activity currentActivity;
	
	private Services()
	{
		
	}
	
	public static Services getInstance()
	{
		if(Services.singleton == null)
		{
			synchronized(Services.class)
			{
				if(Services.singleton == null)
				{
					Services.singleton = new Services();
					AppConfig.getInstance().init();
					
					Services.singleton.navigationContext = NavigationContext.getInstance();
				}
			}
		}
		return Services.singleton;
	}
	
	public static void stopSingleton()
	{
		Services.singleton = null;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public AppResources getResources() 
	{		
		return resources;
	}

	public void setResources(AppResources resources) 
	{		
		this.resources = resources;
	}

	public CommandService getCommandService() 
	{		
		return commandService;
	}

	public void setCommandService(CommandService commandService) 
	{		
		this.commandService = commandService;
	}
	
	public NavigationContext getNavigationContext()
	{		
		return this.navigationContext;
	}
	
	public boolean isFrameworkActive()
	{
		return AppConfig.getInstance().isFrameworkActive();
	}

	public Activity getCurrentActivity()
	{
		return currentActivity;
	}

	public void setCurrentActivity(Activity currentActivity)
	{
		this.currentActivity = currentActivity;
	}
	
	public Class getCurrentActivityClass()
	{
		if(this.currentActivity != null)
		{
			return this.currentActivity.getClass();
		}
		return null;
	}
}
