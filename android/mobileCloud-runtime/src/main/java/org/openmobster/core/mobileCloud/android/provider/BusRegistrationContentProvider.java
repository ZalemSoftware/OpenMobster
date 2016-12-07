/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.provider;

import java.util.Set;
import java.util.Map.Entry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.database.MatrixCursor;

import org.openmobster.core.mobileCloud.android.module.bus.BusRegistration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

/**
 * @author openmobster@gmail.com
 *
 */
public class BusRegistrationContentProvider extends ContentProvider
{
	@Override
	public boolean onCreate()
	{							
		return true;
	}

	@Override
	public String getType(Uri uri)
	{
		return "vnd.android.cursor.dir/vnd.openmobster.bus.registration";
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
	String[] selectionArgs,
	String sortOrder)
	{
		try
		{
			if(selection == null || selection.trim().length()==0)
			{
				MatrixCursor cursor = new MatrixCursor(new String[]{"busId","handler"});
				
				//query all
				Context context = Registry.getActiveInstance().getContext();
				Database database = Database.getInstance(context);
												
				Set<Record> registrations = database.selectAll(Database.bus_registration);
				if(registrations != null)
				{
					for(Record record:registrations)
					{
						BusRegistration cour = this.parse(record);
						this.updateCursor(cursor, cour);
					}
				}
				
				return cursor;
			}
			else
			{
				String busId = selection;
				
				//query by id
				MatrixCursor cursor = new MatrixCursor(new String[]{"busId","handler"});
				
				Context context = this.getContext();
				Database database = Database.getInstance(context);
				
				Record record = database.select(Database.bus_registration,busId);
				if(record != null)
				{
					BusRegistration registration = this.parse(record);
					this.updateCursor(cursor, registration);
				}
				
				return cursor;
			}
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
	public Uri insert(Uri uri, ContentValues values)
	{
		try
		{						
			Context context = this.getContext();
			Database database = Database.getInstance(context);
			BusRegistration reg = this.parse(values);
			String busId = reg.getBusId();
			
			Record saved = database.select(Database.bus_registration, 
			busId);
			
			if(saved != null)
			{
				this.prepareToPersist(saved, reg);
				database.update(Database.bus_registration, saved);
			}
			else
			{
				Record newRecord = new Record();
				this.prepareToPersist(newRecord, reg);
				database.insert(Database.bus_registration, newRecord);
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
		try
		{
			String busId = selection;
			Context context = this.getContext();
			Database database = Database.getInstance(context);
			
			Record record = database.select(Database.bus_registration,busId);
			if(record != null)
			{
				database.delete(Database.bus_registration, record);
				return 1;
			}
			
			return 0;
		}
		catch(Exception e)
		{
			//Error Handler
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(),"delete", new Object[]{
						"BusID being deleted: "+selection,
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					}
			));
			throw new RuntimeException(e);
		}
	}
	//----------------------------------------------------------------------------------
	private BusRegistration parse(ContentValues values)
	{
		BusRegistration reg = new BusRegistration(values.getAsString("busId"));
		
		Set<Entry<String,Object>> valueSet = values.valueSet();
		for(Entry<String,Object> entry:valueSet)
		{
			if(entry.getKey().equals("busId"))
			{
				continue;
			}
			else
			{
				reg.addInvocationHandler((String)entry.getValue());
			}
		}
		
		return reg;
	}
	
	private BusRegistration parse(Record record)
	{
		BusRegistration reg = new BusRegistration(record.getRecordId());
						
		Set<String> names = record.getNames();
		for(String name: names)
		{
			if(name.equals("recordId") || name.equals("dirty"))
			{
				continue;
			}
			reg.addInvocationHandler(record.getValue(name));
		}
		
		return reg;
	}
	
	private void prepareToPersist(Record record, BusRegistration reg)
	{		
		String dirtyStatus = record.getDirtyStatus();		
		record.getState().clear();
		
		record.setRecordId(reg.getBusId());
		if(reg.getInvocationHandlers() != null)
		{
			for(String handler:reg.getInvocationHandlers())
			{
				record.setValue(handler, handler);
			}
		}
		
		if(dirtyStatus != null)
		{
			record.setDirtyStatus(dirtyStatus);
		}
	}
			
	private void updateCursor(MatrixCursor cursor,BusRegistration reg)
	{				
		String busId = reg.getBusId();
		cursor.addRow(new String[]{busId,null});
		if(reg.getInvocationHandlers()!=null)
		{
			for(String handler:reg.getInvocationHandlers())
			{
				cursor.addRow(new String[]{busId,handler});
			}
		}
	}
}
