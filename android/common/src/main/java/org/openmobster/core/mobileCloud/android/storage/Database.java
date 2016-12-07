/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import java.util.Set;

import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

import android.content.Context;
import android.database.Cursor;

/**
 * Concurrency Marker - Concurrent Component (concurrently used by multiple threads)
 * 
 * @author openmobster@gmail.com
 */
public final class Database
{
	private static Database singleton;
	
	//Shared System Tables/Data (Shared between the main device container and all other moblet applications installed on the device)
	public static String config_table = "tb_config"; //stores container configuration
	public static String sync_changelog_table = "tb_changelog"; //stores changelog for the sync service
	public static String sync_anchor = "tb_anchor"; //stores anchor related data for the sync service
	public static String sync_recordmap = "tb_recordmap"; //stores record map related data for the sync service
	public static String sync_error = "tb_sync_error"; //stores sync errors
	public static String bus_registration = "tb_bus_registration"; //stores service bus registrations used for inter-application invocations
	public static String provisioning_table = "tb_provisioning"; //stores device provisioning related information
	public static String system_errors = "tb_errorlog"; //stores runtime errors genenerated by mobile cloud and all the moblets
	
	private CloudDBMetaData cloudbMetaData;
	
	private Database(Context context)
	{
		this.cloudbMetaData = new CloudDBMetaData(context);
	}
	
	public static Database getInstance(Context context) throws DBException
	{
		if(Database.singleton == null)
		{
			synchronized(Database.class)
			{
				if(Database.singleton == null)
				{
					Database.singleton = new Database(context);
					Database.singleton.init();
				}
			}
		}
		return Database.singleton;
	}
	//------Lifecycle operations-----------------------------------------------------------------------------------------------------------------
	private void init() throws DBException
	{
		this.connect();
	}
	
	public void connect() throws DBException
	{
		this.cloudbMetaData.connect();
	}
	
	public void disconnect() throws DBException
	{
		this.cloudbMetaData.disconnect();
	}
	
