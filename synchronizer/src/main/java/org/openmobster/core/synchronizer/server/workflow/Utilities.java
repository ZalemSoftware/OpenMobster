/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import org.jbpm.graph.exe.ExecutionContext;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.synchronizer.model.AbstractOperation;
import org.openmobster.core.synchronizer.model.Add;
import org.openmobster.core.synchronizer.model.Alert;
import org.openmobster.core.synchronizer.model.Delete;
import org.openmobster.core.synchronizer.model.MapItem;
import org.openmobster.core.synchronizer.model.RecordMap;
import org.openmobster.core.synchronizer.model.Replace;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncXMLTags;
import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.Item;
import org.openmobster.core.synchronizer.model.Credential;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncContext;
import org.openmobster.core.synchronizer.server.SyncObjectGenerator;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;
import org.openmobster.core.synchronizer.server.VariableConstants;
import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.engine.Anchor;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

/**
 * @author openmobster@gmail.com
 */
public class Utilities 
{
	private static Logger log = Logger.getLogger(Utilities.class);
	
	/**
	 * 
	 * @param context
	 * @param cmdId
	 * @param currentAnchor
	 * @return
	 */
	static String handleAnchorError(ExecutionContext context, int cmdId, Anchor currentAnchor)
	{
		String payload = null;
		Session session = (Session)context.getContextInstance().getTransientVariable("session");
		SyncXMLGenerator syncXMLGenerator = (SyncXMLGenerator)context.getContextInstance().
		getTransientVariable("syncXMLGenerator");
		SyncObjectGenerator syncObjectGenerator = (SyncObjectGenerator)context.getContextInstance().
		getTransientVariable("syncObjectGenerator");
		ServerSyncEngine syncEngine = (ServerSyncEngine)context.getContextInstance().
		getTransientVariable("syncEngine");
		
		//Clear the serverside anchor
		syncEngine.deleteAnchor(currentAnchor.getTarget(), session.getApp());
		
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
		alert.setData(SyncServer.ANCHOR_FAILURE); 
		session.setSyncType(SyncServer.SLOW_SYNC);

		serverInitMessage.addAlert(alert);
		
		
		serverInitMessage.setFinal(true);
		
		session.getServerInitPackage().addMessage(serverInitMessage);
		
		payload = syncXMLGenerator.generateInitMessage(session, serverInitMessage);
		
		return payload;
	}
	
	static String handleAuthorizationFailure(ExecutionContext context, int cmdId)
	{
		String payload = null;
		Session session = (Session)context.getContextInstance().getTransientVariable("session");
		SyncXMLGenerator syncXMLGenerator = (SyncXMLGenerator)context.getContextInstance().
		getTransientVariable("syncXMLGenerator");
		SyncObjectGenerator syncObjectGenerator = (SyncObjectGenerator)context.getContextInstance().
		getTransientVariable("syncObjectGenerator");
		ServerSyncEngine syncEngine = (ServerSyncEngine)context.getContextInstance().
		getTransientVariable("syncEngine");
		
				
		//Perform server specific processing
		SyncMessage currentMessage = session.getCurrentMessage();
		int messageId = Integer.parseInt(currentMessage.getMessageId());
		messageId++;		
		
		//Setup the reply message
		SyncMessage serverInitMessage = new SyncMessage();
		serverInitMessage.setMessageId(String.valueOf(messageId));
		
		
		//SetUp the Authorization Failure status
		Status status = new Status();
		
		status.setCmdId(String.valueOf(cmdId));
		status.setCmd(SyncXMLTags.SyncHdr);
		status.setMsgRef("1");		
		status.setCmdRef("0");		
		status.setData(SyncServer.AUTHENTICATION_FAILURE);
		
		serverInitMessage.addStatus(status);
		
		serverInitMessage.setFinal(true);
		
		session.getServerInitPackage().addMessage(serverInitMessage);
		
		payload = syncXMLGenerator.generateInitMessage(session, serverInitMessage);
		
		return payload;
	}
	
	/**
	 * 
	 * @param alert
	 * @return
	 */
	static Status getStatus(int cmdId, String messageRef, String data,Alert alert)
	{
		Status status = new Status();
		
		status.setCmdId(String.valueOf(cmdId));
		status.setCmd(SyncXMLTags.Alert);
		status.setMsgRef(messageRef);		
		status.setCmdRef(alert.getCmdId());		
		status.setData(data);
		
		return status;
	}
	
	static Status getAuthorizationSuccessStatus(int cmdId, String messageRef, String newNonce)
	{
		Status status = new Status();
		
		status.setCmdId(String.valueOf(cmdId));
		status.setCmd(SyncXMLTags.SyncHdr);
		status.setMsgRef(messageRef);		
		status.setCmdRef("0");		
		status.setData(SyncServer.AUTH_SUCCESS);
		
		//SetUp the NextNonce Credential
		Credential credential = new Credential();
		credential.setType(SyncXMLTags.sycml_auth_sha);
		credential.setNextNonce(newNonce);
		status.setCredential(credential);
		
		return status;
	}
	
