/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.common.Utilities;

/**
 * @author openmobster@gmail.com
 */
public class MobileObjectDatabase 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(MobileObjectDatabase.class);
	
	/**
	 * 
	 */
	private HibernateManager hibernateManager = null;
	private StorageMonitor storageMonitor = null;
	
	
	public MobileObjectDatabase()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}

	public HibernateManager getHibernateManager() 
	{
		return hibernateManager;
	}

	public void setHibernateManager(HibernateManager hibernateManager) 
	{
		this.hibernateManager = hibernateManager;
	}
		
	public StorageMonitor getStorageMonitor() 
	{
		return storageMonitor;
	}

	public void setStorageMonitor(StorageMonitor storageMonitor) 
	{
		this.storageMonitor = storageMonitor;
	}
	//-----------------------------------------------------------------------------------------------	
	public String create(MobileObject mobileObject)
	{		
		Session session = null;
		Transaction tx = null;
		try
		{
			String newId = null;
			
			String storageId = mobileObject.getStorageId();
			StorageListener listener = this.storageMonitor.lookup(storageId);
			if(listener != null)
			{
				String mappedRecordId = listener.create(mobileObject);
				if(mappedRecordId != null && mappedRecordId.trim().length()>0)
				{
					mobileObject.setRecordId(mappedRecordId);
				}
			}
			
			if(mobileObject.getRecordId() == null || mobileObject.getRecordId().trim().length() == 0)
			{
				mobileObject.setRecordId(Utilities.generateUID());
			}
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			((Serializable)session.save(mobileObject)).toString();
			
			newId = mobileObject.getRecordId();
						
			tx.commit();
			
			return newId;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	
	/*public MobileObject read(String id)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			MobileObject mobileObject = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from MobileObject where id=?";
			
			mobileObject = (MobileObject)session.createQuery(query).setString(0, id).uniqueResult();
						
			tx.commit();
			
			return mobileObject;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}*/
	
	public MobileObject read(String storageId, String id)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			MobileObject mobileObject = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from MobileObject where recordId=? and storageId=?";
			
			mobileObject = (MobileObject)session.createQuery(query).setString(0, id).setString(1, storageId).
			uniqueResult();
						
			tx.commit();
			
			return mobileObject;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	
	public MobileObject readByRecordId(String recordId)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			MobileObject mobileObject = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from MobileObject where recordId=?";
			
			mobileObject = (MobileObject)session.createQuery(query).setString(0, recordId).uniqueResult();
						
			tx.commit();
			
			return mobileObject;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	
	public List<MobileObject> readByStorage(String storageId)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<MobileObject> objects = new ArrayList<MobileObject>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from MobileObject where storageId=?";
			
			objects = session.createQuery(query).setString(0, storageId).list();
						
			tx.commit();
			
			return objects;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	
	public void update(MobileObject mobileObject)
	{		
		Session session = null;
		Transaction tx = null;
		try
		{			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.update(mobileObject);
						
			tx.commit();		
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	
	public void delete(MobileObject mobileObject)
	{		
		Session session = null;
		Transaction tx = null;
		try
		{			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.delete(mobileObject);
						
			tx.commit();		
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	
	public void deleteAll(String storageId)
	{						
		List<MobileObject> allObjects = this.readByStorage(storageId);
		if(allObjects != null)
		{
			for(MobileObject object: allObjects)
			{
				this.delete(object);
			}
		}
	}
}
