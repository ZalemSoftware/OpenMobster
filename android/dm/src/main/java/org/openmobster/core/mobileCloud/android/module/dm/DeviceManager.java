/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.dm;

import android.os.Build;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 * 
 * @author openmobster@gmail.com
 */
public class DeviceManager extends Service
{
	public DeviceManager()
	{
		
	}
	
	public void start()
	{
	}
	
	public void stop()
	{
		
	}
	
	public static DeviceManager getInstance()
	{
		return (DeviceManager)Registry.getActiveInstance().lookup(DeviceManager.class);
	}
	
	public void sendOsCallback()
	{
		try
		{
			Configuration conf = Configuration.getInstance(Registry.getActiveInstance().getContext());
			if(!conf.isActive())
			{
				return;
			}
			
			Request request = new Request("dm_callback");
			
			request.setAttribute("os", "android");
			request.setAttribute("version", Build.VERSION.RELEASE);
			
			MobileService.invoke(request);
		}
		catch(Exception e)
		{
			//Do nothing
			SystemException syse = new SystemException(this.getClass().getName(),
			"sendOsCallback", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
		}
	}
}
