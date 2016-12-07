/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

import test.openmobster.device.agent.sync.server.ServerRecord;

/**
 * @author openmobster@gmail.com
 */
public class TestBootSync extends AbstractSync
{
	public void testBootSync() throws Exception
	{
		this.deviceDatabase.deleteAll(this.service);
		this.createNewServerRecord();
				
		this.performBootSync();
		
		//Assert State of the Server
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerPresence(this.newServerRecordId);
		
		//Assert State of the Device
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDevicePresence(this.newServerRecordId);
		this.assertFalse("unique-1 must *not* be empty", this.getDeviceRecord("unique-1").isProxy());
		this.assertTrue("unique-2 must be empty", this.getDeviceRecord("unique-2").isProxy());
		this.assertTrue(this.newServerRecordId+" must be empty", this.getDeviceRecord(this.newServerRecordId).isProxy());
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
}
