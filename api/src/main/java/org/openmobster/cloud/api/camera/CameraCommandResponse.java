/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud.api.camera;

import org.openmobster.core.common.AttributeManager;

/**
 * 
 * @author openmobster@gmail.com
 */
public class CameraCommandResponse
{
	private AttributeManager attributeManager;
	
	/**
	 * Creates a command request
	 * 
	 * @param service - the unique identifier of the camera command to be invoked
	 */
	public CameraCommandResponse()
	{
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
}
