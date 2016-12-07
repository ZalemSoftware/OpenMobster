/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework.events;

import java.lang.reflect.Method;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * @author openmobster@gmail.com
 *
 */
final class Subscription
{
	private Screen screen;
	private Object eventListener;
	private String eventListenerId;
	
	Subscription(Screen screen,Object eventListener)
	{
		this.screen = screen;
		this.eventListener = eventListener;
		
		Class[] interfaces = eventListener.getClass().getInterfaces();
		for(Class interfaceClass:interfaces)
		{
			String className = interfaceClass.getName();
			if(className.endsWith("Listener"))
			{
				String simpleName = interfaceClass.getSimpleName();
				int indexOfListener = simpleName.lastIndexOf("Listener");
				this.eventListenerId = simpleName.substring(0, 
				indexOfListener);
			}
		}
	}
	
	void invokeEvent(Object event)
	{
		try
		{
			Method onClick = this.eventListener.getClass().
			getDeclaredMethod("onClick", 
			event.getClass());
			
			onClick.invoke(this.eventListener, event);
		}
		catch(Exception e)
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
					
			AppResources appResources = Services.getInstance().getResources();
			
			String errorTitle = appResources.localize(
			SystemLocaleKeys.system_error, 
			"system_error");
			
			String errorMsg = appResources.localize(
			SystemLocaleKeys.unknown_system_error, 
			"unknown_system_error");
			
			//Show the dialog
			AlertDialog alert = new AlertDialog.Builder(currentActivity).
	    	setTitle(errorTitle).
	    	setMessage(errorMsg).
	    	setCancelable(false).
	    	create();
			
			alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", 
					new DialogInterface.OnClickListener() 
					{
						
						public void onClick(DialogInterface dialog, int status)
						{
							dialog.cancel();
						}
					}
		    	);
		    	
		    alert.show();
		}
	}
	
	boolean doesItMatchForDelivery(Screen currentScreen,String eventId)
	{
		if(currentScreen.getId().equals(this.screen.getId())
				&&
		   eventId.equals(this.eventListenerId)
		)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Subscription)
		{
			Subscription newObj = (Subscription)obj;
			if(this.screen.getId().equals(newObj.screen.getId())
					&&
			   this.eventListenerId.equals(newObj.eventListenerId)
			)
			{
				return true;
			}
		}
		return false;
	}
}
