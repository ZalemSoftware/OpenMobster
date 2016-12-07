/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.provider;

import java.util.Set;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.database.MatrixCursor;

import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * @author openmobster@gmail.com
 *
 */
public class SystemErrorsProvider extends ContentProvider
{
	@Override
	public boolean onCreate()
	{
		try
		{
			Database database = Database.getInstance(this.getContext());
			
			//Initialize the provisioning table
			if(!database.doesTableExist(Database.system_errors))
			{
				database.createTable(Database.system_errors);	
			}
			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@Override
	public String getType(Uri uri)
	{
		return "vnd.android.cursor.dir/vnd.openmobster.system.errors";
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
	String[] selectionArgs,
	String sortOrder)
	{
		try
		{
			MatrixCursor cursor = new MatrixCursor(new String[]{"name","value"});	
			
			Context context = this.getContext();
			Database database = Database.getInstance(context);
			
			Set<Record> all = database.selectAll(Database.system_errors);
			if(all != null && !all.isEmpty())
			{
				for(Record errorRecord:all)
				{
					this.prepareCursor(cursor, errorRecord);
				}
			}
			
			return cursor;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues contentValues)
	{
		try
		{
			Context context = this.getContext();
			Database database = Database.getInstance(context);
			
			//insert
			Record errorRecord = new Record();
			this.prepareRecord(errorRecord, contentValues);
			database.insert(Database.system_errors, 
			errorRecord);
			
			return uri;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, 
	String[] selectionArgs)
	{
		//Not needed
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		try
		{
			Context context = this.getContext();
			Database database = Database.getInstance(context);
			
			int deleteCount = 0;
			
			//deleteAll
			long rowCount = database.selectCount(Database.system_errors);
			database.deleteAll(Database.system_errors);
			deleteCount = (int)rowCount;
			
			return deleteCount;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	//------------------------------------------------------------------------------------
	private void prepareCursor(MatrixCursor cursor, Record record)
	{				
		cursor.addRow(new String[]{"message",record.getValue("message")});
	}
	
	private void prepareRecord(Record errorRecord, 
	ContentValues values)
	{
		String local = values.getAsString("message");
		if(local != null)
		{
			errorRecord.setValue("message", local);
		}
	}
}
