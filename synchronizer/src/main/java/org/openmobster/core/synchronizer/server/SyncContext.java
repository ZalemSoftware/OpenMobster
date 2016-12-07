/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import org.openmobster.core.common.ServiceManager;


/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncContext
{
	/**
	 * 
	 */
	private static ThreadLocal threadLocal = null;
	
	static
	{
		threadLocal = new ThreadLocal();
	}
	
	/**
	 * 
	 */
	private Session session = null;
	
	/**
	 * 
	 *
	 */
	private SyncContext()
	{
	}
	
	public static SyncContext getInstance()
	{
		SyncContext syncContext = null;
		
		syncContext = (SyncContext)threadLocal.get();
		if(syncContext == null)
		{
			threadLocal.set(new SyncContext());
			syncContext = (SyncContext)threadLocal.get();
		}
		
		return syncContext;
	}
	
	/**
	 * 
	 * @return
	 */
	public Session getSession()
	{
		return this.session;
	}
	
	/**
	 * 
	 * @param session
	 */
	public void setSession(Session session)
	{
		this.session = session;
	}
	
	public void setSession(String sessionId)
	{
		SessionManager sessionManager = (SessionManager)ServiceManager.locate("synchronizer://SessionManager");
		Session session = sessionManager.findSession(sessionId);
		if(session != null)
		{
			this.session = session;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getServerSource()
	{
		return this.session.getDataSource(true);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDeviceSource()
	{
		return this.session.getDataSource(false);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDeviceId()
	{
		return this.session.getDeviceId();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getApp()
	{
		return this.session.getApp();
	}
}
