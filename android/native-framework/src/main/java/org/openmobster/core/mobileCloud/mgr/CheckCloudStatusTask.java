/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.mgr;

import java.text.MessageFormat;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.app.Activity;
import android.content.Context;

/**
 * @author openmobster@gmail.com
 *
 */
final class CheckCloudStatusTask implements Task
{	
	CheckCloudStatusTask()
	{
	}
	
	public void execute(CommandContext commandContext) throws AppException
	{
		try
		{
			Request request = new Request("/status/mobilebeanservice");
			request.setAttribute("param1", "param1Value");
			request.setAttribute("param2", "param2Value");
			Response response = MobileService.invoke(request);	
			
			//Assert the state
			String param1Value = response.getAttribute("param1");
			String param2Value = response.getAttribute("param2");
			
			if(!param1Value.equals("response://param1Value"))
			{
				throw new RuntimeException("Service Invocation Failure!!");
			}
			
			if(!param2Value.equals("response://param2Value"))
			{
				throw new RuntimeException("Service Invocation Failure!!");
			}
			
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
			SyncInvocation.bootSync, "syncstatuschannel");
			syncInvocation.deactivateBackgroundSync();
			Bus.getInstance().invokeService(syncInvocation);
			
			//Assert the state
			MobileBean[] beans = MobileBean.readAll("syncstatuschannel");
			if(beans.length != 5)
			{
				throw new RuntimeException("Sync Service Failure!!");
			}
			for(int i=0; i<beans.length;i++)
			{
				int beanId = Integer.parseInt(beans[i].getId());
				String beanValue = beans[i].getValue("value");
				
				if(beanId >= 5)
				{
					throw new RuntimeException("Sync Service Failure!!");
				}
				if(!beanValue.startsWith("/status/"))
				{
					throw new RuntimeException("Sync Service Failure!!");
				}

			}
		}		
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
				"Exception :"+ e.getMessage()				
			}));
			
			AppException appException = new AppException();
			appException.setMessageKey("cloud_status_check_failed");
			throw appException;
		}
	}
	
	@Override
	public void postExecute(CommandContext commandContext) throws AppException
	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration configuration = Configuration.getInstance(context);
		String statusOk = "Successfully connected to Cloud at:{0}";
		statusOk = MessageFormat.format(statusOk, new Object[]{
				configuration.getServerIp(),
				configuration.getEmail(),
				configuration.isSSLActivated()?configuration.getSecureServerPort():configuration.getPlainServerPort()
		});
		
		Activity currentActivity = (Activity)commandContext.getAttribute("currentActivity");
		ViewHelper.getOkModal(currentActivity, "", 
		statusOk).
		show();
	}
	
	@Override
	public void postExecuteAppException(CommandContext commandContext) throws AppException
	{
		
	}
}
