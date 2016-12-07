/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.push;

import java.io.Serializable;

import org.openmobster.core.common.AttributeManager;

/**
 * Represents a service request that will be sent to the server side Mobile Service Bean component
 * 
 * @author openmobster@gmail.com
 */
public class PushCommandContext implements Serializable 
{
	private AttributeManager attributeManager;
	
	/**
	 * Carries the context associated with a push command
	 * 
	 * @param command - the unique identier of a registered PushCommand instance on the device
	 */
	public PushCommandContext(String command)
	{
		if(command == null || command.trim().length() == 0)
		{
			throw new IllegalArgumentException("Command cannot be empty!!");
		}
		
		this.attributeManager = new AttributeManager();
		this.attributeManager.setAttribute("service", command);
	}
	
	/**
	 * Sets arbitrary attributes representing the contextual data associated with this particular service request
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value)
	{
		this.attributeManager.setAttribute(name, value);
	}
	
	/**
	 * Gets an arbitrary attribute value from the service request
	 * 
	 * @param name
	 * @return
	 */
	public String getAttribute(String name)
	{
		return this.attributeManager.getAttribute(name);
	}
	
	/**
	 * Gets all the names that identify values of attributes in the service request
	 * 
	 * @return
	 */
	public String[] getNames()
	{
		return this.attributeManager.getNames();
	}
	
	/**
	 * Gets all the values of attributes in the service request
	 * 
	 * @return
	 */
	public Object[] getValues()
	{
		return this.attributeManager.getValues();
	}
	
	/**
	 * Removes an attribute associated with the service request
	 * 
	 * @param name
	 */
	public void removeAttribute(String name)
	{
		this.attributeManager.removeAttribute(name);
	}
	
	/**
	 * Gets the the unique identifier of the server side Mobile Service Bean component
	 * 
	 * @return
	 */
	public String getCommand()
	{
		return this.getAttribute("service");
	}			
}
