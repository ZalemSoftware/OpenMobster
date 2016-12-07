/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.testapp.app;

import android.app.Activity;
import android.widget.Toast;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;

/**
 * Checks to make sure that the data is loaded in the sync channel used by the App
 * 
 * @author openmobster@gmail.com
 */
public class ChannelBootupHelper implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		Activity activity = (Activity)commandContext.getAppContext();
		Toast.makeText(activity, 
				"Waiting for the "+Constants.channel+"to finish bootstrapping....", 
				Toast.LENGTH_LONG).show();
	}

	public void doAction(CommandContext commandContext)
	{
		try
		{
		    int counter = 5;
			while(!MobileBean.isBooted(Constants.channel))
			{
				Thread.currentThread().sleep(1000);
				if(counter-- == 0)
				{
				    throw new RuntimeException();
				}
			}
		}
		catch(Exception e)
		{
			throw new AppException();
		}
	}

	public void doViewAfter(CommandContext commandContext)
	{		
	}

	public void doViewError(CommandContext commandContext)
	{
		Activity activity = (Activity)commandContext.getAppContext();
		ViewHelper.getOkModalWithCloseApp(activity, "App Error", "The "+Constants.channel+" is not ready. Please launch the App again in a few minutes").
		show();
	}
}
