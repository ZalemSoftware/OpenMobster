/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncAdapterResponse implements Serializable 
{		
	/**
	 * 
	 */
	private int status = 0;
	
	/**
	 * 
	 */
	private Map attributes = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6679116728008767089L;
	
	/**
	 * 
	 *
	 */
	public SyncAdapterResponse()
	{
		this.attributes = new HashMap();
	}
	
	/**
	 * 
	 */
	public Object getAttribute(String name)
	{
		Object value = null;
		
		value = this.attributes.get(name);
		
		return value;
	}
	
	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name,Object value)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Attribute Name cannot be null");
		}
		
		if(value == null)
		{
			this.attributes.remove(name);
			return;
		}
		
		this.attributes.put(name, value);
	}
	
	/**
	 * 
	 * @param name
	 */
	public void removeAttribute(String name)
	{
		this.attributes.remove(name);
	}

	/**
	 * 
	 * @return
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}	
}
