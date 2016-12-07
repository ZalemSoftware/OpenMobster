/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.command;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;

import android.app.Activity;


/**
 * @author openmobster@gmail.com
 */
public class ResetChannel implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext) 
	{		
	}
	
	public void doAction(CommandContext commandContext) 
	{		
		try
		{			
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
			SyncInvocation.bootSync, "offlineapp_demochannel");		
			Bus.getInstance().invokeService(syncInvocation);
		}		
		catch(Exception be)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Manually Synchronizing (offlineapp_demochannel)",
				"Target Command:"+commandContext.getTarget()				
			}));
			throw new RuntimeException(be.toString());
		}
	}

	public void doViewAfter(CommandContext commandContext) 
	{				
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "Reset Channel", 
				"Channel is succesfully reset!!").
		show();
		
		NavigationContext.getInstance().refresh();
	}

	public void doViewError(CommandContext commandContext) 
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		this.getClass().getName()+" had an error!!").
		show();
	}					
}
