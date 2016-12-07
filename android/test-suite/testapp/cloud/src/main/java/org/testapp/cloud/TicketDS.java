/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.testapp.cloud;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.database.HibernateManager;

/**
 * 'TicketDS' is a traditional data source component based on the Hibernate framework. It provides
 *  relational database storage of ticket instances 
 * 
 * @author openmobster@gmail.com
 */
public class TicketDS 
{
	private static Logger log = Logger.getLogger(TicketDS.class);
	
	private HibernateManager hibernateManager;
	
	public TicketDS()
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
	//-------------------------------------------------------------------------------------------------------
	public String create(Ticket ticket)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			String ticketId = Utilities.generateUID();
			ticket.setTicketId(ticketId);
			session.save(ticket);
						
			tx.commit();
			
			return ticket.getTicketId();
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
	}
	
	public List<Ticket> readAll()
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<Ticket> tickets = new ArrayList<Ticket>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Ticket";
			
			List cour = session.createQuery(query).list();
			
			if(cour != null)
			{
				tickets.addAll(cour);
			}
						
			tx.commit();
			
			return tickets;
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
	}
	
	public Ticket readByTicketId(String ticketId)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			Ticket ticket = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Ticket where ticketId=?";
			
			ticket = (Ticket)session.createQuery(query).setParameter(0, ticketId).uniqueResult();
						
			tx.commit();
			
			return ticket;
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
	}
	
	public void update(Ticket ticket)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
						
			session.update(ticket);
						
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
	}
	
	public void delete(Ticket ticket)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.delete(ticket);
						
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
	}
	
	public void deleteAll()
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from Ticket";
			List cour = session.createQuery(query).list();			
			if(cour != null)
			{
				for(Object ticket: cour)
				{
					session.delete(ticket);
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
			throw new RuntimeException(e);
		}
	}
}
