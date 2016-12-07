/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dev.tools.android;

import java.lang.reflect.Field;
import java.util.List;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;


/**
 * @author openmobster@gmail.com
 */
public class ManualSyncScreen extends Screen
{	
	private Integer screenId;
			
	public ManualSyncScreen()
	{										
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String main = "home";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
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
	
	public Object getContentPane() 
	{		
		return this.screenId;
	}
	
	public void postRender()
	{		
		//render the list		
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		
		final Configuration conf = Configuration.getInstance(listApp);
		AppResources appResources = Services.getInstance().getResources();	
		
		listApp.setTitle("Sync");
		
		final String[] adapterArray;
		List<String> myChannels = conf.getMyChannels();
		final boolean channelsFound;
		if(myChannels != null && !myChannels.isEmpty())
		{
			channelsFound = true;
			adapterArray = myChannels.toArray(new String[0]);
			listApp.setListAdapter(new ArrayAdapter(listApp, 
					android.R.layout.simple_list_item_1, 
					adapterArray));
		}
		else
		{
			channelsFound = false;
			adapterArray = new String[]{"There aren't any registered channels for synchronization"};
			listApp.setListAdapter(new ArrayAdapter(listApp, 
					android.R.layout.simple_list_item_1, 
					adapterArray));
		}
		
		//Setup Menu
		this.setupMenu();
		
		//Add a List click listener
		ListItemClickListener clickListener = new ListItemClickListener()
		{
			public void onClick(ListItemClickEvent clickEvent)
			{
				if(!channelsFound)
				{
					Services.getInstance().getNavigationContext().back();
					return;
				}
				
				//Perform channel-oriented functions
				ManualSyncScreen.this.startChannelSelect(adapterArray[clickEvent.getPosition()]);
			}
		};
		NavigationContext.getInstance().addClickListener(clickListener);
	}
	
	private void setupMenu()
	{
		//setup menu for this screen
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		if(menu != null)
		{			
			MenuItem backItem = menu.add(0, 0, 0, "Back");
			backItem.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					Services.getInstance().getNavigationContext().back();
					return true;
				}
			});
		}
	}
	
	private void startChannelSelect(String channel)
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		AppResources resources = Services.getInstance().getResources();
		
		ChannelSyncListener listener = new ChannelSyncListener(channel);
		
		AlertDialog dialog = new AlertDialog.Builder(currentActivity).
		setItems(new String[]{"Reset Channel",
		"Sync Channel",
		resources.localize(SystemLocaleKeys.cancel, SystemLocaleKeys.cancel)}, listener).
    	setCancelable(false).
    	create();
		
		dialog.setTitle(channel);
		
		dialog.show();
	}
	
	private static class ChannelSyncListener implements DialogInterface.OnClickListener
	{
		private String selectedChannel;
		
		private ChannelSyncListener(String selectedChannel)
		{
			this.selectedChannel = selectedChannel;
		}
		
		public void onClick(DialogInterface dialog, int status)
		{
			if(status != 2)
			{
				CommandContext commandContext = new CommandContext();
				commandContext.setTarget("manualSync");
				commandContext.setAttribute("syncOption", ""+status);
				commandContext.setAttribute("channel", selectedChannel);
				Services.getInstance().getCommandService().execute(commandContext);
			}
		}
	}
}
