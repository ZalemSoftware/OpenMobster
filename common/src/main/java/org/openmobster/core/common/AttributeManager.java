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
import java.util.List;
import java.util.ArrayList;

/**
 * @author openmobster@gmail.com
 */
public class AttributeManager implements Serializable
{
	private Map<String, String> attributes;
	
	public AttributeManager()
	{
		this.attributes = new HashMap<String,String>();
	}
	
	public void setAttribute(String name, String value)
	{
		this.attributes.put(name, value);
	}
	
	public String getAttribute(String name)
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
	
	public String[] getValues()
	{
		String[] values = null;
		
		Collection<String> cour = this.attributes.values();
		if(cour != null && !cour.isEmpty())
		{
			values = cour.toArray(new String[0]);
		}
		
		return values;
	}
	
	public void removeAttribute(String name)
	{
		this.attributes.remove(name);
	}
	
	public void setListAttribute(String name, List<String> list)
	{
		if(list != null && !list.isEmpty())
		{
			this.setAttribute(name, ""+list.size());
			int index = 0;
			for(String item: list)
			{
				this.setAttribute(name+"["+(index++)+"]", item);
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
