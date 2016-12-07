/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.command;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;

import android.app.Activity;
import android.widget.Toast;

/**
 * This 'RemoteCommand' is executed during App startup from the 'HomeScreen'. It is used to start a 'Boot Sync' of the 'offlineapp_demochannel'
 * 
 * @author openmobster@gmail.com
 */
public class ChannelBootupHelper implements RemoteCommand
{
	/**
	 * Invoked on the UI thread prior to the core execution to do some pre-UI work.
	 * 
	 * In this case, it displays a message saying, the Channel is being bootstrapped
	 */
	public void doViewBefore(CommandContext commandContext)
	{
		Activity activity = (Activity)commandContext.getAppContext();
		Toast.makeText(activity, 
				"Waiting for the sync channel 'offlineapp_demochannel' to finish bootstrapping....", 
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Core execution happens on this invocation.
	 * 
	 * In this case, the 'offlineapp_demochannel' is bootstrapped
	 */
	public void doAction(CommandContext commandContext)
	{
		try
		{
			//Wait for 30 seconds for bootstrapping...This should not hold up the App for too long. 'Boot Sync' is an innovation where
			//the basic beans required for App function are synchronized. Other beans get synchronized later in the background, without any
			//user intervention
			int counter = 30;
			while(!MobileBean.isBooted("offlineapp_demochannel"))
			{
				Thread.currentThread().sleep(1000);
				if(--counter == 0)
				{
					throw new AppException();
				}
			}
		}
		catch(Exception e)
		{
			if(e instanceof AppException)
			{
				throw (AppException)e;
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Invokes on the UI thread to display the after the execution of the command
	 * 
	 * In this case, it just refreshes the 'HomeScreen' to display the synchronized "Demo Beans"
	 */
	public void doViewAfter(CommandContext commandContext)
	{		
		NavigationContext.getInstance().home();
	}

	/**
	 * Invoked on the UI thread in the case of an "Application Error" to show a proper message to the user
	 */
	public void doViewError(CommandContext commandContext)
	{
		Activity activity = (Activity)commandContext.getAppContext();
		ViewHelper.getOkModalWithCloseApp(activity, "App Error", "The 'offlineapp_demochannel' is not ready. Please launch the App again in a few minutes").
		show();
	}
}
