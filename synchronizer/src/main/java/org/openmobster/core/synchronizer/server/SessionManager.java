/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import java.util.Map;
import java.util.HashMap;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.Utilities;

/**
 * TODO: cluster safety
 * 
 * Stateful Component - Holds all active Synchronization Sessions
 * 
 * @author openmobster@gmail.com
 *
 */
public class SessionManager
{
	private Map sessionMap = null;
	
	public SessionManager()
	{
		this.sessionMap = new HashMap();
	}
	
	public static SessionManager getInstance()
	{
		return (SessionManager)ServiceManager.locate("synchronizer://SessionManager");
	}
	
	public Session findSession(String sessionId)
	{
		Session session = null;
		
		if(sessionId != null && sessionId.trim().length()>0)
		{
			session = (Session)this.sessionMap.get(sessionId);
		}
		
		return session;
	}
	
	public String saveSession(Session session)
	{
		String sessionId = null;
		
		if(session != null && 
		   session.getSessionId() != null && 
		   session.getSessionId().trim().length() > 0
		)
		{
			sessionId = session.getSessionId();			
		}
		else
		{
			sessionId = Utilities.generateUID();
			session.setSessionId(sessionId);
			
		}
		
		this.sessionMap.put(session.getSessionId(), session);
		
		return sessionId;
	}
	
	public void cleanup(Session session)
	{
		this.sessionMap.remove(session.getSessionId());
	}
}
