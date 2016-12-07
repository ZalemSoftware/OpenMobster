/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.security.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.security.DBException;

/**
 * 
 * @author openmobster@gmail.com
 */
public class AndroidPushAppController
{
	private static Logger log = Logger.getLogger(AndroidPushAppController.class);
	
	private HibernateManager hibernateManager;

	public HibernateManager getHibernateManager()
	{
		return hibernateManager;
	}

	public void setHibernateManager(HibernateManager hibernateManager)
	{
		this.hibernateManager = hibernateManager;
	}
	
	public static AndroidPushAppController getInstance()
	{
		return (AndroidPushAppController)ServiceManager.locate("security://AndroidPushAppController");
	}
	
	public String create(AndroidPushApp pushApp) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			Serializable id = session.save(pushApp);
						
			tx.commit();
			
			return id.toString();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DBException(e);
		}		
	}
	
	public AndroidPushApp readPushApp(String appId) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			AndroidPushApp pushApp = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from AndroidPushApp pushApp where pushApp.appId=?";
			
			pushApp = (AndroidPushApp)session.createQuery(query).setString(0, appId).uniqueResult();
						
			tx.commit();
			
			return pushApp;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DBException(e);
		}
	}
	
	public List<AndroidPushApp> readAll() throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<AndroidPushApp> all = new ArrayList<AndroidPushApp>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from AndroidPushApp";
			
			List list = session.createQuery(query).list();
			if(list != null)
			{
				all.addAll(list);
			}
						
			tx.commit();
			
			return all;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DBException(e);
		}
	}
	
	public void update(AndroidPushApp pushApp) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.update(pushApp);
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DBException(e);
		}		
	}
	
	
	public void delete(AndroidPushApp pushApp) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.delete(pushApp);
			session.flush();
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DBException(e);
		}		
	}
}
