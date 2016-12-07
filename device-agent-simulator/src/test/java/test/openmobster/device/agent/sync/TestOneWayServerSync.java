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
public class TestOneWayServerSync extends AbstractSync 
{
	public void testAdd() throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
				
		this.performOneWayServerSync();
		
		//Assert State of the Server
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerPresence(this.newServerRecordId);
		this.assertServerAbsence(this.newDeviceRecordId);
		
		//Assert State of the Device
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDevicePresence(this.newServerRecordId);
		this.assertDevicePresence(this.newDeviceRecordId);
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
	
	public void testReplace() throws Exception
	{
		MobileObject deviceRecord = this.updateDeviceRecord("unique-1");
		ServerRecord serverRecord = this.updateServerRecord("unique-2");
		
		this.performOneWayServerSync();
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerMessage("unique-1", this.getServerRecord("unique-1").getMessage());
		this.assertServerMessage("unique-2", serverRecord.getMessage());
		
		//Assert Device State
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceMessage("unique-1", deviceRecord.getValue("message"));
		this.assertDeviceMessage("unique-2", serverRecord.getMessage());
		
		//TODO: Assert Server Sync State
		
		//TODO: Assert Device Sync State
	}
	
	public void testDelete() throws Exception
	{
		this.deleteDeviceRecord("unique-1");
		this.deleteServerRecord("unique-2");
		
		this.performOneWayServerSync();
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerAbsence("unique-2");
				
		//Assert Device State
		this.assertDeviceAbsence("unique-1");
		this.assertDeviceAbsence("unique-2");		
		
		//TODO: Assert Server Sync State
		
		//TODO: Assert Device Sync State
	}
}
