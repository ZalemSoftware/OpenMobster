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
public class TestMapSupportErrorHandling extends AbstractSyncMapping 
{
	public void testDeferMapUpdateToNextSync() throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
		
		this.executeErrorSync(true, false, false);
		
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
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine	
		
		//Assert Map Engine
		String mapInfo = this.mapEngine.mapFromServerToLocal("3");
		assertEquals("Should not be mapped!!",mapInfo, "3");
		
		this.executeErrorSync(true, false, false);
		
		//Assert Map Engine
		mapInfo = this.mapEngine.mapFromServerToLocal("3");
		assertEquals("Should be mapped now!!",mapInfo, "3-luid");
	}
	
	public void testDeferMapUpdateToNextSyncFailure() throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
		
		this.executeErrorSync(true, true, false);
		
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
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
		
		//Assert Map Engine
		String mapInfo = this.mapEngine.mapFromServerToLocal("3");
		assertEquals("Should not be mapped!!",mapInfo, "3");
		
		for(int i=0; i<2; i++)
		{
			this.executeErrorSync(true, true, false);
			
			//Assert Map Engine
			mapInfo = this.mapEngine.mapFromServerToLocal("3");
			assertEquals("Should not be mapped!!",mapInfo, "3");
		}
	}
	
	public void testDeferMapUpdateToNextSyncClientPersistFailure() throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
		
		this.executeErrorSync(true, false, true);
		
		//Assert State of the Server
		this.assertServerPresence("1");
		this.assertServerPresence("2");
		this.assertServerPresence("3");
		this.assertServerPresence("4");
		
		//Assert State of the Device........Device Data should be fully reset
		//Trigerring a SlowSync
		this.assertDeviceAbsence("1-luid");
		this.assertDeviceAbsence("2-luid");
		this.assertDeviceAbsence("3-luid");
		this.assertDeviceAbsence(this.newDeviceRecordId+"-luid");
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine	
		
		//Assert Map Engine
		String mapInfo = this.mapEngine.mapFromServerToLocal("3");
		assertEquals("Should not be mapped!!",mapInfo, "3");
		
		//This should trigger a SlowSync from device
		this.executeErrorSync(false, false, false);
		
		//Assert Map Engine
		mapInfo = this.mapEngine.mapFromServerToLocal("3");
		assertEquals("Should be mapped now!!",mapInfo, "3-luid");
	}
}
