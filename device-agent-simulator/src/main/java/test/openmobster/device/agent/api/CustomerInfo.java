/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.api;

import java.io.Serializable;

/**
 * @author openmobster@gmail.com
 *
 */
public class CustomerInfo implements Serializable
{	
	private static final long serialVersionUID = -1609659465124060671L;

	private long id;
	private String customerId;
	private String name;
	private String comments;
	
	public CustomerInfo()
	{
		
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getCustomerId() 
	{
		return customerId;
	}

	public void setCustomerId(String customerId) 
	{
		this.customerId = customerId;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getComments() 
	{
		return comments;
	}

	public void setComments(String comments) 
	{
		this.comments = comments;
	}	
}
