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
 */
public class Technician implements Serializable
{	
	private static final long serialVersionUID = 4220888048923745430L;
	
	private long id;
	private String employeeId;
	private String name;
	private String status;
	
	public Technician()
	{
		
	}

	public String getEmployeeId() 
	{
		return employeeId;
	}

	public void setEmployeeId(String employeeId) 
	{
		this.employeeId = employeeId;
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

	public String getStatus() 
	{
		return status;
	}

	public void setStatus(String status) 
	{
		this.status = status;
	}			
}
