/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.service.database;

import java.util.Enumeration;
import java.util.Hashtable;

import java.io.Serializable;

/**
 * @author openmobster@gmail.com
 *
 */
public final class Record implements Serializable
{
	private Hashtable state;
	
	public Record()
	{
		
	}	
	public Record(String recordId)
	{		
		this.setRecordId(recordId);
	}
	
	public Record(Hashtable state)
	{
		this.state = state;
	}	
	//---------------------------------------------------------------------------------------------------------------------------------------------
	public String getRecordId()
	{
		return (String)this.getState().get("recordId");
	}
	
	public void setRecordId(String recordId)
	{
		if(recordId == null || recordId.trim().length() == 0)
		{
			throw new IllegalArgumentException("Record Id cannot be empty");
		}
		this.setValue("recordId", recordId);
	}
	
	public void setDirtyStatus(String dirtyStatus)
	{
		this.setValue("dirty", dirtyStatus);
	}
	
	public String getDirtyStatus()
	{
		return this.getValue("dirty");
	}
	
	public void setValue(String name, String value)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Name cannot be Null");
		}
		if(value == null)
		{
			throw new IllegalArgumentException("Value cannot be Null");
		}
		
		this.getState().put(name, value);
	}
	
	public String getValue(String name)
	{
		String value = (String)this.getState().get(name);
		
		if(value != null && value.trim().length() == 0)
		{
			value = null;
		}
		
		return value;
	}
	
	public void removeValue(String name)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Name cannot be Null");
		}
		this.getState().put(name, "");
	}
	
	public Enumeration getNames()
	{
		return this.getState().keys();
	}
	
	public Enumeration getValues()
	{
		return this.getState().elements();
	}
		
	public Hashtable getState()
	{
		if(this.state == null)
		{
			this.state = new Hashtable();
		}
		return this.state;
	}
}
