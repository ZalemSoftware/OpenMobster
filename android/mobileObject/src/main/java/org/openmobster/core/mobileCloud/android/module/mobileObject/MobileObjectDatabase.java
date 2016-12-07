/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmobster.android.utils.OpenMobsterBugUtils;
import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.storage.DBException;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

import android.content.Context;
import android.database.Cursor;

/**
 * @author openmobster@gmail.com
 */
public final class MobileObjectDatabase extends Service
{		
	public MobileObjectDatabase()
	{	
	}
	
	public static MobileObjectDatabase getInstance()
	{
		return (MobileObjectDatabase)Registry.getActiveInstance().lookup(MobileObjectDatabase.class);
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	public boolean isChannelBooted(String channel)
	{
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Verifica o bootup a partir da configuração.
		 */
		return configuration.isBooted(channel);
		
//		try
//		{
//			Context context = Registry.getActiveInstance().getContext();
//			Database database = Database.getInstance(context);
//			
//			boolean doesTableExist = database.doesTableExist(channel);
//			
//			return doesTableExist;
//		}
//		catch(Exception e)
//		{
//			throw new SystemException(this.getClass().getName(), "readAll", new Object[]{
//				channel,
//				"Exception="+e.toString(),
//				"Error="+e.getMessage()
//			});
//		}
	}
	
	public Set<MobileObject> readAll(String channel)
	{	
		try
		{
			Set<MobileObject> objects = new HashSet<MobileObject>();
			Context context = Registry.getActiveInstance().getContext();
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return objects;
			}
			
			//read all the rows
			Set<Record> all = Database.getInstance(context).
			selectAll(channel);
			
			if(all != null)
			{
				objects = this.parse(all);
			}
			
			return objects;
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "readAll", new Object[]{
				channel,
				"Exception="+e.toString(),
				"Error="+e.getMessage()
			});
		}
	}
	
	public MobileObject read(String channel, String recordId)
	{
		try
		{			
			Context context = Registry.getActiveInstance().getContext();
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return null;
			}
			
			Record mobileObject = Database.getInstance(context).select(channel, recordId);
			if(mobileObject == null)
			{
				return null;
			}
		
			return new MobileObject(mobileObject);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "read", new Object[]{
				channel, recordId,
				"Exception="+e.toString(),
				"Error="+e.getMessage()
			});
		}
	}
	
	public String create(MobileObject mobileObject)
	{
		try
		{
			String newId = null;
			
			//Not needed right now
			//Map the objectId locally to the device
			/*Invocation invocation = new Invocation(mobileObject.getStorageId());
			invocation.setValue("storageId", mobileObject.getStorageId());
			invocation.setValue("recordId", mobileObject.getRecordId());
			InvocationResponse response = Bus.getInstance().invokeService(invocation);			
			if(response != null)
			{
				String mappedRecordId = response.getValue(InvocationResponse.returnValue);
				if(mappedRecordId != null && mappedRecordId.trim().length()>0)
				{
					mobileObject.setRecordId(mappedRecordId);
				}
			}*/					
			Context context = Registry.getActiveInstance().getContext();
			String channel = mobileObject.getStorageId();
			this.checkStorage(context,channel);
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 */
			for (IMobileObjectDatabaseListener listener : listeners) {
				listener.beforeMobileObjectCreated(mobileObject);
			}
			
			
			Record insert = mobileObject.getRecord();
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Contorno para problema de valores nulos não serem adicionados no banco, o que acaba afetando as queries.
			 */
			OpenMobsterBugUtils.getInstance().setNullValues(channel, mobileObject, insert);
			
			
			newId = Database.getInstance(context).insert(channel, insert);
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 */
			for (IMobileObjectDatabaseListener listener : listeners) {
				listener.onMobileObjectCreated(mobileObject, newId);
			}
			
			return newId;
		}
		catch(Exception be)
		{			
			throw new SystemException(this.getClass().getName(), "create", new Object[]
   			{
				"storageId="+mobileObject.getStorageId(),
				"recordId="+mobileObject.getRecordId(),
   				"error="+be.getMessage()
   			}
   			);
		}		
	}
		
	public void update(MobileObject mobileObject)
	{
		try
		{
			String channel = mobileObject.getStorageId();
			String recordId = mobileObject.getRecordId();
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context,channel);	
			if(!this.isChannelBooted(channel))
			{
				return;
			}
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 */
			for (IMobileObjectDatabaseListener listener : listeners) {
				listener.beforeMobileObjectUpdated(mobileObject);
			}
			
			
			Record recordToBeUpdated = mobileObject.getRecord();
			String dirtyStatus = recordToBeUpdated.getDirtyStatus();
			if(dirtyStatus == null || dirtyStatus.trim().length() == 0)
			{
				Record currentRecord = database.select(channel, recordId);
				recordToBeUpdated.setDirtyStatus(currentRecord.getDirtyStatus());
			}
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Contorno para problema de valores nulos não serem adicionados no banco, o que acaba afetando as queries.
			 */
			OpenMobsterBugUtils.getInstance().setNullValues(channel, mobileObject, recordToBeUpdated);
			
			
			database.update(channel, recordToBeUpdated);
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 */
			for (IMobileObjectDatabaseListener listener : listeners) {
				listener.onMobileObjectUpdated(mobileObject);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "update", new Object[]
   			{
				"storageId="+mobileObject.getStorageId(),
				"recordId="+mobileObject.getRecordId(),
   				"error="+e.getMessage()
   			}
   			);
		}		
	}
	
	public void delete(MobileObject mobileObject)
	{	
		try
		{
			String channel = mobileObject.getStorageId();
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return;
			}
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 */
			for (IMobileObjectDatabaseListener listener : listeners) {
				listener.beforeMobileObjectDeleted(mobileObject);
			}
			
			
			String recordId = mobileObject.getRecordId();
			Record recordToBeDeleted = Database.getInstance(context).select(channel, recordId);				
			database.delete(channel, recordToBeDeleted);
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 */
			for (IMobileObjectDatabaseListener listener : listeners) {
				listener.onMobileObjectDeleted(mobileObject);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "delete", new Object[]
 			{
				"storageId="+mobileObject.getStorageId(),
				"recordId="+mobileObject.getRecordId(),
				"error="+e.getMessage()
 			}
 			);
		}		
	}
	
	public void deleteAll(String channel)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return;
			}
			
			database.deleteAll(channel);
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 */
			for (IMobileObjectDatabaseListener listener : listeners) {
				listener.onAllMobileObjectsDeleted(channel);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "deleteAll", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}		
	}
	
	public void bootup(String channel)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			this.checkStorage(context, channel);
			
			database.deleteAll(channel);
			database.dropTable(channel);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "deleteAll", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}		
	}
	//---Query Integration------------------------------------------------------------------------------------------------------------------------------------------
	public Set<MobileObject> query(String channel, GenericAttributeManager queryAttributes)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return new HashSet<MobileObject>();
			}
			
			if(AppSystemConfig.getInstance().isEncryptionActivated())
			{
				return this.queryEncryptedMode(channel, queryAttributes);
			}
			
			Set<MobileObject> result = new HashSet<MobileObject>();
			
			int logicLink = LogicChain.AND; //assumed by default
			if(queryAttributes.getAttribute("logicLink")!=null)
			{
				logicLink = ((Integer)queryAttributes.getAttribute("logicLink")).intValue();
			}
			
			List<LogicExpression> expressions = (List<LogicExpression>)queryAttributes.
			getAttribute("expressions");
			if(expressions != null && !expressions.isEmpty())
			{
				LogicChain chain = null;
				switch(logicLink)
				{
					case LogicChain.AND:
						chain = LogicChain.createANDChain();					
					break;
					
					case LogicChain.OR:
						chain = LogicChain.createORChain();
					break;
					
					default:
					break;
				}
				if(chain != null)
				{
					int expressionCounter = 0;
					for(LogicExpression courExpr:expressions)
					{
						chain.add(courExpr);
						
						//get beans that match this expression
						Set<MobileObject> matchedBeans = this.logicExpressionBeans(channel,courExpr,expressionCounter++);
						if(matchedBeans != null)
						{
							result.addAll(matchedBeans);
						}
					}
					
					Query query = Query.createInstance(chain);
					result = query.executeQuery(result);
				}
			}
			
			return result;
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "query", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}	
	}
	
	private Set<MobileObject> logicExpressionBeans(String channel,LogicExpression expression,int expressionCounter)
	{
		try
		{
			Set<MobileObject> objects = new HashSet<MobileObject>();
			Context context = Registry.getActiveInstance().getContext();
			String value = expression.getRhs();
			Set<Record> records = null;
			Database database = Database.getInstance(context);
			
			switch(expression.getOp())
			{
				case LogicExpression.OP_EQUALS:
					records = database.selectByValue(channel, value);
				break;
				
				case LogicExpression.OP_NOT_EQUALS:
					if(expressionCounter == 0)
					{
						records = database.selectByNotEquals(channel, value);
					}
				break;
				
				case LogicExpression.OP_CONTAINS:
					records = database.selectByContains(channel, value);
				break;
				
				default:
					records = database.selectByValue(channel, value);
				break;
			}
			
			objects = this.parse(records);
			
			return objects;
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "logicExpressionBeans", new Object[]{
				channel,
				"Exception="+e.toString(),
				"Error="+e.getMessage()
			});
		}
	}
	
	private Set<MobileObject> queryEncryptedMode(String channel, GenericAttributeManager queryAttributes)
	{
		Set<MobileObject> result = this.readAll(channel);
		
		int logicLink = LogicChain.AND; //assumed by default
		if(queryAttributes.getAttribute("logicLink")!=null)
		{
			logicLink = ((Integer)queryAttributes.getAttribute("logicLink")).intValue();
		}
		
		List<LogicExpression> expressions = (List<LogicExpression>)queryAttributes.
		getAttribute("expressions");
		if(expressions != null && !expressions.isEmpty())
		{
			LogicChain chain = null;
			switch(logicLink)
			{
				case LogicChain.AND:
					chain = LogicChain.createANDChain();					
				break;
				
				case LogicChain.OR:
					chain = LogicChain.createORChain();
				break;
				
				default:
				break;
			}
			if(chain != null)
			{
				for(LogicExpression courExpr:expressions)
				{
					chain.add(courExpr);
				}
				
				Query query = Query.createInstance(chain);
				result = query.executeQuery(result);
			}
		}
		
		return result;
	}
	
	public Cursor readProxyCursor(String channel)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return null;
			}
			
			return database.readProxyCursor(channel);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "readProxyCursor", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}	
	}
	
	public Cursor readByNameValuePair(String channel,String name,String value)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return null;
			}
			
			return database.readByNameValuePair(channel,name,value);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "readByNameValuePair", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}	
	}
	
	public Cursor readByName(String channel,String name)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return null;
			}
			
			return database.readByName(channel,name);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "readByName", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}	
	}
	
	public Cursor readByName(String channel,String name,boolean sortAscending)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return null;
			}
			
			return database.readByName(channel,name,sortAscending);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "readByName/sort", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}	
	}
	
	public Cursor searchExactMatchAND(String channel, GenericAttributeManager criteria)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return null;
			}
			
			return database.searchExactMatchAND(channel,criteria);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "searchExactMatchAND", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}	
	}
	
	public Cursor searchExactMatchOR(String channel, GenericAttributeManager criteria)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();				
			Database database = Database.getInstance(context);
			//this.checkStorage(context, channel);
			if(!this.isChannelBooted(channel))
			{
				return null;
			}
			
			return database.searchExactMatchOR(channel,criteria);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "searchExactMatchOR", new Object[]
   			{
				"storageId="+channel,
				"error="+e.getMessage()
   			}
   			);
		}	
	}
	//----------------------------------------------------------------------------------------------------------------------------------------------
	public void syncWithServer(String storageId)
	{
		
	}	
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	private Set<MobileObject> parse(Set<Record> records)
	{
		Set<MobileObject> mobileObjects = new HashSet<MobileObject>();
		
		if(records != null && !records.isEmpty())
		{
			for(Record record:records)
			{
				mobileObjects.add(new MobileObject(record));
			}
		}
		
		return mobileObjects;
	}
	
	//Alterado para público na versão 2.4-M3.1.
	public void checkStorage(Context context,String storageId) throws DBException
	{
		if(!Database.getInstance(context).doesTableExist(storageId))
		{
			Database.getInstance(context).createTable(storageId);
		}
	}
	
	
	/*
	 * Estruturas adicionada na versão 2.4-M3.1.
	 */
	
	/**
	 * Verifica se um canal está vazio.
	 * 
	 * @param channel canal que será verificado.
	 * @return <code>true</code> se o canal estiver vazio e <code>false</code> caso contrário.
	 */
	public boolean isChannelEmpty(String channel) {
		try {
			Context context = Registry.getActiveInstance().getContext();
			return Database.getInstance(context).isTableEmpty(channel);
		} catch (DBException e) {
			throw new SystemException(this.getClass().getName(), "isEmpty", new Object[]{
				channel,
				"Exception="+e.toString(),
				"Error="+e.getMessage()
			});
		}
	}
	
	
	//Estrutura de listeners das ações executadas no MobileObjectDatabase
	private List<IMobileObjectDatabaseListener> listeners = new ArrayList<IMobileObjectDatabaseListener>();
	
	/**
	 * Adiciona um listener para as operações do {@link MobileObjectDatabase}.
	 * 
	 * @param listener o listener que será adicionado.
	 */
	public void addListener(IMobileObjectDatabaseListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener == null");
		}
		listeners.add(listener);
	}
	
	/**
	 * Remove um listener para as operações do {@link MobileObjectDatabase} adicionado previamente.
	 * 
	 * @param listener o listener que será removido.
	 */
	public void removeListener(IMobileObjectDatabaseListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Interface adicionada na versão 2.4-M3.1 do OpenMobster.<br>
	 * <br>
	 * Listener das operações do {@link MobileObjectDatabase}. As operações podem ser provenientes de ações da
	 * aplicação através do {@link org.openmobster.cloud.api.sync.MobileBean} ou de sincronizações de dados feitas OpenMobster. 
	 * 
	 * @author Thiago Gesser
	 */
	public interface IMobileObjectDatabaseListener {
		
		/**
		 * Chamado antes de um {@link MobileObject} ser inserido no banco.
		 * 
		 * @param mo o MobileObject que será inserido.
		 */
		void beforeMobileObjectCreated(MobileObject mo);

		/**
		 * Chamado quando um {@link MobileObject} é inserido no banco.
		 * 
		 * @param mo o MobileObject criado.
		 * @param id o identificador do registro inserido no banco.
		 */
		void onMobileObjectCreated(MobileObject mo, String id);
		
		
		/**
		 * Chamado antes de um {@link MobileObject} ser atualizado no banco.
		 * 
		 * @param mo o MobileObject que será atualizado.
		 */
		void beforeMobileObjectUpdated(MobileObject mo);
		
		/**
		 * Chamado quando um {@link MobileObject} é atualizado no banco.
		 * 
		 * @param mo o MobileObject atualizado.
		 */
		void onMobileObjectUpdated(MobileObject mo);
		
		
		
		/**
		 * Chamado antes de um {@link MobileObject} ser excluído do banco.
		 * 
		 * @param mo o MobileObject que será excluído.
		 */
		void beforeMobileObjectDeleted(MobileObject mo);
		
		/**
		 * Chamado quando um {@link MobileObject} é excluído do banco.
		 * 
		 * @param mo o MobileObject excluído.
		 */
		void onMobileObjectDeleted(MobileObject mo);
		
		
		/**
		 * Chamado quando todos os objetos de um canal são excluídos..
		 * 
		 * @param channel canal cujo todos os objetos foram excluídos.
		 */
		void onAllMobileObjectsDeleted(String channel);
	}
	
	/**
	 * Classe adicionada na versão 2.4-M3.1 do OpenMobster.<br>
	 * <br>
	 * Adapter para o {@link IMobileObjectDatabaseListener} que permite a implementação dos métodos desejados apenas.
	 *
	 * @author Thiago Gesser
	 */
	public static abstract class MobileObjectDatabaseListenerAdapter implements IMobileObjectDatabaseListener {
		
		@Override
		public void beforeMobileObjectCreated(MobileObject mo) {
		}
		
		@Override
		public void onMobileObjectCreated(MobileObject mo, String id) {
		}
		
		@Override
		public void beforeMobileObjectUpdated(MobileObject mo) {
		}

		@Override
		public void onMobileObjectUpdated(MobileObject mo) {
		}
		
		@Override
		public void beforeMobileObjectDeleted(MobileObject mo) {
		}

		@Override
		public void onMobileObjectDeleted(MobileObject mo) {
		}
		
		@Override
		public void onAllMobileObjectsDeleted(String channel) {
		}
	}
	
	
	//Estrutura necessária para fazer a verificação de bootup funcionar a partir da configuração.
	private Configuration configuration;
	
	@Override
	public void start() {
		//Armazena o Configuration para evitar de carregá-lo toda vez no getInstance().
		Context context = Registry.getActiveInstance().getContext();
		configuration = Configuration.getInstance(context);
	}
	
	@Override
	public void stop() {
		configuration = null;
	}
}
