/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.rpc;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author openmobster@gmail.com
 */
final class AttributeManager
{
	private Map<String,String> attributes;
	
	AttributeManager()
	{
		this.attributes = new HashMap<String,String>();
	}
	
	void setAttribute(String name, String value)
	{
		this.attributes.put(name, value);
	}
	
	String getAttribute(String name)
	{
		return (String)this.attributes.get(name);
	}
	
	void removeAttribute(String name)
	{
		this.attributes.remove(name);
	}
	
	String[] getNames()
	{
		Set<String> keys = this.attributes.keySet();
		return keys.toArray(new String[0]);
	}
	
	String[] getValues()
	{
		Collection<String> values = this.attributes.values();
		return values.toArray(new String[0]);
	}
	
	public void setListAttribute(String name, List<String> list)
	{
		if(list != null && !list.isEmpty())
		{
			int size = list.size();
			this.setAttribute(name, ""+size);
			int i = 0;
			for(String cour:list)
			{
				this.setAttribute(name+"["+(i++)+"]", cour);
			}
		}
	}
	
	public List<String> getListAttribute(String name)
	{
		List<String> attribute = new ArrayList<String>();
		
		String listMetaData = this.getAttribute(name);
		
		if(listMetaData != null && listMetaData.trim().length()>0)
		{
			int listSize = Integer.parseInt(listMetaData);
			for(int i=0; i<listSize; i++)
			{
				attribute.add(this.getAttribute(name+"["+i+"]"));
			}
		}
		
		return attribute;
	}
}
