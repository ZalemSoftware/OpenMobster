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

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

/**
 * @author openmobster@gmail.com
 *
 */
public class ContainerContentProvider extends ContentProvider
{
	@Override
	public boolean onCreate()
	{							
		return true;
	}

	@Override
	public String getType(Uri uri)
	{
		return "vnd.android.cursor.dir/vnd.openmobster.container.status";
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
	String[] selectionArgs,
	String sortOrder)
	{
		try
		{
			MatrixCursor cursor = new MatrixCursor(new String[]{"status"});
			
			//query all
			Registry registry = Registry.getActiveInstance();
			if(!registry.isStarted())
			{
				cursor = new MatrixCursor(new String[]{"status"});
				cursor.addRow(new String[]{""+Boolean.FALSE});
				return cursor;
			}
			
			
			Context context = registry.getContext();
			Database database = Database.getInstance(context);
												
			Set<Record> records = database.selectAll(Database.config_table);
			
			if(records != null && !records.isEmpty())
			{
				Record record = records.iterator().next();		
				String status = record.getValue("container-status");
				cursor.addRow(new String[]{status});
			}
			else
			{
				cursor.addRow(new String[]{""+Boolean.FALSE});
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
			MatrixCursor cursor = new MatrixCursor(new String[]{"status"});
			cursor.addRow(new String[]{""+Boolean.FALSE});
			return cursor;
			//throw new RuntimeException(e);
		}
	}
	

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		try
		{						
			Context context = this.getContext();
			Database database = Database.getInstance(context);		
			Set<Record> records = database.selectAll(Database.config_table);
			
			String status = values.getAsString("status");
			if(records != null)
			{
				Record record = records.iterator().next();		
				record.setValue("container-status", status);
				database.update(Database.config_table, record);
			}
			else
			{
				Record record = new Record();		
				record.setValue("container-status", status);
				database.insert(Database.config_table, record);
			}
			
			return uri;
		}
		catch(Exception e)
		{
			//Error Handler
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(),"insert", new Object[]{
						"BusID to be added: "+values.getAsString("busId"),
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					}
			));
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, 
	String[] selectionArgs)
	{
		//NOT needed
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		//Not needed
		return 0;
	}
}
