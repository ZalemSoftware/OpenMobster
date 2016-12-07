/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.command;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;

import android.app.Activity;

/**
 * @author openmobster@gmail.com
 *
 */
public final class PushTrigger implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			Request request = new Request("/offlineapp/pushtrigger");	
			new MobileService().invoke(request);
			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "Push Trigger", 
				"Push successfully triggered!!").
		show();
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		this.getClass().getName()+" had an error!!").
		show();
	}
}
