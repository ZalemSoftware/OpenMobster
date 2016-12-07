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
public class Equipment implements Serializable 
{
	private static final long serialVersionUID = 7397958790356907995L;
	
	private long id;
	private String name;
	
	public Equipment()
	{
		
	}
	//----------------------------------------------------------------------------------------------------------------------------------
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
}
