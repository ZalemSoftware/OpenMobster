/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.rpc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
	 * @param name name of the attribute
	 * @return value of the attribute
	 */
	public String getAttribute(String name)
	{
		return this.attributeManager.getAttribute(name);
	}
	
	/**
	 * Gets all the names that identify values of attributes in the service response
	 * 
	 * @return all the attribute names of the response
	 */
	public String[] getNames()
	{
		return this.attributeManager.getNames();
	}
	
	/**
	 * Gets all the values of attributes in the service response
	 * 
	 * @return all the values of attributes
	 */
	public String[] getValues()
	{
		return this.attributeManager.getValues();
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Sets arbitrary attributes representing the contextual data associated with this particular service response
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 */
	void setAttribute(String name, String value)
	{
		this.attributeManager.setAttribute(name, value);
	}
	
	/**
	 * Removes an attribute associated with the service response
	 * 
	 * @param name name of the attribute to be removed
	 */
	void removeAttribute(String name)
	{
		this.attributeManager.removeAttribute(name);
	}
	
	/**
	 * Sets related "list" of data representing the contextual data associated 
	 * with this particular service request
	 * 
	 * @param name name of the attribute
	 * @param list list to be set as value
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
	 * @param name name of the list attribute
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
	//----------------------------------------------------------------------------------------------------------	
	/**
	 * Sets the status code of the response
	 * 
	 * @param statusCode the status code
	 */
	public void setStatusCode(String statusCode)
	{
		this.attributeManager.setAttribute("status", statusCode);
	}
	
	/**
	 * Gets the status code of the response
	 * 
	 * @return the status code
	 */
	public String getStatusCode()
	{
		return this.attributeManager.getAttribute("status");
	}
	
	/**
	 * Sets the status message of the response
	 * 
	 * @param statusMsg the status message
	 */
	public void setStatusMsg(String statusMsg)
	{
		this.attributeManager.setAttribute("statusMsg", statusMsg);
	}
	
	/**
	 * Get the status message set on the response
	 * 
	 * @return the status message
	 */
	public String getStatusMsg()
	{
		return this.attributeManager.getAttribute("statusMsg");
	}		
}
