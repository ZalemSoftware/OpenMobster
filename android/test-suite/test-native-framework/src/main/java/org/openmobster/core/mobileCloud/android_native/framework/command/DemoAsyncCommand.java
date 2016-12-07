/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework.command;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author openmobster@gmail.com
 *
 */
public final class DemoAsyncCommand implements AsyncCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		Toast.makeText((Activity)commandContext.getAppContext(), 
				"AsyncCommand about to execute........", 
				Toast.LENGTH_SHORT).show();		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//Simulate network latency
			Thread.currentThread().sleep(10000);
			System.out.println("-------------------------------------------------------");
			System.out.println("Demo Async Command successfully executed...............");
			System.out.println("-------------------------------------------------------");						
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		ViewHelper.getOkModal((Activity)commandContext.getAppContext(), 
		"Success", "Async Command success...").show();
		
		//An Async Command should not navigate away from the screen that launch it...it can result in yucky UI errors
		//Services.getInstance().getNavigationContext().navigate("async");
	}
	
	public void doViewError(CommandContext commandContext)
	{
		ViewHelper.getOkModal((Activity)commandContext.getAppContext(), 
				"Error", "DemoAsyncCommand had an error!!").show();
	}
}
