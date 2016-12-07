/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import java.util.Set;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.testsuite.Test;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 * @author openmobster@gmail.com
 */
public class TestDatabase extends Test 
{
	
	@Override
	public void setUp()
	{
		try
		{
			super.setUp();
			
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			Database db = Database.getInstance(context);
			db.disconnect();
			db.connect();
			
			//some initial state setup
			db.createTable(Database.provisioning_table);
			db.dropTable("emailChannel");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void runTest()
	{
		try
		{
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			this.testConnect(context);
			
			this.testEnumerateTables(context);
			
			this.testDropTable(context);
			
			this.testCreateTable(context);
			
			this.testDoesTableExist(context);
			
			this.testIsTableEmpty(context);
			
			this.testSelectAll(context);
			
			this.testSelectCount(context);
			
			this.testSelect(context);
			
			this.testUpdate(context);
			
			this.testDelete(context);
			
			this.testDeleteAll(context);
			
			this.testLongTransaction(context);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void testConnect(Context context) throws Exception
	{
		Database db = Database.getInstance(context);
		
		for(int i=0; i<3; i++)
		{
			this.assertTrue(db.isConnected(), this.getInfo()+"/testConnect/MustBeConnected");
			
			db.disconnect();
			
			this.assertFalse(db.isConnected(), this.getInfo()+"/testConnect/MustNotBeConnected");
			
			db.connect();
		}
	}
	
	private void testEnumerateTables(Context context) throws Exception
	{
		Database db = Database.getInstance(context);
		
		Set<String> tables = db.enumerateTables();
		System.out.println("--------------------------------------------------");
		for(String table: tables)
		{
			System.out.println("Table: "+table);
		}
		System.out.println("--------------------------------------------------");
	}
	
	private void testDropTable(Context context) throws Exception
	{
		Database db = Database.getInstance(context);
		
		assertTrue(db.doesTableExist(Database.provisioning_table), this.getInfo()+"/dropTable/TableMustExist");
		
		db.dropTable(Database.provisioning_table);
		
		assertFalse(db.doesTableExist(Database.provisioning_table), this.getInfo()+"/dropTable/TableMustBeDropped");
	}
	
	private void testCreateTable(Context context) throws Exception
	{
		Database db = Database.getInstance(context);
		
		assertFalse(db.doesTableExist("emailChannel"), this.getInfo()+"/createTable/TableMustNotExistYet");
		
		db.createTable("emailChannel");
		
		assertTrue(db.doesTableExist("emailChannel"), this.getInfo()+"/createTable/TableMustExistNow");
	}
	
	private void testDoesTableExist(Context context) throws Exception
	{
		Database db = Database.getInstance(context);
		
		db.dropTable("emailChannel");
		
		assertFalse(db.doesTableExist("emailChannel"), this.getInfo()+"/doesTableExist/TableMustNotExistYet");
		
		db.createTable("emailChannel");
		
		assertTrue(db.doesTableExist("emailChannel"), this.getInfo()+"/doesTableExist/TableMustExistNow");
	}
	
	private void testIsTableEmpty(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		assertTrue(db.isTableEmpty("emailChannel"), this.getInfo()+"/testIsTableEmpty/TableMustBeEmpty");
		
		//Insert a record and re-assert
		Record emailRecord = new Record();
		emailRecord.setRecordId("blah");
		emailRecord.setValue("from", "blah@blah.com");
		
		String recordId = db.insert("emailChannel", emailRecord);
		
		this.assertEquals(recordId, "blah", this.getInfo()+"/testIsTableEmpty/RecordIdIsNotConsistent");
		assertFalse(db.isTableEmpty("emailChannel"), this.getInfo()+"/testIsTableEmpty/TableMustNotBeEmpty");
		
		Set<Record> emails = db.selectAll("emailChannel");
		for(Record email: emails)
		{
			System.out.println("--------------------------------------------");
			System.out.println("RecordId: "+ email.getRecordId());
			System.out.println("From: "+ email.getValue("from"));
			System.out.println("Dirty: "+ email.getDirtyStatus());
			System.out.println("--------------------------------------------");
			
			assertEquals(email.getRecordId(), "blah", this.getInfo()+"/testIsTableEmpty/RecordIdDoesnotMatch");
			assertEquals(email.getValue("from"), "blah@blah.com", this.getInfo()+"/testIsTableEmpty/FromDoesnotMatch");
		}
	}
	
	private void testSelectAll(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		for(int i=0; i<5; i++)
		{
			String recordId = "uid:"+i;
			String from = "from("+i+")@blah.com";
			String to = "to("+i+")@blah.com";
			
			Record record = new Record();
			record.setRecordId(recordId);
			record.setValue("from", from);
			record.setValue("to", to);
			record.setValue("message", "<tag apos='apos' quote=\"quote\" ampersand='&'>blahblah/Message</tag>");
			
			db.insert("emailChannel", record);
		}
		
		Set<Record> all = db.selectAll("emailChannel");
		for(Record email: all)
		{
			System.out.println("--------------------------------------------");
			System.out.println("RecordId: "+ email.getRecordId());
			System.out.println("From: "+ email.getValue("from"));
			System.out.println("To: "+ email.getValue("to"));
			System.out.println("Message: "+ email.getValue("message"));
			System.out.println("Dirty: "+ email.getDirtyStatus());
			System.out.println("--------------------------------------------");
		}
		assertEquals(""+all.size(),"5","/selectAll/numberofRecords/match/failure");
	}
	
	private void testSelectCount(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		for(int i=0; i<5; i++)
		{
			String recordId = "uid:"+i;
			String from = "from("+i+")@blah.com";
			String to = "to("+i+")@blah.com";
			
			Record record = new Record();
			record.setRecordId(recordId);
			record.setValue("from", from);
			record.setValue("to", to);
			
			db.insert("emailChannel", record);
		}
		
		long count = db.selectCount("emailChannel");
		
		assertEquals(""+count, "5", this.getInfo()+"/testSelectCount/CountMustBe5");
	}
	
	private void testSelect(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		for(int i=0; i<5; i++)
		{
			String recordId = "uid:"+i;
			String from = "from("+i+")@blah.com";
			String to = "to("+i+")@blah.com";
			
			Record record = new Record();
			record.setRecordId(recordId);
			record.setValue("from", from);
			record.setValue("to", to);
			
			db.insert("emailChannel", record);
		}
		
		Record email = db.select("emailChannel","uid:"+2);
		System.out.println("--------------------------------------------");
		System.out.println("RecordId: "+ email.getRecordId());
		System.out.println("From: "+ email.getValue("from"));
		System.out.println("To: "+ email.getValue("to"));
		System.out.println("Dirty: "+ email.getDirtyStatus());
		System.out.println("--------------------------------------------");
		assertEquals(email.getRecordId(),"uid:2",this.getInfo()+"/testSelect/RecordIdMismatch");
		assertEquals(email.getValue("from"),"from(2)@blah.com",this.getInfo()+"/testSelect/FromFailed");
		assertEquals(email.getValue("to"),"to(2)@blah.com",this.getInfo()+"/testSelect/ToFailed");
	}
	
	private void testUpdate(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		for(int i=0; i<5; i++)
		{
			String recordId = "uid:"+i;
			String from = "from("+i+")@blah.com";
			String to = "to("+i+")@blah.com";
			
			Record record = new Record();
			record.setRecordId(recordId);
			record.setValue("from", from);
			record.setValue("to", to);
			
			db.insert("emailChannel", record);
		}
		
		Record email = db.select("emailChannel","uid:"+2);
		String beforeStatus = email.getDirtyStatus();
		email.setValue("from", "from:updated");
		email.setValue("to", "to:updated");
		db.update("emailChannel", email);
		String afterStatus = email.getDirtyStatus();
		
		
		System.out.println("--------------------------------------------");
		System.out.println("RecordId: "+ email.getRecordId());
		System.out.println("From: "+ email.getValue("from"));
		System.out.println("To: "+ email.getValue("to"));
		System.out.println("Before Status: "+ beforeStatus);
		System.out.println("After Status: "+ afterStatus);
		System.out.println("--------------------------------------------");
		assertFalse(beforeStatus.equals(afterStatus),this.getInfo()+"/testUpdate/DirtyCheckFailed");
		assertEquals(email.getRecordId(),"uid:2",this.getInfo()+"/testUpdate/RecordIdMismatch");
		assertEquals(email.getValue("from"),"from:updated",this.getInfo()+"/testUpdate/FromFailed");
		assertEquals(email.getValue("to"),"to:updated",this.getInfo()+"/testUpdate/ToFailed");
	}
	
	private void testDelete(Context context) throws Exception
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		for(int i=0; i<5; i++)
		{
			String recordId = "uid:"+i;
			String from = "from("+i+")@blah.com";
			String to = "to("+i+")@blah.com";
			
			Record record = new Record();
			record.setRecordId(recordId);
			record.setValue("from", from);
			record.setValue("to", to);
			
			db.insert("emailChannel", record);
		}
		
		Record email = db.select("emailChannel","uid:"+2);
		db.delete("emailChannel", email);
		
		email = db.select("emailChannel","uid:"+2);
		
		assertNull(email, this.getInfo()+"/testDelete/DeletCheckFailed");
	}
	
	private void testDeleteAll(Context context) throws Exception
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		for(int i=0; i<5; i++)
		{
			String recordId = "uid:"+i;
			String from = "from("+i+")@blah.com";
			String to = "to("+i+")@blah.com";
			
			Record record = new Record();
			record.setRecordId(recordId);
			record.setValue("from", from);
			record.setValue("to", to);
			
			db.insert("emailChannel", record);
		}
		
		Record email = db.select("emailChannel","uid:"+2);
		db.deleteAll("emailChannel");
		
		email = db.select("emailChannel","uid:"+2);
		
		assertNull(email, this.getInfo()+"/testDeleteAll/DeleteCheckFailed");
		
		long count = db.selectCount("emailChannel");
		
		assertTrue(count == 0, this.getInfo()+"/testDeleteAll/CountCheckFailed");
	}
	
	private void testLongTransaction(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		for(int i=0; i<5; i++)
		{
			String recordId = "uid:"+i;
			String from = "from("+i+")@blah.com";
			String to = "to("+i+")@blah.com";
			
			Record record = new Record();
			record.setRecordId(recordId);
			record.setValue("from", from);
			record.setValue("to", to);
			
			db.insert("emailChannel", record);
		}
		
		Record email = db.select("emailChannel","uid:"+2);
		
		email.setDirtyStatus(GeneralTools.generateUniqueId());
		String beforeStatus = email.getDirtyStatus();
		email.setValue("from", "from:updated");
		email.setValue("to", "to:updated");
		
		try
		{
			db.update("emailChannel", email);
		}
		catch(DBException dbe)
		{
			dbe.printStackTrace(System.out);
			assertTrue(dbe.getErrorCode()==dbe.ERROR_RECORD_STALE,this.getInfo()+"/testLongTransaction/StaleFailure");
		}
	}
}
