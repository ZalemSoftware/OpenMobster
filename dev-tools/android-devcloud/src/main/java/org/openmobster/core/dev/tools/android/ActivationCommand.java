/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dev.tools.android;

import android.content.Context;
import android.app.Activity;
import android.widget.Toast;

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
import org.openmobster.core.mobileCloud.moblet.BootupConfiguration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;


/**
 * @author openmobster@gmail.com
 */
public class ActivationCommand implements RemoteCommand
{
	public void doAction(CommandContext commandContext) 
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
			throw new AppException();
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

	public void doViewBefore(CommandContext commandContext) 
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		Toast.makeText(currentActivity, "Starting Development Cloud Activation....", 
		Toast.LENGTH_SHORT).show();
	}
	
	public void doViewAfter(CommandContext commandContext) 
	{				
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		Toast.makeText(currentActivity, "Development Cloud successfully activated....", 
		Toast.LENGTH_SHORT).show();
	}

	public void doViewError(CommandContext commandContext) 
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "", "Development Cloud Activation Failed. You may have to change the Cloud Server Ip from the 'Menu'").
		show();
	}
	//-------------------------------------------------------------------------------------------------------------------------------
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
}