	/**
	 * 
	 * @param context
	 */
	static void cleanupChangeLog(ExecutionContext context, boolean isInClosePhase)
	{
		Session session = getSession(context);
		ServerSyncEngine syncEngine = getSyncEngine(context);
		SyncMessage currentMessage = session.getCurrentMessage();		
		
		//If this is a SlowSync ChangeLogs are useless...
		//Blow them away on both sides (server and client)
		if(session.getSyncType().equals(SyncServer.SLOW_SYNC) &&
		   isInClosePhase
		)
		{
			try
			{
				syncEngine.clearChangeLog(SyncContext.getInstance().getDeviceId(),
					SyncContext.getInstance().getServerSource(), SyncContext.getInstance().getApp());
			}
			catch(Exception e)
			{
				//Nothing to do....
				//TODO: log into the ErrorHandling service
				log.error(Utilities.class, e);
			}
			return;
		}
		
		for(int i=0;i<currentMessage.getStatus().size();i++)
		{
			try
			{
				Status cour = (Status)currentMessage.getStatus().get(i);
				if(
						cour.getCmd().equals(SyncXMLTags.Add) ||
						cour.getCmd().equals(SyncXMLTags.Delete) ||
						cour.getCmd().equals(SyncXMLTags.Replace)
				)
				{
					if(
							cour.getData().equals(SyncServer.SUCCESS)
							         ||
							cour.getData().equals(SyncServer.CHUNK_SUCCESS)
					)
					{
						ChangeLogEntry changeLogEntry = null;
						changeLogEntry = session.findLogEntry(cour);
						
						if(changeLogEntry != null)
						{
							if(cour.getData().equals(SyncServer.CHUNK_SUCCESS))
							{
								changeLogEntry.getItem().setData(session.reassembleChunks());
								session.clearChunkBackup();
							}
							syncEngine.clearChangeLogEntry(SyncContext.getInstance().getDeviceId(),
									SyncContext.getInstance().getApp(),
							changeLogEntry);
						}
					}
				}
			}
			catch(Exception e)
			{
				//Nothing to do......
				//TODO: log into the ErrorHandling service
				log.error(Utilities.class, e);
			}
		}
	}
	
