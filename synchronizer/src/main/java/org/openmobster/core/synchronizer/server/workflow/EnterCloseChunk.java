/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.workflow;

import java.util.List;
import org.apache.log4j.Logger;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.openmobster.core.synchronizer.model.AbstractOperation;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;
import org.openmobster.core.synchronizer.server.VariableConstants;


/**
 * @author openmobster@gmail.com
 */
public class EnterCloseChunk implements ActionHandler 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(EnterCloseChunk.class);
	
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
		List statusCodes = (List)context.getContextInstance().
		getTransientVariable(VariableConstants.statusCodes);
		SyncXMLGenerator syncXMLGenerator = Utilities.getSyncXMLGenerator(context);
		SyncMessage reply = Utilities.setUpReply(context);
		int cmdId = 1;
				
		SyncCommand syncCommand = new SyncCommand();
		syncCommand.setCmdId(String.valueOf(cmdId++));		
		syncCommand.setSource(session.getDataSource(true));	
		syncCommand.setTarget(session.getDataTarget(true));
		
		
		//Check and make sure another chunked command does not need to be processed
		boolean cleanup = true;
		for(int i=0;i<statusCodes.size();i++)
		{
			Status status = (Status)statusCodes.get(i);			
			if(status.getData().equals(SyncServer.CHUNK_ACCEPTED))
			{												
				//Find the Command that is chunked
				AbstractOperation chunkedCommand = session.getChunkSource();				
				
				//SetUp the next chunk to be sent back
				Utilities.setUpNextChunk(context, cmdId, syncCommand, chunkedCommand);				
				
				cleanup = false;
				
				break;
			}
		}
				
		//This is executed when all chunkedCommands that are part of this session
		//have been executed
		if(cleanup)
		{
			session.clearChunkState();
		}
		
		reply.addSyncCommand(syncCommand);		
		Utilities.setUpSyncFinal(context, reply, syncCommand);
		
		session.getServerSyncPackage().addMessage(reply);		
		String payload = syncXMLGenerator.generateSyncMessage(session, reply);
		Utilities.preparePayload(context, payload);
	}
}
