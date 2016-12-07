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
public class TestObjectStreaming extends AbstractSync 
{
	public void test() throws Exception
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
		this.assertDevicePresence(this.newServerRecordId);
		this.assertDevicePresence(this.newDeviceRecordId);
		
		//Assert the partial non-attachment nature of the record newServerRecordId
		MobileObject deviceRecord = this.getDeviceRecord(this.newServerRecordId);
		assertNotNull("Device Record to be Streamed must be present!!!", deviceRecord);
		
		//Object attachment = deviceRecord.getValue("attachment");
		//assertNull("Attachment should not be present before streaming!!", attachment);
		
		this.performStreamSync(this.newServerRecordId);
		
		deviceRecord = this.getDeviceRecord(this.newServerRecordId);
		Object attachment = deviceRecord.getValue("attachment");
		assertNotNull("Attachment should *now* be present after streaming!!", attachment);
	}	
}
