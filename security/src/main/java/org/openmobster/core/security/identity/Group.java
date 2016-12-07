/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security.identity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Group/Role of Identities in the System. Will come in handy during Authorization Rules based
 * Security
 * 
 * @author openmobster@gmail.com
 */
public class Group implements Serializable 
{	
	private static final long serialVersionUID = -534897944798759872L;
	
	/**
	 * database uid
	 */
	private long id;
	
	/**
	 * Group Name
	 */
	private String name;
	
	/**
	 * Arbitrary information associated with the Group
	 */
	private Set<GroupAttribute> attributes;
	
	/**
	 * Members of this Group
	 */
	private Set<Identity> members;
	
	
	public Group()
	{
		
	}
	
	
	public Group(String name, Set<GroupAttribute> attributes, Set<Identity> identities)
	{
		this();
		this.name = name;
		this.attributes = attributes;
		this.members = identities;
	}

	
	public long getId() 
	{
		return id;
	}

	
	public void setId(long id) 
	{
		this.id = id;
	}

	
	public String getName() 
	{
		return name;
	}

	
	public void setName(String name) 
	{
		this.name = name;
	}

	
	public Set<GroupAttribute> getAttributes() 
	{
		if(this.attributes == null)
		{
			this.attributes = new HashSet<GroupAttribute>();
		}
		return attributes;
	}

	
	public void setAttributes(Set<GroupAttribute> attributes) 
	{
		this.attributes = attributes;
	}
	
	
	public Set<Identity> getMembers() 
	{
		if(this.members == null)
		{
			this.members = new HashSet<Identity>();
		}
		return members;
	}

	
	public void setMembers(Set<Identity> identities) 
	{
		this.members = identities;
	}
	//---------------------------------------------------------------------------------------------------	
	public void addAttribute(GroupAttribute attribute)
	{		
		this.getAttributes().add(attribute);
	}
	
	
	public void removeAttribute(GroupAttribute attribute)
	{				
		GroupAttribute cour = this.find(attribute);
		if(cour != null)
		{
			this.getAttributes().remove(cour);
		}
	}
	
	
	public void updateAttribute(GroupAttribute attribute)
	{		
		GroupAttribute cour = this.find(attribute);
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
	
	
	public GroupAttribute readAttribute(String name)
	{		
		return this.find(new GroupAttribute(name, null));
	}
	
	
	private GroupAttribute find(GroupAttribute attribute)
	{
		GroupAttribute cour = null;
		
		Set<GroupAttribute> attributes = this.getAttributes();
		for(GroupAttribute loop: attributes)
		{
			if(loop.getName().equals(attribute.getName()))
			{
				return loop;
			}
		}
		
		return cour;
	}	
}
