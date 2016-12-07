/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import java.util.List;

import android.content.Context;

import org.openmobster.android.utils.OpenMobsterBugUtils;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;

import android.util.Log;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class PhaseManager
{	
	/**
	 * 
	 * @param session
	 * @return
	 */
	public void processPhase(SyncAdapter syncAdapter,Session session)
	{
		//Place the current Message in its proper allocation
		if(session.getPhaseCode() == SyncAdapter.PHASE_INIT)
		{
			session.getServerInitPackage().addMessage(session.getCurrentMessage());
		}
		else if(session.getPhaseCode() == SyncAdapter.PHASE_SYNC)
		{
			session.getServerSyncPackage().addMessage(session.getCurrentMessage());
		}
		else if(session.getPhaseCode() == SyncAdapter.PHASE_CLOSE)
		{
			session.getServerClosePackage().addMessage(session.getCurrentMessage());
		}
		
		//Perform proper phase calculation/Phase Update
		if(session.getPhaseCode() == SyncAdapter.PHASE_INIT)
		{
			if(!this.isAuthorized(session))
			{
				session.setPhaseCode(SyncAdapter.PHASE_END);
				return;
			}
			
			//Check if this needs advancement to SYNC phase			
			if(this.containsFinal(session.getClientInitPackage().getMessages()) && 
			   this.containsFinal(session.getServerInitPackage().getMessages()) &&
			   this.containsInitSuccess(session)			    
			)
			{
				//Process the NextNonce
				this.processNextNonce(session);
				
				if(this.hasErrors(session.getCurrentMessage()))
				{
					//Log colocado na versão 2.4-M3.1 do OpenMobster.
					if (OpenMobsterBugUtils.getInstance().isPersistentChannel(session.getSource())) {
						Log.e("OpenMobster Error", String.format("An error ocurred in the synchronization! The local data of the following channel will not be affected: %s/%s.", session.getSource(), session.getTarget()));
					}
					
					//If Anchor Fails, the service data needs to be rebooted
					session.setSyncType(SyncAdapter.BOOT_SYNC);
				}
				
				session.setPhaseCode(SyncAdapter.PHASE_SYNC);
			}
		}
		else if(session.getPhaseCode() == SyncAdapter.PHASE_SYNC)
		{			
			/**
			 * Map Support
			 */
			if(!session.isMapExchangeInProgress())
			{
				if(this.containsSyncFinal(session.getClientSyncPackage().getMessages()) && 
				   this.containsSyncFinal(session.getServerSyncPackage().getMessages()) &&
				   !this.isChunkOpen(session) &&
				   !this.hasErrors(session.getCurrentMessage())
				)
				{
					session.setPhaseCode(SyncAdapter.PHASE_CLOSE);
				}
			}
		}
		else if(session.getPhaseCode() == SyncAdapter.PHASE_CLOSE)
		{
			//Check if this needs advancement to the CLOSE phase
			if(this.containsFinal(session.getClientClosePackage().getMessages()) && 
			   this.containsFinal(session.getServerClosePackage().getMessages()) &&
			   !this.hasErrors(session.getCurrentMessage())
			)
			{
				session.setPhaseCode(SyncAdapter.PHASE_END);
			}
		}
	}	
	
	/**
	 * 
	 * @param messages
	 * @return
	 */
	private boolean containsFinal(List<SyncMessage> messages)
	{
		boolean containsFinal = false;
		
		for(SyncMessage cour:messages)
		{
			if(cour.isFinal())
			{
				containsFinal = true;
				break;
			}
		}
		
		return containsFinal;
	}
	
	/**
	 * 
	 * @param messages
	 * @return
	 */
	private boolean containsSyncFinal(List<SyncMessage> messages)
	{
		boolean containsFinal = false;
		
		for(SyncMessage cour:messages)
		{
			if(cour.isFinal())
			{
				containsFinal = true;
				break;
			}
		}
		
		return containsFinal;
	}
	
	/**
	 * 
	 * @param currentMessage
	 * @return
	 */
	private boolean hasErrors(SyncMessage currentMessage)
	{
		boolean hasErrors = false;
		
		//Check for Anchor Failure
		List<Alert> alerts = currentMessage.getAlerts();
		for(Alert alert:alerts)
		{
			if(alert.getData().equals(SyncAdapter.ANCHOR_FAILURE))
			{
				hasErrors = true;
				break;
			}
		}
		
		return hasErrors;
	}
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	private boolean containsInitSuccess(Session session)
	{
		boolean containsInitSuccess = false;
		
		List<SyncMessage> messages = session.getServerInitPackage().getMessages();
		for(SyncMessage serverMessage:messages)
		{
			List<Status> statuses = serverMessage.getStatus();
			for(Status status:statuses)
			{
				if(status.getData().equals(SyncAdapter.SUCCESS))
				{
					containsInitSuccess = true;
				}
			}
		}
		
		return containsInitSuccess;
	}
	
	private boolean isAuthorized(Session session)
	{
		boolean isAuthorized = true;
		
		List<SyncMessage> messages = session.getServerInitPackage().getMessages();
		for(SyncMessage serverMessage:messages)
		{
			List<Status> statuses = serverMessage.getStatus();
			for(Status status:statuses)
			{
				if(status.getData().equals(SyncAdapter.AUTHENTICATION_FAILURE))
				{
					return false;
				}
			}
		}
		
		return isAuthorized;
	}
	
	private void processNextNonce(Session session)
	{		
		Credential credential = null;
		List<SyncMessage> messages = session.getServerInitPackage().getMessages();
		for(SyncMessage serverMessage:messages)
		{
			List<Status> statuses = serverMessage.getStatus();
			for(Status status:statuses)
			{
				if(status.getCredential() != null)
				{
					credential = status.getCredential();
					break;
				}
			}
		}
		
		if(credential != null)
		{
			//Update the Authentication Nonce of the device
			Context context = Registry.getActiveInstance().getContext();
			Configuration configuration = Configuration.getInstance(context);
			configuration.setAuthenticationNonce(credential.getNextNonce());
			configuration.save(context);
		}
	}
	
	/**
	 * Long Object support
	 *  
	 * @param session
	 * @return
	 */
	private boolean isChunkOpen(Session session)
	{
		return session.isChunkOpen();		
	}
}
