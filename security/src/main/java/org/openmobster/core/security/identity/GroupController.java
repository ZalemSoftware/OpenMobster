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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Hibernate;

import org.openmobster.core.common.database.HibernateManager;

import org.openmobster.core.security.IDMException;

/**
 * Control data storage functions of Group objects
 * 
 * @author openmobster@gmail.com
 */
public class GroupController 
{
	private static Logger log = Logger.getLogger(GroupController.class);
	
	
	private HibernateManager hibernateManager;
	
	
	public GroupController()
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


	
	public void create(Group group) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.save(group);
						
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
	
	
	public Group read(String name) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			Group group = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Group where name=?";
			
			group = (Group)session.createQuery(query).setString(0, name).setCacheable(true).uniqueResult();
						
			tx.commit();
			
			return group;
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
	
	
	public List<Group> readAll() throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<Group> groups = new ArrayList<Group>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Group";
			
			List cour = session.createQuery(query).setCacheable(true).list();
			groups.addAll(cour);
						
			tx.commit();
			
			return groups;
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
	
	
	public void update(Group group) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.update(group);
						
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
	
	
	public void delete(Group group) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.delete(group);
						
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
	//-------------------------------------------------------------------------------------------------------	
	public void loadMembers(Group group) throws IDMException
	{		
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			session.update(group);
			
			for(Identity identity: group.getMembers())
			{
				Hibernate.initialize(identity);				
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
		
	public Identity findMember(String member, Group group) throws IDMException
	{		
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			session.update(group);
			
			Identity identity = null;
			
			for(Identity cour: group.getMembers())
			{
				if(cour.getPrincipal().equals(member))
				{
					Hibernate.initialize(cour);
					identity = cour;
					break;
				}
			}
			
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
	
	public void addMember(Identity member, Group group) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();			
			tx = session.beginTransaction();
			session.update(group);
			
			group.getMembers().add(member);
			
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
	
	
	public void removeMember(String member, Group group) throws IDMException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();			
			tx = session.beginTransaction();
			session.update(group);
			
			Identity identity = null;
			for(Identity cour: group.getMembers())
			{
				if(cour.getPrincipal().equals(member))
				{
					identity = cour;
					break;
				}
			}
			
			if(identity != null)
			{
				group.getMembers().remove(identity);
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
