/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloud.api.rpc;

import java.util.List;
import java.io.Serializable;

/**
 * Represents a service request that will be sent to the server side Mobile Service Bean component
 * 
 * @author openmobster@gmail.com
 */
public class Request implements Serializable 
{
	private AttributeManager attributeManager;
	private String service;
	
	/**
	 * Creates a service request
	 * 
	 * @param service - the unique identifier of the server side Mobile Service Bean component
	 */
	public Request(String service)
	{
		if(service == null || service.trim().length() == 0)
		{
			throw new IllegalArgumentException("Service cannot be empty!!");
		}
		
		this.service = service;
		this.attributeManager = new AttributeManager();
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
	 * Sets related "list" of data representing the contextual data associated 
	 * with this particular service request
	 * 
	 * @param name
	 * @param list
	 */
	public void setListAttribute(String name, List<String> list)
	{
		this.attributeManager.setListAttribute(name, list);
	}
	
	/**
	 * Gets a related "list" of data representing the contextual data associated 
	 * with this particular service request
	 * 
	 * @param name
	 * @return
	 */
	public List<String> getListAttribute(String name)
	{
		return this.attributeManager.getListAttribute(name);
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
	public String[] getValues()
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
	public String getService()
	{
		return this.service;
	}			
}
