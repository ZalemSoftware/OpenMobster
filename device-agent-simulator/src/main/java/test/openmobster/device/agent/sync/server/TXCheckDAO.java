/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.openmobster.device.agent.sync.server;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmobster.core.common.database.HibernateManager;

/**
 * 
 * @author openmobster@gmail.com
 */
public class TXCheckDAO
{
	private HibernateManager hibernateManager;

	public TXCheckDAO()
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

	public String create(TXBean bean, boolean simulateFailure)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			if (simulateFailure)
			{
				throw new RuntimeException("Simulating Failure");
			}

			session.save(bean);

			String oid = bean.getOid();

			tx.commit();

			return oid;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			if (tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
	}

	public List readAll()
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			List all = session.createQuery("from TXBean").list();

			tx.commit();

			return all;
		} 
		catch (Exception e)
		{
			if (tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
	}

	public void delete(TXBean bean)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			session.delete(bean);

			tx.commit();
		} 
		catch (Exception e)
		{
			if (tx != null)
			{
				tx.rollback();
			}
		} 
	}

	public void deleteAll()
	{
		List all = this.readAll();
		if (all != null)
		{
			for (int i = 0; i < all.size(); i++)
			{
				this.delete((TXBean) all.get(i));
			}
		}
	}
}
