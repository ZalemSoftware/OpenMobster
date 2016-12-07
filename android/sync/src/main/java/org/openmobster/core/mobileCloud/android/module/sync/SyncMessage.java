/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncMessage
{
	/**
	 * 
	 */
	private String messageId; //required
	private int maxClientSize; //normally used by client side message		
	private boolean isFinal;
	private boolean isClientInitiated; //signifies if this message was initiated by the client or not
	private List<Alert> alerts; //zero or many
	private List<Status> status; //zero or many
	private List<SyncCommand> syncCommands; //zero or many
	private RecordMap recordMap; //not-required
	private Credential credential;
	
	
	public SyncMessage()
	{
		this.alerts = new ArrayList<Alert>();
		this.status = new ArrayList<Status>();
		this.syncCommands = new ArrayList<SyncCommand>();
	}


	public int getMaxClientSize()
	{
		return maxClientSize;
	}


	public void setMaxClientSize(int maxClientSize)
	{
		this.maxClientSize = maxClientSize;
	}


	public String getMessageId()
	{
		return messageId;
	}


	public void setMessageId(String messageId)
	{
		this.messageId = messageId;
	}

	public boolean isFinal()
	{
		return isFinal;
	}


	public void setFinal(boolean isFinal)
	{
		this.isFinal = isFinal;
	}	
	
	/**
	 * 
	 * @return
	 */
	public List<Alert> getAlerts()
	{
		return alerts;
	}

	/**
	 * 
	 * @param alerts
	 */
	public void setAlerts(List<Alert> alerts)
	{
		if(alerts != null)
		{
			this.alerts = alerts;
		}
		else
		{
			this.alerts = new ArrayList<Alert>();
		}
	}
	
	/**
	 * 
	 * @return
	 */	
	public List<Status> getStatus()
	{
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(List<Status> status)
	{
		if(status != null)
		{
			this.status = status;
		}
		else
		{
			this.status = new ArrayList<Status>();
		}
	}
	
	/**
	 * 
	 * @param alert
	 */
	public void addAlert(Alert alert)
	{
		this.alerts.add(alert);
	}
	
	/**
	 * 
	 * @param status
	 */
	public void addStatus(Status status)
	{
		this.status.add(status);
	}


	public boolean isClientInitiated()
	{
		return isClientInitiated;
	}


	public void setClientInitiated(boolean isClientInitiated)
	{
		this.isClientInitiated = isClientInitiated;
	}


	public List<SyncCommand> getSyncCommands()
	{
		if(this.syncCommands == null)
		{
			this.syncCommands = new ArrayList<SyncCommand>();
		}
		return syncCommands;
	}


	public void setSyncCommands(List<SyncCommand> syncCommands)
	{
		this.syncCommands = syncCommands;
	}
	
	public void addSyncCommand(SyncCommand syncCommand)
	{
		this.getSyncCommands().add(syncCommand);
	}

	/**
	 * 
	 * @return
	 */
	public RecordMap getRecordMap()
	{
		return recordMap;
	}

	/**
	 * 
	 * @param recordMap
	 */
	public void setRecordMap(RecordMap recordMap)
	{
		this.recordMap = recordMap;
	}


	public Credential getCredential() 
	{
		return credential;
	}


	public void setCredential(Credential credential) 
	{
		this.credential = credential;
	}	
}