	static void processSyncCommands(ExecutionContext context, int cmdId,SyncMessage replyMessage)
	{
		Session session = getSession(context);
		ServerSyncEngine syncEngine = getSyncEngine(context);
		List status = new ArrayList(); 
		SyncMessage currentMessage = session.getCurrentMessage();
		
		if(currentMessage.getSyncCommands().isEmpty())
		{
			//There are no syncCommands to process
			return;
		}
		
		if(!session.getSyncType().equals(SyncServer.SLOW_SYNC))
		{
			for(int i=0;i<currentMessage.getSyncCommands().size();i++)
			{
				SyncCommand syncCommand = (SyncCommand)currentMessage.getSyncCommands().get(i);
				
				Status syncStatus = new Status();
				syncStatus.setCmdId(String.valueOf(cmdId++));
				syncStatus.setCmd(SyncXMLTags.Sync);
				syncStatus.setData(SyncServer.SUCCESS);
				syncStatus.setMsgRef(currentMessage.getMessageId());
				syncStatus.setCmdRef(syncCommand.getCmdId());			
				syncStatus.getSourceRefs().add(syncCommand.getSource());
				syncStatus.getTargetRefs().add(syncCommand.getTarget());
				status.add(syncStatus);
				
				
				List cour = syncEngine.processSyncCommand(session,syncCommand.getTarget(), 
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
				syncStatus.setData(SyncServer.SUCCESS);
				syncStatus.setMsgRef(currentMessage.getMessageId());
				syncStatus.setCmdRef(syncCommand.getCmdId());			
				syncStatus.getSourceRefs().add(syncCommand.getSource());
				syncStatus.getTargetRefs().add(syncCommand.getTarget());
				status.add(syncStatus);
				
				
				List cour = syncEngine.processSlowSyncCommand(session,syncCommand.getTarget(), 
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
	
	static SyncMessage setUpReply(ExecutionContext context)
	{
		Session session = getSession(context);
		SyncMessage syncMessage = new SyncMessage();
				
		SyncMessage currentMessage = session.getCurrentMessage();
		int messageId = Integer.parseInt(currentMessage.getMessageId());
		messageId++;
		
		syncMessage.setMessageId(String.valueOf(messageId));
		
						
		return syncMessage;
	}
	
	static SyncCommand generateSyncCommand(ExecutionContext context,int cmdId, SyncMessage replyMessage)
	{
		Session session = getSession(context);
		ServerSyncEngine syncEngine = getSyncEngine(context);
		SyncCommand syncCommand = new SyncCommand();
		syncCommand.setCmdId(String.valueOf(cmdId++));		
		syncCommand.setSource(session.getDataSource(true));	
		syncCommand.setTarget(session.getDataTarget(true));
		
		try
		{
			if(!session.isOperationCommandStateInitiated())
			{
				if(!session.getSyncType().equals(SyncServer.SLOW_SYNC))
				{
					List addCommands = syncEngine.getAddCommands(session.getMaxClientSize(),syncCommand
							.getSource(), session.getSyncType());
					for (int i = 0; i < addCommands.size(); i++)
					{
						Add cour = (Add) addCommands.get(i);
						cour.setCmdId(String.valueOf(cmdId++));
					}
			
					List replaceCommands = syncEngine.getReplaceCommands(session.getMaxClientSize(),
							syncCommand.getSource(), session.getSyncType());
					for (int i = 0; i < replaceCommands.size(); i++)
					{
						Replace cour = (Replace) replaceCommands.get(i);
						cour.setCmdId(String.valueOf(cmdId++));
					}
			
					List deleteCommands = syncEngine.getDeleteCommands(syncCommand
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
					List slowSyncCommands = syncEngine.getSlowSyncCommands(session.getMaxClientSize(), 
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
		}
		catch(Throwable e)
		{
			//Ignore and keep going.....whatever issue there is will be fixed during the next sync session
			//TODO: log into the ErrorHandling service
			log.error(Utilities.class, e);
		}
		
		int numberOfCommands = calculateNumberOfCommands(session.getMaxClientSize(), session.
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
	
	static int calculateNumberOfCommands(int maxClientSize, List commands)
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
	
	static void setUpSyncFinal(ExecutionContext context, SyncMessage reply,SyncCommand syncCommand)
	{
		Session session = getSession(context);
		ServerSyncEngine syncEngine = getSyncEngine(context);
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
				String allData = syncEngine.marshal(chunkedCommand.getChunkedRecord());
				
				long dataSize = getDataSize(allData);
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
	
	static long getDataSize(String data)
	{
		long dataSize = 0;
		
		//String cleanedUp = XMLUtilities.cleanupXML(data).trim();
		//dataSize = cleanedUp.length();
		dataSize = data.trim().length();
						
		return dataSize;
	}
	
	static boolean containsSyncFinal(List messages)
	{
		boolean containsFinal = false;
		
		for(int i=0;i<messages.size();i++)
		{
			SyncMessage cour = (SyncMessage)messages.get(i);
			if(cour.isFinal())
			{
				containsFinal = true;
				break;
			}
		}
		
		return containsFinal;
	}
	
	static boolean hasErrors(SyncMessage currentMessage)
	{
		boolean hasErrors = false;
		
		//Check for Anchor Failure
		for(int i=0;i<currentMessage.getAlerts().size();i++)
		{
			Alert alert = (Alert)currentMessage.getAlerts().get(i);
			
			if(alert.getData().equals(SyncServer.ANCHOR_FAILURE))
			{
				hasErrors = true;
				break;
			}
		}
		
		return hasErrors;
	}
	
	static boolean isChunkOpen(Session session)
	{
		boolean isChunkOpen = false;
		
		isChunkOpen = session.isChunkOpen();
		
		return isChunkOpen;
	}
	
	static Status processRecordMap(ExecutionContext context, int cmdId)
	{
		Session session = getSession(context);
		ServerSyncEngine syncEngine = getSyncEngine(context);
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
			
			syncEngine.saveRecordMap(session.getDataSource(false),session.getDataTarget(false),
			cour);
		
			mapStatus = setUpRecordMapStatus(cmdId, SyncServer.SUCCESS, 
			currentMessage);		
		}	
		catch(Exception e)
		{
			mapStatus = setUpRecordMapStatus(cmdId, SyncServer.COMMAND_FAILURE, 
			currentMessage);
		}
		return mapStatus;
	}
	
	static Status setUpRecordMapStatus(int cmdId, String status, SyncMessage currentMessage)
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
	
	static void setUpNextChunk(ExecutionContext context, int cmdId,SyncCommand syncCommand,AbstractOperation chunkedCommand)
	{
		Session session = getSession(context);
		
		//Get the next chunk to be sent back
		AbstractOperation nextChunk = getNextChunk(chunkedCommand);
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
	
	static AbstractOperation getNextChunk(AbstractOperation chunkedCommand)
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
	//-------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param context
	 * @return
	 */
	static ServerSyncEngine getSyncEngine(ExecutionContext context)
	{
		return (ServerSyncEngine)context.getContextInstance().
		getTransientVariable(VariableConstants.syncEngine);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	static Session getSession(ExecutionContext context)
	{
		return (Session)context.getContextInstance().
		getTransientVariable(VariableConstants.session);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	static SyncXMLGenerator getSyncXMLGenerator(ExecutionContext context)
	{
		return (SyncXMLGenerator)context.getContextInstance().
		getTransientVariable(VariableConstants.syncXMLGenerator);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	static SyncObjectGenerator getSyncObjectGenerator(ExecutionContext context)
	{
		return (SyncObjectGenerator)context.getContextInstance().
		getTransientVariable(VariableConstants.syncObjectGenerator);
	}
	
	/**
	 * 
	 * @param context
	 * @param payload
	 */
	static void preparePayload(ExecutionContext context, String payload)
	{
		context.getContextInstance().setTransientVariable(VariableConstants.payload, payload);
	}	
}
