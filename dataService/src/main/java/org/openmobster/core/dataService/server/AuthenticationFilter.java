/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import org.apache.log4j.Logger;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

import java.security.MessageDigest;

import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.core.dataService.Constants;
import org.openmobster.core.dataService.model.AuthCredential;

import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;

import org.openmobster.core.services.CometService;
import org.openmobster.core.services.subscription.SubscriptionManager;
import org.openmobster.core.services.subscription.Subscription;

/**
 * @author openmobster@gmail.com
 */
public class AuthenticationFilter extends IoFilterAdapter
{
	private static Logger log = Logger.getLogger(AuthenticationFilter.class);
	
	private DeviceController deviceController;
	
	public AuthenticationFilter()
	{
		
	}
		
	public DeviceController getDeviceController() 
	{
		return deviceController;
	}

	public void setDeviceController(DeviceController deviceController) 
	{
		this.deviceController = deviceController;
	}
	//------------------------------------------------------------------------------------------------------------------
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception
	{	
		if(!this.skipAuthentication(session))
		{
			if(session.getAttribute(Constants.subscription) == null &&
			   session.getAttribute(Constants.consoleSession) == null
			)
			{	
				//This session is unauthenticated....the payload must have authentication info
				boolean auth = handleAuth(session);
				
				
				if(!auth)
				{
					session.write(Constants.status+"="+401+Constants.endOfStream);			
					return;
				}
			}
		}
		
		//connecting device was successfully authenticated...proceed
		
		//Setup ThreadLocal information
		SubscriptionManager local = (SubscriptionManager)session.getAttribute(Constants.subscription);
		if(local != null)
		{
			Device device = local.getDevice();
			ExecutionContext executionContext = ExecutionContext.getInstance();
			executionContext.setDevice(device);
			
			/*
			 * Estrutura adicionada na versão 2.4-M3.1
			 * Define o token de autenticação contexto de execução atual, o qual pode ser obtido posteriormente nos Channels. 
			 */
			ConnectionRequest request = (ConnectionRequest) session.getAttribute(Constants.request);
			if (request != null) {
				String authenticationToken = request.getAuthenticationToken();
				executionContext.setAuthenticationToken(authenticationToken);
			}
		}
		
		nextFilter.messageReceived(session, message);
	}	
	//-------------------------------------------------------------------------------------------------------------------
	private boolean handleAuth(IoSession session) throws Exception
	{
		log.debug("Authorization--------------------------------------------------------------------------");
		
		String payload = (String)session.getAttribute(Constants.payload);
		ConnectionRequest request = (ConnectionRequest)session.getAttribute(Constants.request);
		AuthCredential authCredential = this.parseAuthCredential(request);
		if(authCredential == null)
		{
			log.debug("AUTHCredential missing: Access Denied");
			return false;
		}
		
		String deviceId = authCredential.getDeviceId();
		log.debug("Device Id: "+deviceId);
		
		//check to make sure this isn't the console...console has its own authentication mechanism
		if(deviceId.startsWith("console:"))
		{
			session.setAttribute(Constants.consoleSession, Boolean.TRUE);
			return true;
		}
		
		
		String nonce = authCredential.getNonce();
		log.debug("Incoming Nonce: "+nonce);
		
		Device device = deviceController.read(deviceId);
		if(device != null && device.getIdentity().isActive())
		{
			String storedNonce = device.readAttribute("nonce").getValue();	
			
			log.debug("Stored Nonce: "+storedNonce);
			
			if(nonce != null && MessageDigest.isEqual(nonce.getBytes(), storedNonce.getBytes()))
			{
				/*MessageDigest digest = MessageDigest.getInstance("SHA-512");
				String knownInput = device.getIdentifier() + device.getIdentity().getPrincipal() + 
				device.getIdentity().getCredential();
				String randomSalt = org.openmobster.core.common.Utilities.generateUID();
				byte[] newNonceBytes = digest.digest((knownInput+randomSalt).getBytes());
				String newNonce = org.openmobster.core.common.Utilities.encodeBinaryData(newNonceBytes);
				device.updateAttribute(new DeviceAttribute("nonce", newNonce));
				deviceController.update(device);
				session.setAttribute(Constants.nextNonce, newNonce);*/
				
				//Start Subscription
				Subscription subscription = new Subscription();
				subscription.setClientId(deviceId);
				SubscriptionManager manager = CometService.getInstance().activateSubscription(subscription);
				session.setAttribute(Constants.subscription, manager);
				return true;
			}
		}
		else
		{
			log.debug("Device: "+device+" is not registered or inactive");
		}
		
		log.debug("Access Denied!!");
		
		log.debug("--------------------------------------------------------------------------");
		
		return false;
	}
	
	private AuthCredential parseAuthCredential(ConnectionRequest request) throws Exception
	{
		if(request == null)
		{
			return null;
		}
		AuthCredential authCredential = new AuthCredential();
		authCredential.setDeviceId(request.getDeviceId());
		authCredential.setNonce(request.getNonce());
		return authCredential;
	}
	
	private boolean skipAuthentication(IoSession session)
	{	
		try
		{
			String payload = (String)session.getAttribute(Constants.payload);
			ConnectionRequest request = (ConnectionRequest)session.getAttribute(Constants.request);
			String forceAuth = (String)session.getAttribute("force-auth");
			
			if(request == null && forceAuth == null)
			{
				//looks like authenticated session is already established...no need for this
				return true;
			}
			
			String deviceId = null;
			String processor = null;
			if(request != null)
			{
				deviceId = request.getDeviceId();
				processor = request.getProcessor();
			}
			
			if(deviceId !=null)
			{
				return false;
			}
			
			//If just a processor is being picked for execution, go ahead
			if(processor != null)
			{
				//Allows testsuite requests
				if(processor.equals("testsuite") || processor.equals("/testdrive/"))
				{
					session.setAttribute(Constants.anonymousMode, Boolean.TRUE);
				}
				
				return true;
			}
			
			
			if(session.getAttribute(Constants.anonymousMode) != null)
			{
				return true;
			}
			
			//Skip based on service in question
			//Provisioning requests should be skipped...Obviously otherwise
			//a device can never get activated (chicken/egg issue)
			//there is no security issue with making this service unprotected
			//In fact eventually there will be a list of protected and unprotected services
			//supported
			if(
			   ( payload.contains("servicename") && payload.contains("provisioning") ) ||
			   ( payload.contains("servicename") && payload.contains("/anonymous/") )
			)
			{
				session.setAttribute(Constants.anonymousMode, Boolean.TRUE);
				
				return true;
			}
					
			
			
			return false;
		}
		finally
		{
			session.removeAttribute("force-auth");
		}
	}
}
