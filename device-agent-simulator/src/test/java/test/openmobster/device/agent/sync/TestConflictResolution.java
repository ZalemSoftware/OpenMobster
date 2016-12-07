/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import test.openmobster.device.agent.sync.server.ServerRecord;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

/**
 * @author openmobster@gmail.com
 */
public class TestConflictResolution extends AbstractSync 
{
	public void testOptimisticLockStateManagement() throws Exception
	{
		//Make sure both nodes are prepared
		this.deviceDatabase.deleteAll(this.service);
		this.performSlowSync();
		
		MobileObject deviceRecord = this.updateDeviceRecord("unique-1");
		
		this.performTwoWaySync();
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerMessage("unique-1", deviceRecord.getValue("message"));
		
		//Assert Device State
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceMessage("unique-1", deviceRecord.getValue("message"));
		
		deviceRecord = this.updateDeviceRecord("unique-1","updated again!!");
		
		this.performTwoWaySync();
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerMessage("unique-1", deviceRecord.getValue("message"));
		
		//Assert Device State
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceMessage("unique-1", deviceRecord.getValue("message"));
	}
	
	public void testTwoWaySyncConflict() throws Exception
	{
		//Make sure both nodes are prepared
		this.deviceDatabase.deleteAll(this.service);
		this.performSlowSync();
		
		
		MobileObject deviceRecord = this.updateDeviceRecord("unique-1");
		ServerRecord serverRecord = this.updateServerRecord("unique-1");
		
		this.performTwoWaySync();
		
		//Rule for TwoWaySync: The conflict will be resolved based on Conflict Resolution Policy associated with the Channel
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerMessage("unique-1", serverRecord.getMessage());
		
		//Assert Device State
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceMessage("unique-1", serverRecord.getMessage());
	}
	
	public void testOneWayServerSyncConflict() throws Exception
	{
		//Make sure both nodes are prepared
		this.deviceDatabase.deleteAll(this.service);
		this.performSlowSync();
		
		MobileObject deviceRecord = this.updateDeviceRecord("unique-1");
		ServerRecord serverRecord = this.updateServerRecord("unique-1");
		
		this.performOneWayServerSync();
		
		//Rule for OneWayServerSync: The conflict will be resolved based on Conflict Resolution Policy associated with the Channel
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerMessage("unique-1", serverRecord.getMessage());
		
		//Assert Device State
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceMessage("unique-1", serverRecord.getMessage());
	}
	
	public void testOneWayDeviceSyncConflict() throws Exception
	{
		//Make sure both nodes are prepared
		this.deviceDatabase.deleteAll(this.service);
		this.performSlowSync();
		
		MobileObject deviceRecord = this.updateDeviceRecord("unique-1");
		ServerRecord serverRecord = this.updateServerRecord("unique-1");
		
		this.performOneWayDeviceSync();
		
		//Rule for OneWayDeviceSync: The conflict will be resolved based on Conflict Resolution Policy associated with the Channel
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerMessage("unique-1", serverRecord.getMessage());
		
		//Assert Device State
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceMessage("unique-1", deviceRecord.getValue("message"));
	}
	
	public void testConflictSlowSync() throws Exception
	{
		//Make sure both nodes are prepared
		this.deviceDatabase.deleteAll(this.service);
		this.performSlowSync();
		
		MobileObject deviceRecord = this.updateDeviceRecord("unique-1");
		ServerRecord serverRecord = this.updateServerRecord("unique-1");
		
		this.performSlowSync();
		
		//Rule for SlowSync: The conflict will be resolved based on Conflict Resolution Policy associated with the Channel
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerMessage("unique-1", serverRecord.getMessage());
		
		//Assert Device State
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceMessage("unique-1", serverRecord.getMessage());
	}
	
	public void testTwoWaySyncUpdateDeleteConflict() throws Exception
	{
		//Make sure both nodes are prepared
		this.deviceDatabase.deleteAll(this.service);
		this.performSlowSync();
		
		
		MobileObject deviceRecord = this.updateDeviceRecord("unique-1");
		this.deleteServerRecord("unique-1");
		
		//Assert Server State
		this.assertServerAbsence("unique-1");
		this.assertServerPresence("unique-2");
		
		//Assert Device State
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		
		this.performTwoWaySync();
		
		//Rule for TwoWaySync: The conflict will be resolved based on Conflict Resolution Policy associated with the Channel
		
		//Assert Server State
		this.assertServerAbsence("unique-1");
		this.assertServerPresence("unique-2");
		
		//Assert Device State
		this.assertDeviceAbsence("unique-1");
		this.assertDevicePresence("unique-2");
	}
	
	public void testTwoWaySyncUpdateDeleteConflict2() throws Exception
	{
		//Make sure both nodes are prepared
		this.deviceDatabase.deleteAll(this.service);
		this.performSlowSync();
		
		
		this.updateServerRecord("unique-1");
		this.deleteDeviceRecord("unique-1");
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		
		//Assert Device State
		this.assertDeviceAbsence("unique-1");
		this.assertDevicePresence("unique-2");
		
		this.performTwoWaySync();
		
		//Rule for TwoWaySync: The conflict will be resolved based on Conflict Resolution Policy associated with the Channel
		
		//Assert Server State
		this.assertServerAbsence("unique-1");
		this.assertServerPresence("unique-2");
		
		//Assert Device State
		this.assertDeviceAbsence("unique-1");
		this.assertDevicePresence("unique-2");
	}
}
