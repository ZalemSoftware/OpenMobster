/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.module.connection.NetSession;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractAPITest extends Test 
{
	protected final String syncService = "sync://SyncService";
	protected final String service = "testServerBean";
	
	public void setUp() 
	{		 
		MobileObjectDatabase.getInstance().deleteAll(service);
		this.resetServerAdapter("setUp="+this.getClass().getName()+"/App/org.openmobster.core.mobileCloud\n");
		this.resetServerAdapter("setUp="+this.getClass().getName()+"/SetUpAPITestSuite\n");
	}
	
	public void tearDown() 
	{
		MobileObjectDatabase.getInstance().deleteAll(service);
		this.resetServerAdapter("setUp="+this.getClass().getName()+"/CleanUp\n");	
	}
	
	protected void startBootSync() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation(
		"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
		SyncInvocation.bootSync, this.service);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
	
	protected void startBootSyncForPush() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation(
		"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
		SyncInvocation.bootSync, "twitterChannel");		
		Bus.getInstance().invokeService(syncInvocation);		
	}
		
	protected void startOneWayDeviceSync() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation(
		"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
		SyncInvocation.oneWayDeviceOnly, this.service);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
	
	protected void startOneWayServerSync() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation(
		"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
		SyncInvocation.oneWayServerOnly, this.service);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
	
	protected void startLoadProxyDaemon() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation(
		"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
		SyncInvocation.proxySync, this.service);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
		
	protected void waitForBeans() throws Exception
	{
		Set<MobileObject> all = new HashSet<MobileObject>();
		int retries = 90;
		do
		{
			all = MobileObjectDatabase.getInstance().readAll(this.service);
			
			if(all != null && !all.isEmpty())
			{
				break;
			}
							
			Thread.currentThread().sleep(1000);
		}while(retries-- > 0);
	}	
	
	protected void resetServerAdapter(String payload)
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
}
