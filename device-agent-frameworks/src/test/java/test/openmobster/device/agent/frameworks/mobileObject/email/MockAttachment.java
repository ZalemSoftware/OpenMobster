/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject.email;

import org.openmobster.core.common.domain.BusinessObject;

/**
 * Represents an attachment associated with an email
 * 
 * @author openmobster@gmail.com
 *
 */
public class MockAttachment extends BusinessObject 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3666799937974265610L;
	
	/**
	 * 
	 */
	private String  contentType = null;
	private String 	name = null;
	private int size = 0;
	private byte[] data = null;	
	
	public MockAttachment()
	{
		
	}
	
	public MockAttachment(String contentType, String name, int size, byte[] data)
	{
		this.contentType = contentType;
		this.name = name;
		this.size = size;
		this.data = data;
	}

	public String getContentType() 
	{
		return contentType;
	}

	public void setContentType(String contentType) 
	{
		this.contentType = contentType;
	}

	public byte[] getData() 
	{
		return data;
	}

	public void setData(byte[] data) 
	{
		this.data = data;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public int getSize() 
	{
		return size;
	}

	public void setSize(int size) 
	{
		this.size = size;
	}		
}
