/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security.identity;

import java.io.Serializable;

/**
 * Represents arbitrary information to be associated with the Group 
 * 
 * @author openmobster@gmail.com
 */
public class GroupAttribute implements Serializable
{	
	private static final long serialVersionUID = 4562489853653820996L;

	/**
	 * database uid 
	 */
	private long id;
	
	
	/**
	 * Name
	 */
	private String name;
	
	/**
	 * Value
	 */
	private String value;
	
	
	public GroupAttribute()
	{
		
	}
	
	
	public GroupAttribute(String name, String value)
	{
		this.name = name;
		this.value = value;
	}

	
	public long getId() 
	{
		return id;
	}

	
	public void setId(long id) 
	{
		this.id = id;
	}

	
	public String getName() 
	{
		return name;
	}

	
	public void setName(String name) 
	{
		this.name = name;
	}

	
	public String getValue() 
	{
		return value;
	}

	
	public void setValue(String value) 
	{
		this.value = value;
	}
	
	public boolean equals(Object object)
	{
		boolean equals = false;
		
		if(object instanceof GroupAttribute)
		{
			GroupAttribute input = (GroupAttribute)object;
			if(input.getId() > 0 && this.id > 0)
			{
				if(input.getId() == this.id)
				{
					equals = true;
				}
			}
		}
		
		return equals;
	}
}
