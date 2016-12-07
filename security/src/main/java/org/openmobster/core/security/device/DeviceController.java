/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security.device;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.database.HibernateManager;

import org.openmobster.core.security.DeviceSecurityException;

import org.openmobster.core.common.ServiceManager;

/**
 * Controls data storage related functions for the Device objects
 * 
 * @author openmobster@gmail.com
 */
public class DeviceController 
{	
	private static Logger log = Logger.getLogger(DeviceController.class);
	
	
	private HibernateManager hibernateManager;
	
	
	public DeviceController()
	{
		
	}
	
	public static DeviceController getInstance()
	{
		return (DeviceController)ServiceManager.locate("security://DeviceController");
	}
			
	public HibernateManager getHibernateManager() 
	{
		return hibernateManager;
	}

	
	public void setHibernateManager(HibernateManager hibernateManager) 
	{
		this.hibernateManager = hibernateManager;
	}

		
	public void create(Device device) throws DeviceSecurityException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.save(device);
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DeviceSecurityException(e);
		}		
	}
	
	
	public Device read(String identifier) throws DeviceSecurityException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			Device device = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Device device where device.identifier=?";
			device = (Device)session.createQuery(query).setString(0, identifier).setCacheable(true).uniqueResult();
			
						
			tx.commit();
			
			return device;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DeviceSecurityException(e);
		}		
	}
	
	public List<Device> readAll() throws DeviceSecurityException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<Device> allDevices = new ArrayList<Device>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Device";
			
			List devices = session.createQuery(query).setCacheable(true).list();
			if(devices != null)
			{
				allDevices.addAll(devices);
			}
						
			tx.commit();
			
			return allDevices;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DeviceSecurityException(e);
		}
	}
	
	public boolean exists(String identifier) throws DeviceSecurityException
	{
		Session session = null;
		Transaction tx = null;
		try
		{			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Device device where device.identifier=?";
			
			List count = session.createQuery(query).setString(0, identifier).setCacheable(true).list();
			
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
			throw new DeviceSecurityException(e);
		}		
	}
	
	public Set<Device> readByIdentity(String principal) throws DeviceSecurityException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			Set<Device> devices = new HashSet<Device>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
						
			String query = "from Device device, Identity identity " +
			"where device.identity.id=identity.id and identity.principal=?";
			
			List objects = session.createQuery(query).setString(0, principal).setCacheable(true).list();
			if(objects != null && !objects.isEmpty())
			{
				for(Object local:objects)
				{
					Object[] row = (Object[])local;
					devices.add((Device)row[0]);
				}
			}
								
			tx.commit();
			
			return devices;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DeviceSecurityException(e);
		}		
	}
					
	public void update(Device device) throws DeviceSecurityException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.update(device);
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new DeviceSecurityException(e);
		}		
	}
	
	
	public void delete(Device device) throws DeviceSecurityException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.delete(device);
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
			throw new DeviceSecurityException(e);
		}		
	}	
}
