/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileContainer;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.io.Serializable;

import org.openmobster.cloud.api.rpc.Request;


/**
 * 
 * @author openmobster@gmail.com
 */
public class Invocation implements Serializable
{			
	private static final long serialVersionUID = 8606294484177710273L;
	
	private Map<String, Object> attributes = null;
	
	private Invocation()
	{
		this.attributes = new HashMap<String, Object>();
	}
	
	
	public Object getAttribute(String name)
	{
		Object object = null;
		
		if(this.attributes.containsKey(name))
		{
			object = this.attributes.get(name);
		}
		
		return object;
	}
	
	
	public void setAttribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	
	public String[] getNames()
	{
		String[] names = null;
		
		if(!this.attributes.isEmpty())
		{
			Set<String> keys = this.attributes.keySet();
			names = keys.toArray(new String[0]);
		}
		
		return names;
	}
	
	
	public Object[] getValues()
	{
		Object[] values = null;
		
		if(!this.attributes.isEmpty())
		{
			values = this.attributes.values().toArray();
		}
		
		return values;
	}
	//--------Object Creation-----------------------------------------------------------------------------		
	public static Invocation getInstance()
	{
		return new Invocation();
	}
	//-------Some helper operations---------------------------------------------------------------------------------
	public String getServiceUrl()
	{
		return (String)this.getAttribute("serviceUrl");
	}
	
	public void setServiceUrl(String serviceUrl)
	{
		this.setAttribute("serviceUrl", serviceUrl);
	}
	
	public String getConnectorId()
	{
		return (String)this.getAttribute("connectorId");
	}
	
	public void setConnectorId(String connectorId)
	{
		this.setAttribute("connectorId", connectorId);
	}
	
	public String getBeanId()
	{
		return (String)this.getAttribute("beanId");
	}
	
	public void setBeanId(String beanId)
	{
		this.setAttribute("beanId", beanId);
	}
	
	public String getSerializedBean()
	{
		return (String)this.getAttribute("serializedBean");
	}
	
	public void setSerializedBean(String serializedBean)
	{
		this.setAttribute("serializedBean", serializedBean);
	}
	
	public Request getServiceRequest()
	{
		return (Request)this.getAttribute("serviceRequest");
	}
	
	public void setServiceRequest(Request serviceRequest)
	{
		this.setAttribute("serviceRequest", serviceRequest);
	}
	
	public org.openmobster.cloud.api.location.Request getLocationRequest()
	{
		return (org.openmobster.cloud.api.location.Request)this.getAttribute("locationRequest");
	}
	
	public void setLocationRequest(org.openmobster.cloud.api.location.Request locationRequest)
	{
		this.setAttribute("locationRequest", locationRequest);
	}
}
