/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openmobster.core.mobileCloud.android.module.connection.PolicyManager;

/**
 *
 * @author openmobster@gmail.com
 */
public class DeviceManagementBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			String action = intent.getExtras().getString("action");
			
			if(action.equals("lock"))
			{
				PolicyManager.getInstance().lock();
			}
			else if(action.equals("wipe"))
			{
				PolicyManager.getInstance().wipe();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
