/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBootupChannel extends Test
{
	public void runTest()
	{		
		try
		{	
			this.startBootSync("bootChannel");
			
			boolean isBooted = MobileBean.isBooted("bootChannel");
			this.assertTrue(!isBooted, this.getInfo()+"/ChannelShouldNotBeBooted");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
	
	private void startBootSync(String channel) throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation(
		"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
		SyncInvocation.bootSync, channel);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
}
