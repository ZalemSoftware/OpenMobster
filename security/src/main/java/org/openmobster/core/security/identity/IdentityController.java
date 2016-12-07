/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security.identity;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.database.HibernateManager;

import org.openmobster.core.security.DeviceSecurityException;
import org.openmobster.core.security.IDMException;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;

/**
 * Control data storage functions of Identity objects
 * 
 * @author openmobster@gmail.com
 */
public class IdentityController 
{	
	private static Logger log = Logger.getLogger(IdentityController.class);
	
	
	private HibernateManager hibernateManager;
	
	
	public IdentityController()
	{
		
	}
	
	public static IdentityController getInstance()
	{
		return (IdentityController)ServiceManager.locate("security://IdentityController");
	}
	
		
	public HibernateManager getHibernateManager() 
	{
		return hibernateManager;
	}


	
	public void setHibernateManager(HibernateManager hibernateManager) 
	{
		this.hibernateManager = hibernateManager;
	}


	
	public void create(Identity identity) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.save(identity);
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new IDMException(e);
		}		
	}
	
	
	public Identity read(String principal) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			Identity identity = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Identity where principal=?";
			
			identity = (Identity)session.createQuery(query).setString(0, principal).setCacheable(true).uniqueResult();
						
			tx.commit();
			
			return identity;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new IDMException(e);
		}		
	}
	
	public boolean exists(String principal) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Identity where principal=?";
			
			List count = session.createQuery(query).setString(0, principal).setCacheable(true).list();
									
			boolean exists = false;
			if(count != null && !count.isEmpty())
			{
				exists = true;
			}
			
			tx.commit();
			
			return exists;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new IDMException(e);
		}		
	}
	
	
	public List<Identity> readAll() throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<Identity> identities = new ArrayList<Identity>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Identity";
			
			List cour = session.createQuery(query).setCacheable(true).list();
			identities.addAll(cour);
						
			tx.commit();
			
			return identities;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new IDMException(e);
		}		
	}
	
	
	public void update(Identity identity) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.update(identity);
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new IDMException(e);
		}		
	}	
	//---------------------------------------------------------------------------------------------------	
	public void loadGroups(Identity identity) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			session.update(identity);
			
			for(Group group: identity.getGroups())
			{
				Hibernate.initialize(group);
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
			throw new IDMException(e);
		}		
	}
}
