/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.service.database;

/**
 * @author openmobster@gmail.com
 */
public abstract class AbstractTable 
{
	private long id;
	private Record record;
	
	public AbstractTable()
	{
		
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public Record getRecord() 
	{
		return record;
	}

	public void setRecord(Record record) 
	{
		this.record = record;
	}
}
