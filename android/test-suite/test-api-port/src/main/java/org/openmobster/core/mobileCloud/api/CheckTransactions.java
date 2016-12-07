/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.module.connection.NetSession;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;

import android.app.Activity;
import android.content.Context;

/**
 * The MVC AsyncCommand. The 'AsyncCommand' is used to perform actions asynchronously (Ajax from the web world). 
 * 
 * This particular AsyncCommand, invokes the 'GetDetails' service in the Cloud and gets a fully populated Email instance for display.
 * 
 * @author openmobster@gmail.com
 *
 */
public final class CheckTransactions implements RemoteCommand
{
	private static final String channel = "txcheck";
	
	//Executes on the UI thread. All UI related operations are safe here. It is invoked to perform some pre-action UI related tasks.
	public void doViewBefore(CommandContext commandContext)
	{	
		//Nothing to do
	}

	//This does not execute on the UI thread. When this method is invoked, the UI thread is freed up, so that its not frozen while the 
	//information is being loaded from the Cloud
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//Cleanup
			MobileObjectDatabase.getInstance().deleteAll(channel);
			this.resetServerAdapter("setUp=setupTXCheck");
			
			//Setup the beans to be added
			for(int i=0; i<2; i++)
			{
				String uniqueId = "unique-"+String.valueOf(i);
				MobileBean bean = MobileBean.newInstance(channel);
				bean.setValue("name", uniqueId);
				bean.save();					
			}
			
			this.startTwoWaySync();
			
			//Dump the state in the cloud
			this.resetServerAdapter("setUp=dumpTXCheck");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			
			//throw an AppException. If this happens, the doViewError will be invoked to alert the user with an error message
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			
			//Record this error in the Cloud Error Log
			ErrorHandler.getInstance().handle(appe);
			
			throw appe;
		}
	}	
	
	//Executes on the UI thread. All UI operations are safe. It is invoked after the doAction is executed without any errors.
	//From an Ajax standpoint, consider this invocation as the UI callback
	public void doViewAfter(CommandContext commandContext)
	{

	}
	
	//Executes on the UI thread. All UI operations are safe. This method is invokes if there is an error during the doAction execution.
	//From an Ajax standpoint, consider this invocation as a UI callback
	public void doViewError(CommandContext commandContext)
	{
		//Shows an Error Alert
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		commandContext.getAppException().getMessage()).
		show();
	}
	//----------------------------------------------------------------------------------------------------------------------
	private void resetServerAdapter(String payload)
	{		
		NetSession netSession = null;
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			boolean secure = Configuration.getInstance(context).isSSLActivated();
			netSession = NetworkConnector.getInstance().openSession(secure);
			
			String request =
				"<request>" +
						"<header>" +
						"<name>processor</name>"+
						"<value>testsuite</value>"+
					"</header>"+
				"</request>";
			String response = netSession.sendTwoWay(request);
			
			if(response.indexOf("status=200")!=-1)
			{
				netSession.sendOneWay(payload);				
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
		finally
		{
			if(netSession != null)
			{
				netSession.close();
			}
		}
	}
	
	private void startTwoWaySync() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation(
		"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
		SyncInvocation.twoWay,channel);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
}
