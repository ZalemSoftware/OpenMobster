/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.manager;

import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 * @author openmobster@gmail.com
 */
public class DeviceBootupBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{	
			Context appContext = context.getApplicationContext();
			
			//Initialize the kernel
			DeviceContainer container = DeviceContainer.getInstance(appContext);
			
			//start the kernel
			container.propagateNewContext(appContext);
	    	container.startup();
	    	
	    	Intent start = new Intent("org.openmobster.core.mobileCloud.manager.KeepAliveService");
    		start.putExtra("activityClassName", "org.openmobster.core.mobileCloud.manager.CloudManagerApp");
    		context.startService(start);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
