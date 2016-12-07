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


import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.model.AbstractOperation;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;
import org.openmobster.core.synchronizer.server.SyncServer;

/**
 * @author openmobster@gmail.com
 */
public class EnterNormalSync implements ActionHandler 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(EnterNormalSync.class);
	
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
		
		int cmdId = 1; //id for first command in this message...keep progressing
		//this as new commands are added to this message
		SyncMessage reply = Utilities.setUpReply(context);
		
		SyncMessage currentMessage = session.getCurrentMessage();
		if(!currentMessage.isFinal())
		{
			session.setMultiSyncActive(true);
		}
		else
		{
			session.setMultiSyncActive(false);
		}
				
		//Send status on successfull client modifications synced up with the server
		/**
		 * Consume the data changes by passing to the synchronization
		 * engine
		 */
		Utilities.processSyncCommands(context, cmdId, reply);	
		
		SyncCommand syncCommand = session.getSyncCommand();
		if(session.getSyncType().equals(SyncServer.TWO_WAY) || 
		   session.getSyncType().equals(SyncServer.ONE_WAY_SERVER)
		)
		{
			//Send back sync commands
			/**
			 * get this information by performing synchronization with engine
			 */
			if(syncCommand == null)
			{
				syncCommand = Utilities.generateSyncCommand(context,cmdId,reply);
				
				session.setSyncCommands(syncCommand.getAllCommands());
				syncCommand.clear();
				session.setSyncCommand(syncCommand);
			}
			else
			{
				syncCommand.clear();
			}
			
			int numOfCommands = this.calculateNumberOfCommands(session);
			
			if(session.isOperationSyncActive())
			{
				syncCommand = session.getSyncCommand();
				syncCommand.clear();
				for(int i=0; i<numOfCommands; i++)
				{
					AbstractOperation object = session.getNextOperation();
					if(object == null)
					{
						break;
					}
					syncCommand.addOperationCommand(object);
				}
			}
			
			reply.getSyncCommands().clear();
			reply.addSyncCommand(syncCommand);
		}
		else if(session.getSyncType().equals(SyncServer.SLOW_SYNC))
		{
			syncCommand = Utilities.generateSyncCommand(context,cmdId,reply);
		}
		
				
				
		//Check to see if this is the last message of this phase from server end
		/**
		 * handle MoreData/non-final usecase based on the message size that 
		 * can be accepted by the client or some other criteria
		 */
		Utilities.setUpSyncFinal(context, reply, syncCommand);
		if(session.getSyncType().equals(SyncServer.TWO_WAY) || 
		   session.getSyncType().equals(SyncServer.ONE_WAY_SERVER)
		)
		{
			if(session.isOperationSyncFinished())
			{
				reply.setFinal(true);
			}
			else
			{
				reply.setFinal(false);
			}
		}
		
		session.getServerSyncPackage().addMessage(reply);
		Utilities.preparePayload(context,
		syncXMLGenerator.generateSyncMessage(session, reply));
	}
	
	private int calculateNumberOfCommands(Session session)
	{
		if(session.isSnapShotSizeSet())
		{
			return session.getSnapshotSize();
		}
		
		int numberOfCommands = SyncConstants.SNAPSHOT_SIZE;
		
		List operations = session.getSyncCommands();
		if(operations != null)
		{	
			int totalSize = 0;
			for(Object local:operations)
			{
				AbstractOperation operation = (AbstractOperation)local;
				totalSize += operation.totalSize();
			}
			
			if(totalSize > 100000)
			{
				numberOfCommands = 1;
			}
		}
		
		session.setSnapshotSize(numberOfCommands);
		
		return numberOfCommands;
	}
}
