/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud.api.camera;

import java.util.List;
import java.io.IOException;

import org.openmobster.core.common.AttributeManager;
import org.openmobster.core.common.Utilities;

/**
 * 
 * @author openmobster@gmail.com
 */
public class CameraCommandContext
{
	private AttributeManager attributeManager;
	private String command;
	
	/**
	 * Creates a command request
	 * 
	 * @param service - the unique identifier of the camera command to be invoked
	 */
	public CameraCommandContext(String command)
	{
		if(command == null || command.trim().length() == 0)
		{
			throw new IllegalArgumentException("Command cannot be empty!!");
		}
		
		this.command = command;
		this.attributeManager = new AttributeManager();
	}
	
	/**
	 * Sets arbitrary attributes representing the contextual data associated with this particular context
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value)
	{
		this.attributeManager.setAttribute(name, value);
	}
	
	/**
	 * Gets an arbitrary attribute value from the context
	 * 
	 * @param name
	 * @return
	 */
	public String getAttribute(String name)
	{
		return this.attributeManager.getAttribute(name);
	}
	
	/**
	 * Gets all the names that identify values of attributes in the context
	 * 
	 * @return
	 */
	public String[] getNames()
	{
		return this.attributeManager.getNames();
	}
	
	/**
	 * Gets all the values of attributes in the context
	 * 
	 * @return
	 */
	public String[] getValues()
	{
		return this.attributeManager.getValues();
	}
	
	/**
	 * Removes an attribute associated with the context
	 * 
	 * @param name
	 */
	public void removeAttribute(String name)
	{
		this.attributeManager.removeAttribute(name);
	}
	
	/**
	 * Gets the the unique identifier of the camera command
	 * 
	 * @return
	 */
	public String getCommand()
	{
		return this.command;
	}	
	
	public String getFullName()
	{
		return this.attributeManager.getAttribute("fullname");
	}
	
	public String getMimeType()
	{
		return this.attributeManager.getAttribute("mime");
	}
	
	public byte[] getPhoto() throws IOException
	{
		String photo = this.attributeManager.getAttribute("photo");
		byte[] pic = Utilities.decodeBinaryData(photo);
		return pic;
	}
}
