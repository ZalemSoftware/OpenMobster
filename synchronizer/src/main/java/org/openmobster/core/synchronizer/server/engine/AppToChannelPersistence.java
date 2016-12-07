/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.server.engine;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.synchronizer.SyncException;

/**
 *
 * @author openmobster@gmail.com
 */
public class AppToChannelPersistence
{
	private static Logger log = Logger.getLogger(AppToChannelPersistence.class);
	
	private HibernateManager hibernateManager;
	
	public AppToChannelPersistence()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public static AppToChannelPersistence getInstance()
	{
		return (AppToChannelPersistence)ServiceManager.locate("AppToChannelPersistence");
	}

	public HibernateManager getHibernateManager()
	{
		return hibernateManager;
	}

	public void setHibernateManager(HibernateManager hibernateManager)
	{
		this.hibernateManager = hibernateManager;
	}
	//----------------------------------------------------------------------------------------------------------
	public void storeAssociation(AppToChannelAssociation association)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String deviceId = association.getDeviceId();
			String app = association.getApp();
			String channel = association.getChannel();
			
			String query = "from AppToChannelAssociation where deviceId=? AND app=? AND channel=?";
			
			AppToChannelAssociation storedAssociation = (AppToChannelAssociation)session.createQuery(query).setString(0, deviceId).setString(1, app).setString(2, channel).
			uniqueResult();
			if(storedAssociation == null)
			{
				session.save(association);
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
	
	public Set<String> readApps(String deviceId, String channel)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			Set<String> returnValue = new HashSet<String>();
			
			String query = "from AppToChannelAssociation where deviceId=? AND channel=?";
			List apps = session.createQuery(query).setString(0, deviceId).setString(1, channel).list();
			
			if(apps != null && !apps.isEmpty())
			{
				for(Object local:apps)
				{
					AppToChannelAssociation association = (AppToChannelAssociation)local;
					returnValue.add(association.getApp());
				}
			}
			
			tx.commit();
			
			return returnValue;
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
