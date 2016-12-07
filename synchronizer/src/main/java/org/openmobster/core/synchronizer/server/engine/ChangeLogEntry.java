/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.engine;

import java.io.Serializable;

import org.openmobster.core.synchronizer.model.Item;


/**
 * The SyncCacheEntry represents a pointer to a record that must be used during
 * the next synchronization transaction
 * 
 * @author openmobster@gmail.com
 * 
 */
public class ChangeLogEntry implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7819088633763769538L;

	private Long id = null;
	
	private String target = null; //indicates the unique device id
		
	private String nodeId = null; //indicates the bean channel

	private String operation = null;

	private String recordId = null;
	
	private String app = null; //indicates the app to which this sync channel belongs
	
	//not persisted in the database
	private Item item = null;

	/**
	 * 
	 * 
	 */
	public ChangeLogEntry()
	{

	}

	/**
	 * 
	 * @return
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Long id)
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
	
	public String getTarget()
	{
		return this.target;
	}
	
	public void setTarget(String target)
	{
		this.target = target;
	}

	public String getApp()
	{
		return app;
	}

	public void setApp(String app)
	{
		this.app = app;
	}
}
