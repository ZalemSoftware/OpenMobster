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
public class Node1 
{
	private List<Node2> node2 = null;
	private String value = null;
	
	public Node1()
	{
		
	}
	
	public Node1(String value)
	{
		this.value = value;
	}
	
	public Node1(String value, List<Node2> node2)
	{
		this.node2 = node2;
		this.value = value;
	}

	public List<Node2> getNode2() 
	{
		return node2;
	}

	public void setNode2(List<Node2> node2) 
	{
		this.node2 = node2;
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
