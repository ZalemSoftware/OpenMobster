/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.android.testsuite.TestContext;
import org.openmobster.core.mobileCloud.android.testsuite.TestSuite;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import android.app.Activity;

/**
 * @author openmobster@gmail.com
 *
 */
public class RunTestSuiteCommand implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{	
	}

	public void doAction(CommandContext commandContext)
	{
		try
		{
    		TestSuite suite = new TestSuite();
    		commandContext.setAttribute("testsuite", suite);
    		
        	TestContext testContext = new TestContext();
        	suite.setContext(testContext);
        	suite.getContext().setAttribute("android:context", 
        	Registry.getActiveInstance().getContext());
        		        		        	
        	//Load the tests
        	suite.load();
        	
        	suite.execute(commandContext);
		}
		catch(Exception e)
    	{
    		e.printStackTrace(System.out);
    		throw new RuntimeException(e);
    	}
	}

	public void doViewAfter(CommandContext commandContext)
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "Test Suite", (String)commandContext.getAttribute("status")).
		show();
	}

	public void doViewError(CommandContext commandContext)
	{
		//Shows an Error Dialog
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		commandContext.getAppException().getMessage()).
		show();
	}
}
