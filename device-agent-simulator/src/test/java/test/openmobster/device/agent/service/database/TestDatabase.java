/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.service.database;

import java.util.Enumeration;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.device.agent.sync.engine.ChangeLogEntry;
import org.openmobster.device.agent.sync.engine.SyncEngine;
import org.openmobster.device.agent.service.database.Database;
import org.openmobster.device.agent.service.database.Record;

/**
 * @author openmobster@gmail.com
 */
public class TestDatabase extends TestCase  
{
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
	}
	
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}	
	
	public void testInsert() throws Exception
	{
		ChangeLogEntry entry = new ChangeLogEntry();
		entry.setRecordId("unique-1");
		entry.setNodeId("mobileEmail");
		entry.setOperation(SyncEngine.OPERATION_ADD);
		
		String recordId = Database.getInstance().insert(Database.sync_changelog_table, entry.getRecord());
		assertEquals("RecordId does not match", "unique-1", recordId);
		
		Record storedRecord = Database.getInstance().select(Database.sync_changelog_table, recordId);
		assertNotNull("Record should not be null", storedRecord);
		
		ChangeLogEntry storedEntry = new ChangeLogEntry(storedRecord);		
		assertEquals("RecordId does not match", "unique-1", storedEntry.getRecordId());
		assertEquals("Service does not match", "mobileEmail", storedEntry.getNodeId());
		assertEquals("Operation does not match", SyncEngine.OPERATION_ADD, storedEntry.getOperation());
	}
	
	public void testUpdate() throws Exception
	{
		ChangeLogEntry entry = new ChangeLogEntry();
		entry.setRecordId("unique-1");
		entry.setNodeId("mobileEmail");
		entry.setOperation(SyncEngine.OPERATION_ADD);		
		String recordId = Database.getInstance().insert(Database.sync_changelog_table, entry.getRecord());
		
		Record storedRecord = Database.getInstance().select(Database.sync_changelog_table, recordId);				
		storedRecord.setValue("operation", SyncEngine.OPERATION_UPDATE);
		Database.getInstance().update(Database.sync_changelog_table, storedRecord);
		
		storedRecord = Database.getInstance().select(Database.sync_changelog_table, recordId);
		ChangeLogEntry storedEntry = new ChangeLogEntry(storedRecord);
		assertEquals("RecordId does not match", "unique-1", storedEntry.getRecordId());
		assertEquals("Service does not match", "mobileEmail", storedEntry.getNodeId());
		assertEquals("Operation does not match", SyncEngine.OPERATION_UPDATE, storedEntry.getOperation());
	}
	
	public void testDelete() throws Exception
	{
		ChangeLogEntry entry = new ChangeLogEntry();
		entry.setRecordId("unique-1");
		entry.setNodeId("mobileEmail");
		entry.setOperation(SyncEngine.OPERATION_ADD);		
		String recordId = Database.getInstance().insert(Database.sync_changelog_table, entry.getRecord());
		
		Record storedRecord = Database.getInstance().select(Database.sync_changelog_table, recordId);				
		Database.getInstance().delete(Database.sync_changelog_table, storedRecord);
		
		storedRecord = Database.getInstance().select(Database.sync_changelog_table, recordId);
		assertNull("Record must be deleted", storedRecord);
	}
	
	public void testReadAll() throws Exception
	{
		for(int i=1; i<=5; i++)
		{
			ChangeLogEntry entry = new ChangeLogEntry();
			entry.setRecordId("unique-"+i);
			entry.setNodeId("mobileEmail");
			entry.setOperation(SyncEngine.OPERATION_ADD);		
			Database.getInstance().insert(Database.sync_changelog_table, entry.getRecord());
		}
		
		Enumeration all = Database.getInstance().selectAll(Database.sync_changelog_table);
		while(all.hasMoreElements())
		{
			Record curr = (Record)all.nextElement();
			
			ChangeLogEntry entry = new ChangeLogEntry(curr);
			
			boolean match = false;
			if(
					entry.getRecordId().equals("unique-1") ||
					entry.getRecordId().equals("unique-2") ||
					entry.getRecordId().equals("unique-3") ||
					entry.getRecordId().equals("unique-4") ||
					entry.getRecordId().equals("unique-5") 
			)
			{
				match = true;
			}
			assertTrue("RecordId does not match", match);
			assertEquals("Service does not match", "mobileEmail", entry.getNodeId());
			assertEquals("Operation does not match", SyncEngine.OPERATION_ADD, entry.getOperation());
		}		
	}
	
	public void testDeleteAll() throws Exception
	{
		for(int i=1; i<=5; i++)
		{
			ChangeLogEntry entry = new ChangeLogEntry();
			entry.setRecordId("unique-"+i);
			entry.setNodeId("mobileEmail");
			entry.setOperation(SyncEngine.OPERATION_ADD);		
			Database.getInstance().insert(Database.sync_changelog_table, entry.getRecord());
		}
		
		Enumeration all = Database.getInstance().selectAll(Database.sync_changelog_table);
		while(all.hasMoreElements())
		{
			Record curr = (Record)all.nextElement();
			
			ChangeLogEntry entry = new ChangeLogEntry(curr);
			
			boolean match = false;
			if(
					entry.getRecordId().equals("unique-1") ||
					entry.getRecordId().equals("unique-2") ||
					entry.getRecordId().equals("unique-3") ||
					entry.getRecordId().equals("unique-4") ||
					entry.getRecordId().equals("unique-5") 
			)
			{
				match = true;
			}
			assertTrue("RecordId does not match", match);
			assertEquals("Service does not match", "mobileEmail", entry.getNodeId());
			assertEquals("Operation does not match", SyncEngine.OPERATION_ADD, entry.getOperation());
		}	
		
		Database.getInstance().deleteAll(Database.sync_changelog_table);
		all = Database.getInstance().selectAll(Database.sync_changelog_table);
		
		boolean isEmpty = false;		
		if(all == null || !all.hasMoreElements())
		{
			isEmpty = true;
		}
		
		assertTrue("All Records should be deleted", isEmpty);
	}
}
