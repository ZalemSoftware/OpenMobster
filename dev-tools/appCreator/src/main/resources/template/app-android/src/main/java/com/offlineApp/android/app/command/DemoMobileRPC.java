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
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;

import android.app.Activity;

/**
 * Demonstrates an 'Asynchronous' RPC invocation on the '/demo/mobile-rpc' service in the Cloud
 * 
 * @author openmobster@gmail.com
 *
 */
public final class DemoMobileRPC implements AsyncCommand
{
	/**
	 * pre-action UI invocation
	 */
	public void doViewBefore(CommandContext commandContext)
	{		
	}

	/**
	 * Action invocation
	 */
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//Setting up the RPC request
			Request request = new Request("/demo/mobile-rpc");	
			request.setAttribute("param1", "paramValue1");
			request.setAttribute("param2", "paramValue2");
			
			//Making the RPC call
			Response response = new MobileService().invoke(request);
			
			//Setting up the results for display
			commandContext.setAttribute("param1", response.getAttribute("param1"));
			commandContext.setAttribute("param2", response.getAttribute("param2"));
		}
		catch(Exception e)
		{
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			ErrorHandler.getInstance().handle(appe);
			
			throw appe;
		}
	}	
	
	/**
	 * post-action UI invocation
	 */
	public void doViewAfter(CommandContext commandContext)
	{
		String param1 = (String)commandContext.getAttribute("param1");
		String param2 = (String)commandContext.getAttribute("param2");
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("Param1: "+param1+"\n\n\n");
		buffer.append("Param2: "+param2);
		
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "RPC Invocation", 
		buffer.toString()).
		show();
	}
	
	/**
	 * UI invocation in case of an error
	 */
	public void doViewError(CommandContext commandContext)
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		ViewHelper.getOkModal(currentActivity, "App Error", 
		commandContext.getAppException().getMessage()).
		show();
	}
}
