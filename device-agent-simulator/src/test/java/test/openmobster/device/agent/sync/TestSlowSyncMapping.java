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
public class TestSlowSyncMapping extends AbstractSyncMapping
{
	public void testAdd() throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
				
		this.performSlowSync();
		
		//Assert State of the Server
		this.assertServerPresence("1");
		this.assertServerPresence("2");
		this.assertServerPresence("3");
		this.assertServerPresence("4");
		
		//Assert State of the Device
		this.assertDevicePresence("1-luid");
		this.assertDevicePresence("2-luid");
		this.assertDevicePresence("3-luid");
		this.assertDevicePresence(this.newDeviceRecordId+"-luid");
		
		this.assertMappingWithNewObjects();
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
	
	public void testReplace() throws Exception
	{
		MobileObject deviceRecord = this.updateDeviceRecord("1-luid");
		ServerRecord serverRecord = this.updateServerRecord("2");
		
		this.performSlowSync();
		
		//RULE: In a SlowSync, device data overwrite server side data
		
		//Assert Server State
		this.assertServerPresence("1");
		this.assertServerPresence("2");
		this.assertServerMessage("1", deviceRecord.getValue("message"));
		this.assertServerMessage("2", this.getDeviceRecord("2-luid").getValue("message"));
		
		//Assert Device State
		this.assertDevicePresence("1-luid");
		this.assertDevicePresence("2-luid");
		this.assertDeviceMessage("1-luid", deviceRecord.getValue("message"));
		this.assertDeviceMessage("2-luid", this.getDeviceRecord("2-luid").getValue("message"));
		
		this.assertMapping();
		
		//TODO: Assert Server Sync State
		
		//TODO: Assert Device Sync State
	}
	
	public void testDelete() throws Exception
	{
		this.deleteDeviceRecord("1-luid");
		this.deleteServerRecord("2");
		
		this.performSlowSync();
		
		//RULE: In a SlowSync, Server Deletes hold, Device Deletes do not hold
		
		//Assert Server State
		this.assertServerPresence("1");
		this.assertServerAbsence("2");
				
		//Assert Device State
		this.assertDevicePresence("1-luid");
		this.assertDeviceAbsence("2-luid");
		
		this.assertMapping();
		
		//TODO: Assert Server Sync State
		
		//TODO: Assert Device Sync State
	}
	
	public void testConflict() throws Exception
	{
		MobileObject deviceRecord = this.updateDeviceRecord("1-luid");
		ServerRecord serverRecord = this.updateServerRecord("1");
		
		this.performSlowSync();
		
		//Rule for SlowSync: Server State wins over device state
		
		//Assert Server State
		this.assertServerPresence("1");
		this.assertServerPresence("2");
		this.assertServerMessage("1", serverRecord.getMessage());
		
		//Assert Device State
		this.assertDevicePresence("1-luid");
		this.assertDevicePresence("2-luid");
		this.assertDeviceMessage("1-luid", serverRecord.getMessage());
		
		this.assertMapping();
	}
}
