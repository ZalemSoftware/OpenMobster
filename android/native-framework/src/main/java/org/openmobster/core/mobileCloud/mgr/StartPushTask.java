/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.mgr;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

/**
 * @author openmobster@gmail.com
 *
 */
final class StartPushTask implements Task
{	
	StartPushTask()
	{
	}
	
	public void execute(CommandContext commandContext) throws AppException
	{
		try
		{
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometConfigHandler");
			invocation.setValue("mode", "push");					
			
			InvocationResponse response = Bus.getInstance().invokeService(invocation);			
			String status = response.getValue("status");
			commandContext.setAttribute("status", status);	
		}		
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
				"Starting the Push Daemon....",
				"Exception :"+ e.getMessage()				
			}));
			
			AppException appException = new AppException();
			appException.setMessageKey("push_start_failure");
			throw appException;
		}
	}
	
	@Override
	public void postExecute(CommandContext commandContext) throws AppException
	{
		// TODO Auto-generated method stub
		
	}	
	
	@Override
	public void postExecuteAppException(CommandContext commandContext) throws AppException
	{
		
	}
}
