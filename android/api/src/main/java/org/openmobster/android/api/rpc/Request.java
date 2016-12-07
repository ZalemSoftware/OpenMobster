/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.rpc;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Represents a service request that will be sent to the cloud side Mobile Service Bean component
 * 
 * @author openmobster@gmail.com
 */
public final class Request 
{
	private AttributeManager attributeManager;
	private String service;
	
	/**
	 * Creates a service request
	 * 
	 * @param service - the unique identifier of the cloud side Mobile Service Bean component
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
	 * @param name name of the attribute
	 * @param value value of the attribute
	 */
	public void setAttribute(String name, String value)
	{
		this.attributeManager.setAttribute(name, value);
	}
	
	/**
	 * Gets an arbitrary attribute value from the service request
	 * 
	 * @param name name of the attribute
	 * @return value of the attribute
	 */
	public String getAttribute(String name)
	{
		return this.attributeManager.getAttribute(name);
	}
	
	/**
	 * Gets all the names that identify values of attributes in the service request
	 * 
	 * @return all the attribute names
	 */
	public String[] getNames()
	{
		return this.attributeManager.getNames();
	}
	
	/**
	 * Gets all the values of attributes in the service request
	 * 
	 * @return all the attribute values
	 */
	public String[] getValues()
	{
		return this.attributeManager.getValues();
	}
	
	/**
	 * Removes an attribute associated with the service request
	 * 
	 * @param name attribute to be removed from the object
	 */
	public void removeAttribute(String name)
	{
		this.attributeManager.removeAttribute(name);
	}
	
	/**
	 * Gets the the unique identifier of the server side Mobile Service Bean component
	 * 
	 * @return this is the remote service id that will receive the invocation
	 */
	public String getService()
	{
		return this.service;
	}
	
	/**
	 * Sets related "list" of data representing the contextual data associated 
	 * with this particular service request
	 * 
	 * @param name name of this attribute
	 * @param list the list to be set
	 */
	public void setListAttribute(String name, Vector list)
	{
		Enumeration<String> cour = list.elements();
		List<String> values = new ArrayList<String>();
		while(cour.hasMoreElements())
		{
			values.add(cour.nextElement());
		}
		
		this.attributeManager.setListAttribute(name, values);
	}
	
	/**
	 * Gets a related "list" of data representing the contextual data associated 
	 * with this particular service request
	 * 
	 * @param name name of the attribute
	 * @return the list associated with this attribute
	 */
	public Vector getListAttribute(String name)
	{
		List<String> listAttr = this.attributeManager.getListAttribute(name);
		Vector attribute = new Vector();
		
		if(listAttr != null && !listAttr.isEmpty())
		{
			for(String attr:listAttr)
			{
				attribute.add(attr);
			}
		}
		
		return attribute;
	}
}
