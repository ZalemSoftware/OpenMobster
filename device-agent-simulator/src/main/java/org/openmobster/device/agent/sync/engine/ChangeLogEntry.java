/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync.engine;

import org.openmobster.device.agent.sync.Item;
import org.openmobster.device.agent.service.database.Record;

/**
 * The SyncCacheEntry represents a pointer to a record that must be used during
 * the next synchronization transaction
 * 
 * @author openmobster@gmail.com
 * 
 */
public final class ChangeLogEntry
{	
	private String nodeId;

	private String operation;

	private String recordId;
	
	//not persisted in the database
	private Item item;

	/**
	 * 
	 * 
	 */
	public ChangeLogEntry()
	{

	}
	
	public ChangeLogEntry(Record record)
	{
		this.recordId = record.getRecordId();
		this.nodeId = record.getValue("storageId");
		this.operation = record.getValue("operation");
	}
	
	public Record getRecord()
	{
		Record record = new Record();
		
		if(this.recordId != null && this.recordId.trim().length()>0)
		{
			record.setRecordId(this.recordId);
		}
		
		record.setValue("storageId", this.nodeId);
		record.setValue("operation", this.operation);		
		
		return record;
	}	

	/**
	 * 
	 * @return
	 */
	public String getNodeId()
	{
		return nodeId;
	}

	/**
	 * 
	 * @param nodeId
	 */
	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}

	/**
	 * 
	 * @return
	 */
	public String getOperation()
	{
		return operation;
	}

	/**
	 * 
	 * @param operation
	 */
	public void setOperation(String operation)
	{
		this.operation = operation;
	}

	/**
	 * 
	 * @return
	 */
	public String getRecordId()
	{
		return recordId;
	}

	/**
	 * 
	 * @param recordId
	 */
	public void setRecordId(String recordId)
	{
		this.recordId = recordId;
	}

	public Item getItem()
	{
		return item;
	}

	public void setItem(Item item)
	{
		this.item = item;
	}	
	
	public boolean equals(Object input)
	{
		boolean equals = false;
		
		if(input instanceof ChangeLogEntry)
		{
			ChangeLogEntry cour = (ChangeLogEntry)input;
			
			if(cour.getNodeId() != null && cour.getNodeId().equals(this.nodeId) &&
			   cour.getOperation()!= null && cour.getOperation().equals(this.operation) &&
			   cour.getRecordId() != null && cour.getRecordId().equals(this.recordId)
			)
			{
				equals = true;				
			}
		}
		
		return equals;
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();		
		buffer.append("StorageId="+this.nodeId+"\n");
		buffer.append("RecordId="+this.recordId+"\n");
		buffer.append("Operation="+this.operation+"\n");
		return buffer.toString();
	}
}
