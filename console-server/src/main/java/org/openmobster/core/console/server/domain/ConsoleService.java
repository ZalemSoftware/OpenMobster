/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server.domain;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.common.database.HibernateManager;

import org.openmobster.core.common.ServiceManager;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/console/consoleService")
public class ConsoleService implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(ConsoleService.class);
	
	private HibernateManager hibernateManager;
	
	public ConsoleService()
	{
		
	}
	
	public static ConsoleService getInstance()
	{
		return (ConsoleService)ServiceManager.locate("/console/consoleService");
	}
	
	public void start()
	{
		
	}
	
	public void stop()
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
	//------------------------------------------------------------------------------------------------------------------------------
	public Response invoke(Request request)
	{
		String action = request.getAttribute("action");
		String username = request.getAttribute("username");
		String password = request.getAttribute("password");
		
		if(username == null || password == null || username.trim().length() == 0 || password.trim().length() == 0)
		{
			Response response = new Response();
			response.setAttribute("console-error", "missing_required_info");
			return response;
		}
		
		
		if(action.equalsIgnoreCase("configure"))
		{
			this.store(username, password);
		}
		else if(action.equalsIgnoreCase("auth"))
		{	
			boolean auth = this.authenticate(username, password);
			if(!auth)
			{
				Response response = new Response();
				response.setAttribute("console-error", "auth_failed");
				return response;
			}
		}
		
		return null;
	}
	//-------------------------------------------------------------------------------------------------------------------------------
	private void store(String principal, String credential)
	{	
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			ConsoleUser user = this.readIfExists(session, principal);
			if(user == null)
			{
				user = new ConsoleUser();
			}
			
			user.setPrincipal(principal.trim());
			user.setCredential(credential.trim());
			session.saveOrUpdate(user);
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
	private boolean authenticate(String principal, String credential)
	{	
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			ConsoleUser user = this.readIfExists(session, principal.trim());
			
			tx.commit();
			
			return (user != null && user.getCredential().equals(credential.trim()));
		}
		catch(Exception e)
		{
			tx.rollback();
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------
	private ConsoleUser readIfExists(Session session, String principal)
	{
		ConsoleUser user = null;
		
		String query = "from ConsoleUser where principal=?";
		user = (ConsoleUser)session.createQuery(query).setString(0, principal).uniqueResult();
		
		return user;
	}
}
