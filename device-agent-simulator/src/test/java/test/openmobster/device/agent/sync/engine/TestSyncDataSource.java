/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync.engine;

import java.util.Vector;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.device.agent.sync.Anchor;
import org.openmobster.device.agent.sync.engine.SyncDataSource;
import org.openmobster.device.agent.sync.engine.ChangeLogEntry;
import org.openmobster.device.agent.sync.engine.SyncEngine;
import org.openmobster.device.agent.sync.engine.SyncError;

/**
 * @author openmobster@gmail.com
 */
public class TestSyncDataSource extends TestCase
{
	private SyncDataSource syncDataSource = null;
	
	@Override
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
		
		this.syncDataSource = (SyncDataSource)ServiceManager.locate("simulator://SyncDataSource");
	}

	@Override
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();		
	}
	
	public void testAnchor() throws Exception
	{
		//Create
		this.createAnchor();
		
		//Read
		Anchor anchor = this.syncDataSource.readAnchor("test://target");
		assertNotNull("Anchor should be stored", anchor);
		assertNotNull("Anchor ID should not be null", anchor.getId());
		assertEquals("Anchor Target Does Not Match", "test://target", anchor.getTarget());
		assertEquals("Anchor LastSync Does Not Match", "test://lastSync", anchor.getLastSync());
		assertEquals("Anchor NextSync Does Not Match", "test://nextSync", anchor.getNextSync());
		
		//Update
		anchor.setLastSync("test://lastSync/updated");
		anchor.setNextSync("test://nextSync/updated");
		this.syncDataSource.saveAnchor(anchor);
		
		anchor = this.syncDataSource.readAnchor("test://target");
		assertNotNull("Anchor should be stored", anchor);
		assertNotNull("Anchor ID should not be null", anchor.getId());
		assertEquals("Anchor Target Does Not Match", "test://target", anchor.getTarget());
		assertEquals("Anchor LastSync Does Not Match", "test://lastSync/updated", anchor.getLastSync());
		assertEquals("Anchor NextSync Does Not Match", "test://nextSync/updated", anchor.getNextSync());
		
		//Delete
		this.syncDataSource.deleteAnchor();
		anchor = this.syncDataSource.readAnchor("test://target");
		this.assertNull("Anchor must be deleted", anchor);
	}
	
	public void testChangeLog() throws Exception
	{
		//Create ChangeLog
		this.createChangeLog();
		
		//Read ChangeLog
		Vector changelog = this.syncDataSource.readChangeLog();
		assertTrue("ChangeLog should not be empty", changelog != null && 
		!changelog.isEmpty() && changelog.size()==3);
		
		//Delete a ChangeLog entry
		this.syncDataSource.deleteChangeLogEntry((ChangeLogEntry)changelog.get(0));
		
		changelog = this.syncDataSource.readChangeLog();
		assertTrue("ChangeLog should not be empty", changelog != null && 
		!changelog.isEmpty() && changelog.size()==2);
		
		//Delete the entire changelog
		this.syncDataSource.deleteChangeLog();
		changelog = this.syncDataSource.readChangeLog();
		assertTrue("ChangeLog should be empty", changelog == null || changelog.isEmpty());
	}
	
	public void testRecordMap() throws Exception
	{
		this.createRecordMap();
		
		Hashtable map = this.syncDataSource.readRecordMap("test://source", "test://target");
		assertEquals(map.get("1"), "1-luid");
		assertEquals(map.get("2"), "2-luid");
		assertEquals(map.get("3"), "3-luid");
		
		this.syncDataSource.removeRecordMap("test://source", "test://target");
		
		map = this.syncDataSource.readRecordMap("test://source", "test://target");
		assertTrue(map == null || map.isEmpty());
	}
	
	public void testSyncErrors() throws Exception
	{
		this.createSyncErrors();
		
		SyncError error = this.syncDataSource.readError("test://source", "test://target", 
		SyncError.RESET_SYNC_STATE);
		assertNotNull(error);
		assertNotNull(error.getId());
		
		this.syncDataSource.removeError("test://source", "test://target", 
		SyncError.RESET_SYNC_STATE);
		
		error = this.syncDataSource.readError("test://source", "test://target", 
		SyncError.RESET_SYNC_STATE);
		assertNull(error);		
	}
	//-------------------------------------------------------------------------------------------------------
	private void createAnchor() throws Exception
	{
		Anchor anchor = new Anchor();
		anchor.setTarget("test://target");
		anchor.setLastSync("test://lastSync");
		anchor.setNextSync("test://nextSync");
		this.assertNull("Anchor ID should be null", anchor.getId());
		
		this.syncDataSource.saveAnchor(anchor);
	}
	
	private void createChangeLog() throws Exception
	{
		ChangeLogEntry add = new ChangeLogEntry();
		add.setRecordId("unique-1");
		add.setNodeId("mobileEmail");
		add.setOperation(SyncEngine.OPERATION_ADD);
		
		ChangeLogEntry update = new ChangeLogEntry();
		update.setRecordId("unique-2");
		update.setNodeId("mobileEmail");
		update.setOperation(SyncEngine.OPERATION_UPDATE);
		
		ChangeLogEntry delete = new ChangeLogEntry();
		delete.setRecordId("unique-3");
		delete.setNodeId("mobileEmail");
		delete.setOperation(SyncEngine.OPERATION_DELETE);
		
		Vector entries = new Vector();
		entries.add(add);
		entries.add(update);
		entries.add(delete);
		
		this.syncDataSource.createChangeLogEntries(entries);
	}
	
	private void createRecordMap() throws Exception
	{
		String source = "test://source";
		String target = "test://target";
		Hashtable map = new Hashtable();
		
		map.put("1", "1-luid");
		map.put("2", "2-luid");
		map.put("3", "3-luid");
		
		this.syncDataSource.saveRecordMap(source, target, map);
	}
	
	private void createSyncErrors() throws Exception
	{
		SyncError error = new SyncError();
		error.setCode(SyncError.RESET_SYNC_STATE);
		error.setSource("test://source");
		error.setTarget("test://target");
		
		this.syncDataSource.saveError(error);
	}
}
