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
import org.openmobster.core.synchronizer.model.AbstractOperation;
import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;


/**
 * @author openmobster@gmail.com
 */
public class EnterNextMessage implements ActionHandler 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(EnterNextMessage.class);
	
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
		int cmdId = 1;
		
		//Find the Command that is chunked
		AbstractOperation chunkedCommand = session.getChunkSource();
		SyncCommand syncCommand = new SyncCommand();
		syncCommand.setCmdId(String.valueOf(cmdId++));		
		syncCommand.setSource(session.getDataSource(true));	
		syncCommand.setTarget(session.getDataTarget(true));
		
		//SetUp the next chunk to be sent back
		Utilities.setUpNextChunk(context, cmdId, syncCommand, chunkedCommand);
				
				
		reply.addSyncCommand(syncCommand);
		
		Utilities.setUpSyncFinal(context, reply, syncCommand);
		
		session.getServerSyncPackage().addMessage(reply);		
		String payload = syncXMLGenerator.generateSyncMessage(session, reply);
		Utilities.preparePayload(context, payload);
	}
}
