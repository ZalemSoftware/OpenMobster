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

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;


import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.synchronizer.model.Add;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.model.SyncXMLTags;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;
import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

/**
 * @author openmobster@gmail.com
 */
public class EnterStreamSync implements ActionHandler 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(EnterStreamSync.class);
	
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
		ServerSyncEngine syncEngine = Utilities.getSyncEngine(context);
		
		int cmdId = 1; //id for first command in this message...keep progressing
		//this as new commands are added to this message		
		SyncMessage reply = Utilities.setUpReply(context);
		
		
		//Process the incoming message
		SyncMessage currentMessage = session.getCurrentMessage();
		SyncCommand incomingSyncCommand = (SyncCommand)currentMessage.getSyncCommands().get(0);
		String pluginId = incomingSyncCommand.getTarget();
								
		
		//Setup the outgoing message
		SyncCommand syncCommand = new SyncCommand();
		syncCommand.setCmdId(String.valueOf(cmdId++));		
		syncCommand.setSource(session.getDataSource(true));	
		syncCommand.setTarget(session.getDataTarget(true));	
		
		//Get the data that needs to be streamed back as part of the response
		try
		{
			Add stream = syncEngine.getStream(session, pluginId, incomingSyncCommand);
			if(stream != null)
			{
				stream.setCmdId(String.valueOf(cmdId++));
				syncCommand.getAddCommands().add(stream);
			}
		}
		catch(Throwable t)
		{
			//Ignore and keep going.....hopefully whatever issue there is will be fixed during the next sync session
			ErrorHandler.getInstance().handle(t);
			log.error(Utilities.class, t);
		}
				
		//Handle commands that are Long Objects and hence split into
		//smaller manageable chunks of data
		List chunkedCommands = syncCommand.filterChunkedCommands();
		session.setChunkedCommands(chunkedCommands);
		
		//Provide Status of processing the incoming message
		List status = new ArrayList();
		Status syncStatus = new Status();
		syncStatus.setCmdId(String.valueOf(cmdId++));
		syncStatus.setCmd(SyncXMLTags.Sync);
		syncStatus.setData(SyncServer.SUCCESS);
		syncStatus.setMsgRef(currentMessage.getMessageId());
		syncStatus.setCmdRef(syncCommand.getCmdId());			
		syncStatus.getSourceRefs().add(syncCommand.getSource());
		syncStatus.getTargetRefs().add(syncCommand.getTarget());
		status.add(syncStatus);
		
		//Setup the reply to be sent back
		reply.getStatus().addAll(status);		
		reply.addSyncCommand(syncCommand);
				
		//Check to see if this is the last message of this phase from server end
		/**
		 * handle MoreData/non-final usecase based on the message size that 
		 * can be accepted by the client or some other criteria
		 */
		Utilities.setUpSyncFinal(context, reply, syncCommand);
		
		session.getServerSyncPackage().addMessage(reply);
		Utilities.preparePayload(context,
		syncXMLGenerator.generateSyncMessage(session, reply));
	}
}
