/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.core.mobileCloud.android.testsuite.ui.framework.TestSuiteCommandService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{	
	private Integer screenId;
	
	public HomeScreen()
	{										
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String main = "tests";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			this.screenId = field.getInt(clazz);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			
			SystemException se = new SystemException(this.getClass().getName(), "render", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			});
			ErrorHandler.getInstance().handle(se);
			throw se;
		}
	}
	
	public Object getContentPane() 
	{		
		return this.screenId;
	}
	
	public void postRender()
	{
		final Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		//Add the event handlers
		//Find the run_button
		Button runTestSuite = (Button)ViewHelper.findViewById(currentActivity, 
		"runtestsuite");
		runTestSuite.setOnClickListener(
				new OnClickListener()
				{
					public void onClick(View clicked)
					{
						//Execute TestSuite
						CommandContext commandContext = new CommandContext();
						commandContext.setTarget("runtestsuite");
						
						//service.execute(commandContext);
						TestSuiteCommandService.getInstance().execute(commandContext);
					}
				}
		);
		
		this.setupMenu();
	}
	
	private void setupMenu()
	{
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		if(menu != null)
		{
			MenuItem serverItem = menu.add(Menu.NONE, Menu.NONE, 0, "Change Cloud IP Address");
			serverItem.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					try
					{
						HomeScreen.this.changeCloudServer();
						return true;
					}
					catch(Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			});
		}
	}
	
	private void changeCloudServer() throws Exception
	{
		final Activity currentActivity = Services.getInstance().getCurrentActivity();
		final Context context = Registry.getActiveInstance().getContext();
		final Configuration conf = Configuration.getInstance(context);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
		builder.setCancelable(false);
		
		builder.setTitle("Cloud IP Address");
		
		final EditText serverField = new EditText(currentActivity);
		serverField.setText(conf.getServerIp());
		builder.setView(serverField);
		
		//Add the buttons
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id)
			{
				conf.setServerIp(serverField.getText().toString());
				conf.save(context);
				dialog.dismiss();
				Toast.makeText(currentActivity, "Cloud Server IP Address now set to: "+conf.getServerIp(), 
						Toast.LENGTH_SHORT).show();
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.dismiss();
				Toast.makeText(currentActivity, "Cloud Server Ip is set to: "+conf.getServerIp(), 
						Toast.LENGTH_SHORT).show();
			}
		});
		
		//show the dialog
		builder.create().show();
	}
}
