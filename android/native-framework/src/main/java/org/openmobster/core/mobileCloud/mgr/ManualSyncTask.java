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
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.util.ChannelUtil;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

/**
 * @author openmobster@gmail.com
 *
 */
final class ManualSyncTask implements Task
{	
	ManualSyncTask()
	{
	}
	
	public void execute(CommandContext commandContext) throws AppException
	{
		try
		{
			String channel = (String)commandContext.getAttribute("channel");
			String syncOption = (String)commandContext.getAttribute("syncOption");
			
			if(syncOption.equals("1") && ChannelUtil.isChannelActive(channel))
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.twoWay, channel);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			else
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.bootSync, channel);		
				Bus.getInstance().invokeService(syncInvocation);
			}
		}		
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
				"Exception :"+ e.getMessage()				
			}));
			
			AppException appException = new AppException();
			appException.setMessageKey("sync_failure");
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
