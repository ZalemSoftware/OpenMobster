/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.BaseCloudActivity;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.mgr.AppActivation;
import org.openmobster.core.mobileCloud.mgr.CloudManagerOptions;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends BaseCloudActivity
{
	public MainActivity()
	{
		
	}
	
	@Override
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
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if(menu.hasVisibleItems())
		{
			return true;
		}
		
		MenuItem item1 = menu.add(Menu.NONE, Menu.NONE, 0, "App Activation");
		item1.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{	
				AppActivation appActivation = AppActivation.getInstance(MainActivity.this);
				appActivation.start();
				return true;
			}
		});
		
		MenuItem item3 = menu.add(Menu.NONE, Menu.NONE, 1, "Check Push");
		item3.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				try
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometStatusHandler");
					InvocationResponse response = Bus.getInstance().invokeService(invocation);
					
					String status = response.getValue("status");
					
					System.out.println("Push Status: "+status);
					
					return true;
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					return true;
				}
			}
		});
		
		MenuItem item4 = menu.add(Menu.NONE, Menu.NONE, 2, "Cloud Manager");
		item4.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				try
				{
					CloudManagerOptions cm = CloudManagerOptions.getInstance(MainActivity.this);
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
		
		
		MenuItem item5 = menu.add(Menu.NONE, Menu.NONE, 3, "Test Push");
		item5.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				try
				{
					Request request = new Request("/fuseapp/push");
					request.setAttribute("app-id", MainActivity.this.getPackageName());
					
					new MobileService().invoke(request);
					
					ViewHelper.getOkModal(MainActivity.this, "Test Push", 
							"Push successfully triggered..Push Notification should be received in a bit").
					show();
					
					return true;
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					return true;
				}
			}
		});
		
		MenuItem item6 = menu.add(Menu.NONE, Menu.NONE, 4, "Test Sync Push");
		item6.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				try
				{
					Request request = new Request("/fuseapp/pushtrigger");
					
					new MobileService().invoke(request);
					
					ViewHelper.getOkModal(MainActivity.this, "Test Sync Push", 
							"Push successfully triggered..Push Notification should be received in a bit").
					show();
					
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
