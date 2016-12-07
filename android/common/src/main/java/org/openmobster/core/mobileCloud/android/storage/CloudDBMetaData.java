/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

/**
 * @author openmobster@gmail.com
 */
final class CloudDBMetaData
{	
	private SQLiteDatabase db;
	private CloudDBManager manager;
	private CRUDProvider crudProvider;
	
	CloudDBMetaData(Context context)
	{
		List<String> tables = new ArrayList<String>();
		tables.add(Database.config_table); //exposed via provider
		tables.add(Database.sync_anchor);
		tables.add(Database.sync_changelog_table);
		tables.add(Database.sync_error);
		tables.add(Database.sync_recordmap);
		tables.add(Database.bus_registration); //exposed via provider
		tables.add(Database.provisioning_table); //exposed via provider
		tables.add(Database.system_errors);
		
		this.manager = new CloudDBManager(tables, context, "cloudb", 2);
	}
	
	SQLiteDatabase getDb()
	{
		if(!this.isConnected())
		{
			throw new IllegalStateException("CloudDB is closed!!");
		}
		return this.db;
	}
	//--------------------------------------------------------------------------------------------------------------------
	void connect()
	{
		if(this.db == null || !this.db.isOpen())
		{
			this.db = this.manager.getWritableDatabase();
			
			//Decide which CRUDProvider should be used
			boolean isEncryptionActivated = AppSystemConfig.getInstance().isEncryptionActivated();
			
			if(isEncryptionActivated)
			{
				this.crudProvider = new EncryptedCRUD();
			}
			else
			{
				this.crudProvider = new DefaultCRUD();
			}
			
			this.crudProvider.init(this.db);
		}
	}
	
	boolean isConnected()
	{
		return (this.db != null && this.db.isOpen());
	}
	
	void disconnect()
	{
		if(this.isConnected())
		{
			this.db.close();
			this.db = null;
			
			this.crudProvider.cleanup();
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------
	CRUDProvider getCRUDProvider()
	{
		return  this.crudProvider;
	}
	
	Set<String> listTables() throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<String> tables = null;
			
			cursor = this.db.rawQuery("SELECT name FROM sqlite_master WHERE type=?", new String[]{"table"});
			
			if(cursor.getCount() > 0)
			{
				tables = new HashSet<String>();
				int nameIndex = cursor.getColumnIndex("name");
				cursor.moveToFirst();
				do
				{
					String name = cursor.getString(nameIndex);
					tables.add(name);
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return tables;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	void dropTable(String table) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			String tableSql = "DROP TABLE IF EXISTS " + table + ";";
			this.db.execSQL(tableSql);
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	void createTable(String table) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			String tableSql = "CREATE TABLE IF NOT EXISTS " + table + " ("
			+ "id INTEGER PRIMARY KEY," + "recordid TEXT," + "name TEXT," + "value TEXT"
			+ ");";
			this.db.execSQL(tableSql);
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	boolean doesTableExist(String table) throws DBException
	{
		Cursor cursor = null;
		try
		{
			cursor = this.db.rawQuery("SELECT name FROM sqlite_master WHERE type=? AND name=?", 
			new String[]{"table", table});
			
			if(cursor.getCount()>0)
			{
				return true;
			}
			
			return false;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	boolean isTableEmpty(String table) throws DBException
	{
		Cursor cursor = null;
		try
		{
			cursor = this.db.rawQuery("SELECT count(*) FROM "+table, null);
			
			int countIndex = cursor.getColumnIndex("count(*)");
			cursor.moveToFirst();
			int rowCount = cursor.getInt(countIndex);
			if(rowCount > 0)
			{
				return false;
			}
			
			return true;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
}
