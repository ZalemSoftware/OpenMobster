/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security.device;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

import org.openmobster.core.security.identity.Identity;

/**
 * Device models the Mobile Device that is managed by the system
 * 
 * @author openmobster@gmail.com
 */
public class Device implements Serializable 
{	
	private static final long serialVersionUID = -1489176951218957966L;

	/**
	 * database uid. no domain meaning here whatsoever
	 */
	private long id;
	
	/**
	 * A unique identifier for this device
	 */
	private String identifier;
	
	/**
	 * Arbitrary attributes to be associated with the device
	 */
	private Set<DeviceAttribute> attributes;
	
	/**
	 * Unique Identity of the User that this Device is registered with
	 */
	private Identity identity;
	
	public Device()
	{
		
	}
	
	public Device(String identifier, Identity identity)
	{
		this.identifier = identifier;
		this.identity = identity;
	}

	public Set<DeviceAttribute> getAttributes() 
	{
		if(this.attributes == null)
		{
			this.attributes = new HashSet<DeviceAttribute>();
		}
		return attributes;
	}

	public void setAttributes(Set<DeviceAttribute> attributes) 
	{
		this.attributes = attributes;
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getIdentifier() 
	{
		return identifier;
	}

	public void setIdentifier(String identifier) 
	{
		this.identifier = identifier;
	}

	public Identity getIdentity() 
	{		
		return identity;
	}

	public void setIdentity(Identity identity) 
	{
		this.identity = identity;
	}
	
	public String getOs()
	{
		DeviceAttribute osAttr = this.readAttribute("os");
		if(osAttr != null)
		{
			return osAttr.getValue();
		}
		return null;
	}
	
	public String getDeviceToken()
	{
		DeviceAttribute attr = this.readAttribute("device-token");
		if(attr != null)
		{
			return attr.getValue();
		}
		return null;
	}
	//-------------------------------------------------------------------------------------------------------	
	public void addAttribute(DeviceAttribute attribute)
	{				
		this.getAttributes().add(attribute);
	}
	
	
	public void removeAttribute(DeviceAttribute attribute)
	{		
		DeviceAttribute cour = this.find(attribute);
		if(cour != null)
		{
			this.getAttributes().remove(cour);
		}
	}
	
	
	public void updateAttribute(DeviceAttribute attribute)
	{		
		DeviceAttribute cour = this.find(attribute);
		if(cour != null)
		{
			cour.setName(attribute.getName());
			cour.setValue(attribute.getValue());
		}
		else
		{
			this.addAttribute(attribute);
		}
	}
	
	
	public DeviceAttribute readAttribute(String name)
	{		
		return this.find(new DeviceAttribute(name, null));
	}
	
	
	private DeviceAttribute find(DeviceAttribute attribute)
	{
		DeviceAttribute cour = null;
		
		Set<DeviceAttribute> attributes = this.getAttributes();
		for(DeviceAttribute loop: attributes)
		{
			if(loop.getName().equals(attribute.getName()))
			{
				return loop;
			}
		}
		
		return cour;
	}
}
