/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import test.openmobster.device.agent.frameworks.mobileObject.email.MockChild;

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
	
	//Should not be allowed by the spec
	private Set<String> setOfStrings;
	private List nonParametrizedList;
	private String[] arrayWithNullElements;
	private List<BasePOJO> abstractList;
	private List<ConcretePOJO> concreteList;
		
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
		if(this.children == null)
		{
			this.children = new ArrayList<MockChild>();
		}
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

	public Set<String> getSetOfStrings() 
	{
		return setOfStrings;
	}

	public void setSetOfStrings(Set<String> setOfStrings) 
	{
		this.setOfStrings = setOfStrings;
	}

	public List getNonParametrizedList() 
	{
		return nonParametrizedList;
	}

	public void setNonParametrizedList(List nonParametrizedList) 
	{
		this.nonParametrizedList = nonParametrizedList;
	}

	public String[] getArrayWithNullElements() 
	{
		return arrayWithNullElements;
	}

	public void setArrayWithNullElements(String[] arrayWithNullElements) 
	{
		this.arrayWithNullElements = arrayWithNullElements;
	}

	public List<BasePOJO> getAbstractList() 
	{
		return abstractList;
	}

	public void setAbstractList(List<BasePOJO> abstractList) 
	{
		this.abstractList = abstractList;
	}
	
	public List<ConcretePOJO> getConcreteList() 
	{
		return concreteList;
	}

	public void setConcreteList(List<ConcretePOJO> concreteList) 
	{
		this.concreteList = concreteList;
	}
}
