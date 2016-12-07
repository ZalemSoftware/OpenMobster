/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

/**
 * @author openmobster@gmail.com
 */
public class TestMultiDeviceTwoWaySync extends AbstractSync
{
	private static Logger log = Logger.getLogger(TestMultiDeviceTwoWaySync.class);
	
	private String otherDeviceId = "IMEI:4930052";
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		this.createNewServerRecord();
		
		List serverChangeLog = new ArrayList();
		ChangeLogEntry serverEntry = new ChangeLogEntry();
		serverEntry.setNodeId(this.service);
		serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
		serverEntry.setRecordId(this.newServerRecordId);
		serverChangeLog.add(serverEntry);
		this.serverSyncEngine.addChangeLogEntries(this.otherDeviceId, this.app, serverChangeLog);
	}
	
	public void test() throws Exception
	{
		this.assertServerChangeLogPresence(this.deviceId, this.newServerRecordId);
		this.assertServerChangeLogPresence(this.otherDeviceId, this.newServerRecordId);
		
		//Sync with first device
		this.createNewDeviceRecord();		
		this.performTwoWaySync();
		
		this.assertServerChangeLogAbsence(this.deviceId, this.newServerRecordId);
		this.assertServerChangeLogPresence(this.otherDeviceId, this.newServerRecordId);
				
		this.bootAnotherDevice(this.otherDeviceId);
		this.createNewDeviceRecord();		
		this.performTwoWaySync();
		
		this.assertServerChangeLogAbsence(this.deviceId, this.newServerRecordId);
		this.assertServerChangeLogAbsence(this.otherDeviceId, this.newServerRecordId);        				        
	}
}
