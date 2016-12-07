/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.mgr;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.android.api.rpc.ServiceInvocationException;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.BusException;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.dm.DeviceManager;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ServiceContext;
import org.openmobster.core.mobileCloud.android_native.framework.ServiceException;
import org.openmobster.core.mobileCloud.moblet.BootupConfiguration;

import android.content.Context;

/**
 *
 * @author openmobster@gmail.com
 */
public final class AppActivationService
{
	private static AppActivationService singleton;
	
	private AppActivationService()
	{
		
	}
	
	public static AppActivationService getInstance()
	{
		if(singleton == null)
		{
			synchronized(AppActivationService.class)
			{
				if(singleton == null)
				{
					singleton = new AppActivationService();
				}
			}
		}
		return singleton;
	}
	//-------------------------------------------------------------------------------------------------------------
	public void activate(ServiceContext input) throws ServiceException
	{
		Context context = Registry.getActiveInstance().getContext();
		String server = null;
		String email = null;
		String deviceIdentifier = null;
		boolean isReactivation = false;
		try
		{			
			server = (String)input.getAttribute("server");
			email = (String)input.getAttribute("email");
			String password = (String)input.getAttribute("password");
			String port = (String)input.getAttribute("port");
												
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
																
				ServiceException se = new ServiceException(errorKey);
				throw se;
			}	
		}
		catch(ServiceInvocationException sie)
		{
			throw new ServiceException(sie);
		}
		catch(BusException be)
		{
			throw new ServiceException(be);
		}		
	}
	
	private void processProvisioningSuccess(String email,Response response) throws ServiceException
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
	
	
	/*
	 * Método adicionado na versão 2.4-M3.1
	 */
	
	public void deactivateDevice()	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration configuration = Configuration.getInstance(context);
		
		configuration.setEmail(null);
		configuration.setAuthenticationHash(null);
		configuration.setAuthenticationNonce(null);
		configuration.save(context);
	}
}
