/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.provider;

import java.util.Set;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.database.MatrixCursor;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.storage.DBException;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * @author openmobster@gmail.com
 *
 */
public class MobileChannelProvider extends ContentProvider
{
	@Override
	public boolean onCreate()
	{
		return true;
	}
	
	@Override
	public String getType(Uri uri)
	{
		return "vnd.android.cursor.dir/vnd.openmobster.mobile.channels";
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
	String[] selectionArgs,
	String sortOrder)
	{
		try
		{
			Context context = this.getContext();
			String channel = this.parseChannel(uri);
									
			this.checkStorage(channel);
			
			MatrixCursor cursor = null;
			
			if(selection == null || selection.trim().length() == 0)
			{								
				//read all the rows
				Set<Record> all = Database.getInstance(context).
				selectAll(channel);
				if(all != null)
				{
					cursor = new MatrixCursor(new String[]{"recordId","name","value"});
					this.prepareCursor(cursor, all);
				}
			}
			else if(!selection.trim().startsWith("query"))
			{
				String recordId = selection.trim();
				Record mobileObject = Database.getInstance(context).select(channel, recordId);
				if(mobileObject != null)
				{
					cursor = new MatrixCursor(new String[]{"recordId","name","value"});
					this.prepareCursor(cursor, mobileObject);
				}
			}
			else
			{
				//query using a where clause
				if(selectionArgs != null && selectionArgs.length>0)
				{
					cursor = new MatrixCursor(new String[]{"recordId","name","value"});
					Set<Record> recordsFound = new HashSet<Record>();
					for(String arg:selectionArgs)
					{	
						int index = arg.indexOf('=');
						String value = arg.substring(index+1).trim();
						
						Set<Record> records = null;
						if(selection.equals("query://equals"))
						{
							records = Database.getInstance(context).selectByValue(channel, value);
						}
						else if(selection.equals("query://notequals"))
						{
							records = Database.getInstance(context).selectByNotEquals(channel, value);
						}
						else if(selection.equals("query://contains"))
						{
							records = Database.getInstance(context).selectByContains(channel, value);
						}
						if(records != null && !records.isEmpty())
						{
							recordsFound.addAll(records);
						}
					}
					if(!recordsFound.isEmpty())
					{
						this.prepareCursor(cursor, recordsFound);
					}
				}
			}
			
			return cursor;
		}
		catch(Exception e)
		{
			//Error Handler
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(),"query", new Object[]{
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					}
			));
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues contentValues)
	{
		try
		{
			String channel = this.parseChannel(uri);
			Context context = this.getContext();
			this.checkStorage(channel);
			
			if(!contentValues.containsKey("storageId"))
			{
				contentValues.put("storageId", channel);
			}
			
			Record insert = new Record(); 
			this.prepareRecord(insert, contentValues);
			String id = Database.getInstance(context).insert(
			channel, 
			insert);
			
			Uri insertUri = Uri.parse(uri.toString()+"?id="+id);
			return insertUri;
		}
		catch(Exception e)
		{
			//Error Handler
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(),"insert", new Object[]{
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					}
			));
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, 
	String[] selectionArgs)
	{
		try
		{	
			if(!contentValues.containsKey("recordId"))
			{
				//don't know which record needs to be updated
				return 0;
			}
			
			String channel = this.parseChannel(uri);
			Context context = this.getContext();
			Database database = Database.getInstance(context);
			this.checkStorage(channel);			
			if(!contentValues.containsKey("storageId"))
			{
				contentValues.put("storageId", channel);
			}
			String storageId = contentValues.getAsString("storageId");
			String recordId = contentValues.getAsString("recordId");
			
			Record recordToBeUpdated = new Record();
			this.prepareRecord(recordToBeUpdated, 
			contentValues);
			
			String dirtyStatus = recordToBeUpdated.getDirtyStatus();
			if(dirtyStatus == null || dirtyStatus.trim().length() == 0)
			{
				Record currentRecord = database.
				select(storageId, recordId);
				recordToBeUpdated.setDirtyStatus(currentRecord.getDirtyStatus());
			}
			
			database.update(storageId, recordToBeUpdated);
			
			return 1;
		}
		catch(Exception e)
		{
			//Error Handler
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(),"update", new Object[]{
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					}
			));
			return -1;
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		try
		{
			Context context = this.getContext();
			String channel = this.parseChannel(uri);
			this.checkStorage(channel);
			Database database = Database.getInstance(context);
			
			int deleteCount = 0;
			if(selection == null || selection.trim().length() == 0)
			{
				//deleteAll
				long rowCount = database.selectCount(channel);
				database.deleteAll(channel);
				deleteCount = (int)rowCount;
			}
			else
			{
				//delete record in question
				String recordId = selection.trim();
				
				Record recordToBeDeleted = Database.getInstance(context).select(channel, recordId);				
				database.delete(channel, recordToBeDeleted);
				
				deleteCount = 1;
			}
			
			return deleteCount;
		}
		catch(Exception e)
		{
			//Error Handler
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(),"delete", new Object[]{
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					}
			));
			return -1;
		}
	}
	//----------------------------------------------------------------------------------
	private void checkStorage(String storageId) throws DBException
	{
		Context context = this.getContext();
		if(!Database.getInstance(context).doesTableExist(storageId))
		{
			Database.getInstance(context).createTable(storageId);
		}
	}
	
	private String parseChannel(Uri uri)
	{
		String channel = uri.getPath();
		if(channel == null || channel.trim().length()==0)
		{
			return null;
		}
		
		if(channel.startsWith("/"))
		{
			channel = channel.substring(1);
		}
		return channel;
	}
	
	private void prepareCursor(MatrixCursor cursor,Set<Record> mobileObjects)
	{
		for(Record mobileObject: mobileObjects)
		{
			this.prepareCursor(cursor, mobileObject);
		}
	}
	
	private void prepareCursor(MatrixCursor cursor,Record record)
	{
		String recordId = record.getRecordId();
		
		cursor.addRow(new String[]{recordId,"recordId",record.getRecordId()});
		cursor.addRow(new String[]{recordId,"storageId",record.getValue("storageId")});		
		cursor.addRow(new String[]{recordId,"dirty",record.getDirtyStatus()});
		if(record.getValue("serverRecordId") != null)
		{
			cursor.addRow(new String[]{recordId,"serverRecordId",record.getValue("serverRecordId")});
		}
		
		if(record.getValue("isCreatedOnDevice").equals(Boolean.TRUE.toString()))
		{
			cursor.addRow(new String[]{recordId,"isCreatedOnDevice",
			""+Boolean.TRUE});
		}
		else
		{
			cursor.addRow(new String[]{recordId,"isCreatedOnDevice",
			""+Boolean.FALSE});
		}
		
		if(record.getValue("isLocked").equals(Boolean.TRUE.toString()))
		{
			cursor.addRow(new String[]{recordId,"isLocked",
					""+Boolean.TRUE});
		}
		else
		{
			cursor.addRow(new String[]{recordId,"isLocked",
					""+Boolean.FALSE});
		}
		
		if(record.getValue("isProxy").equals(Boolean.TRUE.toString()))
		{
			cursor.addRow(new String[]{recordId,"isProxy",
					""+Boolean.TRUE});
		}
		else
		{
			cursor.addRow(new String[]{recordId,"isProxy",
					""+Boolean.FALSE});
		}
		
		if(record.getValue("count") != null)
		{			
			int count = Integer.parseInt(record.getValue("count"));
			cursor.addRow(new String[]{recordId,"count",
					""+count});
			for(int index=0; index < count; index++)
			{
				cursor.addRow(new 
				String[]{recordId,"field["+index+"].uri", record.getValue("field["+index+"].uri")});
				cursor.addRow(new 
				String[]{recordId,"field["+index+"].name", record.getValue("field["+index+"].name")});
				cursor.addRow(new 
				String[]{recordId,"field["+index+"].value", record.getValue("field["+index+"].value")});				
			}
		}
		
		if(record.getValue("arrayMetaDataCount") != null)
		{
			int count = Integer.parseInt(record.getValue("arrayMetaDataCount"));
			cursor.addRow(new String[]{recordId,"arrayMetaDataCount",
					""+count});
			for(int index=0; index < count; index++)
			{								
				cursor.addRow(new String[]
				{recordId,"arrayMetaData["+index+"].arrayUri",record.getValue("arrayMetaData["+index+"].arrayUri")});
				cursor.addRow(new String[]
				{recordId,"arrayMetaData["+index+"].arrayLength",record.getValue("arrayMetaData["+index+"].arrayLength")});
				cursor.addRow(new String[]
				{recordId,"arrayMetaData["+index+"].arrayClass",record.getValue("arrayMetaData["+index+"].arrayClass")});
				
			}
		}
	}
	
	private void prepareRecord(Record record, ContentValues values)
	{
		String local = values.getAsString("recordId");
		if(local != null && local.trim().length() > 0)
		{
			record.setRecordId(local);
		}
		
		local = values.getAsString("dirty");
		if(local != null && local.trim().length() > 0)
		{
			record.setDirtyStatus(local);
		}
		
		local = values.getAsString("serverRecordId");
		if(local != null && local.trim().length() >0)
		{
			record.setValue("serverRecordId", local);
		}
		
		
		local = values.getAsString("storageId");
		record.setValue("storageId", local);
		
			
		if(values.containsKey("isCreatedOnDevice") && values.getAsBoolean("isCreatedOnDevice"))
		{
			record.setValue("isCreatedOnDevice", Boolean.TRUE.toString());
		}
		else
		{
			record.setValue("isCreatedOnDevice", Boolean.FALSE.toString());
		}
		
		if(values.containsKey("isLocked") && values.getAsBoolean("isLocked"))
		{
			record.setValue("isLocked", Boolean.TRUE.toString());
		}
		else
		{
			record.setValue("isLocked", Boolean.FALSE.toString());
		}
		
		if(values.containsKey("isProxy") && values.getAsBoolean("isProxy"))
		{
			record.setValue("isProxy", Boolean.TRUE.toString());
		}
		else
		{
			record.setValue("isProxy", Boolean.FALSE.toString());
		}
		
		if(values.containsKey("count"))
		{
			int fieldCount = values.getAsInteger("count");
			if(fieldCount > 0)
			{
				record.setValue("count", ""+fieldCount);
				
				for(int index=0; index<fieldCount; index++)
				{		
					local = values.getAsString("field["+index+"].uri");
					if(local != null)
					{
						record.setValue("field["+index+"].uri", local);
					}
					else
					{
						record.setValue("field["+index+"].uri", "");
					}
					
					local = values.getAsString("field["+index+"].name");
					if(local != null)
					{
						record.setValue("field["+index+"].name", local);
					}
					else
					{
						record.setValue("field["+index+"].name", "");
					}
									
					local = values.getAsString("field["+index+"].value");
					if(local != null)
					{
						record.setValue("field["+index+"].value", local);
					}
					else
					{
						record.setValue("field["+index+"].value", "");
					}
				}
			}
		}
		
		if(values.containsKey("arrayMetaDataCount"))
		{
			int arrayMetaDataCount = values.getAsInteger("arrayMetaDataCount");
			if(arrayMetaDataCount > 0)
			{
				record.setValue("arrayMetaDataCount", ""+arrayMetaDataCount);
				
				for(int index=0; index<arrayMetaDataCount; index++)
				{
					local = values.getAsString("arrayMetaData["+index+"].arrayUri");
					if(local != null)
					{
						record.setValue("arrayMetaData["+index+"].arrayUri", local);
					}
					else
					{
						record.setValue("arrayMetaData["+index+"].arrayUri", "");
					}
					
					local = values.getAsString("arrayMetaData["+index+"].arrayLength");
					if(local != null)
					{
						record.setValue("arrayMetaData["+index+"].arrayLength", local);
					}
					else
					{
						record.setValue("arrayMetaData["+index+"].arrayLength", "");
					}
					
					local = values.getAsString("arrayMetaData["+index+"].arrayClass");
					if(local != null)
					{
						record.setValue("arrayMetaData["+index+"].arrayClass", local);
					}
					else
					{
						record.setValue("arrayMetaData["+index+"].arrayClass", "");
					}						
				}
			}
		}
	}
}
