/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import java.util.List;

import org.openmobster.core.synchronizer.model.Alert;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncMessage;


/**
 * 
 * @author openmobster@gmail.com
 */
public class PhaseManager
{
	/**
	 * 
	 * @param session
	 * @return
	 */
	public void processPhase(SyncServer syncAdapter,Session session)
	{
		//Place the current Message in its proper allocation
		if(session.getPhaseCode() == SyncServer.PHASE_INIT)
		{
			session.getClientInitPackage().addMessage(session.getCurrentMessage());
		}
		else if(session.getPhaseCode() == SyncServer.PHASE_SYNC)
		{
			session.getClientSyncPackage().addMessage(session.getCurrentMessage());
		}
		else if(session.getPhaseCode() == SyncServer.PHASE_CLOSE)
		{
			session.getClientClosePackage().addMessage(session.getCurrentMessage());
		}
		
		//Perform proper phase calculation/Phase Update
		if(session.getPhaseCode() == SyncServer.PHASE_INIT)
		{
			//Check if this needs advancement to SYNC phase			
			if(this.containsFinal(session.getClientInitPackage().getMessages()) && 
			   this.containsFinal(session.getServerInitPackage().getMessages()) &&
			   this.containsInitSuccess(session) &&
			   !this.hasErrors(session.getCurrentMessage()) 
			)
			{
				session.setPhaseCode(SyncServer.PHASE_SYNC);
			}
		}
		else if(session.getPhaseCode() == SyncServer.PHASE_SYNC)
		{
			//Check if this needs advancement to the CLOSE phase
			if(!session.isMapExchangeInProgress())
			{
				if(this.containsSyncFinal(session.getClientSyncPackage().getMessages()) && 
				   this.containsSyncFinal(session.getServerSyncPackage().getMessages()) &&
				   !this.isChunkOpen(session) && 
				   !this.hasErrors(session.getCurrentMessage())
				)
				{
					session.setPhaseCode(SyncServer.PHASE_CLOSE);
				}
			}
		}
		else if(session.getPhaseCode() == SyncServer.PHASE_CLOSE)
		{
			//Check if this needs advancement to the CLOSE phase
			if(this.containsFinal(session.getClientClosePackage().getMessages()) && 
			   this.containsFinal(session.getServerClosePackage().getMessages()) &&
			   !this.hasErrors(session.getCurrentMessage())
			)
			{
				session.setPhaseCode(SyncServer.PHASE_END);
			}
		}
	}	
	
	/**
	 * 
	 * @param messages
	 * @return
	 */
	private boolean containsFinal(List messages)
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
	
	/**
	 * 
	 * @param messages
	 * @return
	 */
	private boolean containsSyncFinal(List messages)
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
	
	/**
	 * 
	 * @param currentMessage
	 * @return
	 */
	private boolean hasErrors(SyncMessage currentMessage)
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
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	private boolean containsInitSuccess(Session session)
	{
		boolean containsInitSuccess = false;
		
		String syncType = session.getSyncType();
		for(int i=0;i<session.getServerInitPackage().getMessages().size();i++)
		{
			SyncMessage serverMessage = (SyncMessage)session.getServerInitPackage().getMessages().get(i);
			for(int j=0;j<serverMessage.getStatus().size();j++)
			{
				Status status = (Status)serverMessage.getStatus().get(j);
				if(status.getData().equals(SyncServer.SUCCESS))
				{
					containsInitSuccess = true;
				}
			}
		}
		
		return containsInitSuccess;
	}
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	private boolean isChunkOpen(Session session)
	{
		boolean isChunkOpen = false;
		
		isChunkOpen = session.isChunkOpen();
		
		return isChunkOpen;
	}
}
