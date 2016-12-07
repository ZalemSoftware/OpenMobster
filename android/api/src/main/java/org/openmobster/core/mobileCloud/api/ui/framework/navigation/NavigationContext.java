/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.navigation;

import java.util.Stack;
import java.util.Hashtable;
import java.util.Enumeration;

import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;
import org.openmobster.core.mobileCloud.api.ui.framework.state.AppStateManager;
import org.openmobster.core.mobileCloud.api.ui.framework.state.ScreenContext;
import org.openmobster.core.mobileCloud.spi.ui.framework.SPIServices;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.spi.ui.framework.EventBusSPI;

/**
 * @author openmobster@gmail.com
 *
 */
public final class NavigationContext 
{
	private static NavigationContext singleton;	
	
	private GenericAttributeManager contextManager;
	private Stack<String> history;		
	
	
	private NavigationContext()
	{
		try
		{
			this.contextManager = new GenericAttributeManager();
			this.history = new Stack<String>();
			
			//Setup the screen manager
			ScreenManager screenManager = ScreenManager.getInstance();
			Hashtable screenConfig = AppConfig.getInstance().getScreenConfig();
			if(screenConfig != null)
			{
				Enumeration screenIds = screenConfig.keys();
				while(screenIds.hasMoreElements())
				{
					String screenId = (String)screenIds.nextElement();
					String screenClassName = (String)screenConfig.get(screenId);
					screenManager.register(screenId, (Screen)Class.
					forName(screenClassName).newInstance());
				}
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(),"constructor", new Object[]{
				"Exception: "+e,
				"Message: "+e.getMessage()
			});
		}
	}
	
	public static NavigationContext getInstance()
	{
		if(singleton == null)
		{
			synchronized(NavigationContext.class)
			{
				if(singleton == null)
				{
					singleton = new NavigationContext();
				}
			}
		}
		
		return NavigationContext.singleton;
	}
	
	public static void stopSingleton()
	{
		NavigationContext.singleton = null;
		ScreenManager.stopSingleton();
	}
	//----------------------------------------------------------------------------------------------------------------------------------------------
	public void back()
	{
		//Get the back screen		
		String backId = this.history.pop();
				
		if(backId != null)
		{								
			//Make the back screen the current screen			
			this.contextManager.setAttribute("current", backId);
			
			//Display the current screen			
			ScreenManager screenManager = ScreenManager.getInstance();
			screenManager.forceRender(backId);
			Screen screen = screenManager.find(backId);
			SPIServices.getInstance().getNavigationContextSPI().back(screen);			
		}
	}
	
	public void home()
	{
		//Get the home screen
		String homeId = (String)this.contextManager.getAttribute("home");
		
		if(homeId != null)
		{
			//Clear the history
			this.history.removeAllElements();
			
			//Make the home screen the current screen
			this.contextManager.setAttribute("current", homeId);
			
			//Display the current screen
			ScreenManager screenManager = ScreenManager.getInstance();
			screenManager.forceRender(homeId);
			Screen screen = screenManager.find(homeId);
			SPIServices.getInstance().getNavigationContextSPI().home(screen);
		}
	}
		
	public void navigate(String screenId)
	{
		//Get the current screen
		String currentId = (String)this.contextManager.getAttribute("current");		 
		
		//Update the history by moving the current screen onto the stack
		if(currentId != null)
		{
			Screen current = ScreenManager.getInstance().find(currentId);
			if(current != null)
			{
				this.history.push(currentId);
			}
		}
		
		//Set the new screen as the current screen
		this.contextManager.setAttribute("current", screenId);
		
		//Display the current screen
		ScreenManager screenManager = ScreenManager.getInstance();
		screenManager.forceRender(screenId);
		Screen screen = screenManager.find(screenId);
		SPIServices.getInstance().getNavigationContextSPI().navigate(screen);
	}
	
	public void refresh()
	{
		String currentId = (String)this.contextManager.getAttribute("current");
		
		if(currentId != null)
		{
			ScreenManager screenManager = ScreenManager.getInstance();
			screenManager.forceRender(currentId);
			Screen screen = screenManager.find(currentId);
			screen.postRender();
			SPIServices.getInstance().getNavigationContextSPI().refresh();
		}
		else
		{
			this.home();
		}
	}
	
	public Screen getCurrentScreen()
	{
		String currentId = (String)this.contextManager.getAttribute("current");
		ScreenManager screenManager = ScreenManager.getInstance();
		Screen screen = screenManager.find(currentId);
		return screen;
	}
	
	public boolean isHome()
	{
		String currentId = (String)this.contextManager.getAttribute("current");
		String homeId = (String)this.contextManager.getAttribute("home");
		
		if(currentId.equals(homeId))
		{
			return true;
		}
		
		return false;
	}
	//-----------------------------------------------------------------------------------------------------------------
	public void setHome(String homeId)
	{
		this.contextManager.setAttribute("home", homeId);
	}		
	//-------------------------------------------------------------------------------------------------------------------
	public void setAttribute(String name, Object value)
	{
		this.contextManager.setAttribute(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return this.contextManager.getAttribute(name);
	}	
	//---Screen State Management API exposed to the App Developers---------------------------------------------------------------------
	public void setAttribute(String screenId, String name, Object value)
	{		
		Screen screen = ScreenManager.getInstance().find(screenId);				
		ScreenContext screenContext = (ScreenContext)AppStateManager.getInstance().
		getContext(screen);		
		
		screenContext.setAttribute(name, value);
	}
	
	public Object getAttribute(String screenId, String name)
	{
		Screen screen = ScreenManager.getInstance().find(screenId);
		ScreenContext screenContext = (ScreenContext)AppStateManager.
		getInstance().getContext(screen);
		
		return screenContext.getAttribute(name);
	}
	
	public void removeAttribute(String screenId, String name)
	{
		Screen screen = ScreenManager.getInstance().find(screenId);
		ScreenContext screenContext = (ScreenContext)AppStateManager.
		getInstance().getContext(screen);
		
		screenContext.removeAttribute(name);
	}
	//------Screen level event handling------------------------------------------------------------------------
	public void addClickListener(Object clickListener)
	{
		EventBusSPI eventBus = SPIServices.getInstance().getEventBusSPI();
		
		String currentId = (String)this.contextManager.getAttribute("current");
		Screen screen = ScreenManager.getInstance().find(currentId);
		
		eventBus.addEventListener(screen,clickListener);
	}
	
	public void sendEvent(Object event)
	{
		EventBusSPI eventBus = SPIServices.getInstance().getEventBusSPI();
		
		eventBus.sendEvent(event);
	}
}
