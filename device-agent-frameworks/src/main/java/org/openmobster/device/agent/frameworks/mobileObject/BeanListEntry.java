/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.Hashtable;

/**
 * @author openmobster@gmail.com
 *
 */
public class BeanListEntry 
{
	private Hashtable properties;
	private int index;
	private String listProperty;
	
	public BeanListEntry()
	{
		this(0, new Hashtable());
	}
	public BeanListEntry(int index, Hashtable properties)
	{
		this.properties = properties;
		this.index = index;
	}		
	String getListProperty()
	{
		return this.listProperty;
	}
	void setListProperty(String listProperty)
	{
		this.listProperty = listProperty;
	}
	//Public API-----------------------------------------------------------------------------------------------------------------------
	public String getProperty(String propertyExpression)
	{
		String propertyUri = this.calculatePropertyUri(propertyExpression);		
		return (String)this.properties.get(propertyUri);
	}
	
	public void setProperty(String propertyExpression, String value)
	{
		String propertyUri = this.calculatePropertyUri(propertyExpression);
		this.properties.put(propertyUri, value);
	}
	
	public Hashtable getProperties()
	{		
		return this.properties;
	}
	
	public String getValue()
	{
		if(this.properties.size() == 1)
		{
			String key = (String)this.properties.keys().nextElement();
			if(key.trim().length()==0 || 
			   this.calculatePropertyUri(this.listProperty).endsWith(key.trim()))
			{
				return (String)this.properties.elements().nextElement();
			}
		}
		return null;
	}
	
	public void setValue(String value)
	{
		this.properties.put("", value);
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	private String calculatePropertyUri(String propertyExpression)
	{
		StringBuffer buffer = new StringBuffer();		
		buffer.append("/"+propertyExpression.replace(".", "/"));
		return buffer.toString();
	}
}
