/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6785245323364319778L;
	
	private String messageId = null; //required
	private int maxClientSize = 0; //normally used by client side message		
	private boolean isFinal = false;
	private boolean isClientInitiated = false; //signifies if this message was initiated by the client or not
	private List alerts = null; //zero or many
	private List status = null; //zero or many
	private List syncCommands = null; //zero or many
	private RecordMap recordMap = null; //not-required
	private Credential credential;
	
	
	public SyncMessage()
	{
		this.alerts = new ArrayList();
		this.status = new ArrayList();
		this.syncCommands = new ArrayList();
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
	public List getAlerts()
	{
		return alerts;
	}

	/**
	 * 
	 * @param alerts
	 */
	public void setAlerts(List alerts)
	{
		if(alerts != null)
		{
			this.alerts = alerts;
		}
		else
		{
			this.alerts = new ArrayList();
		}
	}
	
	/**
	 * 
	 * @return
	 */	
	public List getStatus()
	{
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(List status)
	{
		if(status != null)
		{
			this.status = status;
		}
		else
		{
			this.status = new ArrayList();
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


	public List getSyncCommands()
	{
		if(this.syncCommands == null)
		{
			this.syncCommands = new ArrayList();
		}
		return syncCommands;
	}


	public void setSyncCommands(List syncCommands)
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
