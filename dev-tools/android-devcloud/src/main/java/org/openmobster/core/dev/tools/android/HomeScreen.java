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
import java.util.Properties;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.EditText;
import android.content.DialogInterface;

/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{
	private Integer screenId;
	
	@Override
	public void render()
	{
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String home = "home";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(home);
			
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
	public Object getContentPane()
	{
		return this.screenId;
	}
	
	@Override
	public void postRender()
	{
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		
		listApp.setTitle("Development Cloud");
		
		listApp.setListAdapter(new ArrayAdapter(listApp, 
			    android.R.layout.simple_list_item_1, 
			    new String[]{"Activate", "Manual Sync"}));
		
		//ovveride the serverIp if one is already stored
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		String storedCloudIp = conf.getServerIp();
		
		ListItemClickListener clickListener = new ListItemClickListener()
		{
			public void onClick(ListItemClickEvent clickEvent)
			{
				int functionId = clickEvent.getPosition();
				switch(functionId)
				{
					case 0:
						try
						{
							HomeScreen.this.collectCloudIP();
						}
						catch(Exception e)
						{
							e.printStackTrace(System.out);
							throw new RuntimeException(e);
						}
					break;
					
					case 1:
						try
						{
							HomeScreen.this.resetChannels();
						}
						catch(Exception e)
						{
							e.printStackTrace(System.out);
							throw new RuntimeException(e);
						}
					break;
				}
			}
		};
		NavigationContext.getInstance().addClickListener(clickListener);
		
		this.setupMenu();
	}
	
	private void setupMenu()
	{
		final ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		if(menu != null)
		{
			MenuItem item1 = menu.add(Menu.NONE, Menu.NONE, 0, "Activate");
			item1.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					try
					{
						HomeScreen.this.collectCloudIP();
						return true;
					}
					catch(Exception e)
					{
						e.printStackTrace(System.out);
						throw new RuntimeException(e);
					}
				}
			});
			
			MenuItem item2 = menu.add(Menu.NONE, Menu.NONE, 1, "ManualSync");
			item2.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					try
					{
						HomeScreen.this.resetChannels();
						return true;
					}
					catch(Exception e)
					{
						e.printStackTrace(System.out);
						throw new RuntimeException(e);
					}
				}
			});
			
			MenuItem serverItem = menu.add(Menu.NONE, Menu.NONE, 2, "Change Cloud IP Address");
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
						e.printStackTrace(System.out);
						throw new RuntimeException(e);
					}
				}
			});
			
			MenuItem item3 = menu.add(Menu.NONE, Menu.NONE, 3, "Exit");
			item3.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					listApp.finish();
					return true;
				}
			});
		}
	}
	
	private void collectCloudIP() throws Exception
	{
		final Activity currentActivity = Services.getInstance().getCurrentActivity();
		final Context context = Registry.getActiveInstance().getContext();
		final Configuration conf = Configuration.getInstance(context);
		
		String cloudIp = conf.getServerIp();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
		builder.setCancelable(false);
		
		builder.setTitle("Cloud IP Address");
		
		final EditText serverField = new EditText(currentActivity);
		serverField.setText(cloudIp);
		builder.setView(serverField);
		
		//Add the buttons
		builder.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id)
			{
				try
				{
					String cloudIp = serverField.getText().toString();
					
					if(cloudIp != null && cloudIp.trim().length()>0)
					{
						conf.setServerIp(serverField.getText().toString());
						conf.save(context);
						dialog.dismiss();
						HomeScreen.this.mockActivate();
					}
					else
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Cloud IP is required").
						show();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					throw new RuntimeException(e);
				}
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.dismiss();
			}
		});
		
		//show the dialog
		builder.create().show();
	}
	
	private void mockActivate() throws Exception
	{
		final Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		
		String cloudIp = conf.getServerIp();
		
		this.startup(context);
		
		//Initialize the activation properties
		Properties properties = new Properties();
		properties.load(HomeScreen.class.getResourceAsStream("/moblet-app/activation.properties"));
		
		String server = properties.getProperty("cloud_server_ip");
		if(cloudIp == null || cloudIp.trim().length() == 0)
		{
			conf.setServerIp(server);
		}
		else
		{
			conf.setServerIp(cloudIp);
		}
		
		String email = properties.getProperty("email");
		String password = properties.getProperty("password");
		conf.setEmail(email);
		conf.setAuthenticationHash(password);
		conf.setAuthenticationNonce(password);
		conf.save(context);
		
		this.activateDevice(conf.getServerIp(), "1502", email, password);
	}
	
	private void resetChannels() throws Exception
	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		if(conf.isActive())
		{
			Services.getInstance().getNavigationContext().navigate("manualSync");
		}
		else
		{
			Toast.makeText(currentActivity, "Device needs to be activated with the Cloud!!", 
			Toast.LENGTH_SHORT).show();
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
	
	private void startup(final Context context) throws Exception
    {
		Database database = Database.getInstance(context);
		if(database.doesTableExist(Database.provisioning_table))
		{
	    	//Make a local copy of registered channels
	    	//System.out.println("Copying the channels...........");
	    	Configuration configuration = Configuration.getInstance(context);
	    	List<String> myChannels = configuration.getMyChannels();
	    	
	    	//drop the configuration so new one will be generated
	    	//System.out.println("Dropping the configuration.......");
	    	configuration.stop();
	    	
	    	//Clear out the provisioning table
	    	database.dropTable(Database.provisioning_table);
	    	database.createTable(Database.provisioning_table);
	    	
	    	//restart the configuration
	    	//System.out.println("Restarting the configuration.......");
	    	configuration.start(context);
	    	
	    	//Now reload the registered channels if any were found
	    	//System.out.println("Reloading the channels.......");
	    	if(myChannels != null && myChannels.size()>0)
	    	{
		    	configuration = Configuration.getInstance(context);
		    	for(String channel:myChannels)
		    	{
		    		configuration.addMyChannel(channel);
		    	}
		    	configuration.save(context);
	    	}
	    	//System.out.println("Startup successfull.............");
		}
    }
	
	private void activateDevice(String server, String port, String email, String password)
	{
		Context context = Registry.getActiveInstance().
		getContext();
		Configuration conf = Configuration.getInstance(context);
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("activate");
		
		/*System.out.println("-------------------------------------------------");
		System.out.println("Server: "+server);
		System.out.println("Port: "+port);
		System.out.println("Email: "+email);
		System.out.println("Password: "+password);
		System.out.println("-------------------------------------------------");*/
		
		commandContext.setAttribute("server", server);
		commandContext.setAttribute("email", email);
		commandContext.setAttribute("password", password);
		if(!conf.isActive())
		{
			commandContext.setAttribute("port", port);
		}
		Services.getInstance().getCommandService().execute(commandContext);
	}
}
