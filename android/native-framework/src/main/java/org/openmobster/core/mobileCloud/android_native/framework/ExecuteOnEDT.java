/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.Command;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.UIInitiatedCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.SystemInitiatedCommand;

/**
 * @author openmobster@gmail.com
 *
 */
final class ExecuteOnEDT implements Runnable
{
	private CommandContext commandContext;
	
	ExecuteOnEDT(CommandContext commandContext)
	{
		this.commandContext = commandContext;
	}
	
	public void run()
	{
		try
		{
			CommandService service = Services.getInstance().getCommandService();			
			Command command = service.
			findUICommand(commandContext.getTarget());
			
			if(command instanceof LocalCommand)
			{
				LocalCommand localCommand = (LocalCommand)command;
				
				//View Before
				localCommand.doViewBefore(commandContext);				
				
				//Perform action
				try
				{
					localCommand.doAction(commandContext);
				}
				catch(AppException ape)
				{
					service.reportAppException(commandContext, ape);
					command.doViewError(commandContext);
					return;
				}
												
				//View After
				localCommand.doViewAfter(commandContext);
			}
			else if(command instanceof RemoteCommand)
			{
				RemoteCommand remoteCommand = (RemoteCommand)command;
				
				//View Before
				remoteCommand.doViewBefore(commandContext);
				
				//Perform this in async mode
				new BusyAsyncTask().execute(commandContext);
			}
			else if(command instanceof AsyncCommand)
			{
				AsyncCommand asyncCommand = (AsyncCommand)command;
				
				//View Before
				asyncCommand.doViewBefore(commandContext);
				
				//Perform this in the background
				new BackgroundAsyncTask().execute(commandContext);
			}
		}
		catch(Exception e)
		{
			//report to ErrorHandling system
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString(),
				"Target Command:"+commandContext.getTarget()
			}));
			ShowError.showGenericError(commandContext);
		}
	}
}
