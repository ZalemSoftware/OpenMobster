/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security.device;

import java.io.Serializable;

/**
 * Represents arbitrary information that should be associated with a registered Device
 * 
 * @author openmobster@gmail.com
 */
public class DeviceAttribute implements Serializable
{	
	private static final long serialVersionUID = -5272131151984976199L;


	/**
	 * unique database uid. no domain meaning
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
	
	
	public DeviceAttribute()
	{
		
	}
	
	
	public DeviceAttribute(String name, String value)
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
		
		if(object instanceof DeviceAttribute)
		{
			DeviceAttribute input = (DeviceAttribute)object;
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
