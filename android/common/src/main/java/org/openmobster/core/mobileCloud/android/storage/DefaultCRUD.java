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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.text.MessageFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openmobster.core.mobileCloud.android.filesystem.FileSystem;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.JsonReader;

import org.openmobster.core.mobileCloud.android.util.OpenMobsterUtils;

import android.util.Log;
import android.database.sqlite.SQLiteTransactionListener;

/**
 *
 * @author openmobster@gmail.com
 */
public class DefaultCRUD implements CRUDProvider
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
		try
		{
			this.db.beginTransaction();
			
			if(!record.isStoreable())
			{
				throw new DBException(DefaultCRUD.class.getName(),"insert", new Object[]{
					"Exception: The JSON Object exceeds the maximum size limit"
				});
			}
			
			//SetUp the RecordId
			String recordId = record.getRecordId();
			if(recordId == null || recordId.trim().length() == 0)
			{
				recordId = GeneralTools.generateUniqueId();
				record.setRecordId(recordId);
			}
			
			this.addRecord(table, record);
			
			this.db.setTransactionSuccessful();
			
			
			if (!table.startsWith("tb_") && OpenMobsterUtils.isDebug()) {
				Log.i("OpenMobster Info", String.format("Record inserted: channel = %s, id = %s.", table, record.getRecordId()));
			}
			
			return recordId;
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"insert", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	public long selectCount(String from) throws DBException
	{
		Cursor cursor = null;
		try
		{
			cursor = this.db.rawQuery("SELECT count(*) FROM "+from+" WHERE name=?", new String[]{"om:json"});
			if(cursor.getCount()>0)
			{
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				return count;
			}
			
			return 0;
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"selectCount", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public Set<Record> selectAll(String from, boolean ordered) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> all = null;
			
			Map<String,Record> cached = this.cache.all(from);
			
			cursor = this.db.rawQuery("SELECT recordid,value FROM "+from+" WHERE name=?", new String[]{"om:json"});
			
			if(cursor.getCount() > 0)
			{
				/*
				 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
				 * Adicionado suporte ao retorno ordenado. Na verdade, todos os selects desta classe poderiam retornar Lists,
				 * mas esta alteração teria um impacto muito grande. Por isto, simplesmente usa o LinkedHashSet quando necessário.
				 */
				if (ordered) {
					all = new LinkedHashSet<Record>();
				} else {
					all = new HashSet<Record>();
				}
				int recordidIndex = cursor.getColumnIndex("recordid");
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					if(cached.containsKey(recordid))
					{
						//object is cached...no need to read from the database
						all.add(cached.get(recordid));
						cursor.moveToNext();
						continue;
					}
					
					try {
					
						String value = cursor.getString(valueIndex);
						
						Map<String,String> state = new HashMap<String,String>();
						FileSystem fileSystem = FileSystem.getInstance();
						InputStream in = fileSystem.openInputStream(value);
						JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
						try
						{
							reader.beginObject();
							while(reader.hasNext())
							{
								String localName = reader.nextName();
								String localValue = reader.nextString();
								state.put(localName, localValue);
							}
							reader.endObject();
						}
						finally
						{
							reader.close();
						}
						Record record = new Record(state);
						all.add(record);
						this.cache.put(from,record.getRecordId(),record);
						
					} catch (IOException e) {
						/*
						 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
						 * Se por ventura o arquivo do registro for corrompido, apenas ignora se estiver em produção para evitar o travamento total do aplicativo.
						 */
						if (OpenMobsterUtils.isDebug()) {
							throw e;
						}
						Log.e("OpenMobster Error", "Corrupted record file", e);
					}
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return all;
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"selectAll", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	
	
	public Record select(String from, String recordId) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Record record = null;
			
			record = this.cache.get(from, recordId);
			if(record != null)
			{
				return record;
			}
			
			cursor = this.db.rawQuery("SELECT value FROM "+from+" WHERE recordid=? AND name=?", new String[]{recordId,"om:json"});
			
			if(cursor.getCount() > 0)
			{
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					try {
						String value = cursor.getString(valueIndex);
						
						Map<String,String> state = new HashMap<String,String>();
						FileSystem fileSystem = FileSystem.getInstance();
						InputStream in = fileSystem.openInputStream(value);
						JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
						try
						{
							reader.beginObject();
							while(reader.hasNext())
							{
								String localName = reader.nextName();
								String localValue = reader.nextString();
								state.put(localName, localValue);
							}
							reader.endObject();
						}
						finally
						{
							reader.close();
						}
						
						record = new Record(state);
						this.cache.put(from,record.getRecordId(), record);
						
					} catch (IOException e) {
						/*
						 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
						 * Se por ventura o arquivo do registro for corrompido, apenas ignora se estiver em produção para evitar o travamento total do aplicativo.
						 */
						if (OpenMobsterUtils.isDebug()) {
							throw e;
						}
						Log.e("OpenMobster Error", "Corrupted record file", e);
					}
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return record;
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"select", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public void update(String table, Record record) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			if(!record.isStoreable())
			{
				throw new DBException(DefaultCRUD.class.getName(),"update", new Object[]{
					"Exception: The JSON Object exceeds the maximum size limit"
				});
			}
			
			String recordId = record.getRecordId();
			
			//invalidate the cacched copy if present
			this.cache.invalidate(table, recordId);
			
			this.addRecord(table, record);
			
			this.db.setTransactionSuccessful();
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"update", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
		finally
		{
			this.db.endTransaction();
		}
		
		if (!table.startsWith("tb_") && OpenMobsterUtils.isDebug()) {
			Log.i("OpenMobster Info", String.format("Record updated: channel = %s, id = %s.", table, record.getRecordId()));
		}
	}
	
	public void delete(String table, Record record) throws DBException
	{
		Cursor cursor = null;
		try
		{
			this.db.beginTransaction();
			
			String recordId = record.getRecordId();
			
			//invalidate the cached object 
			this.cache.invalidate(table, recordId);
			
			//Get the JSonPointer that must be cleaned up along with deleting this record
			String jsonPointer = null;
			cursor = this.db.rawQuery("SELECT value FROM "+table+" WHERE recordid=? AND name=?", new String[]{recordId,"om:json"});
			if(cursor.getCount()>0)
			{
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					jsonPointer = cursor.getString(valueIndex);
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			//delete this record
			String delete = "DELETE FROM "+table+" WHERE recordid='"+recordId+"'";
			this.db.execSQL(delete);
			
			//delete the json pointer
			if(jsonPointer != null)
			{
				/*
				 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
				 * Se há uma transação em andamento, armazena uma referência do arquivo para que ele seja excluído apenas
				 * se a transação sofrer commit.
				 */
				if (transaction == null) {
					FileSystem fileSystem = FileSystem.getInstance();
					fileSystem.cleanup(jsonPointer);
				} else {
					transaction.addFileToDelete(jsonPointer);
				}
//				FileSystem fileSystem = FileSystem.getInstance();
//				fileSystem.cleanup(jsonPointer);
			}
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
			this.db.endTransaction();
		}
		
		if (!table.startsWith("tb_") && OpenMobsterUtils.isDebug()) {
			Log.i("OpenMobster Info", String.format("Record deleted: channel = %s, id = %s.", table, record.getRecordId()));
		}
	}
	
	public void deleteAll(String table) throws DBException
	{
		Cursor cursor = null;
		try
		{
			this.db.beginTransaction();
			
			//clear the entire cache
			this.cache.clear(table);
			
			//Get JSONPointers that must be deleted for this table
			Set<String> jsonPointers = new HashSet<String>();
			cursor = this.db.rawQuery("SELECT value FROM "+table+" WHERE name=?", new String[]{"om:json"});
			if(cursor.getCount()>0)
			{
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					String jsonPointer = cursor.getString(valueIndex);
					jsonPointers.add(jsonPointer);
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			//delete this record
			String delete = "DELETE FROM "+table;
			this.db.execSQL(delete);
			
			//Cleanup the JSonPointers
			FileSystem fileSystem = FileSystem.getInstance();
			for(String jsonPointer:jsonPointers)
			{
				/*
				 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
				 * Se há uma transação em andamento, armazena uma referência do arquivo para que ele seja excluído apenas
				 * se a transação sofrer commit.
				 */
				if (transaction == null) {
					fileSystem.cleanup(jsonPointer);
				} else {
					transaction.addFileToDelete(jsonPointer);
				}
//				fileSystem.cleanup(jsonPointer);
			}
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
			this.db.endTransaction();
		}
		
		if (!table.startsWith("tb_") && OpenMobsterUtils.isDebug()) {
			Log.i("OpenMobster Info", String.format("All records deleted: channel = %s", table));
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
	public Cursor readProxyCursor(String from) throws DBException
	{
		Cursor cursor = this.db.rawQuery("SELECT recordid FROM "+from+" WHERE name=? AND value=?", new String[]{"isProxy","true"});
		return cursor;
	}
	
	public Cursor readByName(String from,String name) throws DBException
	{
		Cursor cursor = this.db.rawQuery("SELECT recordid FROM "+from+" WHERE name=?",new String[]{name});
		return cursor;
	}
	
	public Cursor readByName(String from,String name,boolean sortAscending) throws DBException
	{
		Cursor cursor = null;
		if(sortAscending)
		{
			cursor = this.db.rawQuery("SELECT recordid FROM "+from+" WHERE name=? ORDER BY value ASC",new String[]{name});
		}
		else
		{
			cursor = this.db.rawQuery("SELECT recordid FROM "+from+" WHERE name=? ORDER BY value DESC",new String[]{name});
		}
		return cursor;
	}
	
	public Cursor readByNameValuePair(String from,String name,String value) throws DBException
	{
		Cursor cursor = this.db.rawQuery("SELECT recordid FROM "+from+" WHERE name=? AND value=?", new String[]{name,value});
		return cursor;
	}
	
	public Cursor searchExactMatchAND(String from, GenericAttributeManager criteria) throws DBException
	{
		if(criteria == null || criteria.isEmpty())
		{
			return null;
		}
		
		String fragment = "value LIKE ''%{0}={1}%''";
		List<String> fragments = new ArrayList<String>();
		String[] names = criteria.getNames();
		for(String name:names)
		{
			String value = (String)criteria.getAttribute(name);
			String queryFragment = MessageFormat.format(fragment, name, value);
			
			fragments.add(queryFragment);
		}
		
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("SELECT recordid FROM "+from+" WHERE name='om:search' AND (");
		for(int i=0,size=fragments.size();i<size;i++)
		{
			String queryFragment = fragments.get(i);
			queryBuffer.append(queryFragment);
			
			if(i==(size-1))
			{
				//this is the last fragment
				queryBuffer.append(")");
			}
			else
			{
				queryBuffer.append(" AND ");
			}
		}
		
		
		String query = queryBuffer.toString();
		
		Cursor cursor = this.db.rawQuery(query, new String[]{});
		
		return cursor;
	}
	
	public Cursor searchExactMatchOR(String from, GenericAttributeManager criteria) throws DBException
	{
		if(criteria == null || criteria.isEmpty())
		{
			return null;
		}
		
		String fragment = "value LIKE ''%{0}={1}%''";
		List<String> fragments = new ArrayList<String>();
		String[] names = criteria.getNames();
		for(String name:names)
		{
			String value = (String)criteria.getAttribute(name);
			String queryFragment = MessageFormat.format(fragment, name, value);
			
			fragments.add(queryFragment);
		}
		
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("SELECT recordid FROM "+from+" WHERE name='om:search' AND (");
		for(int i=0,size=fragments.size();i<size;i++)
		{
			String queryFragment = fragments.get(i);
			queryBuffer.append(queryFragment);
			
			if(i==(size-1))
			{
				//this is the last fragment
				queryBuffer.append(")");
			}
			else
			{
				queryBuffer.append(" OR ");
			}
		}
		
		
		String query = queryBuffer.toString();
		
		Cursor cursor = this.db.rawQuery(query, new String[]{});
		
		return cursor;
	}
	//------------------------------------------------Private Impl-------------------------------------------------------------------------------
	private void addRecord(String table,Record record) throws DBException
	{
		String recordId = record.getRecordId();
		
		//SetUp the DirtyStatus
		record.setDirtyStatus(GeneralTools.generateUniqueId());
		
		//Delete a record if one exists by this id...cleanup
		this.delete(table, record);
		
		Set<String> names = record.getNames();
		Map<String,String> nameValuePairs = new HashMap<String,String>();
		for(String name: names)
		{
			String value = record.getValue(name);
			boolean addProperty = true;
			
			//check if this is a name
			if(name.startsWith("field[") && name.endsWith("].uri"))
			{
				nameValuePairs.put(name, value);
				addProperty = false;
			}
			
			//check fi this is a value
			if(name.startsWith("field[") && name.endsWith("].value"))
			{
				nameValuePairs.put(name, value);
				addProperty = false;
			}
			
			//insert this row
			if(addProperty)
			{	
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
			}
		}
		
		//insert the name value pairs
		Set<String> keys = nameValuePairs.keySet();
		StringBuilder builder = new StringBuilder();
		for(String key:keys)
		{
			if(key.endsWith("].value"))
			{
				continue;
			}
			
			String name = nameValuePairs.get(key);
			name = this.calculatePropertyUri(name);
			
			//Get the value
			String value = nameValuePairs.get(key.replace("].uri", "].value"));
			
			if(value.length() < 1000)
			{
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
				
				builder.append(name+"="+value+"&amp;");
			}
		}
		
		String metaDataInsert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
		this.db.execSQL(metaDataInsert,new Object[]{recordId,"om:search",builder.toString()});
		
		//insert the JSON representation
		String jsonPointer = record.toJson();
		String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
		this.db.execSQL(insert,new Object[]{recordId,"om:json",jsonPointer});
		
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Se há uma transação em andamento, armazena uma referência do arquivo para que ele seja excluído caso a
		 * transação sofra rollback.
		 */
		if (transaction != null) {
			transaction.addCreatedFile(jsonPointer);
		}
	}
	
	private static String calculatePropertyUri(String uri)
	{
		String propertyUri = null;
		if(uri == null)
		{
			return null;
		}
		
		if(uri.startsWith("/"))
		{
			if(uri.equals("/"))
			{
				return "";
			}
			
			propertyUri = uri.substring(1);
		}
		else
		{
			propertyUri = uri;
		}
		
		propertyUri = propertyUri.replaceAll("/", ".");
		
		return propertyUri;
	}
	//----------------------------------------------Deprecated------------------------------------------------------------------------
	public Set<Record> select(String from, String name, String value) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT DISTINCT recordid FROM "+from+" WHERE name=? AND value=?", new String[]{name,value});
			
			if(cursor.getCount() > 0)
			{
				records = new HashSet<Record>();
				
				int recordidIndex = cursor.getColumnIndex("recordid");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					
					Record record = this.select(from, recordid);
					records.add(record);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return records;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public Set<Record> selectByValue(String from, String value) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT DISTINCT recordid FROM "+from+" WHERE value=?", new String[]{value});
			
			if(cursor.getCount() > 0)
			{
				records = new HashSet<Record>();
				
				int recordidIndex = cursor.getColumnIndex("recordid");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					
					Record record = this.select(from, recordid);
					records.add(record);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return records;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public Set<Record> selectByNotEquals(String from, String value) throws DBException
	{
		/*Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT DISTINCT recordid FROM "+from,null);
			
			if(cursor.getCount() > 0)
			{
				records = new HashSet<Record>();
				
				int recordidIndex = cursor.getColumnIndex("recordid");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					
					Record record = this.select(from, recordid);
					records.add(record);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return records;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}*/
		return this.selectAll(from, false);
	}
	
	public Set<Record> selectByContains(String from, String value) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT DISTINCT recordid FROM "+from+" WHERE value LIKE ?", new String[]{"%"+value+"%"});
			
			if(cursor.getCount() > 0)
			{
				records = new HashSet<Record>();
				
				int recordidIndex = cursor.getColumnIndex("recordid");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					
					Record record = this.select(from, recordid);
					records.add(record);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return records;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	
	/*
	 * Estruturas adicionados na versão 2.4-M3.1
	 */
	
	/**
	 * Converte o texto baseado no formato de campos em memória do OpenMobster (/x[0]/y/z) para o formato de campos
	 * armazenados no banco (x[0].y.z).
	 * 
	 * @param text o texto que será convertido.
	 * @return o texto convertido.
	 */
	public static final String convertToDatabaseFormat(String text) {
		return calculatePropertyUri(text);
	}
	
	@Override
	public final Cursor rawQuery(String query, String... args) {
		return db.rawQuery(query, args);
	}
	
	
	
	/*
	 * Estrutura de transações.
	 */
	
	private FileDataTransactionListener transaction;
	
	@Override
	public void beginTransaction() {
		if (transaction != null) {
			throw new IllegalStateException("DefaultCRUD does not support nested transactions");
		}
		
		transaction = new FileDataTransactionListener();
		db.beginTransactionWithListener(transaction);
		
		applyWorkaroundForSQLiteTransactionListenerBug_begin();
	}
	
	@Override
	public void setTransactionSuccessful() {
		db.setTransactionSuccessful();
		
		applyWorkaroundForSQLiteTransactionListenerBug_setSuccessful();
	}
	
	@Override
	public void endTransaction() {
		db.endTransaction();
		
		applyWorkaroundForSQLiteTransactionListenerBug_end();
	}
	
	private void onFinishTransaction() {
		transaction = null;
	}
	
	/**
	 * Listener que armazena os arquivos de dados que devem ser excluídos no final de uma transação, da seguinte forma:<br>
	 * <ul>
	 * 	<li>exclui os arquivos de registros removidos apenas se a transação for commitada;</li>
	 * 	<li>exclui os arquivos de registros criados caso a transação sofra rollback.</li>
	 * </ul>
	 */
	private final class FileDataTransactionListener implements SQLiteTransactionListener {

		private final HashSet<String> filesToDelete = new HashSet<String>();
		private final HashSet<String> createdFiles = new HashSet<String>();
		
		@Override
		public void onBegin() {
			if (OpenMobsterUtils.isDebug()) {
				Log.i("OpenMobster Info", "onBegin transaction");
			}
		}

		@Override
		public void onCommit() {
			//Exclui os arquivos que foram marcados para exclusão.
			finish(filesToDelete);
			
			if (OpenMobsterUtils.isDebug()) {
				Log.i("OpenMobster Info", "onCommit transaction");
			}
		}

		@Override
		public void onRollback() {
			//Exclui os arquivos que foram criados.
			finish(createdFiles);
			
			if (OpenMobsterUtils.isDebug()) {
				Log.i("OpenMobster Info", "onRollback transaction");
			}
		}
		
		public void addFileToDelete(String file) {
			filesToDelete.add(file);
		}
		
		public void addCreatedFile(String file) {
			createdFiles.add(file);
		}

		private void finish(HashSet<String> files) {
			try {
				FileSystem fileSystem = FileSystem.getInstance();
				for(String file: files) {
					fileSystem.cleanup(file);
				}
			} catch (Exception e) {
				Log.e("OpenMobster Error", "finish transaction (cleaning up files)", e);
			} finally {
				onFinishTransaction();
			}
		}
		
		public void finish(boolean successful) {
			finish(successful ? filesToDelete : createdFiles);
		}
	}
	
	
	
	/*
	 * Contorno para bug na API 15- do Android em que o "onCommit" e o "onRollback" do SQLiteTransactionListener não é chamado,
	 * fazendo com que o listener não seja anulado e o próximo "beginTransaction" considere que ainda há uma transação ativa,
	 * lançando uma exceção.
	 * O erro ocorre por causa da utilização de transações internas (pelo OpenMobster) que acabam anulando o listener,
	 * conforme descrito aqui: http://stackoverflow.com/questions/4524377/sqlitetransactionlistener-oncommit-and-onrollback-never-called
	 */
	
	private boolean transactionSucessful;
	
	private final void applyWorkaroundForSQLiteTransactionListenerBug_begin() {
		if (!needSQLiteTransactionListenerWorkaround()) {
			return;
		}
		
		transactionSucessful = false;
	}
	
	private final void applyWorkaroundForSQLiteTransactionListenerBug_setSuccessful() {
		if (!needSQLiteTransactionListenerWorkaround()) {
			return;
		}
		
		transactionSucessful = true;
	}
	
	private final void applyWorkaroundForSQLiteTransactionListenerBug_end() {
		if (!needSQLiteTransactionListenerWorkaround()) {
			return;
		}
		

		if (transaction != null) {
			transaction.finish(transactionSucessful);
		}
	}
	
	private boolean needSQLiteTransactionListenerWorkaround() {
		return Build.VERSION.SDK_INT <= 15 /*Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1*/;
	}
}