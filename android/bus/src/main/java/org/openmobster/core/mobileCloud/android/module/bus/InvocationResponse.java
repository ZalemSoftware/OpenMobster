/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Map;
import java.util.HashMap;

/**
 * @author openmobster@gmail.com
 *
 */
public final class InvocationResponse 
{	
	public static String returnValue = "returnValue";
	
	private Map<String,String> response;
	
	public InvocationResponse()
	{
	}	
		
	public void setValue(String name, String value)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Name cannot be Null");
		}
		if(value == null)
		{
			value = "";
		}
		
		this.getResponse().put(name, value);
	}
	
	public String getValue(String name)
	{
		String value = this.getResponse().get(name);
		
		if(value != null && value.trim().length() == 0)
		{
			value = null;
		}
		
		return value;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public Map<String,String> getShared()
	{
		return this.getResponse();
	}
	
	public static InvocationResponse createFromShared(Map<String,String> shared)
	{
		InvocationResponse response = new InvocationResponse();
		response.response = shared;
		return response;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	private Map<String,String> getResponse() 
	{
		if(this.response == null)
		{
			this.response = new HashMap<String,String>();
		}
		return this.response;
	}
}
