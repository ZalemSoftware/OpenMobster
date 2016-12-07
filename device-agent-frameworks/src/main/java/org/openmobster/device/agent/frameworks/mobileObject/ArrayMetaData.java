/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

/**
 * @author openmobster@gmail.com
 */
final class ArrayMetaData 
{
	private long id;
	
	private String arrayUri;
	private String arrayLength;
	private String arrayClass;
	
	ArrayMetaData()
	{
		
	}
	
	long getId() 
	{
		return id;
	}

	void setId(long id) 
	{
		this.id = id;
	}
	
	String getArrayUri() 
	{
		return arrayUri;
	}

	void setArrayUri(String arrayUri) 
	{
		this.arrayUri = arrayUri;
	}

	String getArrayLength() 
	{
		return arrayLength;
	}

	void setArrayLength(String arrayLength) 
	{
		this.arrayLength = arrayLength;
	}

	String getArrayClass() 
	{
		return arrayClass;
	}

	void setArrayClass(String arrayClass) 
	{
		this.arrayClass = arrayClass;
	}	
}
