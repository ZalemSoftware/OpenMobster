/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.command;

import java.lang.StringBuffer;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import android.app.Activity;


/**
 * Invoked from the 'HomeScreen' to show the details associated with a locally stored/synchronized bean
 * 
 * This is a 'LocalCommand' as it executes very fast, since it works with locally stored data.
 * 
 * @author openmobster@gmail.com
 *
 */
public final class DemoDetails implements LocalCommand
{
	/**
	 * pre-action UI thread call. Nothing to do here
	 */
	public void doViewBefore(CommandContext commandContext)
	{		
	}

	/**
	 * Finds the appropriate bean and sets it into the CommandContext for display
	 */
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//Find the selected bean from the HomeScreen
			String channel = "offlineapp_demochannel";
			String selectedBean = (String)commandContext.getAttribute("selectedBean");
			
			//System.out.println("---------------------------------------");
			//System.out.println("Bean: "+selectedBean);
			//System.out.println("---------------------------------------");
			
			String details = null;
			
			//Lookup by state..in this case, that of 'demoString' field of the bean
			GenericAttributeManager criteria = new GenericAttributeManager();
			criteria.setAttribute("demoString", selectedBean);
			MobileBean[] beans = MobileBean.queryByEqualsAll(channel, criteria);
			MobileBean unique = beans[0];
		
			//Sets up the String that will be displayed
			StringBuffer buffer = new StringBuffer();
			buffer.append("DemoString: "+unique.getValue("demoString"));
			details = buffer.toString();
			
			//Sets up the state of the CommandContext
			commandContext.setAttribute("details", details);
		}
		catch(Exception e)
		{
			//e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
	
	/**
	 * Invoked post-action on the UI thread.
	 * 
	 * Displays the "Details" in a Modal Dialog
	 */
	public void doViewAfter(CommandContext commandContext)
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		ViewHelper.getOkModal(currentActivity, "Details", 
		(String)commandContext.getAttribute("details")).
		show();
	}
	
	/**
	 * Invoked on the UI thread if an error occured
	 */
	public void doViewError(CommandContext commandContext)
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		ViewHelper.getOkModal(currentActivity, "App Error", 
		this.getClass().getName()+" had an error!!").
		show();
	}
}
