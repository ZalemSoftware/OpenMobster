/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import android.content.Context;
import android.database.Cursor;

import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.testsuite.Test;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public class TestDatabasePerformance extends Test 
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
			
			this.testSearchExactMatchAND(context);
			this.testSearchExactMatchOR(context);
			this.testReadByName(context);
			this.testReadByNameWithSort(context);
			this.testReadByNameValuePair(context);
			
			
			this.testStoreLargeJson(context);
			this.testMultipleJson(context);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void testSearchExactMatchAND(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		StringBuilder builder = new StringBuilder();
		
		/*for(int i=0;i<1024;i++)
		{
			for(int j=0;j<2100;j++)
			{
				builder.append("a");
			}
		}*/
		
		String message = builder.toString();
		
		MobileObject mobileObject = new MobileObject();
		mobileObject.setStorageId("emailChannel");
		mobileObject.setValue("from", from);
		mobileObject.setValue("to", to);
		mobileObject.setValue("message", message);
		mobileObject.setCreatedOnDevice(false);
		mobileObject.setLocked(false);
		mobileObject.setProxy(false);
		MobileObjectDatabase.getInstance().create(mobileObject);
		
		for(int i=0; i<5; i++)
		{
			from = "from("+i+")@blah.com";
			to = "to("+i+")@blah.com";
			
			mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			mobileObject.setValue("message", message);
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			MobileObjectDatabase.getInstance().create(mobileObject);
		}
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("to", "to@gmail.com");
		criteria.setAttribute("from", "from@gmail.com");
		Cursor cursor = db.searchExactMatchAND("emailChannel", criteria);
		cursor.moveToFirst();
		do
		{
			String value = cursor.getString(0);
			System.out.println("Value: "+value);
			
			Record record = db.select("emailChannel", value);
			System.out.println("Record: "+record);
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		
		this.assertTrue(cursor.getCount()==1, this.getInfo()+"/testSearchExactMatchAND/MustFindOneRow");
		
		db.deleteAll("emailChannel");
	}
	
	private void testSearchExactMatchOR(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		StringBuilder builder = new StringBuilder();
		
		/*for(int i=0;i<1024;i++)
		{
			for(int j=0;j<2100;j++)
			{
				builder.append("a");
			}
		}*/
		
		String message = builder.toString();
		
		MobileObject mobileObject = new MobileObject();
		mobileObject.setStorageId("emailChannel");
		mobileObject.setValue("from", from);
		mobileObject.setValue("to", to);
		mobileObject.setValue("message", message);
		mobileObject.setCreatedOnDevice(false);
		mobileObject.setLocked(false);
		mobileObject.setProxy(false);
		MobileObjectDatabase.getInstance().create(mobileObject);
		
		for(int i=0; i<5; i++)
		{
			from = "from("+i+")@blah.com";
			to = "to("+i+")@blah.com";
			
			mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			mobileObject.setValue("message", message);
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			MobileObjectDatabase.getInstance().create(mobileObject);
		}
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("to", "blahblah");
		criteria.setAttribute("from", "from@gmail.com");
		Cursor cursor = db.searchExactMatchOR("emailChannel", criteria);
		cursor.moveToFirst();
		do
		{
			String value = cursor.getString(0);
			System.out.println("Value: "+value);
			
			Record record = db.select("emailChannel", value);
			System.out.println("Record: "+record);
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		
		this.assertTrue(cursor.getCount()==1, this.getInfo()+"/testSearchExactMatchOR/MustFindOneRow");
		
		db.deleteAll("emailChannel");
	}
	
	private void testStoreLargeJson(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		StringBuilder builder = new StringBuilder();
		
		StringBuilder packetBuilder = new StringBuilder();
		for(int i=0; i<1000; i++)
		{
			packetBuilder.append("a");
		}
		String packet = packetBuilder.toString();
		for(int i=0;i<1900;i++)
		{
			builder.append(packet);
		}
		
		String message = builder.toString();
		
		MobileObject mobileObject = new MobileObject();
		mobileObject.setStorageId("emailChannel");
		mobileObject.setValue("from", from);
		mobileObject.setValue("to", to);
		mobileObject.setValue("message", message);
		mobileObject.setCreatedOnDevice(false);
		mobileObject.setLocked(false);
		mobileObject.setProxy(false);
		String recordId = MobileObjectDatabase.getInstance().create(mobileObject);
		
		Record largeRecord = db.select("emailChannel", recordId);
		largeRecord = db.select("emailChannel", recordId); //testing cache integrity
		this.assertNotNull(largeRecord, this.getInfo()+"/testStoreLargeJson/MustNotBeNull");
		
		mobileObject = new MobileObject(largeRecord);
		String largeMessage = mobileObject.getValue("message");
		
		this.assertEquals(""+largeMessage.length(), "1900000", this.getInfo()+"/testStoreLargeJson/MessageLengthDoesNotMatch");
		
		db.delete("emailChannel",largeRecord);
	}
	
	private void testMultipleJson(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		StringBuilder builder = new StringBuilder();
		
		StringBuilder packetBuilder = new StringBuilder();
		for(int i=0; i<1000; i++)
		{
			packetBuilder.append("a");
		}
		String packet = packetBuilder.toString();
		for(int i=0;i<100;i++)
		{
			builder.append(packet);
		}
		
		String message = builder.toString();
		
		for(int i=0; i<5; i++)
		{
			System.out.println("Testing Multiple JSON # "+i);
			
			MobileObject mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			mobileObject.setValue("message", message);
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			String recordId = MobileObjectDatabase.getInstance().create(mobileObject);
			
			Record largeRecord = db.select("emailChannel", recordId);
			largeRecord = db.select("emailChannel", recordId); //testing cache integrity
			this.assertNotNull(largeRecord, this.getInfo()+"/testMultipleJson/MustNotBeNull");
			
			mobileObject = new MobileObject(largeRecord);
			String largeMessage = mobileObject.getValue("message");
			
			this.assertEquals(""+largeMessage.length(), "100000", this.getInfo()+"/testMultipleJson/MessageLengthDoesNotMatch");
		}
		
		db.deleteAll("emailChannel");
	}
	
	private void testReadByName(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		String message = "message";
		
		for(int i=0; i<100; i++)
		{
			System.out.println("Testing ReadByName # "+i);
			
			MobileObject mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			if(i%2 == 0)
			{
				mobileObject.setValue("message", message);
			}
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			MobileObjectDatabase.getInstance().create(mobileObject);
		}
		
		Cursor cursor = MobileObjectDatabase.getInstance().readByName("emailChannel", "message");
		int count = cursor.getCount();
		this.assertTrue(50 == count, this.getInfo()+"/testReadByName/CountMustBe50");
		int columnIndex = cursor.getColumnIndex("recordid");
		cursor.moveToFirst();
		do
		{
			String recordid = cursor.getString(columnIndex);
			System.out.println("RecordId: "+recordid);
			
			MobileObject cour = MobileObjectDatabase.getInstance().read("emailChannel", recordid);
			
			String messageValue = cour.getValue("message");
			this.assertEquals(messageValue, "message", this.getInfo()+"/testReadByName/MessageValueMisMatch");
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		
		db.deleteAll("emailChannel");
	}
	
	private void testReadByNameWithSort(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		String message = "message";
		
		for(int i=0; i<100; i++)
		{
			System.out.println("Testing ReadByNameWithSort # "+i);
			
			MobileObject mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			if(i%2 == 0)
			{
				mobileObject.setValue("message", message+"://"+i);
			}
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			MobileObjectDatabase.getInstance().create(mobileObject);
		}
		
		Cursor cursor = MobileObjectDatabase.getInstance().readByName("emailChannel", "message", true);
		this.processReadByNameWithSort(cursor);
		
		cursor = MobileObjectDatabase.getInstance().readByName("emailChannel", "message", false);
		this.processReadByNameWithSort(cursor);
		
		db.deleteAll("emailChannel");
	}
	
	private void processReadByNameWithSort(Cursor cursor)
	{
		int count = cursor.getCount();
		this.assertTrue(50 == count, this.getInfo()+"/testReadByNameWithSort/CountMustBe50");
		
		int columnIndex = cursor.getColumnIndex("recordid");
		cursor.moveToFirst();
		do
		{
			String recordid = cursor.getString(columnIndex);
			
			MobileObject cour = MobileObjectDatabase.getInstance().read("emailChannel", recordid);
			String messageValue = cour.getValue("message");
			System.out.println(messageValue);
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
	}
	
	private void testReadByNameValuePair(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		String message = "message";
		
		for(int i=0; i<100; i++)
		{
			System.out.println("Testing ReadByNameValuePair # "+i);
			
			MobileObject mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			if(i%2 == 0)
			{
				mobileObject.setValue("message", message+"://"+i);
			}
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			MobileObjectDatabase.getInstance().create(mobileObject);
		}
		
		Cursor cursor = MobileObjectDatabase.getInstance().readByNameValuePair("emailChannel", "from", "from@gmail.com");
		int count = cursor.getCount();
		this.assertTrue(100 == count, this.getInfo()+"/testReadByNameValuePair/CountMustBe100");
		
		db.deleteAll("emailChannel");
	}
}
