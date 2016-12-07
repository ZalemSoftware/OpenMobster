/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.openmobster.core.mobileCloud.android.crypto.Cryptographer;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *
 * @author openmobster@gmail.com
 */
public class EncryptedCRUD implements CRUDProvider
{
	private SQLiteDatabase db;
	private Cache cache;
	
	public void init(SQLiteDatabase db)
	{
		this.db = db;
		this.cache = new Cache();
		this.cache.start();
	}
	
	public void cleanup()
	{
		this.db = null;
		
		this.cache.stop();
		this.cache = null;
	}
	
	public String insert(String table, Record record) throws DBException
	{
		return null;
	}
	
	public Set<Record> selectAll(String from, boolean ordered) throws DBException
	{
		return null;
	}
	
	public long selectCount(String from) throws DBException
	{
		return 0;
	}
	
	public Record select(String from, String recordId) throws DBException
	{
		return null;
	}
	
	public Set<Record> select(String from, String name, String value) throws DBException
	{
		//Cannot be implemented as a WHERE clause cannot be used on an encrypted record
		return null;
	}
	
	public Set<Record> selectByValue(String from, String name) throws DBException
	{
		//Cannot be implemented as a WHERE clause cannot be used on an encrypted record
		return null;
	}
	
	public Set<Record> selectByNotEquals(String from, String value) throws DBException
	{
		//Cannot be implemented as a WHERE clause cannot be used on an encrypted record
		return null;
	}
	
	public Set<Record> selectByContains(String from, String value) throws DBException
	{
		//Cannot be implemented as a WHERE clause cannot be used on an encrypted record
		return null;
	}
	
	public void update(String table, Record record) throws DBException
	{
	}
	
	public void delete(String table, Record record) throws DBException
	{
	}
	
	public void deleteAll(String table) throws DBException
	{
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public Cursor readByName(String from,String name) throws DBException
	{
		return null;
	}
	
	public Cursor readByName(String from,String name,boolean sortAscending) throws DBException
	{
		return null;
	}
	
	public Cursor readProxyCursor(String from) throws DBException
	{
		return null;
	}
	
	public Cursor readByNameValuePair(String from,String name,String value) throws DBException
	{
		return null;
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public Cursor searchExactMatchAND(String from, GenericAttributeManager criteria) throws DBException
	{
		return null;
	}
	public Cursor searchExactMatchOR(String from, GenericAttributeManager criteria) throws DBException
	{
		return null;
	}
	
	
	/*
	 * Métodos adicionados na versão 2.4-M3.1
	 */
	
	@Override
	public Cursor rawQuery(String query, String... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void beginTransaction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTransactionSuccessful() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void endTransaction() {
		throw new UnsupportedOperationException();
	}
}
