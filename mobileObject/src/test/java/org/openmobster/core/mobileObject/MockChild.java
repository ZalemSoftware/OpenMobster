/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject;

/**
 * @author openmobster@gmail.com
 */
public class MockChild 
{
	private int id;
	private String value = null;	
	private MockPOJO parent = null;	
	
	public MockChild()
	{
		
	}
	
	public MockChild(String value)
	{
		this.value = value;
	}
	
	public MockChild(String value, MockPOJO parent)
	{
		this.value = value;
		this.parent = parent;
	}

	public String getValue() 
	{
		return value;
	}

	public void setValue(String value) 
	{
		this.value = value;
	}

	public MockPOJO getParent() 
	{
		return parent;
	}

	public void setParent(MockPOJO parent) 
	{
		this.parent = parent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	
	
	
}
