/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncAdapterRequest 
{
	/**
	 * 
	 */
	private Map<String, Object> attributes;
	
	/**
	 * 
	 *
	 */
	public SyncAdapterRequest()
	{
		this.attributes = new HashMap<String, Object>();
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
}
