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
import org.openmobster.core.synchronizer.server.Session;

/**
 * @author openmobster@gmail.com
 */
public class DecideEndSynchronize implements DecisionHandler
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1278481265825418267L;
	
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(DecideEndSynchronize.class);

	/**
	 * 
	 */
	public String decide(ExecutionContext context) throws Exception 
	{
		Session session = Utilities.getSession(context);
		String result = WorkflowConstants.goback;
		
		if(!session.isMapExchangeInProgress())
		{
			if(
			   Utilities.containsSyncFinal(session.getClientSyncPackage().getMessages()) && 
			   Utilities.containsSyncFinal(session.getServerSyncPackage().getMessages()) &&
			   !Utilities.isChunkOpen(session) && 
			   !Utilities.hasErrors(session.getCurrentMessage()) &&
			   !session.isMultiSyncActive()
			)
			{
				result = WorkflowConstants.proceed;
			}
		}
		
		return result;
	}
}
