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

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

/**
 *
 * @author openmobster@gmail.com
 */
public interface CRUDProvider
{
	void init(SQLiteDatabase db);
	
	void cleanup();
	
	String insert(String table, Record record) throws DBException;
	
	/*
	 * Alterado na versão 2.4-M3.1 do OpenMobster.
	 */
//	Set<Record> selectAll(String from) throws DBException;
	Set<Record> selectAll(String from, boolean ordered) throws DBException;
	
	long selectCount(String from) throws DBException;
	
	Record select(String from, String recordId) throws DBException;
	
	Set<Record> select(String from, String name, String value) throws DBException;
	
	Set<Record> selectByValue(String from, String value) throws DBException;
	
	Set<Record> selectByNotEquals(String from, String value) throws DBException;
	
	Set<Record> selectByContains(String from, String value) throws DBException;
	
	void update(String table, Record record) throws DBException;
	
	void delete(String table, Record record) throws DBException;
	
	void deleteAll(String table) throws DBException;
	
	//---------------------------------------------------------------------------------------------------------------------
	Cursor readProxyCursor(String table) throws DBException;
	Cursor readByName(String table,String name) throws DBException;
	Cursor readByName(String table,String name,boolean sortAscending) throws DBException;
	Cursor readByNameValuePair(String from,String name,String value) throws DBException;
	Cursor searchExactMatchAND(String from, GenericAttributeManager criteria) throws DBException;
	Cursor searchExactMatchOR(String from, GenericAttributeManager criteria) throws DBException;
	
	
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
	Cursor rawQuery(String query, String... args);
	
	
	/**
	 * Inicia uma transação no modo EXCLUSIVE no banco de dados do OpenMobster.
	 */
	void beginTransaction();
	
	/**
	 * Marca a transação atual do banco de dados do OpenMobster como bem sucedida. Isto fará com que a ela seja
	 * commitada quando o {@link #endTransaction()} for chamado.
	 */
	void setTransactionSuccessful();
	
	/**
	 * Finaliza a transação atual do banco de dados do OpenMobster. Se ela foi marcada como bem sucedida (através do método
	 * {@link #setTransactionSuccessful()}) commita as alterações. Caso contrário, faz o rollback.
	 */
	void endTransaction();
}
