/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite.ui.framework;

import android.os.Handler;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;


/**
 * @author openmobster@gmail.com
 */
public final class TestSuiteCommandService extends CommandService
{		
	private static TestSuiteCommandService singleton;
	
	public TestSuiteCommandService()
	{
		
	}
	
	public static TestSuiteCommandService getInstance()
	{
		if(singleton == null)
		{
			synchronized(TestSuiteCommandService.class)
			{
				if(singleton == null)
				{
					singleton = new TestSuiteCommandService();
				}
			}
		}
		return singleton;
	}
	
	public void execute(CommandContext commandContext) 
	{
		try
		{						
			Handler handler = new Handler();
			
			commandContext.setAppContext(Registry.getActiveInstance().
			getContext());
			handler.post(new ExecuteOnEDT(commandContext));
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
