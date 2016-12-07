/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.server.engine;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;
import org.openmobster.core.synchronizer.SyncException;

/**
 * FIXME: implement my persistence
 * 
 * @author openmobster@gmail.com
 */
public class ConflictEngine
{
	private static Logger log = Logger.getLogger(ConflictEngine.class);
	
	private MobileObjectSerializer serializer;
	private HibernateManager hibernateManager = null;
	
	public ConflictEngine()
	{
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public MobileObjectSerializer getSerializer()
	{
		return serializer;
	}

	public void setSerializer(MobileObjectSerializer serializer)
	{
		this.serializer = serializer;
	}
	
	
	public HibernateManager getHibernateManager()
	{
		return hibernateManager;
	}

	public void setHibernateManager(HibernateManager hibernateManager)
	{
		this.hibernateManager = hibernateManager;
	}
	//-------------------------------------------------------------------------------------------------------------------
	public void startOptimisticLock(String app, String channel,MobileBean cloudBean) throws SyncException
	{
		String deviceId = Tools.getDeviceId();
		String serializedBean = this.serializer.serialize(cloudBean).trim();
		String oid = Tools.getOid(cloudBean);
		
		if(oid.startsWith("proxy[[") && oid.endsWith("]]"))
		{
			//don't lock...just proxy objects
			return;
		}
		
		ConflictEntry bean = this.readLock(deviceId, oid, app, channel);
		
		//log.debug("******StartLock*****************************************");
		//log.debug("Device: "+deviceId);
		//log.debug("App: "+app);
		//log.debug("Channel: "+channel);
		//log.debug("Serialized: "+serializedBean);
		//log.debug("***********************************************");
		
		bean.setState(serializedBean.getBytes());
		
		this.saveLock(bean);
	}
	
	public boolean checkOptimisticLock(String app, String channel,MobileBean cloudBean) throws SyncException
	{
		String deviceId = Tools.getDeviceId();
		String oid = Tools.getOid(cloudBean);
		
		ConflictEntry bean = this.readLock(deviceId, oid, app, channel);
		
		String state = bean.getStateAsString();
		if(state != null && state.trim().length()>0)
		{
			String serializedBean = this.serializer.serialize(cloudBean).trim();
			String checkAgainst = bean.getStateAsString();
			
			if(!serializedBean.equals(checkAgainst))
			{
				//log.debug("**********Check Lock*************************************");
				//log.debug("Device: "+deviceId);
				//log.debug("App: "+app);
				//log.debug("Channel: "+channel);
				//log.debug("********************************************************");
				//log.debug("Serialized: "+serializedBean);
				//log.debug("********************************************************");
				//log.debug("Checkagainst: "+checkAgainst);
				//log.debug("***********************************************");
				
				this.handleConflict(deviceId, app, channel, cloudBean);
				return false;
			}
			else
			{
				//log.debug("**********Check Succeeded*************************************");
				//log.debug("Device: "+deviceId);
				//log.debug("App: "+app);
				//log.debug("Channel: "+channel);
				//log.debug("********************************************************");
				//log.debug("Serialized: "+serializedBean);
				//log.debug("********************************************************");
				//log.debug("Checkagainst: "+checkAgainst);
				//log.debug("***********************************************");
			}
		}
		return true;
	}
	
	
	private void handleConflict(String deviceId, String app ,String channel, MobileBean bean)
	{
		//TODO: report this to the Console so that admins can pull and see what happened
	}
	
	public void clearAll()
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			List all = session.createQuery("from ConflictEntry").list();
			if(all != null && !all.isEmpty())
			{
				for(Object entry:all)
				{
					session.delete(entry);
				}
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new SyncException(e);
		}
	}
	//------Persistence related code--------------------------------------------------------------------------------------------
	void saveLock(ConflictEntry conflictEntry)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			if(conflictEntry.getId() == 0)
			{
				session.save(conflictEntry);
			}
			else
			{
				session.update(conflictEntry);
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new SyncException(e);
		}
	}
	
	ConflictEntry readLock(String deviceId, String oid, String app, String channel)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			ConflictEntry local = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from ConflictEntry where deviceId=? AND oid=? AND app=? AND channel=?";
			
			local = (ConflictEntry)session.createQuery(query).setParameter(0, deviceId).
			setParameter(1,oid).
			setParameter(2,app).
			setParameter(3,channel).uniqueResult();
						
			tx.commit();
			
			if(local == null)
			{
				local = new ConflictEntry();
				local.setDeviceId(deviceId);
				local.setOid(oid);
				local.setApp(app);
				local.setChannel(channel);
			}
			
			return local;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}
	}
	
	public Set<String> findLiveApps(String deviceId, String channel)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			Set<String> apps = new HashSet<String>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from ConflictEntry where deviceId=? AND channel=?";
			
			List entries = session.createQuery(query).setParameter(0, deviceId).
			setParameter(1,channel).list();
			
			if(entries != null && !entries.isEmpty())
			{
				for(Object local:entries)
				{
					ConflictEntry entry = (ConflictEntry)local;
					apps.add(entry.getApp());
				}
			}
						
			tx.commit();
			
			return apps;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}
	}
	
	public List<ConflictEntry> findLiveEntries(String channel, String oid)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<ConflictEntry> liveEntries = new ArrayList<ConflictEntry>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from ConflictEntry where oid=? AND channel=?";
			
			List entries = session.createQuery(query).setParameter(0, oid).
			setParameter(1,channel).list();
			
			if(entries != null && !entries.isEmpty())
			{
				for(Object local:entries)
				{
					ConflictEntry entry = (ConflictEntry)local;
					liveEntries.add(entry);
				}
			}
						
			tx.commit();
			
			return liveEntries;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}
	}
}
