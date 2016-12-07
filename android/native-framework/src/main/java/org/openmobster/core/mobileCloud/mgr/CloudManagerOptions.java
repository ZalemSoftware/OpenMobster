/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.mgr;


import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 *
 * @author openmobster@gmail.com
 */
public final class CloudManagerOptions
{
	private Activity currentActivity;
	
	private CloudManagerOptions(Activity currentActivity)
	{
		this.currentActivity = currentActivity;
	}
	
	public static CloudManagerOptions getInstance(Activity currentActivity)
	{
		return new CloudManagerOptions(currentActivity);
	}
	
	public void start()
	{
		AlertDialog appDialog = new AlertDialog.Builder(currentActivity).
		setItems(new String[]{"App Activation","Enterprise App Store","Push","Manual Sync","Cloud Status"}, 
		new ClickListener()).
    	setCancelable(true).
    	create();
		
		appDialog.setTitle("CloudManager");
						
		appDialog.show();
	}
	
	private class ClickListener implements DialogInterface.OnClickListener
	{	
		public void onClick(DialogInterface dialog, int status)
		{
			switch(status)
			{
				case 0:
					dialog.cancel();
					
					AppActivation appActivation = AppActivation.getInstance(CloudManagerOptions.this.currentActivity);
					appActivation.start();
				break;
				
				case 1:
					dialog.cancel();
					
					AppStore appStore = AppStore.getInstance(CloudManagerOptions.this.currentActivity);
					appStore.start();
				break;
				
				case 2:
					dialog.cancel();
					
					PushOptions push = PushOptions.getInstance(CloudManagerOptions.this.currentActivity);
					push.start();
				break;
				
				case 3:
					dialog.cancel();
					
					ManualSync manualSync = ManualSync.getInstance(CloudManagerOptions.this.currentActivity);
					manualSync.start();
				break;
				
				case 4:
					dialog.cancel();
					
					CommandContext commandContext = new CommandContext();
					commandContext.setAttribute("task", new CheckCloudStatusTask());
				    commandContext.setAttribute("currentActivity", currentActivity);
					
					TaskExecutor taskExecutor = new TaskExecutor("Status","Sync in Progress....",
					null,currentActivity);
					taskExecutor.execute(commandContext);
				break;
			}
		}
	}
}
