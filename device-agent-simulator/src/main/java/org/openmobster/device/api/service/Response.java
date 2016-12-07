/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.api.service;

import java.util.Vector;


/**
 * Represents a Response sent back by an invocation of the server side Mobile Service Bean component
 * 
 * @author openmobster@gmail.com
 */
public final class Response
{
	private AttributeManager attributeManager;
	
	/**
	 * Creates an instance of the Response
	 */
	public Response()
	{
		this.attributeManager = new AttributeManager();
	}
		
	/**
	 * Gets an arbitrary attribute value from the service response
	 * 
	 * @param name
	 * @return
	 */
	public String getAttribute(String name)
	{
		return this.attributeManager.getAttribute(name);
	}
	
	/**
	 * Gets all the names that identify values of attributes in the service response
	 * 
	 * @return
	 */
	public String[] getNames()
	{
		return this.attributeManager.getNames();
	}
	
	/**
	 * Gets all the values of attributes in the service response
	 * 
	 * @return
	 */
	public String[] getValues()
	{
		return this.attributeManager.getValues();
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Sets arbitrary attributes representing the contextual data associated with this particular service response
	 * 
	 * @param name
	 * @param value
	 */
	void setAttribute(String name, String value)
	{
		this.attributeManager.setAttribute(name, value);
	}
	
	/**
	 * Removes an attribute associated with the service response
	 * 
	 * @param name
	 */
	void removeAttribute(String name)
	{
		this.attributeManager.removeAttribute(name);
	}
	
	/**
	 * Sets related "list" of data representing the contextual data associated 
	 * with this particular service request
	 * 
	 * @param name
	 * @param list
	 */
	public void setListAttribute(String name, Vector list)
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
	public Vector getListAttribute(String name)
	{
		return this.attributeManager.getListAttribute(name);
	}
	//----------------------------------------------------------------------------------------------------------		
	public void setStatusCode(String statusCode)
	{
		this.attributeManager.setAttribute("status", statusCode);
	}
	
	public String getStatusCode()
	{
		return this.attributeManager.getAttribute("status");
	}
	
	public void setStatusMsg(String statusMsg)
	{
		this.attributeManager.setAttribute("statusMsg", statusMsg);
	}
	
	public String getStatusMsg()
	{
		return this.attributeManager.getAttribute("statusMsg");
	}		
}
