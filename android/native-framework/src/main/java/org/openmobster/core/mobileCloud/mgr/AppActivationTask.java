/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.mgr;

import android.content.Context;
import android.app.Activity;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.android.api.rpc.ServiceInvocationException;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.BusException;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.dm.DeviceManager;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.moblet.BootupConfiguration;

/**
 * @author openmobster@gmail.com
 *
 */
final class AppActivationTask implements Task
{
	private Activity activity;
	private AppActivationCallback callback;
	
	AppActivationTask(Activity activity,AppActivationCallback callback)
	{
		this.activity = activity;
		this.callback = callback;
	}
	
	public void execute(CommandContext commandContext) throws AppException
	{
		this.performActivation(commandContext);
	}
	
	private void performActivation(CommandContext commandContext) throws AppException
	{
		Context context = Registry.getActiveInstance().getContext();
		String server = null;
		String email = null;
		String deviceIdentifier = null;
		boolean isReactivation = false;
		try
		{			
			server = (String)commandContext.getAttribute("server");
			email = (String)commandContext.getAttribute("email");
			String password = (String)commandContext.getAttribute("password");
			String port = (String)commandContext.getAttribute("port");
												
			Configuration conf = Configuration.getInstance(context);
			if(conf.isActive())
			{
				isReactivation = true;
			}
			
			
			BootupConfiguration.bootup(server, port);
			
			deviceIdentifier = conf.getDeviceId();
			
			
			//Go ahead and activate the device now
			Request request = new Request("provisioning");
			request.setAttribute("email", email);
			request.setAttribute("password", password);			
			request.setAttribute("identifier", deviceIdentifier);
			
			Response response = MobileService.invoke(request);
						
			if(response.getAttribute("idm-error") == null)
			{
				//Success Scenario
				this.processProvisioningSuccess(email,response);
				
				//Start the CometDaemon
				if(!isReactivation)
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.StartCometDaemon");
					Bus.getInstance().invokeService(invocation);
				}
				else
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometRecycleHandler");
					Bus.getInstance().invokeService(invocation);
				}
			}
			else
			{
				//Error Scenario
				String errorKey = response.getAttribute("idm-error");
																
				//report to ErrorHandling system
				SystemException syse = new SystemException(this.getClass().getName(), "doAction", new Object[]{
					"Error Key:"+errorKey,					
					"Target Command:"+commandContext.getTarget(),
					"Device Identifier:"+ deviceIdentifier,
					"Server:"+ server,
					"Email:"+email
				});
												
				ErrorHandler.getInstance().handle(syse);
				AppException appException = new AppException();
				appException.setMessageKey(errorKey);
				throw appException;
			}	
		}
		catch(ServiceInvocationException sie)
		{
			//sie.printStackTrace(System.out);			
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{					
				"Target Command:"+commandContext.getTarget(),
				"Device Identifier:"+ deviceIdentifier,
				"Server:"+ server,
				"Email:"+email
			}));
			throw new RuntimeException(sie.toString());
		}
		catch(BusException be)
		{
			//be.printStackTrace(System.out);			
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Trying to start the Comet Daemon....",
				"Target Command:"+commandContext.getTarget(),
				"Device Identifier:"+ deviceIdentifier,
				"Server:"+ server,
				"Email:"+email
			}));
			throw new RuntimeException(be.toString());
		}		
	}
	
	private void processProvisioningSuccess(String email,Response response)
	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration configuration = Configuration.getInstance(context);
		
		String authenticationHash = response.getAttribute("authenticationHash");
		configuration.setEmail(email);
		configuration.setAuthenticationHash(authenticationHash);
		configuration.setAuthenticationNonce(authenticationHash);
		configuration.setActive(true);
		
		configuration.save(context);
		
		//Broadcast deviceManagement meta data to the server
		DeviceManager dm = DeviceManager.getInstance();
		dm.sendOsCallback();
	}

	@Override
	public void postExecute(CommandContext commandContext) throws AppException
	{
		if(this.callback != null)
		{
			ViewHelper.getOkModal(this.activity, "Activation", 
			"Your App is successfully activated with the Cloud")
			.show();
			
			//Invoke callback on  the invoker of this workflow
			this.callback.success();
		}
		else
		{
			ViewHelper.getOkModalWithCloseApp(this.activity, "Activation", 
					"Your App is successfully activated with the Cloud. You must restart at this point")
			.show();
		}
	}
	
	@Override
	public void postExecuteAppException(CommandContext commandContext) throws AppException
	{
		Configuration conf = Configuration.getInstance(Registry.getActiveInstance().getContext());
		if(!conf.isActive())
		{
			AppActivation appActivation = AppActivation.getInstance(this.activity,this.callback);
			appActivation.start();
		}
	}
}
