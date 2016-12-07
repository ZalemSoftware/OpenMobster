/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.moblet;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.common.domain.BusinessObject;

/**
 * @author openmobster@gmail.com
 */
public class MobletApp extends BusinessObject
{
	private static final long serialVersionUID = 2259570098365402852L;
	private static Logger log = Logger.getLogger(MobletApp.class);	
	
	private String uri;
	private String name;
	private String description;
	private String binaryLocation;
	private String configLocation;
	private String deploymentUrl;
	
	public MobletApp()
	{
		
	}
	
	
	public String getUri() 
	{
		return uri;
	}


	public void setUri(String uri) 
	{
		this.uri = uri;
	}

	
	public String getBinaryLocation() 
	{
		return binaryLocation;
	}

	public void setBinaryLocation(String binaryLocation) 
	{
		this.binaryLocation = binaryLocation;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String toString()
	{
		return this.name+": "+this.binaryLocation;
	}
		
	public String getConfigLocation() 
	{
		return configLocation;
	}


	public void setConfigLocation(String configLocation) 
	{
		this.configLocation = configLocation;
	}
	
	
	public String getDeploymentUrl()
	{
		return deploymentUrl;
	}


	public void setDeploymentUrl(String deploymentUrl)
	{
		this.deploymentUrl = deploymentUrl;
	}
	//-------Persistence Services--------------------------------------------------------------------------------------------------
	public static void create(MobletApp mobletApp) throws SystemException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = getHibernateManager().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
		
			String query = "from MobletApp where uri=?";
			Object appConfig = session.createQuery(query).setString(0, mobletApp.getUri()).uniqueResult();
						
			if(appConfig != null)
			{
				session.delete(appConfig);
				session.flush();
			}
			
			
			session.save(mobletApp);
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(MobletApp.class, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new SystemException(e.getMessage(), e);
		}
	}
			
	public static List<MobletApp> readAll() throws SystemException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<MobletApp> allApps = new ArrayList<MobletApp>();
			
			session = getHibernateManager().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from MobletApp";
			
			List cour = session.createQuery(query).list();
			allApps.addAll(cour);
						
			tx.commit();
			
			return allApps;
		}
		catch(Exception e)
		{
			log.error(MobletApp.class, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new SystemException(e.getMessage(), e);
		}
	}
	
	public static void delete(MobletApp mobletApp) throws SystemException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = getHibernateManager().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
		
			String query = "delete from MobletApp where uri=?";
			session.createQuery(query).setString(0, mobletApp.getUri()).executeUpdate();
						
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(MobletApp.class, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new SystemException(e.getMessage(), e);
		}
	}
	
	private static HibernateManager getHibernateManager()
	{
		return (HibernateManager)ServiceManager.locate("moblet-management://HibernateManager");
	}
}
