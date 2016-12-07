/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author openmobster@gmail.com
 */
public class InVMAttributeManager implements Serializable
{
	private Map<String, Object> attributes;
	
	public InVMAttributeManager()
	{
		this.attributes = new HashMap<String,Object>();
	}
	
	public void setAttribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	public String[] getNames()
	{
		String[] names = null;
		Set<String> keys = this.attributes.keySet();
		if(keys != null && !keys.isEmpty())
		{
			names = keys.toArray(new String[0]);
		}
		return names;
	}
	
	public Object[] getValues()
	{
		Object[] values = null;
		
		Collection<Object> cour = this.attributes.values();
		if(cour != null && !cour.isEmpty())
		{
			values = cour.toArray();
		}
		
		return values;
	}
	
	public void removeAttribute(String name)
	{
		this.attributes.remove(name);
	}		
}
