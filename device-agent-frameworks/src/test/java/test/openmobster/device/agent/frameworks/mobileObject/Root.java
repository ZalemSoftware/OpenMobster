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
public class Root 
{
	private List<Node1> node1 = null;
	private String value = null;
	
	public Root()
	{
		
	}
	
	public Root(String value)
	{
		this.value = value;
	}
	
	public Root(String value, List<Node1> node1)
	{
		this.node1 = node1;
		this.value = value;
	}

	public List<Node1> getNode1() 
	{
		return node1;
	}

	public void setNode1(List<Node1> node1) 
	{
		this.node1 = node1;
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
