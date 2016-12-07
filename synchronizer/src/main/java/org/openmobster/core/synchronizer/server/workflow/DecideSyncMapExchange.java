/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.workflow;

import org.apache.log4j.Logger;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.server.Session;

/**
 * @author openmobster@gmail.com
 */
public class DecideSyncMapExchange implements DecisionHandler
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -499298824650639065L;
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(DecideSyncMapExchange.class);

	/**
	 * 
	 */
	public String decide(ExecutionContext context) throws Exception 
	{
		String result = WorkflowConstants.synchronize;
		Session session = Utilities.getSession(context);
		SyncMessage currentMessage = session.getCurrentMessage();
		
		if(currentMessage.getRecordMap() != null)
		{
			return WorkflowConstants.performMapExchange;
		}
		
		return result;
	}
}
