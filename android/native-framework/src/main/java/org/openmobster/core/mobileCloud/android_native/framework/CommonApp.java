/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import java.util.Timer;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.android_native.framework.events.NativeEventBusSPI;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.spi.ui.framework.SPIServices;

import android.view.KeyEvent;
import android.view.Menu;
import android.app.Activity;

/**
 * @author openmobster@gmail.com
 *
 */
public final class CommonApp
{	
	static void onStart(final Activity activity)
	{
		Services services = Services.getInstance();
		
		//Initialize the UI Framework
		if(AppConfig.getInstance() == null || !AppConfig.getInstance().isFrameworkActive())
		{
			activity.showDialog(1);
			return;
		}
		
		bootstrapContainer(activity);
		
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					bootstrapActivity(activity);
				}
				catch(Exception e)
				{
					//e.printStackTrace(System.out);
					ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
						"Message:"+e.getMessage(),
						"Exception:"+e.toString()
					}));
					activity.showDialog(0);
				}
			}
		}
		);
		t.start();
	}
	
	static void onResume(final Activity activity)
	{
		NavigationContext navigationContext = NavigationContext.getInstance();
		navigationContext.setHome("home");
		navigationContext.home();
	}
	
	static boolean onPrepareOptionsMenu(final Activity activity, final Menu menu)
	{
		NavigationContext.getInstance().setAttribute("options-menu", menu);
	    
	    //This should be a refresh so that current screen is refreshed
	    NavigationContext.getInstance().refresh();
	    
	    return true;
	}
	
	static boolean onKeyDown(final int keyCode, final KeyEvent event)
	{
		NavigationContext navigationContext = NavigationContext.getInstance();
		if(keyCode == KeyEvent.KEYCODE_BACK && !navigationContext.isHome())
		{
			navigationContext.back();
			return true;
		}
		return false;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	static void bootstrapContainer(final Activity context)
	{
		//Initialize some of the higher level services
		Services services = Services.getInstance();
		//Load API Services
		services.setResources(new NativeAppResources());
		services.setCommandService(new NativeCommandService());
		services.setCurrentActivity(context);
		
		//Load SPI Services
		SPIServices.getInstance().setNavigationContextSPI(new NativeNavigationContextSPI());
		SPIServices.getInstance().setEventBusSPI(new NativeEventBusSPI());
		
		//Initialize the kernel
		DeviceContainer container = DeviceContainer.getInstance(context.getApplicationContext());
		
		//start the kernel
		container.propagateNewContext(context.getApplicationContext());
    	container.startup();
	}
	
	static void bootstrapActivity(final Activity activity)
	{
		//Handle auto sync checking upon App launch, only if channels are not
    	//being initialized
    	Timer timer = new Timer();
		timer.schedule(new BackgroundSync(), 
		5000);
	}
	
	static void showError(final Activity activity)
	{
		activity.showDialog(0);
	}
}
