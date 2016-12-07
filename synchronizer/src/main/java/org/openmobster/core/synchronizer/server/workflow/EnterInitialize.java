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

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.synchronizer.model.Alert;
import org.openmobster.core.synchronizer.model.Item;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;
import org.openmobster.core.synchronizer.server.SyncObjectGenerator;
import org.openmobster.core.synchronizer.server.engine.Anchor;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;
import org.openmobster.core.synchronizer.server.engine.AppToChannelAssociation;



/**
 * @author openmobster@gmail.com
 */
public class EnterInitialize implements ActionHandler 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(EnterInitialize.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5637962645508771892L;

	/**
	 * 
	 */
	public void execute(ExecutionContext context) throws Exception 
	{			
		//Process Anchors sent by the client
		Session session = Utilities.getSession(context);
		ServerSyncEngine syncEngine = Utilities.getSyncEngine(context);
		SyncXMLGenerator syncXMLGenerator = Utilities.getSyncXMLGenerator(context);
		SyncObjectGenerator syncObjectGenerator = Utilities.getSyncObjectGenerator(context);		
		String payload = null;
		boolean anchorError = false;
		
		String deviceId = session.getDeviceId();
		String app = session.getApp();
		String channel = session.getDataSource(false);
		
		//Create the device-app-channel association
		AppToChannelAssociation.associate(new AppToChannelAssociation(deviceId,app,channel));
		
		
		DeviceController deviceController = (DeviceController)ServiceManager.locate("security://DeviceController");
		Device device = deviceController.read(deviceId);
		String storedNonce = device.readAttribute("nonce").getValue();
		
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
		Anchor currentAnchor = null;
		for(int i=0;i<currentMessage.getAlerts().size();i++)
		{
			Alert alert = (Alert)currentMessage.getAlerts().get(i);
			
			//Process Anchor
			Item item = (Item)alert.getItems().get(0);
			String anchorTarget = session.getSource()+"/"+item.getSource();
			currentAnchor = syncEngine.getAnchor(anchorTarget,session.getApp());
			String anchorXml = XMLUtilities.removeCData(item.getMeta());
			Anchor clientAnchor = syncObjectGenerator.parseAnchor(anchorXml);
			clientAnchor.setTarget(session.getSource()+"/"+item.getSource());
			clientAnchor.setApp(session.getApp());
			
			log.debug("**************************************************");
			log.debug("Incoming Target: "+clientAnchor.getTarget());
			log.debug("Incoming LastSync: "+clientAnchor.getLastSync());
			log.debug("Incoming NextSync: "+clientAnchor.getNextSync());
			
			
			
			if(currentAnchor != null)
			{
				log.debug("Active Target: "+currentAnchor.getTarget());
				log.debug("Active LastSync: "+currentAnchor.getLastSync());
				log.debug("Active NextSync: "+currentAnchor.getNextSync());
				
				//Make sure the anchors match
				if(
						!clientAnchor.getTarget().equals(currentAnchor.getTarget()) ||
						!clientAnchor.getLastSync().equals(currentAnchor.getNextSync())
				)
				{										
					//Initiate a 508 error from the client
				    //payload = Utilities.handleAnchorError(context, cmdId,currentAnchor);
				    //Utilities.preparePayload(context, payload);
					anchorError = true;
				}
				else
				{
					currentAnchor.setLastSync(clientAnchor.getLastSync());
					currentAnchor.setNextSync(clientAnchor.getNextSync());
					session.setAnchor(currentAnchor);
					syncEngine.updateAnchor(session.getAnchor());
				}
			}
			else
			{
				//This is the first sync so store this anchor
				session.setAnchor(clientAnchor);
				syncEngine.updateAnchor(session.getAnchor());
			}
			log.debug("Anchor Error: "+anchorError);
			log.debug("**************************************************");
			
			//Setup Status
			Status status = Utilities.getStatus(cmdId++, 
			currentMessage.getMessageId(), SyncServer.SUCCESS, alert);
			serverInitMessage.addStatus(status);
		}
					
		//Handle sync type related exchange
		Alert clientAlert = (Alert)currentMessage.getAlerts().get(0);
		
		if(!anchorError)
		{
			//Anchor match is just fine
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
		}
		else
		{
			//Anchors don't match. 
			//In that case, clear the anchor and issue a boot sync from the client
			//that will initialize the state of the service on the device			
			syncEngine.deleteAnchor(currentAnchor.getTarget(),session.getApp());
			
			Alert serverAlert = new Alert();
			
			serverAlert.setCmdId(String.valueOf(cmdId++));
			
			serverAlert.setData(SyncServer.BOOT_SYNC);
			
			String dataSource = session.getDataTarget(false);
			String dataTarget = session.getDataSource(false);
			Item item = new Item();
			item.setSource(dataSource);
			item.setTarget(dataTarget);
			serverAlert.addItem(item);
			
			serverInitMessage.addAlert(serverAlert);			
			session.setSyncType(SyncServer.BOOT_SYNC);
			
			//Indicate an Anchor Failure
			Alert anchorFailure = new Alert();
			anchorFailure.setCmdId(String.valueOf(cmdId++));			
			anchorFailure.setData(SyncServer.ANCHOR_FAILURE);
			serverInitMessage.addAlert(anchorFailure);
		}
		
		//Process the MaxClientMessageSize here
		if(currentMessage.getMaxClientSize() > 0)
		{
			session.setMaxClientSize(currentMessage.getMaxClientSize());
		}
		
		//If I get here, it means the requesting device is legitimately authorized to perform
		//data synchronization
		//Process a successful authorization status
		Status authSuccess = Utilities.getAuthorizationSuccessStatus(cmdId++, 
		currentMessage.getMessageId(), storedNonce);
		serverInitMessage.addStatus(authSuccess);
		
		//Check and see if this is the final message for this phase from the
		//server end
		serverInitMessage.setFinal(true);		
		
		//Set the message
		session.getServerInitPackage().addMessage(serverInitMessage);		
		payload = syncXMLGenerator.generateInitMessage(session, serverInitMessage);
		Utilities.preparePayload(context, payload);
	}
}
