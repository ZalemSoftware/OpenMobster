/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite.ui.framework;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.Command;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;

import org.openmobster.core.mobileCloud.android.testsuite.RunTestSuiteCommand;


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
			
			((RunTestSuiteCommand)command).doViewBefore(commandContext);
			new BusyAsyncTask().execute(this.commandContext);
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