	public boolean isConnected() throws DBException
	{
		return this.cloudbMetaData.isConnected();
	}
	//-------Table related operations--------------------------------------------------------------------------------------------------
	//Note: Used only by test environment
	public Set<String> enumerateTables() throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(),"enumerateTables",null,DBException.ERROR_NOT_CONNECTED);
		}
		
		return this.cloudbMetaData.listTables();
	}
	
	public void dropTable(String table) throws DBException
	{
		//Validate
		this.validateConnection(table, "dropTable");
		
		if(table.equals(config_table))
		{			
			throw new DBException(this.getClass().getName(), "dropTable", new Object[]{table}, DBException.ERROR_CONFIG_TABLE_DELETE_NOT_ALLOWED);
		}
		this.cloudbMetaData.dropTable(table);
	}
	//Note: these are used by the core engine
	public void createTable(String table) throws DBException
	{
		//Validate
		this.validateConnection(table, "createTable");
		
		this.cloudbMetaData.createTable(table);
	}
	
	public boolean doesTableExist(String table) throws DBException
	{
		//Validate
		this.validateConnection(table, "doesTableExist");
		
		return this.cloudbMetaData.doesTableExist(table);
	}
	
	public boolean isTableEmpty(String table) throws DBException
	{
		//Validate
		this.validateConnection(table, "isTableEmpty");
		
		return this.cloudbMetaData.isTableEmpty(table);
	}
	//-----Record insertion--------------------------------------------------------------------------------------------------------------------
	public String insert(String into, Record record) throws DBException
	{
		//Validate
		this.validateConnection(into, "insert");
		
		return this.cloudbMetaData.getCRUDProvider().insert(into, record);
	}
	
	public Set<Record> selectAll(String from) throws DBException
	{
		//Alteração feita na versão 2.4-M3.1 do OpenMobster.
		return selectAll(from, false);
		
//		//Validate
//		this.validateConnection(from, "selectAll");
//		
//		return this.cloudbMetaData.getCRUDProvider().selectAll(from);
	}
	
	public long selectCount(String from) throws DBException
	{
		//Validate
		this.validateConnection(from, "selectCount");
		
		return this.cloudbMetaData.getCRUDProvider().selectCount(from);
	}
	
	public Record select(String from, String recordId) throws DBException
	{
		//Validate
		this.validateConnection(from, "select");
		
		return this.cloudbMetaData.getCRUDProvider().select(from, recordId);
	}
	
	public Set<Record> select(String from, String name, String value) throws DBException
	{
		//Validate
		this.validateConnection(from, "selectWithWhereClause");
		
		return this.cloudbMetaData.getCRUDProvider().select(from, name, value);
	}
	
	public Set<Record> selectByValue(String from,String value) throws DBException
	{
		//Validate
		this.validateConnection(from, "selectByValue");
		
		return this.cloudbMetaData.getCRUDProvider().selectByValue(from,value);
	}
	
	public Set<Record> selectByNotEquals(String from,String value) throws DBException
	{
		//Validate
		this.validateConnection(from, "selectByNotEquals");
		
		return this.cloudbMetaData.getCRUDProvider().selectByNotEquals(from,value);
	}
	
	public Set<Record> selectByContains(String from,String value) throws DBException
	{
		//Validate
		this.validateConnection(from, "selectByContains");
		
		return this.cloudbMetaData.getCRUDProvider().selectByContains(from,value);
	}
	
	public void update(String into, Record record) throws DBException
	{
		//Validate
		this.validateConnection(into, "update");
		
		//Note: With Android, write threads lock the entire database.
		//so this operation is thread-safe
		
		//Check Dirty Status (support for long transactions)
		Record stored = this.cloudbMetaData.getCRUDProvider().select(into, record.getRecordId());
		if(!stored.getDirtyStatus().equals(record.getDirtyStatus()))
		{
			throw new DBException(this.getClass().getName(),"update",new Object[]{into},DBException.ERROR_RECORD_STALE);
		}
		
		this.cloudbMetaData.getCRUDProvider().update(into, record);
	}
	
	public void delete(String table, Record record) throws DBException
	{
		//Validate
		this.validateConnection(table, "delete");
		
		this.cloudbMetaData.getCRUDProvider().delete(table, record);
	}
	
	public void deleteAll(String table) throws DBException
	{
		//Validate
		this.validateConnection(table, "deleteAll");
		
		this.cloudbMetaData.getCRUDProvider().deleteAll(table);
	}
	
	public Cursor readProxyCursor(String from) throws DBException
	{
		//Validate
		this.validateConnection(from, "readProxyCursor");
		
		return this.cloudbMetaData.getCRUDProvider().readProxyCursor(from);
	}
	
	public Cursor readByName(String table,String name) throws DBException
	{
		//Validate
		this.validateConnection(table, "readByName");
		return this.cloudbMetaData.getCRUDProvider().readByName(table,name);
	}
	
	public Cursor readByName(String table,String name,boolean sortAscending) throws DBException
	{
		//Validate
		this.validateConnection(table, "readByName/sort");
		return this.cloudbMetaData.getCRUDProvider().readByName(table,name,sortAscending);
	}
	
	public Cursor readByNameValuePair(String from,String name,String value) throws DBException
	{
		//Validate
		this.validateConnection(from, "readByNameValuePair");
		return this.cloudbMetaData.getCRUDProvider().readByNameValuePair(from,name,value);
	}
	
	public Cursor searchExactMatchAND(String from, GenericAttributeManager criteria) throws DBException
	{
		//Validate
		this.validateConnection(from, "searchExactMatchAND");
		return this.cloudbMetaData.getCRUDProvider().searchExactMatchAND(from, criteria);
	}
	
	public Cursor searchExactMatchOR(String from, GenericAttributeManager criteria) throws DBException
	{
		//Validate
		this.validateConnection(from, "searchExactMatchOR");
		return this.cloudbMetaData.getCRUDProvider().searchExactMatchOR(from, criteria);
	}
	//--------Validation methods-----------------------------------------------------------------------------------------
	private void validateConnection(String table, String caller) throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(),caller,new Object[]{table},DBException.ERROR_NOT_CONNECTED);
		}
	}
	
	
	/*
	 * Métodos adicionados na versão 2.4-M3.1
	 */
	
	/**
	 * Roda uma query diretamente no banco de dados do OpenMobster. Esta abertura é necessária para a nova estrutura
	 * de consultas dos dados.
	 * 
	 * @param query query que será executada.
	 * @param args argumentos da query. Opcional.
	 * @return o cursor resultante da execução.
	 */
	public Cursor rawQuery(String query, String... args) {
		return cloudbMetaData.getCRUDProvider().rawQuery(query, args);
	}
	
	/**
	 * Seleciona todos os registros da tabela.
	 * 
	 * @param from tabela cujo os registros serão selecionados.
	 * @param ordered <code>true</code> se os registros devem estar na mesma ordem obtida do banco de dados ou <code>false</code>
	 * se a ordem dos registros não importar.
	 * @return os registros selecionados.
	 */
	public Set<Record> selectAll(String from, boolean ordered) throws DBException {
		this.validateConnection(from, "selectAll");
		
		return this.cloudbMetaData.getCRUDProvider().selectAll(from, ordered);
	}
	
	
	/**
	 * Inicia uma transação no modo EXCLUSIVE no banco de dados do OpenMobster.
	 */
	public void beginTransaction() {
		cloudbMetaData.getCRUDProvider().beginTransaction();
	}
	
	/**
	 * Marca a transação atual do banco de dados do OpenMobster como bem sucedida. Isto fará com que a ela seja
	 * commitada quando o {@link #endTransaction()} for chamado.
	 */
	public void setTransactionSuccessful() {
		cloudbMetaData.getCRUDProvider().setTransactionSuccessful();
	}
	
	/**
	 * Finaliza a transação atual do banco de dados do OpenMobster. Se ela foi marcada como bem sucedida (através do método
	 * {@link #setTransactionSuccessful()}) commita as alterações. Caso contrário, faz o rollback.
	 */
	public void endTransaction() {
		cloudbMetaData.getCRUDProvider().endTransaction();
	}
}
