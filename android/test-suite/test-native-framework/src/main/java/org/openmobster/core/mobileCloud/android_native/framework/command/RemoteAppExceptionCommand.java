/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework.command;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author openmobster@gmail.com
 *
 */
public final class RemoteAppExceptionCommand implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		Toast.makeText((Activity)commandContext.getAppContext(), 
				"RemoteAppExceptionCommand about to execute........", 
				Toast.LENGTH_SHORT).show();	
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			Thread.currentThread().sleep(10000);
			System.out.println("-------------------------------------------------------");
			System.out.println("RemoteAppExceptionCommand successfully executed...............");
			System.out.println("-------------------------------------------------------");
			throw new AppException();
		}
		catch(Exception e)
		{
			throw new AppException();
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{		
	}
	
	public void doViewError(CommandContext commandContext)
	{
		ViewHelper.getOkModal((Activity)commandContext.getAppContext(), 
				"Error", "RemoteAppException had an AppException....").show();
	}
}
