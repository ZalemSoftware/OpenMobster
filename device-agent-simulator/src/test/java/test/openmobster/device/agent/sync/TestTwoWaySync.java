/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import test.openmobster.device.agent.sync.server.ServerRecord;

import org.openmobster.core.common.Utilities;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

/**
 * @author openmobster@gmail.com
 */
public class TestTwoWaySync extends AbstractSync 
{
	public void testAdd() throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
		this.createNewDeviceRecords();
		this.createNewServerRecords();
				
		this.performTwoWaySync();
		
		//Assert State of the Server
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerPresence(this.newServerRecordId);
		this.assertServerPresence(this.newDeviceRecordId);
		
		//Assert State of the Device
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDevicePresence(this.newServerRecordId);
		this.assertDevicePresence(this.newDeviceRecordId);
		
		//Assert total # of records on the server
		this.assertTrue(this.deviceDatabase.readByStorage(this.service).size()==19);
		this.assertTrue(this.serverController.readAll().size()==19);
		
		//TODO: Assert the State of the Server Sync Engine
		ServerRecord record = this.serverController.readServerRecord(this.newServerRecordId);
		String attachment = new String(record.getAttachment());
		System.out.println("************Attachment******************");
		System.out.println(attachment);
		System.out.println("****************************************");
		
		//TODO: Assert the State of teh Device Sync Engine
		MobileObject mobileObject = this.deviceDatabase.read(this.service, this.newServerRecordId);
		String attachment2 = mobileObject.getValue("attachment");
		attachment2 = new String(Utilities.decodeBinaryData(attachment2));
		System.out.println("************Attachment******************");
		System.out.println(attachment2);
		System.out.println("****************************************");
		
		assertEquals(attachment,attachment2);
	}
	
	public void testReplace() throws Exception
	{
		MobileObject deviceRecord = this.updateDeviceRecord("unique-1");
		ServerRecord serverRecord = this.updateServerRecord("unique-2");
		
		this.performTwoWaySync();
		
		//Assert Server State
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerMessage("unique-1", deviceRecord.getValue("message"));
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
		
		this.performTwoWaySync();
		
		//Assert Server State
		this.assertServerAbsence("unique-1");
		this.assertServerAbsence("unique-2");
				
		//Assert Device State
		this.assertDeviceAbsence("unique-1");
		this.assertDeviceAbsence("unique-2");		
		
		//TODO: Assert Server Sync State
		
		//TODO: Assert Device Sync State
	}
}
