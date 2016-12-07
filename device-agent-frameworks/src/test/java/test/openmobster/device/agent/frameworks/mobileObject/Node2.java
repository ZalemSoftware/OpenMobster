/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject;

import java.util.List;

/**
 * @author openmobster@gmail.com
 */
public class Node2 
{
	private List<Node3> node3 = null;
	private String value = null;
	
	public Node2()
	{
		
	}
	
	public Node2(String value)
	{
		this.value = value;
	}
	
	public Node2(String value, List<Node3> node3)
	{
		this.node3 = node3;
		this.value = value;
	}

	public List<Node3> getNode3() 
	{
		return node3;
	}

	public void setNode3(List<Node3> node3) 
	{
		this.node3 = node3;
	}
	
	public String getValue() 
	{
		return value;
	}

	public void setValue(String value) 
	{
		this.value = value;
	}
	
}
