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
import java.util.List;

import java.io.Serializable;

import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.sync.MobileBean;


/**
 * 
 * @author openmobster@gmail.com
 */
public class InvocationResponse implements Serializable
{			
	private static final long serialVersionUID = -7313062908028023917L;
	
	private Map<String, Object> attributes = null;
	
	public static String STATUS_NOT_FOUND = "404";
	public static String STATUS_SUCCESS = "200";
	
	private InvocationResponse()
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
	public static InvocationResponse getInstance()
	{
		return new InvocationResponse();
	}
	//--------helper operations--------------------------------------------------------------------------
	public List<MobileBean> getAllBeans()
	{
		return (List<MobileBean>)this.getAttribute("allBeans");
	}
	
	public void setAllBeans(List<MobileBean> allBeans)
	{
		this.setAttribute("allBeans", allBeans);
	}
	
	public MobileBean getBean()
	{
		return (MobileBean)this.getAttribute("bean");
	}
	
	public void setBean(MobileBean bean)
	{
		this.setAttribute("bean", bean);
	}
	
	public String getBeanId()
	{
		return (String)this.getAttribute("beanId");
	}
	
	public void setBeanId(String beanId)
	{
		this.setAttribute("beanId", beanId);
	}
	
	public String getStatus()
	{
		return (String)this.getAttribute("status");
	}
	
	public void setStatus(String status)
	{
		this.setAttribute("status", status);
	}
	
	public Response getServiceResponse()
	{
		return (Response)this.getAttribute("serviceResponse");
	}
	
	public void setServiceResponse(Response serviceResponse)
	{
		this.setAttribute("serviceResponse", serviceResponse);
	}
	
	public org.openmobster.cloud.api.location.Response getLocationResponse()
	{
		return (org.openmobster.cloud.api.location.Response)this.getAttribute("locationResponse");
	}
	
	public void setLocationResponse(org.openmobster.cloud.api.location.Response locationResponse)
	{
		this.setAttribute("locationResponse", locationResponse);
	}
}
