/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;

import android.app.Activity;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;


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
			String main = "main";
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
						CommandService service = Services.getInstance().getCommandService();
						CommandContext commandContext = new CommandContext();
						commandContext.setTarget("runtestsuite");
						service.execute(commandContext);
					}
				}
		);
		
		//Wire the Test Push button
		Button push = (Button)ViewHelper.findViewById(currentActivity, 
		"push");
		push.setOnClickListener(
				new OnClickListener()
				{
					public void onClick(View clicked)
					{
						CommandContext commandContext = new CommandContext();
						
						commandContext.setTarget("/test/start/push");
						
						//Making the invocation
						Services.getInstance().getCommandService().execute(commandContext);
					}
				}
		);
														
		//setup menu
		try
		{
			Menu menu = (Menu)NavigationContext.getInstance().
			getAttribute("options-menu");
			
			if(menu != null)
			{				
				MenuInflater inflater = currentActivity.getMenuInflater();
				
				String menuClass = currentActivity.getPackageName()+".R$menu";
				String optionsMenu = "main";
				Class clazz = Class.forName(menuClass);
				Field field = clazz.getField(optionsMenu);
			    inflater.inflate(field.getInt(clazz), menu);
			    
			    //Add MenuItemListeners
			    for(int i=0,size=menu.size();i<size;i++)
			    {
			    	MenuItem cour = menu.getItem(i);
			    	
			    	switch(i)
			    	{
			    		case 0:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					//Exit the App
									currentActivity.finish();
			    					return true;
			    				}
			    			});
			    		break;
			    		
			    		case 1:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();
			    					commandContext.setTarget("/demo/local");
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 2:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();
			    					commandContext.setTarget("/demo/localException");
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 3:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();
			    					commandContext.setTarget("/demo/localAppException");
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 4:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();
			    					commandContext.setTarget("/demo/remote");
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 5:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();
			    					commandContext.setTarget("/demo/remoteException");
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 6:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();
			    					commandContext.setTarget("/demo/remoteAppException");
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 7:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();				
			    					commandContext.setTarget("/demo/remote/timeout");
			    					commandContext.activateTimeout();
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 8:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();
			    					commandContext.setTarget("/demo/asyncCommand");
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 9:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					CommandService service = Services.getInstance().getCommandService();
			    					CommandContext commandContext = new CommandContext();
			    					commandContext.setTarget("pushTrigger");
			    					service.execute(commandContext);
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 10:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					String errorLog = ErrorHandler.getInstance().generateReport();
			    					
			    					if(errorLog != null && errorLog.trim().length()>0)
			    					{
			    						ViewHelper.getOkModal(currentActivity, "Error Log", errorLog).
			    						show();
			    					}
			    					else
			    					{
			    						ViewHelper.getOkModal(currentActivity, "Error Log", "Error Log is Empty").
			    						show();
			    					}
			    					return true;
			    				}
			    			});
			    	    break;
			    	    
			    		case 11:
			    			cour.setOnMenuItemClickListener(new OnMenuItemClickListener()
			    			{
			    				public boolean onMenuItemClick(MenuItem clickedItem)
			    				{
			    					ErrorHandler.getInstance().clearAll();
			    					Toast.makeText(currentActivity, "Error Log is cleared...", 
			    					Toast.LENGTH_SHORT).show();
			    					return true;
			    				}
			    			});
			    	    break;
			    	}
			    }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
