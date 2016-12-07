/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.server.engine;

import java.util.Set;

import org.openmobster.core.common.ServiceManager;
import junit.framework.TestCase;

/**
 * 
 * @author openmobster@gmail.com
 */
public class TestAppToChannelPersistence extends TestCase
{
	/**
	 * 
	 */
	protected void setUp() throws Exception
	{
		ServiceManager.bootstrap();
	}

	/**
	 * 
	 */
	protected void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testDuplicateHandling() throws Exception
	{
		AppToChannelAssociation association = new AppToChannelAssociation("deviceId","app", "channel");
		
		//Testing duplicate handling
		AppToChannelAssociation.associate(association);
		AppToChannelAssociation.associate(association);
		
		Set<String> apps = AppToChannelAssociation.getApps("deviceId", "channel");
		
		this.assertEquals(apps.size(), 1);
		this.assertTrue(apps.contains("app"));
	}
	
	public void testFullScenario() throws Exception
	{
		String deviceId = "deviceId";
		String channel = "channel";
		
		for(int i=0; i<10; i++)
		{
			String app = i+"://app";
			AppToChannelAssociation.associate(new AppToChannelAssociation(deviceId,app,channel));
		}
		
		Set<String> apps = AppToChannelAssociation.getApps(deviceId, channel);
		this.assertEquals(10, apps.size());
	}
}
