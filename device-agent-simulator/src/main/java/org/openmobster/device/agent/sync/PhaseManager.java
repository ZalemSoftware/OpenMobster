/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync;

import java.util.Vector;

import org.openmobster.device.agent.configuration.Configuration;

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
				
				//Check for Anchor Failure
				if(this.hasErrors(session.getCurrentMessage()))
				{
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
	private boolean containsFinal(Vector messages)
	{
		boolean containsFinal = false;
		
		for(int i=0, size=messages.size();i<size;i++)
		{
			SyncMessage cour = (SyncMessage)messages.elementAt(i);
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
	private boolean containsSyncFinal(Vector messages)
	{
		boolean containsFinal = false;
		
		for(int i=0, size=messages.size();i<size;i++)
		{
			SyncMessage cour = (SyncMessage)messages.elementAt(i);
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
		Vector alerts = currentMessage.getAlerts();
		for(int i=0, size=alerts.size();i<size;i++)
		{
			Alert alert = (Alert)alerts.elementAt(i);
			
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
		
		Vector messages = session.getServerInitPackage().getMessages();
		for(int i=0, size=messages.size();i<size;i++)
		{
			SyncMessage serverMessage = (SyncMessage)messages.elementAt(i);
			Vector statuses = serverMessage.getStatus();
			for(int j=0, jSize=statuses.size();j<jSize;j++)
			{
				Status status = (Status)statuses.elementAt(j);
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
		
		Vector messages = session.getServerInitPackage().getMessages();
		for(int i=0, size=messages.size();i<size;i++)
		{
			SyncMessage serverMessage = (SyncMessage)messages.elementAt(i);
			Vector statuses = serverMessage.getStatus();
			for(int j=0, jSize=statuses.size();j<jSize;j++)
			{
				Status status = (Status)statuses.elementAt(j);
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
		Vector messages = session.getServerInitPackage().getMessages();
		for(int i=0, size=messages.size();i<size;i++)
		{
			SyncMessage serverMessage = (SyncMessage)messages.elementAt(i);
			Vector statuses = serverMessage.getStatus();
			for(int j=0, jSize=statuses.size();j<jSize;j++)
			{
				Status status = (Status)statuses.elementAt(j);
				if(status.getCredential() != null)
				{
					credential = status.getCredential();
					break;
				}
			}
		}
		
		if(credential != null)
		{
			//Strictly simulation related code for security integration with the synchronization process
			Configuration.getInstance().setAuthenticationNonce(credential.getNextNonce());
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
