/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager;

import java.lang.reflect.Field;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.android.module.connection.PolicyManager;

import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.mgr.AppActivation;
import org.openmobster.core.mobileCloud.mgr.DMCloudManagerOptions;

/**
 * @author openmobster@gmail.com
 *
 */
public class CloudManagerApp extends Activity
{
	@Override
	protected void onStart()
	{
		try
		{
			this.bootstrapApp();
			CloudService.getInstance().start(this);
			
			//Initialize the Device Administration Policy Manager
			PolicyManager.getInstance().showAdminScreen(this);
			
			super.onStart();
		} 
		catch (Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			e.printStackTrace(System.out);
		}
	}
	
	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			this.displayMainScreen();
		}
		catch(Exception e)
		{
			//e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), 
					"onResume", new Object[]{
						"Message:"+e.getMessage(),
						"Exception:"+e.toString()
					}));
		}
	}
	
	public void displayMainScreen()
	{
		try
		{
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			Button activate = (Button)ViewHelper.findViewById(this, "activate");
			activate.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					AppActivation appActivation = AppActivation.getInstance(CloudManagerApp.this);
					appActivation.start();
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void bootstrapApp() throws Exception
	{
		DeviceContainer container = DeviceContainer.getInstance(this.getApplicationContext());
		boolean isActive = container.isContainerActive();
    	
    	//Start the Keep Alive service
    	if(!isActive)
    	{
    		Intent start = new Intent("org.openmobster.core.mobileCloud.manager.KeepAliveService");
    		start.putExtra("activityClassName", this.getClass().getName());
    		this.startService(start);
    	}
	}
	
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if(menu.hasVisibleItems())
		{
			return true;
		}
		
		MenuItem item = menu.add(Menu.NONE, Menu.NONE, 0, "Cloud Manager");
		item.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				try
				{
					DMCloudManagerOptions cm = DMCloudManagerOptions.getInstance(CloudManagerApp.this);
					cm.start();
					return true;
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					return true;
				}
			}
		});
		
		return true;
	}
}
