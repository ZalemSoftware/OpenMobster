/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject;

import java.util.List;
import java.util.ArrayList;

/**
 * @author openmobster@gmail.com
 */
public class MockPOJO 
{
	private String value;
	
	private MockChild child;
	
	private List<MockChild> children;
	
	private List<String> strings;
	
	private String[] childArray;
	
		
	public MockPOJO()
	{
		
	}
	
	public MockPOJO(String value)
	{
		this.value = value;
	}
	
	public MockPOJO(String value, MockChild child, List<MockChild> children, List<String> strings)
	{
		this.value = value;
		this.child = child;
		this.children = children;
		this.strings = strings;
	}

	public String getValue() 
	{
		return value;
	}

	public void setValue(String value) 
	{
		this.value = value;
	}

	public MockChild getChild() 
	{
		return child;
	}

	public void setChild(MockChild child) 
	{
		this.child = child;
	}

	public List<MockChild> getChildren() 
	{
		/*if(this.children == null)
		{
			this.children = new ArrayList<MockChild>();
		}*/
		return children;
	}

	public void setChildren(List<MockChild> children) 
	{
		this.children = children;
	}

	public List<String> getStrings() 
	{
		if(this.strings == null)
		{
			this.strings = new ArrayList<String>();
		}
		return strings;
	}

	public void setStrings(List<String> strings) 
	{
		this.strings = strings;
	}

	public String[] getChildArray() 
	{
		return childArray;
	}

	public void setChildArray(String[] childArray) 
	{
		this.childArray = childArray;
	}	
}
