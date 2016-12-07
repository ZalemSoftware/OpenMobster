/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.model.Alert;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.VariableConstants;

/**
 * @author openmobster@gmail.com
 */
public class DecideSyncScenario implements DecisionHandler
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -499298824650639065L;
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(DecideSyncScenario.class);

	/**
	 * 
	 */
	public String decide(ExecutionContext context) throws Exception 
	{
		String result = WorkflowConstants.normalSync;
		Session session = Utilities.getSession(context);
		SyncMessage currentMessage = session.getCurrentMessage();
		
		//Process other Status/Scenarios
		List statusCodes = new ArrayList();
		// Check Status messages and make sure everything is ok
		for (int i = 0; i < currentMessage.getStatus().size(); i++)
		{
			Status status = (Status) currentMessage.getStatus().get(i);
			if (!status.getData().equals(SyncServer.SUCCESS))
			{
				// some issue happened....perform error handling
				statusCodes.add(status);
			}
		}
		
		//Start handling out of the ordinary scenarios
		for(int i=0;i<statusCodes.size();i++)
		{
			Status status = (Status)statusCodes.get(i);
			
			if(status.getData().equals(SyncServer.CHUNK_ACCEPTED))
			{
				context.getContextInstance().setTransientVariable(VariableConstants.status, 
				status);
				return WorkflowConstants.chunkAccepted;
			}
			else if(status.getData().equals(SyncServer.CHUNK_SUCCESS) || 
					status.getData().equals(SyncServer.SIZE_MISMATCH))
			{
				context.getContextInstance().setTransientVariable(VariableConstants.statusCodes, 
				statusCodes);
				return WorkflowConstants.closeChunk;
			}			
		}
		
		//Start handling Alerts
		List alerts = currentMessage.getAlerts();
		for(int i=0;i<alerts.size();i++)
		{
			Alert alert = (Alert)alerts.get(i);
			
			if(alert.getData().equals(SyncServer.NEXT_MESSAGE))
			{				
				return WorkflowConstants.nextMessage;
			}
		}
		
		if(session.getSyncType().equals(SyncServer.STREAM))
		{
			return WorkflowConstants.streamSync;
		}
		else if(session.getSyncType().equals(SyncServer.BOOT_SYNC))
		{
			return WorkflowConstants.bootSync;
		}
		
		return result;
	}
}
