/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.android.testsuite.RunTestSuiteCommand;
import org.openmobster.core.mobileCloud.android.testsuite.TestContext;
import org.openmobster.core.mobileCloud.android.testsuite.TestSuite;
import org.openmobster.core.mobileCloud.android.testsuite.moblet.ActivationUtil;

import android.content.Context;

/**
 * @author openmobster@gmail.com
 *
 */
public class SyncTestSuiteCommand extends RunTestSuiteCommand
{
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
        	
        	testContext.setAttribute("service", "testServerBean");
			testContext.setAttribute("identifier", "IMEI:8675309");
			
        	
        	//This will cleanup any old configuration
			Context context = Registry.getActiveInstance().getContext();
			Database db = Database.getInstance(context);
			db.dropTable(Database.provisioning_table);
			db.createTable(Database.provisioning_table);
			
			//Activate the device
    		ActivationUtil.activateDevice(suite);
        		        		        	
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
}
