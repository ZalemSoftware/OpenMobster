/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;


/**
 * @author openmobster@gmail.com
 */
final class Field 
{	
	private String uri;
	private String name;
	private String value;
	
	
	Field()
	{
		
	}
	
	
	Field(String uri, String name, String value)
	{
		this.uri = uri;
		this.name = name;
		this.value = value;		
	}
	
	
	void setName(String name) 
	{
		this.name = name;
	}



	void setUri(String uri)
	{
		this.uri = uri;
	}
	
	
	String getUri()
	{
		return this.uri;
	}

	
	String getName() 
	{
		return name;
	}

	
	String getValue() 
	{
		return value;
	}
		
	void setValue(String value)
	{
		this.value = value;
	}
	
	public boolean equals(Object obj) 
	{
		if(obj instanceof Field)
		{
			Field local = (Field)obj;
			String uri = local.getUri();
			String name = local.getName();
			String value = local.getValue();
			if(uri.equals(this.uri) &&
			   name.equals(this.name) &&
			   value.equals(this.value)
			)
			{
				//System.out.println("Matched(Uri): "+this.uri);
				//System.out.println("Matched(Name): "+this.name);
				//System.out.println("Matched(Value): "+this.value);
				//System.out.println("----------------------------------");
				return true;
			}
		}
		
		return false;
	}		
}
