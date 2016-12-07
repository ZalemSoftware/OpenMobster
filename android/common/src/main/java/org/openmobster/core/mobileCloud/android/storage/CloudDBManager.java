/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author openmobster@gmail.com
 */
final class CloudDBManager extends SQLiteOpenHelper
{
	private List<String> tables;
	
	CloudDBManager(List<String> tables, Context context, String name, int version)
	{
		super(context, name, null, version);
		
		if(tables == null)
		{
			throw new IllegalArgumentException("List of Tables to managed should not be null!!");
		}
		
		this.tables = tables;
	}
	
	@Override
	public void onCreate(SQLiteDatabase clouddb)
	{	
		for(String table: this.tables)
		{
			this.createTable(clouddb, table);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase clouddb, int oldVersion, int newVersion)
	{	
		//Do Nothing for now
	}
	//------------------------------------------------------------------------------------------------------------------
	private void createTable(SQLiteDatabase database, String tableName)
	{
		try
		{
			database.beginTransaction();
			
			// Create a table
			String tableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
					+ "id INTEGER PRIMARY KEY," + "recordid TEXT," + "name TEXT," + "value TEXT"
					+ ");";
			database.execSQL(tableSql);
			
			//this makes sure transaction is committed
			database.setTransactionSuccessful();
		} 
		finally
		{
			database.endTransaction();
		}
	}
}
