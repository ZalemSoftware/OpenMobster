/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync.engine;

import org.openmobster.core.mobileCloud.android.module.sync.Item;
import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * The SyncCacheEntry represents a pointer to a record that must be used during
 * the next synchronization transaction
 * 
 * @author openmobster@gmail.com
 * 
 */
public final class ChangeLogEntry
{
	private String id;
	
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
		this.id = record.getRecordId();
		this.nodeId = record.getValue("storageId");
		this.operation = record.getValue("operation");
		this.recordId = record.getValue("sync-recordId");
	}
	
	public Record getRecord()
	{
		Record record = new Record();
		
		if(this.id != null && this.id.trim().length()>0)
		{
			record.setRecordId(this.id);
		}
		
		record.setValue("storageId", this.nodeId);
		record.setValue("operation", this.operation);
		record.setValue("sync-recordId", this.recordId);
		
		return record;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
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
			
			if(cour.getNodeId().equals(this.nodeId) &&
			   cour.getOperation().equals(this.operation) &&
			   cour.getRecordId().equals(this.recordId)
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
		if(this.id != null && this.id.length() > 0)
		{
			buffer.append("Id="+this.id+"\n");
		}
		buffer.append("StorageId="+this.nodeId+"\n");
		buffer.append("RecordId="+this.recordId+"\n");
		buffer.append("Operation="+this.operation+"\n");
		return buffer.toString();
	}
}
