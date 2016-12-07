/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloud.api.location;

import java.util.List;
import java.util.Map;
import java.io.Serializable;

import org.openmobster.core.common.InVMAttributeManager;

/**
 * Represents a service request that will be sent to the server side Location Bean component
 * 
 * @author openmobster@gmail.com
 */
public class Request implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3433828877116507510L;
	
	private InVMAttributeManager attributeManager;
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
		this.attributeManager = new InVMAttributeManager();
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
		Object value = this.attributeManager.getAttribute(name);
		if(value instanceof String)
		{
			return (String)value;
		}
		else
		{
			return null;
		}
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
		this.attributeManager.setAttribute(name,list);
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
		return (List<String>)this.attributeManager.getAttribute(name);
	}
	
	/**
	 * Sets related "map" of string data representing the contextual data associated 
	 * with this particular service request
	 * 
	 * @param name
	 * @param map
	 */
	public void setMapAttribute(String name, Map<String,String> map)
	{
		this.attributeManager.setAttribute(name,map);
	}
	
	/**
	 * Gets a related "map" of string data representing the contextual data associated 
	 * with this particular service request
	 * 
	 * @param name
	 * @return
	 */
	public Map<String,String> getMapAttribute(String name)
	{
		return (Map<String,String>)this.attributeManager.getAttribute(name);
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
	 * Removes an attribute associated with the service request
	 * 
	 * @param name
	 */
	public void removeAttribute(String name)
	{
		this.attributeManager.removeAttribute(name);
	}
	
	/**
	 * Gets an arbitrary attribute value from the service response
	 * 
	 * @param name
	 * @return
	 */
	public Object get(String name)
	{
		return this.attributeManager.getAttribute(name);
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
