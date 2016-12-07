/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * @author openmobster@gmail.com
 */
final class GenericAttributeManager
{
	private Hashtable attributes;
	
	GenericAttributeManager()
	{
		this.attributes = new Hashtable();
	}
	
	void setAttribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	Object getAttribute(String name)
	{
		return this.attributes.get(name);
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
	
	Object[] getValues()
	{
		Object[] values = new Object[this.attributes.size()];
		
		
		Enumeration elements = this.attributes.elements();
		int i = 0;
		while(elements.hasMoreElements())
		{
			values[i++] = (Object)elements.nextElement();
		}
		
		return values;
	}	
}
