/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.workflow;

import java.security.MessageDigest;

import org.apache.log4j.Logger;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.model.Credential;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.SyncXMLGenerator;
import org.openmobster.core.synchronizer.server.SyncObjectGenerator;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;


/**
 * @author openmobster@gmail.com
 */
public class DecideAuthorization implements DecisionHandler
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(DecideAuthorization.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5637962645508771892L;

	/**
	 * 
	 */
	public String decide(ExecutionContext context) throws Exception 
	{
		//Process Anchors sent by the client
		Session session = Utilities.getSession(context);				
		String payload = null;
		
		//Perform server specific processing
		SyncMessage currentMessage = session.getCurrentMessage();
		int messageId = Integer.parseInt(currentMessage.getMessageId());
		messageId++;
		int cmdId = 1; //id for first command in this message...keep progressing
		//this as new commands are added to this message
		
		String deviceId = session.getDeviceId();
		DeviceController deviceController = (DeviceController)ServiceManager.locate("security://DeviceController");
		Device device = deviceController.read(deviceId);
		
		if(device == null)
		{
			//Send back unauthorized
			payload = Utilities.handleAuthorizationFailure(context, cmdId);
		    Utilities.preparePayload(context, payload);
		    
		    log.error("---------------------------------------------------------------------");
		    log.error("Device Not Activated ["+deviceId+"]");
		    log.error("---------------------------------------------------------------------");
		    
		    return Boolean.FALSE.toString();
		}
		
		String storedNonce = device.readAttribute("nonce").getValue();
								
		//Setup the reply message
		SyncMessage serverInitMessage = new SyncMessage();
		serverInitMessage.setMessageId(String.valueOf(messageId));
		
		//Process the Credential, and make sure the device is allowed
		//to access the system
		Credential credential = currentMessage.getCredential();
		boolean isAllowed = false;
		if(credential != null)
		{
			//Perform authentication of the device before establishing a session
			String authenticationData = credential.getData();			
						
			if(authenticationData != null && 
			   MessageDigest.isEqual(authenticationData.getBytes(), storedNonce.getBytes()))
			{
				isAllowed = true;
				
			}
		}
		
		if(isAllowed)
		{
			/**
			 * Dont generate a new nonce for now.
			 */
			//If I am here, authentication was allowed....Generate a new nonce
			/*MessageDigest digest = MessageDigest.getInstance("SHA-512");
			String knownInput = device.getIdentifier() + device.getIdentity().getPrincipal() + 
			device.getIdentity().getCredential();
			String randomSalt = org.openmobster.core.common.Utilities.generateUID();
			byte[] newNonceBytes = digest.digest((knownInput+randomSalt).getBytes());
			String newNonce = org.openmobster.core.common.Utilities.encodeBinaryData(newNonceBytes);
			device.updateAttribute(new DeviceAttribute("nonce", newNonce));
			deviceController.update(device);*/
		}
		else
		{
			//Send back unauthorized
			payload = Utilities.handleAuthorizationFailure(context, cmdId);
		    Utilities.preparePayload(context, payload);
		    
		    log.error("---------------------------------------------------------------------");
		    log.error("Device Not Authorized ["+deviceId+"]");
		    log.error("---------------------------------------------------------------------");
		}
		
		return Boolean.toString(isAllowed);
	}
}
