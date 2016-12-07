/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.workflow;

import org.apache.log4j.Logger;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;


/**
 * @author openmobster@gmail.com
 */
public class EnterPerformMapExchange implements ActionHandler 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(EnterPerformMapExchange.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5637962645508771892L;

	/**
	 * 
	 */
	public void execute(ExecutionContext context) throws Exception 
	{
		SyncMessage reply = null;
		Session session = Utilities.getSession(context);
		SyncXMLGenerator syncXMLGenerator = Utilities.getSyncXMLGenerator(context);
		
		reply = Utilities.setUpReply(context);
		
		Status mapStatus = Utilities.processRecordMap(context, 1);			
		reply.getStatus().add(mapStatus);			
				
		reply.setFinal(true);			
		if(mapStatus.getData().equals(SyncServer.SUCCESS))
		{
			session.setMapExchangeInProgress(true);
		}
		else
		{
			session.setMapExchangeInProgress(false);
		}
		
		session.getServerSyncPackage().addMessage(reply);		
		String payload = syncXMLGenerator.generateSyncMessage(session, reply);
		Utilities.preparePayload(context, payload);
	}
}
