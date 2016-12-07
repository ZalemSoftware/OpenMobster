/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.mgr;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PushOptions
{
	private Activity currentActivity;
	
	private PushOptions(Activity currentActivity)
	{
		this.currentActivity = currentActivity;
	}
	
	public static PushOptions getInstance(Activity currentActivity)
	{
		return new PushOptions(currentActivity);
	}
	
	public void start()
	{
		try
		{
			String[] items = null;
			String title = null;
			boolean isRunning = false;
			
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometStatusHandler");
			InvocationResponse response = Bus.getInstance().invokeService(invocation);
			String status = response.getValue("status");
			
			if(status.trim().equals(""+Boolean.TRUE))
			{
				//push is running
				items = new String[]{"Stop Push"};
				title = "Push > Running";
				isRunning = true;
			}
			else
			{
				//push is stopped
				items = new String[]{"Start Push"};
				title = "Push > Stopped";
				isRunning = false;
			}
			
			AlertDialog appDialog = new AlertDialog.Builder(currentActivity).
			setItems(items, 
			new ClickListener(isRunning)).
	    	setCancelable(true).
	    	create();
			
			appDialog.setTitle(title);
							
			appDialog.show();
		}
		catch(Exception e)
		{
			ViewHelper.getOkModal(currentActivity, "System Error", "Push Configuration Failed").show();
		}
	}
	
	private class ClickListener implements DialogInterface.OnClickListener
	{
		private boolean isRunning;
		
		private ClickListener(boolean isRunning)
		{
			this.isRunning = isRunning;
		}
		
		public void onClick(DialogInterface dialog, int status)
		{
			switch(status)
			{
				case 0:
					dialog.cancel();
					
					if(isRunning)
					{
						//go ahead and stop the push
						CommandContext commandContext = new CommandContext();
						
						commandContext.setAttribute("task", new StopPushTask());
						
						TaskExecutor taskExecutor = new TaskExecutor("Push > Stop","Stopping Push....",
								"Push system was successfully stopped",PushOptions.this.currentActivity);
						taskExecutor.execute(commandContext);
					}
					else
					{
						//go ahead and start the push
						CommandContext commandContext = new CommandContext();
						
						commandContext.setAttribute("task", new StartPushTask());
						
						TaskExecutor taskExecutor = new TaskExecutor("Push > Start","Starting Push....",
								"Push system was successfully started",PushOptions.this.currentActivity);
						taskExecutor.execute(commandContext);
					}
				break;
			}
		}
	}
}
