/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.util;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Collection;

/**
 * @author openmobster@gmail.com
 */
public final class GenericAttributeManager
{
	private Map<String, Object> attributes;
	
	public GenericAttributeManager()
	{
		this.attributes = new HashMap<String, Object>();
	}
	
	public void setAttribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	public void removeAttribute(String name)
	{
		this.attributes.remove(name);
	}
	
	public String[] getNames()
	{
		String[] names = new String[this.attributes.size()];
		
		Set<String> keys = this.attributes.keySet();
		names = keys.toArray(new String[0]);
		
		return names;
	}
	
	public Object[] getValues()
	{
		Object[] values = new Object[this.attributes.size()];
		
		Collection<Object> cour = this.attributes.values();
		values = cour.toArray();
		
		return values;
	}
	
	public boolean isEmpty()
	{
		if(this.attributes == null || this.attributes.isEmpty())
		{
			return true;
		}
		return false;
	}
	
	public void clear()
	{
		this.attributes.clear();
	}
}
