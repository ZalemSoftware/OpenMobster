/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.moblet;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.core.mobileCloud.android.crypto.CryptoManager;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.api.ui.framework.state.AppStateManager;
import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;
import org.openmobster.android.api.d2d.D2DService;

/**
 * Application Container. There is one instance of an Application Container deployed per Application. Application Container provides
 * services that proxy system level requests to the Device Container to optimize resource usage like 
 * "Open a Comet Socket to the Server, so that each application does not have an Open Comet Socket for notifications" etc 
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Moblet 
{
	private static Moblet singleton;
	
	private Context context;
	
	private Moblet(Context context)
	{
		this.context = context;
	}
	
	/**
	 * Returns the instance of the device container
	 * 
	 * @return
	 */
	public static Moblet getInstance(Context context)
	{
		if(Moblet.singleton == null)
		{
			synchronized(Moblet.class)
			{
				if(Moblet.singleton == null)
				{
					Registry.getInstance(context).setContainer(false);
					Moblet.singleton = new Moblet(context);
				}
			}
		}
		return Moblet.singleton;
	}
	//------------------------------------------------------------------------------------------------------------------------------------------\
	/**
	 * Starts the Container
	 */
	public synchronized void startup()
	{
		try
		{
			if(this.isContainerActive())
			{
				return;
			}
			
			AppSystemConfig.getInstance().start();
			CryptoManager.getInstance().start();
			
			Database.getInstance(this.context).connect();
									
			List<Service> services = new ArrayList<Service>();
						
			services.add(new Bus());
			
			services.add(new NetworkConnector());
			
			services.add(new MobileObjectDatabase());
			
			//Moblet App State management service			
			services.add(new AppStateManager());
			
			//Device-To-Device Push service
			services.add(new D2DService());
										
			Registry.getActiveInstance().start(services);
			
			this.registerPush();
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "startup", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
		}
	}
	
	/**
	 * Shuts down the container
	 */
	public synchronized void shutdown()
	{
		try
		{						
			if(!this.isContainerActive())
			{
				return;
			}
			
			Registry.getActiveInstance().stop();
			Database.getInstance(this.context).disconnect();
			CryptoManager.getInstance().stop();
			AppSystemConfig.stop();
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "shutdown", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
		}
		finally
		{
			Moblet.singleton = null;
		}
	}
	
	/**
	 * Checks if the Container is currently running on the device
	 * 
	 * @return boolean true: if container is running, false: otherwise
	 */
	public boolean isContainerActive()
	{
		return Registry.getActiveInstance().isStarted();
	}
	
	public synchronized void propagateNewContext(Context context)
	{
		this.context = context;
		Registry.getActiveInstance().setContext(context);
	}
	
	private void registerPush()
	{
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					//Populate the Cloud Request
					Request request = new Request("android_push_callback");	
					request.setAttribute("app-id", Registry.getActiveInstance().getContext().getPackageName());
					
					new MobileService().invoke(request);
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					
					//Record this error in the Cloud Error Log
					ErrorHandler.getInstance().handle(e);
				}
			}
		});
		t.start();
	}
}
