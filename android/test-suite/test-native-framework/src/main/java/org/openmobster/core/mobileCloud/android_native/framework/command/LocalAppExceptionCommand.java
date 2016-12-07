/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework.command;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author openmobster@gmail.com
 *
 */
public final class LocalAppExceptionCommand implements LocalCommand
{
	public void doViewBefore(CommandContext commandContext)
	{		
		Toast.makeText((Activity)commandContext.getAppContext(), 
		"LocalAppExceptionCommand about to execute........", 
		Toast.LENGTH_SHORT).show();
	}

	public void doAction(CommandContext commandContext) 
	{
		System.out.println("-------------------------------------------------------");
		System.out.println("LocalAppExceptionCommand successfully executed...............");
		System.out.println("-------------------------------------------------------");
		AppException appe = new AppException();
		ErrorHandler.getInstance().handle(appe);
		throw appe;
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{		
	}
	
	public void doViewError(CommandContext commandContext)
	{				
		ViewHelper.getOkModal((Activity)commandContext.getAppContext(), 
		"Error", "LocalAppException had an AppException....").show();
	}
}
