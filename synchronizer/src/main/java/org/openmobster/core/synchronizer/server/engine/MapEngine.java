/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmobster.core.synchronizer.SyncException;
import org.openmobster.core.synchronizer.server.SyncContext;

import org.openmobster.core.common.database.HibernateManager;

/**
 * 
 * @author openmobster@gmail.com
 */
public class MapEngine
{
	/**
	 * 
	 */
	private HibernateManager hibernateManager = null;
	
	/**
	 * 
	 *
	 */
	public MapEngine()
	{
		
	}
	
	/**
	 * 
	 * @return
	 */
	public HibernateManager getHibernateManager()
	{
		return hibernateManager;
	}

	/**
	 * 
	 * @param hibernateManager
	 */
	public void setHibernateManager(HibernateManager hibernateManager)
	{
		this.hibernateManager = hibernateManager;
	}
	
	/**
	 * 
	 *
	 */
	public void start()
	{
		
	}
	
	/**
	 * 
	 *
	 */
	public void stop()
	{
		
	}
	
	public Map readRecordMap(String source, String target)
	{
		Map recordMap = new HashMap();
		
		Session session = this.hibernateManager.getSessionFactory()
		.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			Query query = session.createQuery("from RecordMap as recordmap where recordmap.source=? and recordmap.target=?").
			setString(0, source).setString(1, target);
			
			List recordMaps = query.list();
			
			for(int i=0; i<recordMaps.size(); i++)
			{
				RecordMap cour = (RecordMap)recordMaps.get(i);				
				recordMap.put(cour.getGuid(), cour.getLuid());
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
			throw new SyncException(e);
		}		
		return recordMap;
	}
	
	public RecordMap readMapping(String source, String target, String uid, boolean isServerId)
	{
		RecordMap recordMap = null;
		
		Session session = this.hibernateManager.getSessionFactory()
		.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			Query query = null; 
			
			if(isServerId)
			{
				query = session.createQuery("from RecordMap as recordmap where recordmap.source=? and recordmap.target=? and recordmap.guid=?").
				setString(0, source).setString(1, target).setString(2, uid);
			}
			else
			{
				query = session.createQuery("from RecordMap as recordmap where recordmap.source=? and recordmap.target=? and recordmap.luid=?").
				setString(0, source).setString(1, target).setString(2, uid);
			}
			
			List cour = query.list();
			if(cour != null && !cour.isEmpty())
			{
				recordMap = (RecordMap)cour.get(0);
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
			throw new SyncException(e);
		}		
		return recordMap;
	}
	//---------------------------------------------------------------------------------------------------------------------
	public void saveRecordMap(String source, String target, Map recordMap)
	{
		if(recordMap != null)
		{
			Set guids = recordMap.keySet();
			Session session = this.hibernateManager.getSessionFactory()
			.getCurrentSession();
			Transaction tx = session.beginTransaction();
			try
			{
				for(Iterator itr=guids.iterator(); itr.hasNext();)
				{
					Object guid = itr.next();
					Object luid = recordMap.get(guid);
					RecordMap entry = new RecordMap();
					entry.setGuid(guid);
					entry.setLuid(luid);
					entry.setSource(source);
					entry.setTarget(target);
					
					//Persist this entry into the database
					session.save(entry);								
				}
				
				tx.commit();
			}
			catch(Exception e)
			{
				tx.rollback();
				throw new SyncException(e);
			}			
		}
	}
	
	public void saveRecordMap(Map recordMap)
	{
		org.openmobster.core.synchronizer.server.Session session = SyncContext.getInstance().getSession();
		String source = session.getDataSource(false);
		String target = session.getDataTarget(false);
		
		this.saveRecordMap(source,target,recordMap);
	}
	//-----------------------------------------------------------------------------------------------------------------------------	
	public void removeRecordMap(String source, String target)
	{
		Session session = this.hibernateManager.getSessionFactory()
		.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			Query query = session.createQuery("delete RecordMap as recordmap where recordmap.source=? and recordmap.target=?")
			.setString(0, source).setString(1, target);
			
			query.executeUpdate();
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
			throw new SyncException(e);
		}		
	}
	
	public void clearAll()
	{
		Session session = this.hibernateManager.getSessionFactory()
		.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			Query query = session.createQuery("delete RecordMap as recordmap");
			
			query.executeUpdate();
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
			throw new SyncException(e);
		}		
	}
	//----------------------------------------------------------------------------------------------------------
	public String mapFromServerToLocal(String recordId)
	{
		String mappedRecordId = null;
		
		org.openmobster.core.synchronizer.server.Session session = SyncContext.getInstance().getSession();
		String source = session.getDataSource(false);
		String target = session.getDataTarget(false);
		
		RecordMap recordMap = this.readMapping(source, target, recordId, true);
		if(recordMap != null)
		{
			mappedRecordId = recordMap.getLuid().toString();
		}
		else
		{
			//Nothing to map...ids are same on both ends
			mappedRecordId = recordId;
		}
		
		return mappedRecordId;
	}
	
	
	public String mapFromLocalToServer(String recordId)
	{
		String mappedRecordId = null;
		
		org.openmobster.core.synchronizer.server.Session session = SyncContext.getInstance().getSession();
		String source = session.getDataSource(false);
		String target = session.getDataTarget(false);
		
		RecordMap recordMap = this.readMapping(source, target, recordId, false);
		if(recordMap != null)
		{
			mappedRecordId = recordMap.getGuid().toString();
		}
		else
		{
			//Nothing to map...ids are same on both ends
			mappedRecordId = recordId;
		}
		
		return mappedRecordId;
	}
}
