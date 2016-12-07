/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.core.synchronizer.model.Alert;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncServerAdapter;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncServerAdapterWithMapErrorSimulation extends SyncServerAdapter
{
	private static Logger logger = Logger.getLogger(SyncServerAdapterWithMapErrorSimulation.class);
		
	/**
	 * switch for swapping test scenarios
	 */
	public boolean activateDeferMapUpdateToNextSyncFailure = false;
	public boolean activateProcessRecordMapFailure = false;
	
	/**
	 * 
	 *
	 */
	public SyncServerAdapterWithMapErrorSimulation(ServerSyncEngine syncEngine)
	{		
		super();
		this.setServerSyncEngine(syncEngine);
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
			Status mapStatus = null;
			
			if(!this.activateProcessRecordMapFailure)
			{
				mapStatus = this.processRecordMap(cmdId++, session);
			}
			else
			{			
				//UnitTest Simulation Code for:
				//TestMapSupport: testtestDeferMapUpdateToNextSync, testDeferMapUpdateToNextSyncFailure, 
				//and testDeferMapUpdateToNextSyncClientPersistFailure
				mapStatus = this.setUpRecordMapStatus(cmdId, SyncServerAdapter.COMMAND_FAILURE, currentMessage);
			}
			
			
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
			
			Status mapStatus = null;
			
			if(!this.activateDeferMapUpdateToNextSyncFailure)
			{
				mapStatus = this.processRecordMap(1, session);			
				reply.getStatus().add(mapStatus);
			}
			else
			{
				//UnitTest simulation code for
				//TestMapSupport: testDeferMapUpdateToNextSyncFailure
				mapStatus = this.setUpRecordMapStatus(1, SyncServerAdapter.COMMAND_FAILURE,currentMessage);
				reply.getStatus().add(mapStatus);
			}
			
			
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
		
		//
		reply = this.processNormalSync(session);
		
		
		return reply;
	}	
}
