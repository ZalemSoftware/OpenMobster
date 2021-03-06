/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync;

import java.util.Vector;

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
	private Vector alerts; //zero or many
	private Vector status; //zero or many
	private Vector syncCommands; //zero or many
	private RecordMap recordMap; //not-required
	private Credential credential;
	
	
	public SyncMessage()
	{
		this.alerts = new Vector();
		this.status = new Vector();
		this.syncCommands = new Vector();
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
	public Vector getAlerts()
	{
		return alerts;
	}

	/**
	 * 
	 * @param alerts
	 */
	public void setAlerts(Vector alerts)
	{
		if(alerts != null)
		{
			this.alerts = alerts;
		}
		else
		{
			this.alerts = new Vector();
		}
	}
	
	/**
	 * 
	 * @return
	 */	
	public Vector getStatus()
	{
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(Vector status)
	{
		if(status != null)
		{
			this.status = status;
		}
		else
		{
			this.status = new Vector();
		}
	}
	
	/**
	 * 
	 * @param alert
	 */
	public void addAlert(Alert alert)
	{
		this.alerts.addElement(alert);
	}
	
	/**
	 * 
	 * @param status
	 */
	public void addStatus(Status status)
	{
		this.status.addElement(status);
	}


	public boolean isClientInitiated()
	{
		return isClientInitiated;
	}


	public void setClientInitiated(boolean isClientInitiated)
	{
		this.isClientInitiated = isClientInitiated;
	}


	public Vector getSyncCommands()
	{
		if(this.syncCommands == null)
		{
			this.syncCommands = new Vector();
		}
		return syncCommands;
	}


	public void setSyncCommands(Vector syncCommands)
	{
		this.syncCommands = syncCommands;
	}
	
	public void addSyncCommand(SyncCommand syncCommand)
	{
		this.getSyncCommands().addElement(syncCommand);
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
