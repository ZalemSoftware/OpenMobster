/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

/**
 * @author openmobster@gmail.com
 */
public final class Credential 
{
	private String type;
	private String data;
	private String nextNonce;
	
	public Credential()
	{
		
	}
	
	public Credential(String type, String data)
	{
		this.type = type;
		this.data = data;
	}
	
	public Credential(String type, String data, String nextNonce)
	{
		this(type, data);
		this.nextNonce = nextNonce;
	}

	public String getData() 
	{
		return data;
	}

	public void setData(String data) 
	{
		this.data = data;
	}

	public String getType() 
	{
		return type;
	}

	public void setType(String type) 
	{
		this.type = type;
	}

	public String getNextNonce() 
	{
		return nextNonce;
	}

	public void setNextNonce(String nextNonce) 
	{
		this.nextNonce = nextNonce;
	}

	public String getFormat() 
	{
		return "b64";
	}				
}
