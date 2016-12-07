/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject.email;

/**
 * @author openmobster@gmail.com
 *
 */
public class MockPriority 
{	
	public static final MockPriority NORMAL = new MockPriority((byte)0);
	public static final MockPriority LOW = new MockPriority((byte)1);
	public static final MockPriority HIGH = new MockPriority((byte)2);
		
	
	private byte type = 0; //non-null
		
	public MockPriority()
	{
		
	}
			
	public byte getType() 
	{
		return type;
	}

	
	public void setType(byte type) 
	{
		this.type = type;
	}


	private MockPriority(byte type)
	{
		this.type = type;
	}
	
	
	public boolean isNormal()
	{
		return (this.type == NORMAL.type);
	}
	
	
	public boolean isLow()
	{
		return (this.type == LOW.type);
	}
		
	public boolean isHigh()
	{
		return (this.type == HIGH.type);
	}
	
	
	public String toString()
	{
		String priority = "NORMAL";
		
		if(this.isHigh())
		{
			priority = "HIGH";
		}
		else if(this.isLow())
		{
			priority = "LOW";
		}
		
		return priority;
	}
	
	
	public static MockPriority createInstance(String priorityStr)
	{
		MockPriority priority = new MockPriority((byte)0);
		
		if(priorityStr.equalsIgnoreCase("HIGH"))
		{
			priority = new MockPriority((byte)2);
		}
		else if(priorityStr.equalsIgnoreCase("LOW"))
		{
			priority = new MockPriority((byte)1);
		}
		
		return priority;
	}
}
