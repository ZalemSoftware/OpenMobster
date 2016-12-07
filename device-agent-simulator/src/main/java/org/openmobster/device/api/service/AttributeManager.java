/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.api.service;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author openmobster@gmail.com
 */
final class AttributeManager
{
	private Hashtable attributes;
	
	AttributeManager()
	{
		this.attributes = new Hashtable();
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
		String[] names = new String[this.attributes.size()];
		
		
		Enumeration keys = this.attributes.keys();
		int i = 0;
		while(keys.hasMoreElements())
		{
			names[i++] = (String)keys.nextElement();
		}
		
		return names;
	}
	
	String[] getValues()
	{
		String[] values = new String[this.attributes.size()];
		
		
		Enumeration elements = this.attributes.elements();
		int i = 0;
		while(elements.hasMoreElements())
		{
			values[i++] = (String)elements.nextElement();
		}
		
		return values;
	}
	
	public void setListAttribute(String name, Vector list)
	{
		if(list != null && !list.isEmpty())
		{
			int size = list.size();
			this.setAttribute(name, ""+size);
			for(int i=0; i<size; i++)
			{
				this.setAttribute(name+"["+(i)+"]", (String)list.elementAt(i));
			}
		}
	}
	
	public Vector getListAttribute(String name)
	{
		Vector attribute = new Vector();
		
		String listMetaData = this.getAttribute(name);
		
		if(listMetaData != null && listMetaData.trim().length()>0)
		{
			int listSize = Integer.parseInt(listMetaData);
			for(int i=0; i<listSize; i++)
			{
				attribute.addElement(this.getAttribute(name+"["+i+"]"));
			}
		}
		
		return attribute;
	}
}
