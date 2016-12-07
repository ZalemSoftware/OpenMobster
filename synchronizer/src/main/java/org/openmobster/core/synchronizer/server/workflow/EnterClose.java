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
import org.openmobster.core.synchronizer.server.SessionManager;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;


/**
 * @author openmobster@gmail.com
 */
public class EnterClose implements ActionHandler 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(EnterClose.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5637962645508771892L;

	/**
	 * 
	 */
	public void execute(ExecutionContext context) throws Exception 
	{	
		Session session = Utilities.getSession(context);
		SyncXMLGenerator syncXMLGenerator = Utilities.getSyncXMLGenerator(context);
		
		//Process SyncCommand Status Messages from the client and clear ChangeLog entries on tcontexthe server side
		Utilities.cleanupChangeLog(context, true);
		
		SyncMessage currentMessage = session.getCurrentMessage();
		SyncMessage serverSyncMessage = new SyncMessage();
		int messageId = Integer.parseInt(currentMessage.getMessageId());
		messageId++;
		int cmdId = 1; //id for first command in this message...keep progressing
		//this as new commands are added to this message
		
		serverSyncMessage.setMessageId(String.valueOf(messageId));		
		
		//Process client's Map information, and send Status related to the Map operation
		if(currentMessage.getRecordMap() != null)
		{
			Status mapStatus = Utilities.processRecordMap(context,cmdId++);					
			serverSyncMessage.getStatus().add(mapStatus);
		}
					
		//Setup Final
		serverSyncMessage.setFinal(true);
		
		session.getServerClosePackage().addMessage(serverSyncMessage);	
		
		String payload = syncXMLGenerator.generateSyncMessage(session, serverSyncMessage);
		Utilities.preparePayload(context, payload);
		
		//Clear the session here from session management...this will save memory usage
		SessionManager.getInstance().cleanup(session);
	}
}
