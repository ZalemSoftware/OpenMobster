/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dev.tools.android;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.ChannelUtil;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;


/**
 * @author openmobster@gmail.com
 */
public class ManualSync implements RemoteCommand
{
	public void doAction(CommandContext commandContext) 
	{		
		try
		{
			String channel = (String)commandContext.getAttribute("channel");
			String syncOption = (String)commandContext.getAttribute("syncOption");
			
			if(syncOption.equals("1") && ChannelUtil.isChannelActive(channel))
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.twoWay, channel);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			else
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.bootSync, channel);		
				Bus.getInstance().invokeService(syncInvocation);
			}
		}		
		catch(Exception be)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Manually Synchronizing ("+commandContext.getAttribute("channel")+")",
				"Target Command:"+commandContext.getTarget()				
			}));
			throw new RuntimeException(be.toString());
		}
	}

	public void doViewBefore(CommandContext commandContext) 
	{		
	}
	
	public void doViewAfter(CommandContext commandContext) 
	{				
		AppResources resources = Services.getInstance().getResources();
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		Toast.makeText(currentActivity, "Sync is done!!", 
		Toast.LENGTH_SHORT).show();
		
		NavigationContext navigationContext = Services.getInstance().getNavigationContext();
		navigationContext.home();
	}

	public void doViewError(CommandContext commandContext) 
	{
		AppResources resources = Services.getInstance().getResources();
		AppException appException = commandContext.getAppException();
		
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "", 
		resources.localize(appException.getMessageKey(), appException.getMessageKey())).
		show();
	}					
}
