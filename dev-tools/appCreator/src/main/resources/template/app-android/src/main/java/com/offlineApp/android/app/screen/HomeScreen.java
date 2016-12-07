/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.screen;

import java.lang.reflect.Field;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;

/**
 * This is the home screen of the App. It displays the 'List' of data synchronized with the Cloud. It also presents a 'Menu' for displaying other functions of the App
 * 
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{
	private Integer screenId;
	
	@Override
	/**
	 * Invoked by the MVC runtime so that the screen can perform its layout and obtain a screen id
	 */
	public void render()
	{
		try
		{
			//Gets the currently active 'Activity' instance
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			//Gets the layout for this screen
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String home = "home";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(home);
			
			//Obtains a screen Id
			this.screenId = field.getInt(clazz);						
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(), "render", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			});
			ErrorHandler.getInstance().handle(se);
			throw se;
		}
	}
	
	@Override
	/**
	 * Invoked by the MVC runtime to get the unique screen Id for displaying the screen
	 */
	public Object getContentPane()
	{
		return this.screenId;
	}
	
	@Override
	/**
	 * Invoked by the MVC runtime once the screen is rendered. This callback allows the screen setup the business state of the screen
	 * and update the UI to show this information
	 */
	public void postRender()
	{
		//Gets the currently active 'Activity' instance
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		
		//Gets the 'Cloud' configuration
		AppResources res = Services.getInstance().getResources();
		Configuration configuration = Configuration.getInstance(listApp);
		
		//Check to see if the 'Demo Beans' are synchronized from the 'Cloud'. If not, a 'Boot Sync' is issued.
		if(configuration.isActive() && !MobileBean.isBooted("offlineapp_demochannel"))
		{
			//Boots up the 'Demo Bean' sync channel
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("/channel/bootup/helper");
			Services.getInstance().getCommandService().execute(commandContext);
			
			return;
		}
		
		//Show the List of "Demo Beans" synchronized from the 'Cloud'. 
		this.showList(listApp);
		
		//Setup the App Menu
		this.setMenuItems();
	}
	//-------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Displays a 'List' of 'Demo Beans'
	 */
	private void showList(ListActivity listApp)
	{
		//Reads the synchronized/locally stored demo beans from the 'offlineapp_demochannel' channel on the device
		MobileBean[] demoBeans = MobileBean.readAll("offlineapp_demochannel");
		
		//Shows these beans in a List
		if(demoBeans != null && demoBeans.length >0)
		{
			String[] ui = new String[demoBeans.length];
			for(int i=0,size=ui.length;i<size;i++)
			{
				ui[i] = demoBeans[i].getValue("demoString");
			}
			listApp.setListAdapter(new ArrayAdapter(listApp, 
		    android.R.layout.simple_list_item_1, 
		    ui));
			
			//List Listener
			ListItemClickListener clickListener = new ClickListener(demoBeans);
			NavigationContext.getInstance().addClickListener(clickListener);
		}
	}
	
	/**
	 * Sets up the Menu for this App
	 */
	private void setMenuItems()
	{
		//Get an instance of the 'Options Menu' from the MVC runtime
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		
		if(menu != null)
		{
			//Reset Channel: This resets the 'offlineapp_demochannel' by issuing a 'Boot Sync'
			MenuItem resetChannel = menu.add(Menu.NONE, Menu.NONE, 0, "Reset Channel");
			resetChannel.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/offlineapp/reset");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
			
			//Push Trigger: This issues a 'Push Trigger' to demonstrate 'Cloud Push' capabilities. This is for demo only.
			//In an actual app, the data state changes on the 'Cloud' serve as the trigger to initiate the Push
			MenuItem pushTrigger = menu.add(Menu.NONE, Menu.NONE, 1, "Push Trigger");
			pushTrigger.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/offlineapp/pushtrigger");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
			
			//Make an RPC invocation: Demonstrates an RPC invocation to a service in the 'Cloud'
			MenuItem rpc = menu.add(Menu.NONE, Menu.NONE, 0, "Make RPC Invocation");
			rpc.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/offlineapp/rpc");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
		}
	}
	
	/**
	 * ClickListener for the 'Demo Beans' list
	 * 
	 * @author openmobster@gmail.com
	 */
	private static class ClickListener implements ListItemClickListener
	{
		private MobileBean[] activeBeans;
		
		private ClickListener(MobileBean[] activeBeans)
		{
			this.activeBeans = activeBeans;
		}
		
		public void onClick(ListItemClickEvent clickEvent)
		{
			//Gets the "Demo Bean" in question
			int selectedIndex = clickEvent.getPosition();
			MobileBean selectedBean = activeBeans[selectedIndex];
			
			//Issues a request to show the details associated with this bean
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("/demo/details");
			commandContext.setAttribute("selectedBean", selectedBean.getValue("demoString"));
			Services.getInstance().getCommandService().execute(commandContext);
		}
	}
}
