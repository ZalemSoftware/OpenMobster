/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import java.util.List;

import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.sync.engine.SyncEngine;

import org.openmobster.core.common.ServiceManager;

import test.openmobster.device.agent.sync.server.ServerRecord;



/**
 * 
 * @author openmobster@gmail.com
 */
public class TestDeviceErrorHandling extends AbstractSync
{
	protected void setUp() throws Exception
	{
		super.setUp();
		
		//Replace the client sync engine with the one which simulates errors
		this.deviceSyncEngine = (SyncEngine)ServiceManager.
		locate("test://errors/SyncEngine");
	}
	
	public void testRecordLevelErrorHandling() throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
				
		this.performTwoWaySync();
		
		//Assert State of the Server
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerPresence(this.newServerRecordId);
		this.assertServerPresence(this.newDeviceRecordId);
		
		//Assert State of the Device
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceAbsence(this.newServerRecordId);
		this.assertDevicePresence(this.newDeviceRecordId);
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
	
	public void testErrorInChunkProcessing() throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		
		//Perform Sync
		this.performLongObjectTwoWaySync(150);
		
		//Assert State of the Server
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerPresence(this.newServerRecordId);		
		
		//Assert State of the Device
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDeviceAbsence(this.newServerRecordId);
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
}
