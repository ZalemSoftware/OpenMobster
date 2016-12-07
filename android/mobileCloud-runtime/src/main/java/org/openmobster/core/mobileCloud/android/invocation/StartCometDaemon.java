/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.invocation;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;

/**
 * @author openmobster@gmail
 *
 */
public final class StartCometDaemon extends Service implements InvocationHandler 
{
	public StartCometDaemon()
	{
		
	}
	
	public void start()
	{
		try
		{
			Bus.getInstance().register(this);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop()
	{
		
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public String getUri()
	{
		return this.getClass().getName();
	}
	
	public InvocationResponse handleInvocation(Invocation invocation)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			DeviceContainer.getInstance(context).notifyDeviceActivated();
			
			return null;
		}
		catch(Exception e)
		{
			//e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "handleInvocation", new Object[]{																		
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
			return null;
		}
	}
}
