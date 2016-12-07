/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.sync;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.DBException;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.util.Base64;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.module.mobileObject.LogicChain;
import org.openmobster.core.mobileCloud.android.module.mobileObject.LogicExpression;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * MobileBean is a managed Mobile Component which is an extension of its corresponding Mobile Component in the Cloud.
 * 
 * MobileBean provides seamless access to the synchronized data. This data is used in a  Mobile application in various contexts like showing reports, GUI for the 
 * service etc
 * 
 * It is designed to shield the Mobile Developer from low-level services like Offline Access, Receiving Notifications related to data changes on the server,
 * synchronizing modified beans back with the server, etc. This helps the developer to focus on the business logic for their applications without having to
 * worry about low level synchronization details
 * 
 * @author openmobster@gmail.com
 */
public final class MobileBean 
{
	private MobileObject data;
	
	private boolean isDirty = false;
	private boolean isNew = false;
		
	private MobileBean(MobileObject data)
	{
		this.data = data;
	}	
	
	private MobileBean()
	{
		
	}
	//-------------Data operations-----------------------------------------------------------------------------------------------------------------
	/**
	 * Gets the name of the mobile channel associated with this bean
	 * 
	 * @return the Mobile channel related to this bean
	 */
	public String getService()
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		return this.data.getStorageId().trim();
	}
	
	/**
	 * Gets the unique identifier of the bean
	 * 
	 * @return the unique identifier of this Mobile Bean
	 */
	public String getId()
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		
		String recordId = this.data.getRecordId();
		if(recordId != null)
		{
			recordId = recordId.trim();
		}		
		return recordId;
	}
	
	/**
	 * Gets the unique identifier of this bean on the Cloud Side. 99% of the times this is same as the one on the Device Side.
	 * 
	 * @return the unique identifier for this bean on the cloud side
	 */
	public String getServerId()
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		
		String recordId = this.data.getServerRecordId();
		if(recordId != null)
		{
			recordId = recordId.trim();
		}		
		return recordId;
	}
	
	/**
	 * Checks if the bean instance is properly initialized
	 * 
	 * @return true if it is initialized
	 */
	public boolean isInitialized()
	{
		return (this.data != null);
	}
	
	/**
	 * Checks if this bean was originally created on the device
	 * 
	 * @return true if the bean is originated on the device
	 */
	public boolean isCreateOnDevice()
	{
		return this.data.isCreatedOnDevice();
	}
	
	/**
	 * Checks if the particular instance is in proxy state or its state is fully downloaded from the server
	 * 
	 * @return true if it is not fully loaded from the Cloud
	 */
	public boolean isProxy()
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		return this.data.isProxy();
	}
	
	/**
	 * Gets the Value of a Field of the bean
	 * 
	 * @param fieldUri expression identifying the field on the bean
	 * @return the Value value of the field on the bean
	 */
	public String getValue(String fieldUri)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		String value = this.data.getValue(fieldUri);
		if(value != null)
		{
			return value.trim();
		}
		
		return null;
	}
	
	/**
	 * Sets the Value of a Field of the bean
	 * 
	 * @param fieldUri expression identifying the field on the bean
	 * @param value value to be set
	 */
	public void setValue(String fieldUri, String value)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		this.data.setValue(fieldUri, value);
		this.isDirty = true;
	}
	
	/**
	 * Gets the Binary Value of a Field of the bean
	 * 
	 * @param fieldUri expression identifying the field on the bean
	 * @return the Binary Value value of the field on the bean
	 */
	public byte[] getBinaryValue(String fieldUri) throws IOException
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		String encodedValue = this.getValue(fieldUri);
		if(encodedValue != null && encodedValue.trim().length()>0)
		{
			byte[] decodedValue = Base64.decode(encodedValue);
			return decodedValue;
		}
		return null;
	}
	
	/**
	 * Sets the BinaryValue of a Field of the bean
	 * 
	 * @param fieldUri expression identifying the field on the bean
	 * @param value value to be set
	 */
	public void setBinaryValue(String fieldUri, byte[] value)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		String encodedValue = Base64.encodeBytes(value);
		this.setValue(fieldUri, encodedValue);
	}
				
	/**
	 * Reads a List of Beans under the Mobile Bean "parent" Object
	 * 
	 * @param listProperty expression to specify the List
	 * @return the listProperty
	 */
	public BeanList readList(String listProperty)
	{		
		BeanList beanList = new BeanList(listProperty);
		
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
				
		int arrayLength = this.data.getArrayLength(listProperty);		
		for(int i=0; i<arrayLength; i++)
		{			
			Map<String,String> arrayElement = this.data.
			getArrayElement(listProperty, i);
									
			BeanListEntry entry = new BeanListEntry(i, 
			this.port(arrayElement));			
			beanList.addEntry(entry);
		}
		return beanList;		
	}
	
	/**
	 * Saves the List of Beans under the Mobile Bean "parent" Object
	 * If the list is null, a new list is created. If the list exists, then this list replaces the old list
	 * 
	 * @param list expression of the list property of the bean
	 */
	public void saveList(BeanList list)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		String listProperty = list.getListProperty();		
		this.data.clearArray(listProperty);
		
		int arrayLength = list.size();
		for(int i=0; i<arrayLength; i++)
		{
			BeanListEntry local = list.getEntryAt(i);
			this.data.addToArray(listProperty, local.getProperties());
		}
		
		this.isDirty = true;
	}
	
	/**
	 * Clears the List of Beans under the Mobile Bean "parent" Object
	 * 
	 * @param listProperty expression of the list property to be cleared
	 */
	public void clearList(String listProperty)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		this.data.clearArray(listProperty);
		
		this.isDirty = true;
	}
	
	/**
	 * Add a Bean to the List of Beans under the Mobile Bean "parent" Object
	 * 
	 * @param expression of the list property in question
	 * @param bean a BeanListEntry instance to be added to the list property
	 */
	public void addBean(String listProperty, BeanListEntry bean)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		this.data.addToArray(listProperty, bean.getProperties());
		
		this.isDirty = true;
	}
	
	/**
	 * Remove the Bean present at the specified index from the List of Beans under the Mobile Bean "parent" Object
	 * 
	 * @param listProperty expression representing the list property
	 * @param elementAt index of the element to be removed from the list property
	 */
	public void removeBean(String listProperty, int elementAt)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		this.data.removeArrayElement(listProperty, elementAt);
		
		this.isDirty = true;
	}
	//----------Persistence operations-----------------------------------------------------------------------------------------------------------
	/**
	 * Persists the state of the Mobile Bean. This also makes sure the consistent bean state is reflected on the Cloud Side as well
	 * 
	 */
	public synchronized void save() throws CommitException
	{
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Não permite salvar registros excluídos.
		 */
		checkNotDeleted();
		
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		//If Bean Created on Device
		if(this.isNew)
		{
			String newId = deviceDB.create(this.data);			
			this.data = deviceDB.read(this.data.getStorageId(), newId);
			
			this.isNew = false;
			this.refresh();
			
			//Integration with the SyncService
			try
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.updateChangeLog, this.getService(), this.getId(), SyncInvocation.OPERATION_ADD);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException sys = new SystemException(this.getClass().getName(), "save://Create", new Object[]{
					"Exception="+e.toString(),
					"Message="+e.getMessage()
				});
				ErrorHandler.getInstance().handle(sys);
				throw new CommitException(sys);
			}			
			
			return;
		}
		
		//If Bean Updated on Device
		if(this.isDirty)
		{
			try
			{
				deviceDB.update(this.data);
				this.clearMetaData();
			
				//Integration with the SyncService
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.updateChangeLog, this.getService(), this.getId(), SyncInvocation.OPERATION_UPDATE);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException sys = new SystemException(this.getClass().getName(), "save://Update", new Object[]{
					"Exception="+e.toString(),
					"Message="+e.getMessage()
				});
				ErrorHandler.getInstance().handle(sys);
				throw new CommitException(sys);
			}
		}
	}
	
	public synchronized void saveWithoutSync() throws CommitException
	{
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Não permite salvar registros excluídos.
		 */
		checkNotDeleted();
		
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		//If Bean Created on Device
		if(this.isNew)
		{
			String newId = deviceDB.create(this.data);			
			this.data = deviceDB.read(this.data.getStorageId(), newId);
			
			this.isNew = false;
			this.refresh();
			
			//Integration with the SyncService
			try
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.changelogOnly, this.getService(), this.getId(), SyncInvocation.OPERATION_ADD);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException sys = new SystemException(this.getClass().getName(), "save://Create", new Object[]{
					"Exception="+e.toString(),
					"Message="+e.getMessage()
				});
				ErrorHandler.getInstance().handle(sys);
				throw new CommitException(sys);
			}			
			
			return;
		}
		
		//If Bean Updated on Device
		if(this.isDirty)
		{
			try
			{
				deviceDB.update(this.data);
				this.clearMetaData();
			
				//Integration with the SyncService
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.changelogOnly, this.getService(), this.getId(), SyncInvocation.OPERATION_UPDATE);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException sys = new SystemException(this.getClass().getName(), "save://Update", new Object[]{
					"Exception="+e.toString(),
					"Message="+e.getMessage()
				});
				ErrorHandler.getInstance().handle(sys);
				throw new CommitException(sys);
			}
		}
	}
	
	public synchronized void synchronousSave() throws CommitException
	{
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Não permite salvar registros excluídos.
		 */
		checkNotDeleted();
		
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		//If Bean Created on Device
		if(this.isNew)
		{
			String newId = deviceDB.create(this.data);			
			this.data = deviceDB.read(this.data.getStorageId(), newId);
			
			this.isNew = false;
			this.refresh();
			
			//Integration with the SyncService
			try
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.synchronousSave, this.getService(), this.getId(), SyncInvocation.OPERATION_ADD);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException sys = new SystemException(this.getClass().getName(), "save://Create", new Object[]{
					"Exception="+e.toString(),
					"Message="+e.getMessage()
				});
				ErrorHandler.getInstance().handle(sys);
				throw new CommitException(sys);
			}			
			
			return;
		}
		
		//If Bean Updated on Device
		if(this.isDirty)
		{
			try
			{
				deviceDB.update(this.data);
				this.clearMetaData();
			
				//Integration with the SyncService
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.synchronousSave, this.getService(), this.getId(), SyncInvocation.OPERATION_UPDATE);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException sys = new SystemException(this.getClass().getName(), "save://Update", new Object[]{
					"Exception="+e.toString(),
					"Message="+e.getMessage()
				});
				ErrorHandler.getInstance().handle(sys);
				throw new CommitException(sys);
			}
		}
	}
	
	/**
	 * Deletes the bean from the channel. This also makes sure this action is reflected on the Cloud Side as well
	 */
	public synchronized void delete() throws CommitException
	{
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Não permite excluir registros já excluídos.
		 */
		checkNotDeleted();
		

		if(this.isNew)
		{
			throw new IllegalStateException("Instance is created on the device and not saved. Hence it cannot be deleted");
		}
		
		try
		{
			MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
			String service = this.getService();
			String id = this.getId();
			
			deviceDB.delete(this.data);

			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Não remove os dados do registro excluído, apenas marca-o como tal.
			 */
//			this.clearAll();
			
			
			//Integration with the SyncService
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
			SyncInvocation.updateChangeLog, service, id, SyncInvocation.OPERATION_DELETE);		
			Bus.getInstance().invokeService(syncInvocation);
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Não remove os dados do registro excluído, apenas marca-o como tal.
			 */
			deleted = true;
		}
		catch(Exception e)
		{
			SystemException sys = new SystemException(this.getClass().getName(), "delete", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(sys);
			throw new CommitException(sys);
		}
	}
	
	public synchronized void deleteWithoutSync() throws CommitException
	{
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Não permite excluir registros já excluídos.
		 */
		checkNotDeleted();
		

		if(this.isNew)
		{
			throw new IllegalStateException("Instance is created on the device and not saved. Hence it cannot be deleted");
		}
		
		try
		{
			MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
			String service = this.getService();
			String id = this.getId();
			
			deviceDB.delete(this.data);
			
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Não remove os dados do registro excluído, apenas marca-o como tal.
			 */
//			this.clearAll();
			
			
			//Integration with the SyncService
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
			SyncInvocation.changelogOnly, service, id, SyncInvocation.OPERATION_DELETE);		
			Bus.getInstance().invokeService(syncInvocation);

			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Não remove os dados do registro excluído, apenas marca-o como tal.
			 */
			deleted = true;
		}
		catch(Exception e)
		{
			SystemException sys = new SystemException(this.getClass().getName(), "delete", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(sys);
			throw new CommitException(sys);
		}
	}
	
	/**
	 * Re-Read the state of the MobileBean from the database
	 *
	 */
	public synchronized void refresh()
	{
		if(this.isNew)
		{
			throw new IllegalStateException("Instance is created on the device and not saved. Hence it cannot be refreshed");
		}
		
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Não permite excluir registros já excluídos.
		 */
		checkNotDeleted();
		
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Não remove os dados se o registro não existe mais na base, apenas marca-o como excluído.
		 */
//		this.data = deviceDB.read(this.getService(), this.getId());
		MobileObject refreshedData = deviceDB.read(this.getService(), this.getId());
		if (refreshedData == null) {
			deleted = true;
		} else {
			data = refreshedData;
			this.clearMetaData();
		}
		
	}
	//--------static operations-------------------------------------------------------------------------------------------------------------------
	/**
	 * Checks if the Channel has been booted up on the device with initial data or not
	 * 
	 * @return true if the channel has been booted up
	 */
	public static boolean isBooted(String channel)
	{
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		return deviceDB.isChannelBooted(channel);
	}
	/**
	 * Provides all the instances of Mobile Beans for the specified channel
	 * 
	 * @param the channel to read the beans from
	 * @return all the beans stored in the channel
	 */
	public static MobileBean[] readAll(String service)
	{
		MobileBean[] all = null;
		if(!isBooted(service))
		{
			return all;
		}
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		Set<MobileObject> allObjects = deviceDB.readAll(service);
		if(allObjects != null && !allObjects.isEmpty())
		{
			//filter out the proxies
			allObjects = MobileBean.filterProxies(allObjects);
			int size = allObjects.size();
			all = new MobileBean[size];
			int i=0;
			for(MobileObject curr:allObjects)
			{
				all[i] = new MobileBean(curr);
				i++;
			}
		}
		
		return all;
	}
	
	/**
	 * Provides an instance of a Mobile Bean
	 * 
	 * @param channel channel of the bean
	 * @param id id of the bean
	 * @return an instance of the MobileBean from the channel
	 */
	public static MobileBean readById(String service, String id)
	{
		MobileBean bean = null;
		if(!isBooted(service))
		{
			return bean;
		}
		
		MobileObject data = MobileObjectDatabase.getInstance().read(service, id);
		if(data != null && !data.isProxy())
		{
			bean = new MobileBean(data);
		}
		
		return bean;
	}
	
	/**
	 * Create a new transient instance of a Mobile Bean. The Mobile Bean has to be explicitly saved in order
	 * to persist it on the device and have it reflect on the server. In this case, when the bean is persisted
	 * the Id will be generated by the device since its not explicitly specified
	 * 
	 * @param channel channel of the bean
	 * @return an instance of the MobileBean originated from the device (does not have a Cloud counterpart yet, until synchronized)
	 */
	public static MobileBean newInstance(String service)
	{
		MobileBean newInstance = null;
		
		MobileObject data = new MobileObject();
		data.setCreatedOnDevice(true);
		data.setStorageId(service);
		
		newInstance = new MobileBean(data);
		newInstance.isNew = true;
		
		return newInstance;
	}
	
	public static void bulkSave(Set<MobileBean> beans) throws CommitException
	{
		if(beans == null)
		{
			return;
		}
		
		for(MobileBean bean:beans)
		{
			bean.saveWithoutSync();
		}
		
		//sync now
		//Integration with the SyncService
		try
		{
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
					SyncInvocation.scheduleSync);		
			Bus.getInstance().invokeService(syncInvocation);
		}
		catch(Exception e)
		{
			SystemException sys = new SystemException(MobileBean.class.getName(), "bulkSave", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(sys);
			throw new CommitException(sys);
		}
	}
	//---Query functionality--------------------------------------------------------------------------------------------------------------
	/**
	 * Query the Channel such that the criteria provided is separated by AND in the WHERE clause.
	 * The criteria consists of name/value pairs of the data to be matched
	 * 
	 * @param channel Channel being queried
	 * @param criteria name/value pairs of the data to be matched
	 * @return an array of beans that match the specified query
	 */
	@Deprecated
	public static MobileBean[] queryByEqualsAll(String service,GenericAttributeManager criteria)
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		if(!isBooted(service))
		{
			return beans;
		}
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.add(LogicExpression.
			createInstance(names[i], rhs, LogicExpression.OP_EQUALS));
		}
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
		query(service, input);		
		if(result != null && !result.isEmpty())
		{
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			int i=0;
			for(MobileObject cour:result)
			{
				beans[i] = new MobileBean(cour);
				i++;
			}
		}
		
		return beans;
	}
	
	/**
	 * Query the Channel such that the criteria provided is separated by an OR in the WHERE clause.
	 * The criteria consists of name/value pairs of the data to be matched
	 * 
	 * @param channel Channel being queried
	 * @param criteria name/value pairs of the data to be matched
	 * @return an array of beans that match the specified query
	 */
	@Deprecated
	public static MobileBean[] queryByEqualsAtleastOne(String service, 
	GenericAttributeManager criteria)	
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		if(!isBooted(service))
		{
			return beans;
		}
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.add(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_EQUALS));
		}
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
		query(service, input);		
		if(result != null && !result.isEmpty())
		{
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			int i=0;
			for(MobileObject cour:result)
			{
				beans[i] = new MobileBean(cour);
				i++;
			}
		}
		
		return beans;
	}
	
	/**
	 * Query the Channel such that the criteria provided is separated by a AND in the WHERE clause.
	 * The criteria consists of name/value pairs of the data to be matched. In this case, it returns rows that
	 * "do not" match the specified criteria
	 * 
	 * @param channel Channel being queried
	 * @param criteria name/value pairs of the data to be matched
	 * @return an array of beans that "do not" match the specified query
	 */
	@Deprecated
	public static MobileBean[] queryByNotEqualsAll(String service, 
	GenericAttributeManager criteria)
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		if(!isBooted(service))
		{
			return beans;
		}
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.add(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_NOT_EQUALS));
		}
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
		query(service, input);		
		if(result != null && !result.isEmpty())
		{
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			int i=0;
			for(MobileObject cour:result)
			{
				beans[i] = new MobileBean(cour);
				i++;
			}
		}
		
		return beans;
	}
	
	/**
	 * Query the Channel such that the criteria provided is separated by a OR in the WHERE clause.
	 * The criteria consists of name/value pairs of the data to be matched. In this case, it returns rows that
	 * "do not" match the specified criteria
	 * 
	 * @param channel Channel being queried
	 * @param criteria name/value pairs of the data to be matched
	 * @return an array of beans that "do not" match the specified query
	 */
	@Deprecated
	public static MobileBean[] queryByNotEqualsAtleastOne(String service, 
	GenericAttributeManager criteria)	
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		if(!isBooted(service))
		{
			return beans;
		}
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.add(LogicExpression.createInstance(names[i], rhs, 
			LogicExpression.OP_NOT_EQUALS));
		}
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
		query(service, input);		
		if(result != null && !result.isEmpty())
		{
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			int i=0;
			for(MobileObject cour:result)
			{
				beans[i] = new MobileBean(cour);
				i++;
			}
		}
		
		return beans;
	}
	
	/*public static MobileBean[] queryByLikeAll(String service,
	GenericAttributeManager criteria)
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.add(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_LIKE));
		}
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
		query(service, input);		
		if(result != null && !result.isEmpty())
		{	
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			int i=0;
			for(MobileObject cour:result)
			{
				beans[i] = new MobileBean(cour);
				i++;
			}
		}
		
		return beans;
	}
	
	public static MobileBean[] queryByLikeAtleastOne(String service, 
	GenericAttributeManager criteria)	
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.add(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_LIKE));
		}
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
		query(service, input);		
		if(result != null && !result.isEmpty())
		{	
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			int i=0;
			for(MobileObject cour:result)
			{
				beans[i] = new MobileBean(cour);
				i++;
			}
		}
		
		return beans;
	}*/
	
	/**
	 * Query the Channel such that the criteria provided is separated by an AND in the WHERE clause.
	 * The criteria consists of name/value pairs of the data to be matched. The matching is done using the
	 * LIKE clause to check if the specified criteria is contained within the data
	 * 
	 * @param channel Channel being queried
	 * @param criteria name/value pairs of the data to be matched
	 * @return an array of beans that match the specified query
	 */
	@Deprecated
	public static MobileBean[] queryByContainsAll(String service,
	GenericAttributeManager criteria)
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		if(!isBooted(service))
		{
			return beans;
		}
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.add(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_CONTAINS));
		}
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
		query(service, input);		
		if(result != null && !result.isEmpty())
		{	
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			int i=0;
			for(MobileObject cour:result)
			{
				beans[i] = new MobileBean(cour);
				i++;
			}
		}
		
		return beans;
	}
	
	/**
	 * Query the Channel such that the criteria provided is separated by an OR in the WHERE clause.
	 * The criteria consists of name/value pairs of the data to be matched. The matching is done using the
	 * LIKE clause to check if the specified criteria is contained within the data
	 * 
	 * @param channel Channel being queried
	 * @param criteria name/value pairs of the data to be matched
	 * @return an array of beans that match the specified query
	 */
	@Deprecated
	public static MobileBean[] queryByContainsAtleastOne(String service, 
	GenericAttributeManager criteria)	
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		if(!isBooted(service))
		{
			return beans;
		}
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.add(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_CONTAINS));
		}
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
		query(service, input);		
		if(result != null && !result.isEmpty())
		{		
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			int i=0;
			for(MobileObject cour:result)
			{
				beans[i] = new MobileBean(cour);
				i++;
			}
		}
		
		return beans;
	}
	
	/**
	 * Query the channel such the results are sorted by the value of the specified property of the bean
	 * 
	 * @param channel
	 * @param property
	 * @param ascending
	 * @return
	 */
	public static MobileBeanCursor sortByProperty(String channel, String property, boolean ascending)
	{
		if(!isBooted(channel))
		{
			return null;
		}
		
		MobileObjectDatabase db = MobileObjectDatabase.getInstance();
		
		Cursor cursor = db.readByName(channel, property, ascending);
		
		MobileBeanCursor mobileBeanCursor = new MobileBeanCursorImpl(channel,cursor);
		
		return mobileBeanCursor;
	}
	
	/**
	 * Query the channel by the value of the specified bean property
	 * 
	 * @param channel
	 * @param property
	 * @param value
	 * @return
	 */
	public static MobileBeanCursor queryByProperty(String channel, String property, String value)
	{
		if(!isBooted(channel))
		{
			return null;
		}
		
		MobileObjectDatabase db = MobileObjectDatabase.getInstance();
		
		Cursor cursor = db.readByNameValuePair(channel, property, value);
		
		MobileBeanCursor mobileBeanCursor = new MobileBeanCursorImpl(channel,cursor);
		
		return mobileBeanCursor;
	}
	
	/**
	 * Search beans by criteria made up of name/value pairs to be matched against
	 * The query uses the 'AND' expression between each name/value pair to make sure the criteria is fully matched
	 * 
	 * @param channel
	 * @param criteria
	 * @return
	 */
	public static MobileBeanCursor searchByMatchAll(String channel, GenericAttributeManager criteria)
	{
		if(!isBooted(channel))
		{
			return null;
		}
		
		MobileObjectDatabase db = MobileObjectDatabase.getInstance();
		
		Cursor cursor = db.searchExactMatchAND(channel, criteria);
		
		MobileBeanCursor mobileBeanCursor = new MobileBeanCursorImpl(channel,cursor);
		
		return mobileBeanCursor;
	}
	
	/**
	 * Search beans by criteria made up of name/value pairs to be matched against
	 * The query uses the 'OR' expression between each name/value pair to make sure atleast one name/value pair from the criteria is matched
	 * 
	 * @param channel
	 * @param criteria
	 * @return
	 */
	public static MobileBeanCursor searchByMatchAtleastOne(String channel, GenericAttributeManager criteria)
	{
		if(!isBooted(channel))
		{
			return null;
		}
		
		MobileObjectDatabase db = MobileObjectDatabase.getInstance();
		
		Cursor cursor = db.searchExactMatchOR(channel, criteria);
		
		MobileBeanCursor mobileBeanCursor = new MobileBeanCursorImpl(channel,cursor);
		
		return mobileBeanCursor;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	
	/*
	 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
	 * Não remove mais os dados de registros excluídos, então este método não é mais necessário.
	 */
//	private synchronized void clearAll()
//	{
//		this.data = null;
//		this.isDirty = false;
//		this.isNew = false;
//	}
	
	private synchronized void clearMetaData()
	{
		this.isDirty = false;
		this.isNew = false;
	}
		
	//Loading proxies on demand gives a bad app experience....proxies need to be loaded in the background
	//by the MobileCloud
	/*private synchronized void loadProxy()
	{
		String service = this.getService();
		String beanId = this.getId();
		try
		{			
			if(this.data.isProxy())
			{				
				//Integration with the SyncService				
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
				SyncInvocation.stream, service, beanId);		
				Bus.getInstance().invokeService(syncInvocation);
				
				
				//Refresh the bean state with the newly downloaded data
				this.refresh();
			}
		}
		catch(Exception e)
		{
			SystemException sys = new SystemException(this.getClass().getName(), "loadProxy", new Object[]{
				"Service="+service,
				"BeanId="+beanId,
				"SyncType=stream",					
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(sys);
			
			throw sys;
		}
	}*/	
	
	private static Set<MobileObject> filterProxies(Set<MobileObject> 
	mobileObjects)
	{
		Set<MobileObject> filtered = new HashSet<MobileObject>();
		if(mobileObjects != null)
		{
			for(MobileObject mo:mobileObjects)
			{				
				if(!mo.isProxy())
				{
					filtered.add(mo);
				}
			}
		}
		return filtered;
	}
	//-----------------------------------------------------------------------------
	//need to use a Hashtable for platform compatibility across 
	//Java based native apps
	//BlackBerry is still stuck in the year 1700 ;)
	private Hashtable<String,String> port(Map<String,String> original)
	{
		Hashtable<String,String> port = new Hashtable<String,String>();
		
		if(original != null)
		{
			Set<String> keys = original.keySet();
			for(String key: keys)
			{
				port.put(key, original.get(key));
			}
		}
		
		return port;
	}
	//---------------------------------Only For Performance Testing------------------------------------------------
	/**
	 * This method is needed during performance testing of this component
	 * It should not be used in your App development
	 * 
	 * @throws CommitException
	 */
	public synchronized void perfSave() throws CommitException
	{
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		deviceDB.create(this.data);
	}
	
	
	
	/*
	 * Estrutura adicionados na versão 2.4-M3.1
	 */
	
	private boolean deleted;
	
	/**
	 * Verifica se o bean foi excluído do banco de dados. Não é possível fazer operação com beans excluídos, apenas ler seus dados.
	 * 
	 * @return <code>true</code> se o bean foi excluído e <code>false</code> caso contrário. 
	 */
	public boolean isDeleted() {
		return deleted;
	}
	
	/**
	 * Verifica se o bean é novo e não está salvo no banco de dados.
	 * 
	 * @return <code>true</code> se o bean é novo e <code>false</code> caso contrário. 
	 */
	public boolean isNew() {
		return isNew;
	}
	
	private void checkNotDeleted() {
		if (deleted) {
			throw new IllegalStateException("MobileBean is deleted!");
		}
	}
	
	
	/**
	 * Salva o bean de forma local, sem atualizar o change log do serviço de sincronização e consequentemente sem 
	 * enviá-lo para o backend.
	 */
	public synchronized void saveLocal() {
		checkNotDeleted();
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		if(this.isNew) {
			String newId = deviceDB.create(this.data);			
			this.data = deviceDB.read(this.data.getStorageId(), newId);
			this.isNew = false;
			this.refresh();
			return;
		}
		
		if(this.isDirty) {
			deviceDB.update(this.data);
			this.clearMetaData();
			return;
		}
	}
	
	public synchronized void addToChangeLog() throws CommitException {
		try {
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
															   SyncInvocation.changelogOnly, this.getService(), this.getId(), SyncInvocation.OPERATION_ADD);
			syncInvocation.setValue("bulk", "true");
			Bus.getInstance().invokeService(syncInvocation);
		} catch(Exception e) {
			SystemException sys = new SystemException(this.getClass().getName(), "save://Create", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(sys);
			throw new CommitException(sys);
		}	
	}
	
	
	/**
	 * Cria um MobileBean através do método {@link #newInstance(String)} e define seu identificador.
	 * 
	 * @param channel canal do MobileBean que será criado.
	 * @param id identificador do MobileBean. Se for <code>null</code>, será gerado um automaticamente quando ele for salvo.
	 * @return o MobileBean criado.
	 */
	public static MobileBean newInstance(String channel, String id) {
		MobileBean mobileBean = newInstance(channel);
		mobileBean.data.setRecordId(id);
		return mobileBean;
	}
	
	/**
	 * Verifica se um canal está vazio.
	 * 
	 * @param channel canal que será verificado.
	 * @return <code>true</code> se o canal estiver vazio e <code>false</code> caso contrário.
	 */
	public static boolean isEmpty(String channel) {
		return MobileObjectDatabase.getInstance().isChannelEmpty(channel);
	}
	
	
	/**
	 * Verifica se o campo não possui valor designado.
	 * 
	 * @param fieldName o nome do campo que será verificado.
	 * @return <code>true</code> se o campo não possuir valor e <code>false</code> caso contrário.
	 */
	public boolean isNull(String fieldName) {
		if(!this.isInitialized()) {
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())	{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		return !data.hasFieldOrArray(fieldName);
	}
	
	/**
	 * Obtém um bean específico de uma lista a partir de seu índice.
	 * 
	 * @param listProperty nome da propriedade de lista.
	 * @param index índice do bean na lista.
	 * @return o bean obtido.
	 */
	public BeanListEntry readListEntry(String listProperty, int index) {		
		if(!this.isInitialized()) {
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())	{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
				
		Map<String,String> arrayElement = this.data.getArrayElement(listProperty, index);
		return new BeanListEntry(index, this.port(arrayElement));			
	}
	
	
	/**
	 * Roda uma query diretamente no banco de dados do OpenMobster. Esta abertura é necessária para a nova estrutura
	 * de consultas dos dados.
	 * 
	 * @param query query que será executada.
	 * @param args argumentos da query. Opcional.
	 * @return o cursor resultante da execução.
	 */
	public static Cursor rawQuery(String query, String... args) {
		try	{
			Context context = Registry.getActiveInstance().getContext();
			return Database.getInstance(context).rawQuery(query, args);
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Inicia uma transação no modo EXCLUSIVE no banco de dados do OpenMobster.
	 */
	public static void beginTransaction() {
		try	{
			Context context = Registry.getActiveInstance().getContext();
			Database.getInstance(context).beginTransaction();
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Marca a transação atual do banco de dados do OpenMobster como bem sucedida. Isto fará com que a transação seja
	 * commitada quando o {@link #endTransaction()} for chamado.
	 */
	public static void setTransactionSuccessful() {
		try	{
			Context context = Registry.getActiveInstance().getContext();
			Database.getInstance(context).setTransactionSuccessful();
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Finaliza a transação atual do banco de dados do OpenMobster. Se ela foi marcada como bem sucedida (através do método
	 * {@link #setTransactionSuccessful()}) commita as alterações. Caso contrário, faz o rollback.
	 */
	public static void endTransaction() {
		try	{
			Context context = Registry.getActiveInstance().getContext();
			Database.getInstance(context).endTransaction();
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
}
