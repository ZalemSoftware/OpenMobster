/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openmobster.core.synchronizer.SyncException;
import org.openmobster.core.synchronizer.model.*;
import org.openmobster.core.synchronizer.server.engine.Anchor;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

import org.openmobster.core.common.XMLUtilities;

/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncServerAdapter implements SyncServer
{
	private static Logger logger = Logger.getLogger(SyncServerAdapter.class);
		
	/**
	 * objects helping out
	 */
	protected PhaseManager phaseManager = null;
	protected SessionManager sessionManager = null;
	protected SyncObjectGenerator syncObjectGenerator = null;
	protected SyncXMLGenerator syncXMLGenerator = null;
	protected ServerSyncEngine syncEngine = null;
	
	/**
	 * 
	 *
	 */
	public SyncServerAdapter()
	{
		super();
		
		this.phaseManager = new PhaseManager();
		this.sessionManager = new SessionManager();
		this.syncObjectGenerator = new SyncObjectGenerator();
		this.syncXMLGenerator = new SyncXMLGenerator();		
	}
	
	//-----Microcontainer management related methods--------------------------------------------------------------
	/**
	 * 
	 *
	 */
	public void start()
	{
		
	}
	
	/**
	 * 
	 *
	 */
	public void stop()
	{
		
	}
	
	/**
	 * 
	 * @return
	 */
	public ServerSyncEngine getServerSyncEngine() 
	{
		return syncEngine;
	}

	/**
	 * 
	 * @param syncEngine
	 */
	public void setServerSyncEngine(ServerSyncEngine syncEngine) 
	{
		this.syncEngine = syncEngine;
	}
	//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param request
	 */
	public SyncAdapterResponse service(SyncAdapterRequest request) throws SyncException
	{		
		try
		{
			SyncAdapterResponse response = new SyncAdapterResponse();
			
			//Grab the payload
			String payload = (String)request.getAttribute(SyncServerAdapter.PAYLOAD);
			
			//Turn xml into object representation
			Session localSession = this.syncObjectGenerator.parseCurrentSyncMessage(payload);
			SyncMessage localMessage = localSession.getCurrentMessage();
			
			
			//Perform SessionManagement
			if(this.sessionManager.findSession(localSession.getSessionId()) == null)
			{
				//This establishes the session
				this.sessionManager.saveSession(localSession);											
			}
							
			//Process localMessage and update session data accordingly
			Session session = this.sessionManager.findSession(localSession.getSessionId());
			session.setCurrentMessage(localMessage);
			
			//Pre-process phase management
			this.phaseManager.processPhase(this, session);
			
			//Place session as a Thread Local object to be consumed by other
			//components in the call stack
			SyncContext.getInstance().setSession(session);
			
			switch(session.getPhaseCode())
			{
				case SyncServerAdapter.PHASE_INIT:				
					String initPayload = this.init(session);
					response.setAttribute(SyncServerAdapter.PAYLOAD, initPayload);
				break;
				
				case SyncServerAdapter.PHASE_SYNC:
					String syncPayload = this.sync(session);
					response.setAttribute(SyncServerAdapter.PAYLOAD, syncPayload);
				break;
				
				case SyncServerAdapter.PHASE_CLOSE:
					String closePayload = this.close(session);
					response.setAttribute(SyncServerAdapter.PAYLOAD, closePayload);
				break;
				
				case SyncServerAdapter.PHASE_END:
					this.end(session);
					response.setStatus(SyncServerAdapter.RESPONSE_CLOSE);
				break;
			}
			
			return response;
		}
		catch(Exception e)
		{
			logger.error(this, e);
			throw new SyncException(e);
		}
	}	
	//----------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	protected String init(Session session)
	{
		String payload = null;
				
		//Perform server specific processing
		SyncMessage currentMessage = session.getCurrentMessage();
		int messageId = Integer.parseInt(currentMessage.getMessageId());
		messageId++;
		int cmdId = 1; //id for first command in this message...keep progressing
		//this as new commands are added to this message
		
		//Setup the reply message
		SyncMessage serverInitMessage = new SyncMessage();
		serverInitMessage.setMessageId(String.valueOf(messageId));			
		
		//Setup status and Process the Anchor
		for(int i=0;i<currentMessage.getAlerts().size();i++)
		{
			Alert alert = (Alert)currentMessage.getAlerts().get(i);
			
			//Process Anchor
			Item item = (Item)alert.getItems().get(0);			
			Anchor currentAnchor = this.syncEngine.getAnchor(session.getSource()+"/"+item.getSource(),session.getApp());
			String anchorXml = XMLUtilities.removeCData(item.getMeta());
			Anchor clientAnchor = this.syncObjectGenerator.parseAnchor(anchorXml);
			clientAnchor.setTarget(session.getSource()+"/"+item.getSource());
			clientAnchor.setApp(session.getApp());
			if(currentAnchor != null)
			{				
				//Make sure the anchors match
				if(
						!clientAnchor.getTarget().equals(currentAnchor.getTarget()) ||
						!clientAnchor.getLastSync().equals(currentAnchor.getNextSync())
				)
				{										
					//Initiate a 508 slow sync from the client
				    payload = this.handleAnchorError(cmdId,currentAnchor,session);
				    return payload;
				}
				else
				{
					currentAnchor.setLastSync(clientAnchor.getLastSync());
					currentAnchor.setNextSync(clientAnchor.getNextSync());
					session.setAnchor(currentAnchor);
					this.syncEngine.updateAnchor(session.getAnchor());
				}
			}
			else
			{
				//This is the first sync so store this anchor
				session.setAnchor(clientAnchor);
				this.syncEngine.updateAnchor(session.getAnchor());
			}
			
			//Setup Status
			Status status = this.getStatus(cmdId++, 
			currentMessage.getMessageId(), SyncServerAdapter.SUCCESS, alert);
			serverInitMessage.addStatus(status);
		}
					
		//Handle sync type related exchange
		Alert clientAlert = (Alert)currentMessage.getAlerts().get(0);
		Alert serverAlert = new Alert();
		serverAlert.setCmdId(String.valueOf(cmdId++));
		serverAlert.setData(clientAlert.getData());
		String dataSource = session.getDataTarget(false);
		String dataTarget = session.getDataSource(false);
		Item item = new Item();
		item.setSource(dataSource);
		item.setTarget(dataTarget);
		serverAlert.addItem(item);
		serverInitMessage.addAlert(serverAlert);
		session.setSyncType(serverAlert.getData());
		
		//Process the MaxClientMessageSize here
		if(currentMessage.getMaxClientSize() > 0)
		{
			session.setMaxClientSize(currentMessage.getMaxClientSize());
		}
		
		//Check and see if this is the final message for this phase from the
		//server end
		serverInitMessage.setFinal(true);
		
		//Set the message
		session.getServerInitPackage().addMessage(serverInitMessage);
		
		payload = this.syncXMLGenerator.generateInitMessage(session, serverInitMessage);
				
		return payload;
	}
	
	/**
	 * 
	 * @param session
	 */
	protected String sync(Session session)
	{
		String payload = null;
		
		SyncMessage currentMessage = session.getCurrentMessage();
		
		SyncMessage syncReply = this.handleSyncScenarios(session);
		session.getServerSyncPackage().addMessage(syncReply);
		payload = this.syncXMLGenerator.generateSyncMessage(session, syncReply);
		
						
		return payload;
	}
	
	/**
	 * 
	 * @param session
	 */
	protected String close(Session session)
	{
		String payload = null;
		
		//Process SyncCommand Status Messages from the client and clear ChangeLog entries on the server side
		this.cleanupChangeLog(session);
		
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
			Status mapStatus = this.processRecordMap(cmdId++, session);							
			
			//UnitTest Simulation Code for:
			//TestMapSupport: testtestDeferMapUpdateToNextSync, testDeferMapUpdateToNextSyncFailure, 
			//and testDeferMapUpdateToNextSyncClientPersistFailure
			//Status mapStatus = this.setUpRecordMapStatus(cmdId, SyncServerAdapter.COMMAND_FAILURE, currentMessage);
			
			
			serverSyncMessage.getStatus().add(mapStatus);
		}
					
		//Setup Final
		serverSyncMessage.setFinal(true);
		
		session.getServerClosePackage().addMessage(serverSyncMessage);	
		
		//Persist the anchor for the synchronization
		//this.syncEngine.updateAnchor(session.getAnchor());
		
		payload = this.syncXMLGenerator.generateSyncMessage(session, serverSyncMessage);
		
		return payload;
	}
	
	/**
	 * 
	 */
	protected void end(Session session)
	{		
	}
	//-------Handlers-------------------------------------------------------------------------------		
	/**
	 * 
	 * @param session
	 * @return
	 */
	protected SyncMessage handleSyncScenarios(Session session)
	{	
		SyncMessage reply = null;
		
			
		SyncMessage currentMessage = session.getCurrentMessage();
		
		//Handle the scenario where Map update from the previous sync
		//is being sent in by the client
		if(currentMessage.getRecordMap() != null)
		{
			reply = this.setUpReply(session);
			
			Status mapStatus = this.processRecordMap(1, session);			
			reply.getStatus().add(mapStatus);			
			
			//UnitTest simulation code for
			//TestMapSupport: testDeferMapUpdateToNextSyncFailure
			//Status mapStatus = this.setUpRecordMapStatus(1, SyncServerAdapter.COMMAND_FAILURE,currentMessage);
			//reply.getStatus().add(mapStatus);
			
			
			reply.setFinal(true);			
			if(mapStatus.getData().equals(SyncServerAdapter.SUCCESS))
			{
				session.setMapExchangeInProgress(true);
			}
			else
			{
				session.setMapExchangeInProgress(false);
			}
						
			
			return reply;
		}
		
		session.setMapExchangeInProgress(false);
		
		
		//Process Command Status to clear up the ChangeLog
		this.cleanupChangeLog(session);
		
		//Process other Status/Scenarios
		List statusCodes = new ArrayList();
		// Check Status messages and make sure everything is ok
		for (int i = 0; i < currentMessage.getStatus().size(); i++)
		{
			Status status = (Status) currentMessage.getStatus().get(i);
			if (!status.getData().equals(SyncServerAdapter.SUCCESS))
			{
				// some issue happened....perform error handling
				statusCodes.add(status);
			}
		}
		
		//Start handling out of the ordinary scenarios
		for(int i=0;i<statusCodes.size();i++)
		{
			Status status = (Status)statusCodes.get(i);
			
			if(status.getData().equals(SyncServerAdapter.CHUNK_ACCEPTED))
			{
				reply = this.sendNextChunk(session, status);					
				return reply;
			}
			else if(status.getData().equals(SyncServerAdapter.CHUNK_SUCCESS) || 
					status.getData().equals(SyncServerAdapter.SIZE_MISMATCH))
			{
				reply = this.closeChunkSending(session,statusCodes);
				return reply;
			}
			/*else if(status.getData().equals(SyncServerAdapter.SIZE_MISMATCH))
			{
				reply = this.handleChunkSizeMismatch(session, status);
				return reply;
			}*/
		}
		
		//Start handling Alerts
		List alerts = currentMessage.getAlerts();
		for(int i=0;i<alerts.size();i++)
		{
			Alert alert = (Alert)alerts.get(i);
			
			if(alert.getData().equals(SyncServerAdapter.NEXT_MESSAGE))
			{
				reply = this.sendNextChunk(session);
				return reply;
			}
		}
		
		//Handle STREAM Sync
		if(session.getSyncType().equals(SyncServerAdapter.STREAM))
		{
			reply = this.processStreamSync(session);
			return reply;
		}
		
		
		//
		reply = this.processNormalSync(session);
		
		
		return reply;
	}
	
	/**
	 * This performs the normal sync scenario. The Happy Path
	 * @param session
	 * @return
	 */
	protected SyncMessage processNormalSync(Session session)
	{
		int cmdId = 1; //id for first command in this message...keep progressing
		//this as new commands are added to this message
		SyncMessage reply = this.setUpReply(session);
				
		//Send status on successfull client modifications synced up with the server
		/**
		 * Consume the data changes by passing to the synchronization
		 * engine
		 */
		this.processSyncCommands(cmdId, session, reply);	
		
		SyncCommand syncCommand = null;
		if(session.getSyncType().equals(SyncServerAdapter.TWO_WAY) || 
		   session.getSyncType().equals(SyncServerAdapter.SLOW_SYNC) ||
		   session.getSyncType().equals(SyncServerAdapter.ONE_WAY_SERVER)
		)
		{
			//Send back sync commands
			/**
			 * get this information by performing synchronization with engine
			 */
			syncCommand = this.generateSyncCommand(cmdId,session,reply);
		}
				
				
		//Check to see if this is the last message of this phase from server end
		/**
		 * handle MoreData/non-final usecase based on the message size that 
		 * can be accepted by the client or some other criteria
		 */
		this.setUpSyncFinal(session, reply, syncCommand);
		
		
		return reply;
	}
	
	/**
	 * This performs the stream sync scenario. The Happy Path
	 * @param session
	 * @return
	 */
	protected SyncMessage processStreamSync(Session session)
	{
		int cmdId = 1; //id for first command in this message...keep progressing
		//this as new commands are added to this message		
		SyncMessage reply = this.setUpReply(session);
		
		
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
		Add stream = this.syncEngine.getStream(session, pluginId, incomingSyncCommand);
		if(stream != null)
		{
			stream.setCmdId(String.valueOf(cmdId++));
			syncCommand.getAddCommands().add(stream);
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
		syncStatus.setData(SyncServerAdapter.SUCCESS);
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
		this.setUpSyncFinal(session, reply, syncCommand);
		
		
		return reply;
	}
	
	/**
	 * 
	 *
	 */
	protected SyncCommand generateSyncCommand(int cmdId,Session session, SyncMessage replyMessage)
	{
		SyncCommand syncCommand = new SyncCommand();
		syncCommand.setCmdId(String.valueOf(cmdId++));		
		syncCommand.setSource(session.getDataSource(true));	
		syncCommand.setTarget(session.getDataTarget(true));
		
		if(!session.isOperationCommandStateInitiated())
		{
			if(!session.getSyncType().equals(SyncServerAdapter.SLOW_SYNC))
			{
				List addCommands = this.syncEngine.getAddCommands(session.getMaxClientSize(),syncCommand
						.getSource(), session.getSyncType());
				for (int i = 0; i < addCommands.size(); i++)
				{
					Add cour = (Add) addCommands.get(i);
					cour.setCmdId(String.valueOf(cmdId++));
				}
		
				List replaceCommands = this.syncEngine.getReplaceCommands(session.getMaxClientSize(),
						syncCommand.getSource(), session.getSyncType());
				for (int i = 0; i < replaceCommands.size(); i++)
				{
					Replace cour = (Replace) replaceCommands.get(i);
					cour.setCmdId(String.valueOf(cmdId++));
				}
		
				List deleteCommands = this.syncEngine.getDeleteCommands(syncCommand
						.getSource(), session.getSyncType());
				for (int i = 0; i < deleteCommands.size(); i++)
				{
					Delete cour = (Delete) deleteCommands.get(i);
					cour.setCmdId(String.valueOf(cmdId++));
				}
				
				//Add operation commands	
				session.clearOperationCommandState();
				session.initiateOperationCommandState();
				session.getAllOperationCommands().addAll(addCommands);
				session.getAllOperationCommands().addAll(replaceCommands);
				session.getAllOperationCommands().addAll(deleteCommands);
			}
			else
			{
				List slowSyncCommands = this.syncEngine.getSlowSyncCommands(session.getMaxClientSize(), 
				syncCommand.getSource());
				for(int i=0; i<slowSyncCommands.size(); i++)
				{
					AbstractOperation cour = (AbstractOperation)slowSyncCommands.get(i);
					cour.setCmdId(String.valueOf(cmdId++));
				}
				
				session.clearOperationCommandState();
				session.initiateOperationCommandState();
				session.getAllOperationCommands().addAll(slowSyncCommands);				
			}
		}
		
		int numberOfCommands = this.calculateNumberOfCommands(session.getMaxClientSize(), session.
		getAllOperationCommands()); 
				
		for(int i=0;i<numberOfCommands;i++)
		{
			int commandIndex = session.getOperationCommandIndex();
			if(commandIndex<session.getAllOperationCommands().size())
			{
				AbstractOperation op = (AbstractOperation)session.getAllOperationCommands().get(commandIndex);
				commandIndex++;
				session.setOperationCommandIndex(commandIndex);
				
				if(op instanceof Add)
				{
					syncCommand.getAddCommands().add(op);
				}
				else if(op instanceof Replace)
				{
					syncCommand.getReplaceCommands().add(op);
				}
				else if(op instanceof Delete)
				{
					syncCommand.getDeleteCommands().add(op);
				}
				
				if(commandIndex == session.getAllOperationCommands().size())
				{
					session.clearOperationCommandState();
				}
			}
			else
			{
				session.clearOperationCommandState();
				break;
			}
		}
		
		
		//Handle commands that are Long Objects and hence split into
		//smaller manageable chunks of data
		List chunkedCommands = syncCommand.filterChunkedCommands();
		session.setChunkedCommands(chunkedCommands);
				
		
		replyMessage.addSyncCommand(syncCommand);
		
		return syncCommand;
	}
	
	/**
	 * 
	 * @param currentMessage
	 * @return
	 */
	protected void processSyncCommands(int cmdId,Session session, SyncMessage replyMessage)
	{
		List status = new ArrayList(); 
		SyncMessage currentMessage = session.getCurrentMessage();
		
		if(currentMessage.getSyncCommands().isEmpty())
		{
			//There are no syncCommands to process
			return;
		}
		
		if(!session.getSyncType().equals(SyncServerAdapter.SLOW_SYNC))
		{
			for(int i=0;i<currentMessage.getSyncCommands().size();i++)
			{
				SyncCommand syncCommand = (SyncCommand)currentMessage.getSyncCommands().get(i);
				
				Status syncStatus = new Status();
				syncStatus.setCmdId(String.valueOf(cmdId++));
				syncStatus.setCmd(SyncXMLTags.Sync);
				syncStatus.setData(SyncServerAdapter.SUCCESS);
				syncStatus.setMsgRef(currentMessage.getMessageId());
				syncStatus.setCmdRef(syncCommand.getCmdId());			
				syncStatus.getSourceRefs().add(syncCommand.getSource());
				syncStatus.getTargetRefs().add(syncCommand.getTarget());
				status.add(syncStatus);
				
				
				List cour = this.syncEngine.processSyncCommand(session,syncCommand.getTarget(), 
				syncCommand);
				
				//Process the Status
				for(int j=0;j<cour.size();j++)
				{
					Status courStatus = (Status)cour.get(j);
					courStatus.setCmdId(String.valueOf(cmdId++));
					courStatus.setMsgRef(currentMessage.getMessageId());
					status.add(courStatus);
				}
				
				replyMessage.getStatus().addAll(status);
			}
		}
		else
		{
			for(int i=0;i<currentMessage.getSyncCommands().size();i++)
			{
				SyncCommand syncCommand = (SyncCommand)currentMessage.getSyncCommands().get(i);
				
				Status syncStatus = new Status();
				syncStatus.setCmdId(String.valueOf(cmdId++));
				syncStatus.setCmd(SyncXMLTags.Sync);
				syncStatus.setData(SyncServerAdapter.SUCCESS);
				syncStatus.setMsgRef(currentMessage.getMessageId());
				syncStatus.setCmdRef(syncCommand.getCmdId());			
				syncStatus.getSourceRefs().add(syncCommand.getSource());
				syncStatus.getTargetRefs().add(syncCommand.getTarget());
				status.add(syncStatus);
				
				
				List cour = this.syncEngine.processSlowSyncCommand(session,syncCommand.getTarget(), 
				syncCommand);
				
				//Process the Status
				for(int j=0;j<cour.size();j++)
				{
					Status courStatus = (Status)cour.get(j);
					courStatus.setCmdId(String.valueOf(cmdId++));
					courStatus.setMsgRef(currentMessage.getMessageId());
					status.add(courStatus);
				}
				
				replyMessage.getStatus().addAll(status);
			}
		}
	}
	
	/**
	 * 
	 * @param maxClientSize
	 * @param commands
	 * @return
	 */
	protected int calculateNumberOfCommands(int maxClientSize, List commands)
	{
		int numberOfCommands = 0;
		
		int numberOfCommandsAfterFiltering = 0;
		for(int i=0; i<commands.size(); i++)
		{
			AbstractOperation command = (AbstractOperation)commands.get(i);
			
			if(!(command instanceof Delete) && !command.isChunked())
			{
				numberOfCommandsAfterFiltering++;
			}
		}
		
		long sizeCounter = 0;
		for(int i=0;i<commands.size();i++)
		{
			AbstractOperation command = (AbstractOperation)commands.get(i);
			
			if(command instanceof Delete || command.isChunked())
			{
				continue;
			}
			
			Item item = (Item)command.getItems().get(0);
			if((sizeCounter + item.getData().length()) <= maxClientSize)
			{
				sizeCounter += item.getData().length();
				numberOfCommands ++;
			}
			else
			{
				//MaxClientSize exceeded
				break;
			}
		}
		
		if(numberOfCommands == 0 || numberOfCommands == numberOfCommandsAfterFiltering)
		{
			//All commands are chunked or there are no add,replace commands
			//in this case include all commands for sending back
			//or all the commands including Delete can fit
			numberOfCommands = commands.size();
		}
		
		return numberOfCommands;
	}	
	//-------Long Object Support-----------------------------------------------------------------------
	/**
	 * 
	 * @param session
	 * @param status
	 */
	protected SyncMessage sendNextChunk(Session session,Status status)
	{
		SyncMessage reply = null;
		
		reply = this.setUpReply(session);
		int cmdId = 1;
		
		//Find the Command that is chunked
		AbstractOperation chunkedCommand = session.findOperationCommand(status);
		SyncCommand syncCommand = new SyncCommand();
		syncCommand.setCmdId(String.valueOf(cmdId++));		
		syncCommand.setSource(session.getDataSource(true));	
		syncCommand.setTarget(session.getDataTarget(true));
		
		//SetUp the next chunk to be sent back
		this.setUpNextChunk(cmdId, session, syncCommand, chunkedCommand);
				
		reply.addSyncCommand(syncCommand);
		
		this.setUpSyncFinal(session, reply, syncCommand);
		
		return reply;
	}
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	protected SyncMessage sendNextChunk(Session session)
	{
		SyncMessage reply = null;
		
		reply = this.setUpReply(session);
		int cmdId = 1;
		
		//Find the Command that is chunked
		AbstractOperation chunkedCommand = session.getChunkSource();
		SyncCommand syncCommand = new SyncCommand();
		syncCommand.setCmdId(String.valueOf(cmdId++));		
		syncCommand.setSource(session.getDataSource(true));	
		syncCommand.setTarget(session.getDataTarget(true));
		
		//SetUp the next chunk to be sent back
		this.setUpNextChunk(cmdId, session, syncCommand, chunkedCommand);
				
				
		reply.addSyncCommand(syncCommand);
		
		this.setUpSyncFinal(session, reply, syncCommand);
		
		return reply;
	}
	
			
	/**
	 * 
	 * @param session
	 * @return
	 */
	protected SyncMessage closeChunkSending(Session session,List statusCodes)
	{
		SyncMessage reply = this.setUpReply(session);
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
			if(status.getData().equals(SyncServerAdapter.CHUNK_ACCEPTED))
			{												
				//Find the Command that is chunked
				AbstractOperation chunkedCommand = session.getChunkSource();				
				
				//SetUp the next chunk to be sent back
				this.setUpNextChunk(cmdId, session, syncCommand, chunkedCommand);				
				
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
		
		this.setUpSyncFinal(session, reply, syncCommand);
		
		return reply;
	}
	
	
	/**
	 * 
	 * @param chunkedCommand
	 * @return
	 * @throws Exception
	 */
	protected AbstractOperation getNextChunk(AbstractOperation chunkedCommand)
	{
		try
		{
			AbstractOperation nextChunk = (AbstractOperation)chunkedCommand.clone();
			nextChunk.getChunkedObject().processNextChunk(nextChunk);
			return nextChunk;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param syncCommand
	 * @param chunkedCommand
	 */
	protected void setUpNextChunk(int cmdId, Session session,SyncCommand syncCommand,AbstractOperation chunkedCommand)
	{
		//Get the next chunk to be sent back
		AbstractOperation nextChunk = this.getNextChunk(chunkedCommand);
		nextChunk.setCmdId(String.valueOf(cmdId++));
		
		if(nextChunk.isChunked())
		{
			syncCommand.addChunkedCommand(nextChunk);
		}
		else
		{
			syncCommand.addOperationCommand(nextChunk);
			
			//This is the last chunk to be sent for this chunked command
			nextChunk.setMeta(null);
			session.saveChunkState(nextChunk);
		}
	}		
	//------------Map Support----------------------------------------------------------------------------
	/**
	 * 
	 * @param cmdId
	 * @param session
	 * @return
	 */
	protected Status processRecordMap(int cmdId,Session session)
	{
		Status mapStatus = null;
		SyncMessage currentMessage = session.getCurrentMessage();
		try
		{
			RecordMap recordMap = currentMessage.getRecordMap();
			Map cour = new HashMap();
			for(int i=0; i<recordMap.getMapItems().size();i++)
			{
				MapItem mapItem = (MapItem)recordMap.getMapItems().get(i);
				cour.put(mapItem.getTarget(), mapItem.getSource());
			}
			
			this.syncEngine.saveRecordMap(session.getDataSource(false),session.getDataTarget(false),
			cour);
		
			mapStatus = this.setUpRecordMapStatus(cmdId, SyncServerAdapter.SUCCESS, 
			currentMessage);		
		}	
		catch(Exception e)
		{
			mapStatus = this.setUpRecordMapStatus(cmdId, SyncServerAdapter.COMMAND_FAILURE, 
			currentMessage);
		}
		return mapStatus;
	}
	
		
	/**
	 * 
	 * @param cmdId
	 * @param status
	 * @param currentMessage
	 * @return
	 */
	protected Status setUpRecordMapStatus(int cmdId, String status, SyncMessage currentMessage)
	{
		Status mapStatus = new Status();
		mapStatus.setCmdId(String.valueOf(cmdId));
		mapStatus.setData(status);
		mapStatus.setMsgRef(currentMessage.getMessageId());
		mapStatus.setCmdRef(currentMessage.getRecordMap().getCmdId());
		mapStatus.setCmd(SyncXMLTags.Map);
		mapStatus.getSourceRefs().add(currentMessage.getRecordMap().getSource());
		mapStatus.getTargetRefs().add(currentMessage.getRecordMap().getTarget());
		return mapStatus;
	}
	//--------ChangeLog Support---------------------------------------------------------------------------
	/**
	 * 
	 * @param status
	 */
	protected void cleanupChangeLog(Session session)
	{
		SyncMessage currentMessage = session.getCurrentMessage();		
		
		//If this is a SlowSync ChangeLogs are useless...
		//Blow them away on both sides (server and client)
		if(session.getSyncType().equals(SyncServerAdapter.SLOW_SYNC) &&
		   session.getPhaseCode() == SyncServerAdapter.PHASE_CLOSE
		)
		{
			this.syncEngine.clearChangeLog(SyncContext.getInstance().getDeviceId(),
			SyncContext.getInstance().getServerSource(), SyncContext.getInstance().getApp());
			return;
		}
		
		for(int i=0;i<currentMessage.getStatus().size();i++)
		{
			Status cour = (Status)currentMessage.getStatus().get(i);
			if(
					cour.getCmd().equals(SyncXMLTags.Add) ||
					cour.getCmd().equals(SyncXMLTags.Delete) ||
					cour.getCmd().equals(SyncXMLTags.Replace)
			)
			{
				if(
						cour.getData().equals(SyncServerAdapter.SUCCESS)
						         ||
						cour.getData().equals(SyncServerAdapter.CHUNK_SUCCESS)
				)
				{
					ChangeLogEntry changeLogEntry = null;
					changeLogEntry = session.findLogEntry(cour);
					
					if(changeLogEntry != null)
					{
						if(cour.getData().equals(SyncServerAdapter.CHUNK_SUCCESS))
						{
							changeLogEntry.getItem().setData(session.reassembleChunks());
							session.clearChunkBackup();
						}
						this.syncEngine.clearChangeLogEntry(SyncContext.getInstance().getDeviceId(),
								SyncContext.getInstance().getApp(),
						changeLogEntry);
					}
				}
			}
		}
	}
	//-----------Helper methods----------------------------------------------------------------------------	
	/**
	 * 
	 * @param alert
	 * @return
	 */
	protected Status getStatus(int cmdId, String messageRef, String data,Alert alert)
	{
		Status status = new Status();
		
		status.setCmdId(String.valueOf(cmdId));
		status.setCmd(SyncXMLTags.Alert);
		status.setMsgRef(messageRef);		
		status.setCmdRef(alert.getCmdId());		
		status.setData(data);
		
		return status;
	}
	
		
	/**
	 * 
	 * @param session
	 * @return
	 */
	protected SyncMessage setUpReply(Session session)
	{
		SyncMessage syncMessage = new SyncMessage();
				
		SyncMessage currentMessage = session.getCurrentMessage();
		int messageId = Integer.parseInt(currentMessage.getMessageId());
		messageId++;
		
		syncMessage.setMessageId(String.valueOf(messageId));
		
						
		return syncMessage;
	}
	
	/**
	 * 
	 * @param session
	 * @param syncCommand
	 */
	protected void setUpSyncFinal(Session session,SyncMessage reply,SyncCommand syncCommand)
	{
		List sessionChunkedCommands = session.getChunkedCommands();
		List syncChunkedCommands = new ArrayList();
		if(syncCommand != null)
		{
			syncChunkedCommands = syncCommand.getChunkedCommands();
		}
		if(sessionChunkedCommands.isEmpty() && syncChunkedCommands.isEmpty())
		{
			//Here make sure all the operation commands to be sent are
			//done
			if(!session.isOperationCommandStateActive())
			{
				reply.setFinal(true);
			}			
		}
		else
		{
			if(syncChunkedCommands.isEmpty())
			{
				/**
				 * This block marks the origination of a brand new ChunkedCommand processing
				 */
				//Place one of the queued chunked commands onto the response being sent back								
				AbstractOperation chunkedCommand = (AbstractOperation)sessionChunkedCommands.get(0);
				
				//Make a backup of the chunks for the command being closed
				//This is used when doing logCleanup on this chunkedCommand
				if(session.isChunkOpen())
				{
					session.performChunkBackup();
				}
				
				//Some cleanup
				session.getChunkedCommands().remove(chunkedCommand);
				session.clearChunkState();
				
				//Setup the Size Meta Data of the Total Data Size being sent
				//String allData = ((SyncRecordSupportsLongObject)chunkedCommand.getChunkedRecord()).getData();
				String allData = this.syncEngine.marshal(chunkedCommand.getChunkedRecord());
				
				long dataSize = this.getDataSize(allData);
				chunkedCommand.setMeta("<Size>"+String.valueOf(dataSize)+"</Size>");
				
				//Chunk state setup
				syncCommand.addChunkedCommand(chunkedCommand);
				session.saveChunkState(chunkedCommand);									
			}
			else
			{
				AbstractOperation chunkedCommand = (AbstractOperation)syncChunkedCommands.get(0); 
				
				chunkedCommand.setMeta(null);
				session.setChunkSource(chunkedCommand);
				session.saveChunkState(chunkedCommand);
			}
						
			reply.setFinal(false);
		}
	}
	
				
	/**
	 * 
	 * @param data
	 * @return
	 */
	protected long getDataSize(String data)
	{
		long dataSize = 0;
				
		dataSize = XMLUtilities.cleanupXML(data).length();
						
		return dataSize;
	}
	
	/**
	 * 
	 */
	protected String handleAnchorError(int cmdId, Anchor currentAnchor,Session session)
	{
		String payload = null;
		
		//Clear the serverside anchor
		this.syncEngine.deleteAnchor(currentAnchor.getTarget(),session.getApp());
		
		//Perform server specific processing
		SyncMessage currentMessage = session.getCurrentMessage();
		int messageId = Integer.parseInt(currentMessage.getMessageId());
		messageId++;		
		
		//Setup the reply message
		SyncMessage serverInitMessage = new SyncMessage();
		serverInitMessage.setMessageId(String.valueOf(messageId));
		
		
		//Add SlowSync Alert				
		Alert alert = new Alert();
		alert.setCmdId(String.valueOf(cmdId++));
		alert.setData(SyncServerAdapter.ANCHOR_FAILURE); 
		session.setSyncType(SyncServerAdapter.SLOW_SYNC);

		serverInitMessage.addAlert(alert);
		
		
		serverInitMessage.setFinal(true);
		
		session.getServerInitPackage().addMessage(serverInitMessage);
		
		payload = this.syncXMLGenerator.generateInitMessage(session, serverInitMessage);
		
		return payload;
	}	
}
