/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.provider;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.database.MatrixCursor;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * @author openmobster@gmail.com
 *
 */
public class ConfigurationProvider extends ContentProvider
{
	@Override
	public boolean onCreate()
	{
		try
		{
			Database database = Database.getInstance(this.getContext());
			
			//Initialize the provisioning table
			if(!database.doesTableExist(Database.provisioning_table))
			{
				database.createTable(Database.provisioning_table);	
			}
			
			if(database.isTableEmpty(Database.provisioning_table))
			{
				//Create an empty provisioning record
				database.insert(Database.provisioning_table, new Record());
			}
			
			return true;
		}
		catch(Exception e)
		{
			//Error Handler
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(),"onCreate", new Object[]{
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					}
			));
			return false;
		}
	}
	
	@Override
	public String getType(Uri uri)
	{
		return "vnd.android.cursor.dir/vnd.openmobster.configuration";
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
			
			Set<Record> all = database.selectAll(Database.provisioning_table);
			if(all != null && !all.isEmpty())
			{
				Record provisioningRecord = all.iterator().next();
				this.prepareCursor(cursor, provisioningRecord);
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
			Context context = this.getContext();
			Database database = Database.getInstance(context);
			
			Set<Record> all = database.selectAll(Database.provisioning_table);
			if(all == null || all.isEmpty())
			{
				//insert
				Record provisioningRecord = new Record();
				this.prepareRecord(provisioningRecord, contentValues);
				database.insert(Database.provisioning_table, 
				provisioningRecord);
			}
			else
			{
				//update
				Record provisioningRecord = all.iterator().next();
				this.prepareRecord(provisioningRecord, contentValues);
				database.update(Database.provisioning_table, 
				provisioningRecord);
			}
			
			return uri;
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
		//Not needed
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		//Not needed
		return 0;
	}
	//------------------------------------------------------------------------------------
	private void prepareCursor(MatrixCursor cursor, Record record)
	{				
		cursor.addRow(new String[]{"deviceId",record.getValue("deviceId")});
		cursor.addRow(new String[]{"serverId",record.getValue("serverId")});
		cursor.addRow(new String[]{"serverIp",record.getValue("serverIp")});
		cursor.addRow(new String[]{"plainServerPort",record.getValue("plainServerPort")});
		cursor.addRow(new String[]{"secureServerPort",record.getValue("secureServerPort")});
		cursor.addRow(new String[]{"authenticationHash",record.getValue("authenticationHash")});
		cursor.addRow(new String[]{"authenticationNonce",record.getValue("authenticationNonce")});
		cursor.addRow(new String[]{"email",record.getValue("email")});
		cursor.addRow(new String[]{"cometMode",record.getValue("cometMode")});
		cursor.addRow(new String[]{"httpPort",record.getValue("httpPort")});
		
		String cometPollIntervalStr = record.getValue("cometPollInterval");
		if(cometPollIntervalStr != null && cometPollIntervalStr.trim().length()>0)
		{
			cursor.addRow(new String[]{"cometPollInterval",
			cometPollIntervalStr});			
		}
		
		String maxPacketSizeStr = record.getValue("maxPacketSize");
		if(maxPacketSizeStr != null && maxPacketSizeStr.trim().length()>0)
		{
			cursor.addRow(new String[]{"maxPacketSize",
			maxPacketSizeStr});
		}
				
		String sslStatus = record.getValue("isSSLActive");
		if(sslStatus != null && sslStatus.trim().length()>0)
		{
			cursor.addRow(new String[]{"isSSLActive",
			sslStatus});
		}
						
		String isActiveStr = record.getValue("isActive");
		if(isActiveStr != null && isActiveStr.trim().length()>0)
		{
			cursor.addRow(new String[]{"isActive",
			isActiveStr});
		}
		
		String isSSLCertStoredStr = record.getValue("isSSLCertStored");
		if(isSSLCertStoredStr != null && isSSLCertStoredStr.trim().length()>0)
		{
			cursor.addRow(new String[]{"isSSLCertStored",
			isSSLCertStoredStr});
		}
		
		this.prepareChannels(cursor, record);
	}
	
	private void prepareChannels(MatrixCursor cursor, Record record)
	{
		String cour = record.getValue("myChannels:size");
		if(cour != null && cour.trim().length()>0)
		{
			int channelCount = Integer.parseInt(cour);
			for(int i=0; i<channelCount; i++)
			{
				String channel = record.getValue("myChannels["+i+"]");
				cursor.addRow(new String[]{"myChannels["+i+"]",channel});
			}
		}
	}
	
	private void prepareRecord(Record provisioningRecord, 
	ContentValues values)
	{
		String local = values.getAsString("deviceId");
		if(local != null)
		{
			provisioningRecord.setValue("deviceId", local);
		}
		
		local = values.getAsString("serverId");
		if(local != null)
		{
			provisioningRecord.setValue("serverId", local);
		}
		
		local = values.getAsString("serverIp");
		if(local != null)
		{
			provisioningRecord.setValue("serverIp", local);
		}
		
		local = values.getAsString("plainServerPort");
		if(local != null)
		{
			provisioningRecord.setValue("plainServerPort", local);
		}
		
		local = values.getAsString("secureServerPort");
		if(local != null)
		{
			provisioningRecord.setValue("secureServerPort", local);
		}
		
		local = values.getAsString("authenticationHash");
		if(local != null)
		{
			provisioningRecord.setValue("authenticationHash", local);
		}
		else
		{
			provisioningRecord.removeValue("authenticationHash");
		}
		
		local = values.getAsString("authenticationNonce");
		if(local != null)
		{
			provisioningRecord.setValue("authenticationNonce", local);
		}
		else
		{
			provisioningRecord.removeValue("authenticationNonce");
		}
		
		local = values.getAsString("email");
		if(local != null)
		{
			provisioningRecord.setValue("email", local);
		}
		
		local = values.getAsString("cometMode");
		if(local != null)
		{
			provisioningRecord.setValue("cometMode", local);
		}
		
		local = values.getAsString("cometPollInterval");
		if(local != null)
		{
			provisioningRecord.setValue("cometPollInterval", local);
		}
		
		local = values.getAsString("httpPort");
		if(local != null)
		{
			provisioningRecord.setValue("httpPort", local);
		}
				
		provisioningRecord.setValue("isSSLActive", 
		values.getAsString("isSSLActive"));
		
		provisioningRecord.setValue("maxPacketSize", 
		values.getAsString("maxPacketSize"));
		
		provisioningRecord.setValue("isActive", 
				values.getAsString("isActive"));
		
		provisioningRecord.setValue("isSSLCertStored", 
				values.getAsString("isSSLCertStored"));
		
		this.prepareChannels(provisioningRecord,values);
	}
	
	private void prepareChannels(Record record, ContentValues values)
	{
		List<String> myChannels = new ArrayList<String>();
		Set<Entry<String,Object>> input = values.valueSet();		
		if(input == null)
		{
			return;
		}
		
		boolean channelsFound = false;
		for(Entry<String,Object> entry:input)
		{
			if(entry.getKey().startsWith("myChannels["))
			{
				myChannels.add((String)entry.getValue());
				channelsFound = true;
			}
		}
		
		if(!channelsFound)
		{
			return;
		}
		
		int channelCount = myChannels.size();
		record.setValue("myChannels:size", ""+channelCount);
		int i = 0;
		for(String channel: myChannels)
		{
			record.setValue("myChannels["+(i++)+"]", channel);
		}
	}
}
