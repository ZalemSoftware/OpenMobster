/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.connection;

import java.util.Properties;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.android.testsuite.RunTestSuiteCommand;
import org.openmobster.core.mobileCloud.android.testsuite.TestSuite;

import android.content.Context;

/**
 * @author openmobster@gmail.com
 *
 */
public class ConnectionTestSuiteCommand extends RunTestSuiteCommand
{
	public void doAction(CommandContext commandContext)
	{
		try
		{
			Properties properties = new Properties();
			properties.load(TestSuite.class.getResourceAsStream("/moblet-app/activation.properties"));
			String cloudServer = properties.getProperty("cloud_server_ip");
			
			//Load some info into the configuration
        	Context context = Registry.getActiveInstance().getContext();
        	Configuration conf = Configuration.getInstance(context);
        	conf.setServerIp(cloudServer);
        	conf.setPlainServerPort("1502");
        	conf.setSecureServerPort("1500");
        	conf.deActivateSSL();
        	conf.setActive(true);
        	conf.save(context);
        	
        	super.doAction(commandContext);
		}
		catch(Exception e)
    	{
    		e.printStackTrace(System.out);
    		throw new RuntimeException(e);
    	}
	}
}
