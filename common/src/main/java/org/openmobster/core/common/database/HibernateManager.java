/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.database;

import org.w3c.dom.Document;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.collection.PersistentCollection;

import org.apache.log4j.Logger;

/**
 * 
 * @author openmobster@gmail.com
 */
public class HibernateManager
{
	private static Logger log = Logger.getLogger(HibernateManager.class);
	
	private String config = null;
		
	private SessionFactory sessionFactory = null;
	
	
	public HibernateManager()
	{		
	}
		
	public String getConfig()
	{
		return this.config;
	}
		
	public void setConfig(String config)
	{
		this.config = config;
	}
	
	//service lifecycle------------------------------------------------------------------------------------------	
	public void create()
	{
		try
		{
			if(this.config != null && this.config.trim().length() > 0)
			{
				//Load using the specified configuration location
				Configuration configuration = new Configuration();
				configuration.configure(this.config);
				this.sessionFactory = configuration.buildSessionFactory();
			}
			else
			{
				//Load using the default location
				this.sessionFactory = new Configuration().configure().buildSessionFactory();
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
		
	public void start()
	{	
	}
		
	public void stop()
	{
	}
	
	public void destroy()
	{
		if(this.sessionFactory != null)
		{
			this.sessionFactory.close();
		}
		
		this.config = null;
		this.sessionFactory = null;
	}
	//-------------------------------------------------------------------------------------------------------------
	/**
	 * return the Session Factory instance managed by this HibernateManager instance
	 */
	public SessionFactory getSessionFactory()
	{
		return this.sessionFactory;
	}
	
	public void makePOJO(Object object)
	{
		try
		{
			EntityCleaner.cleanObject(object, null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void startSessionFactory(Document doc)
	{
		//Load using the specified configuration location
		Configuration configuration = new Configuration();
		configuration.configure(doc);
		this.sessionFactory = configuration.buildSessionFactory();
	}
	//-------------------------------------------------------------------------------------------------------------
	private static class EntityCleaner
	{		
		private static Logger log = Logger.getLogger(EntityCleaner.class);

		/**
		* This function would take as a parameter any kind of object and recursively
		* access all of its member and clean it from any uninitialized variables, or replace with a pojo equivalent
		* if a hibernate specific object is being used
		* 
		*
		* @param listObj
		* @throws ClassNotFoundException
		* @throws IllegalAccessException
		* @throws IllegalArgumentException
		* @throws InvocationTargetException
		* @throws InstantiationException
		*/
		private static void cleanObject(Object listObj, HashSet visitedBeansSet) throws 
		IllegalArgumentException, IllegalAccessException,
		ClassNotFoundException, InstantiationException, InvocationTargetException
		{
			if(visitedBeansSet == null)
			{
				visitedBeansSet = new HashSet();
			}
			if(listObj == null)
			{
				return;
			}
	
			// to handle the case of abnormal return consisting of array Object
			// case if hybrid bean
			if(listObj instanceof Object[])
			{
				Object[] objArray = (Object[]) listObj;
				for(int z = 0; z < objArray.length; z++)
				{
					cleanObject(objArray[z], visitedBeansSet);
				}
			}
			else
			{
				Iterator itOn = null;
		
				if(listObj instanceof List)
				{
					itOn = ((List) listObj).iterator();
				}
				else if(listObj instanceof Set)
				{
					itOn = ((Set) listObj).iterator();
				}
				else if(listObj instanceof Map)
				{
					itOn = ((Map) listObj).values().iterator();
				}
		
				
				if(itOn != null)
				{
					while(itOn.hasNext())
					{
						cleanObject(itOn.next(), visitedBeansSet);
					}
				}
				else
				{
					if(!visitedBeansSet.contains(listObj))
					{
						visitedBeansSet.add(listObj);
						processBean(listObj, visitedBeansSet);
					}
				}
			}
		}

		/**
		* Remove/Replace the hibernate proxies from the given object
		*
		* @param objBean
		* @throws Exception
		* @throws IllegalAccessException
		* @throws ClassNotFoundException
		* @throws IllegalArgumentException
		* @throws InvocationTargetException
		* @throws InstantiationException
		*/
		private static void processBean(Object objBean, HashSet visitedBeans) 
		throws IllegalAccessException, IllegalArgumentException,
		ClassNotFoundException, InstantiationException, InvocationTargetException
		{
			Class tmpClass = objBean.getClass();
			Field[] classFields = null;
			while(tmpClass != null && tmpClass != Object.class)
			{
				classFields = tmpClass.getDeclaredFields();
				cleanFields(objBean, classFields, visitedBeans);
				tmpClass = tmpClass.getSuperclass();
			}
		}

		private static void cleanFields(Object objBean, Field[] classFields, HashSet visitedBeans) 
		throws ClassNotFoundException,
		IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException
		{
			boolean accessModifierFlag = false;
			for(int z = 0; z < classFields.length; z++)
			{
				Field field = classFields[z];
				accessModifierFlag = false;
				
				//Make this field accessible for cleanup
				if(!field.isAccessible())
				{
					field.setAccessible(true);
					accessModifierFlag = true;
				}
		
				Object fieldValue = field.get(objBean);
		
				if(fieldValue instanceof HibernateProxy)
				{
					LazyInitializer lazyInitializer = ((HibernateProxy) fieldValue).getHibernateLazyInitializer();
					if(!lazyInitializer.isUninitialized())
					{
						Object pojo = lazyInitializer.getImplementation();
						field.set(objBean, pojo);
						cleanObject(pojo, visitedBeans);
					}
					else
					{
						field.set(objBean, null);
					}
				}
				else if(fieldValue instanceof PersistentCollection)
				{
					PersistentCollection persistentCollection = (PersistentCollection)fieldValue;
					
					if(persistentCollection.wasInitialized())
					{
						//TODO: Make this more generic
						Collection pojoCollection = new ArrayList();
						pojoCollection.addAll((Collection)fieldValue);						
						field.set(objBean, pojoCollection);
						cleanObject(pojoCollection, visitedBeans);
					}
					else
					{
						field.set(objBean, null);
					}
				}				
				
				//Reset this field's access modifier
				if(accessModifierFlag)
				{
					field.setAccessible(false);
				}
			}				
		}
	}
}
