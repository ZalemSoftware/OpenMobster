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
public class TestLongObject extends AbstractSync 
{
	private long size = 729;
	
	public void testTwoChunkSupport() throws Exception
	{
		this.performSingleCommand((int)(size/2));
	}
	
	public void testMoreThanTwoChunkSupport() throws Exception
	{
		this.performSingleCommand(50);
	}	
	
	public void testMultiChunkedCommandSupport() throws Exception
	{
		this.performMultiCommand((int)(size/2));
	}
	
	public void testMultiCommandSupport() throws Exception
	{
		this.performMultiCommand(0);
	}
	
	public void testTwoChunkSupportSlowSync() throws Exception
	{
		this.performSingleCommandSlowSync((int)(size/2));
	}
	
	public void testMoreThanTwoChunkSupportSlowSync() throws Exception
	{
		this.performSingleCommandSlowSync(50);
	}	
	
	public void testMultiChunkedCommandSupportSlowSync() throws Exception
	{
		this.performMultiCommandSlowSync((int)(size/2));
	}
	
	public void testMultiCommandSupportSlowSync() throws Exception
	{
		this.performMultiCommandSlowSync(0);
	}
	//-----------------------------------------------------------------------------------------------------
	private void performSingleCommand(int size) throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
				
		this.performLongObjectTwoWaySync(size);
		
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
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
	
	private void performMultiCommand(int size) throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
		this.createNewServerRecord("unique-5");
		this.createNewServerRecord("unique-6");
		this.createNewServerRecord("unique-7");
				
		this.performLongObjectTwoWaySync(size);
		
		//Assert State of the Server
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerPresence("unique-5");
		this.assertServerPresence("unique-6");
		this.assertServerPresence("unique-7");
		this.assertServerPresence(this.newServerRecordId);
		this.assertServerPresence(this.newDeviceRecordId);
		
		//Assert State of the Device
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDevicePresence("unique-5");
		this.assertDevicePresence("unique-6");
		this.assertDevicePresence("unique-7");
		this.assertDevicePresence(this.newServerRecordId);
		this.assertDevicePresence(this.newDeviceRecordId);
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
	
	private void performSingleCommandSlowSync(int size) throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
				
		this.performLongObjectSlowSync(size);
		
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
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
	
	private void performMultiCommandSlowSync(int size) throws Exception
	{
		ServerRecord newServerRecord = this.createNewServerRecord();
		MobileObject newDeviceRecord = this.createNewDeviceRecord();
		this.createNewServerRecord("unique-5");
		this.createNewServerRecord("unique-6");
		this.createNewServerRecord("unique-7");
				
		this.performLongObjectSlowSync(size);
		
		//Assert State of the Server
		this.assertServerPresence("unique-1");
		this.assertServerPresence("unique-2");
		this.assertServerPresence("unique-5");
		this.assertServerPresence("unique-6");
		this.assertServerPresence("unique-7");
		this.assertServerPresence(this.newServerRecordId);
		this.assertServerPresence(this.newDeviceRecordId);
		
		//Assert State of the Device
		this.assertDevicePresence("unique-1");
		this.assertDevicePresence("unique-2");
		this.assertDevicePresence("unique-5");
		this.assertDevicePresence("unique-6");
		this.assertDevicePresence("unique-7");
		this.assertDevicePresence(this.newServerRecordId);
		this.assertDevicePresence(this.newDeviceRecordId);
		
		//TODO: Assert the State of the Server Sync Engine
		
		//TODO: Assert the State of teh Device Sync Engine
	}
}
