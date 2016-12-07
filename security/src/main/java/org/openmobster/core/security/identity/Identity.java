/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security.identity;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents a User registered with the system
 * 
 * @author openmobster@gmail.com
 */
public class Identity implements Serializable
{	
	private static final long serialVersionUID = -6292388708208410284L;

	/**
	 * database uid 
	 */
	private long id;
	
	/**
	 * Unique Identifier
	 */
	private String principal;
	
	/**
	 * Credential for authentication
	 */
	private String credential;
	
	/**
	 * Arbitrary information associated with Identity
	 */
	private Set<IdentityAttribute> attributes;
	
	/**
	 * Groups that this User belongs to
	 */
	private Set<Group> groups;
	
	
	public Identity()
	{
		
	}
	
	public Identity(String principal, String credential)
	{
		this.principal = principal;
		this.credential = credential;
	}
	
	
	public Identity(String principal, String credential, Set<IdentityAttribute> attributes,
	Set<Group> groups)
	{
		this.principal = principal;
		this.credential = credential;
		this.attributes = attributes;
		this.groups = groups;
	}

	
	public long getId() 
	{
		return id;
	}

	
	public void setId(long id) 
	{
		this.id = id;
	}

	
	public String getCredential() 
	{
		return credential;
	}

	
	public void setCredential(String credential) 
	{
		this.credential = credential;
	}

	
	public String getPrincipal() 
	{
		return principal;
	}

	
	public void setPrincipal(String principal) 
	{
		this.principal = principal;
	}

	
	public Set<IdentityAttribute> getAttributes() 
	{
		if(this.attributes == null)
		{
			this.attributes = new HashSet<IdentityAttribute>();
		}
		return attributes;
	}

	
	public void setAttributes(Set<IdentityAttribute> attributes) 
	{
		this.attributes = attributes;
	}
	
		
	public Set<Group> getGroups() 
	{
		if(this.groups == null)
		{
			this.groups = new HashSet<Group>();
		}
		return groups;
	}

	
	public void setGroups(Set<Group> groups) 
	{
		this.groups = groups;
	}
	//------------------------------------------------------------------------------------------------------	
	public boolean isActive()
	{
		boolean isActive = false;
		
		IdentityAttribute active = this.readAttribute("active");
		if(active != null && active.getValue().equals(Boolean.TRUE.toString()))
		{
			isActive = true;
		}
		
		return isActive;
	}
	
	public void activate()
	{
		IdentityAttribute active = this.readAttribute("active");
		if(active == null)
		{
			active = new IdentityAttribute("active", Boolean.TRUE.toString());
			this.addAttribute(active);
		}
		else
		{
			active.setValue(Boolean.TRUE.toString());
			this.updateAttribute(active);
		}
	}
	
	public void deactivate()
	{
		IdentityAttribute active = this.readAttribute("active");
		if(active == null)
		{
			active = new IdentityAttribute("active", Boolean.FALSE.toString());
			this.addAttribute(active);
		}
		else
		{
			active.setValue(Boolean.FALSE.toString());
			this.updateAttribute(active);
		}
	}
	
	public String getSecretQuestion()
	{
		String secretQuestion = null;
		
		if(this.readAttribute("secretQuestion") != null)
		{
			secretQuestion = this.readAttribute("secretQuestion").getValue();
		}
		
		return secretQuestion;
	}
	
	public void setSecretQuestion(String secretQuestion)
	{
		if(secretQuestion != null && secretQuestion.trim().length()>0)
		{
			this.updateAttribute(new IdentityAttribute("secretQuestion", secretQuestion));
		}
	}
	
	public String getAnswer()
	{
		String answer = null;
		
		if(this.readAttribute("answer") != null)
		{
			answer = this.readAttribute("answer").getValue();
		}
		
		return answer;
	}
	
	public void setAnswer(String answer)
	{
		if(answer != null && answer.trim().length()>0)
		{
			this.updateAttribute(new IdentityAttribute("answer", answer));
		}
	}
	
	public String getInactiveCredential()
	{
		String inactiveCredential = null;
		
		if(this.readAttribute("inactive_credential") != null)
		{
			inactiveCredential = this.readAttribute("inactive_credential").getValue();
		}
		
		return inactiveCredential;
	}
	
	public void setInactiveCredential(String inactiveCredential)
	{
		if(inactiveCredential != null && inactiveCredential.trim().length()>0)
		{
			this.updateAttribute(new IdentityAttribute("inactive_credential", inactiveCredential));
		}
		else
		{
			this.removeAttribute(new IdentityAttribute("inactive_credential", ""));
		}
	}
	//-------------------------------------------------------------------------------------------------------	
	public void addAttribute(IdentityAttribute attribute)
	{				
		this.getAttributes().add(attribute);
	}
	
	
	public void removeAttribute(IdentityAttribute attribute)
	{		
		IdentityAttribute cour = this.find(attribute);
		if(cour != null)
		{
			this.getAttributes().remove(cour);
		}
	}
	
	
	public void updateAttribute(IdentityAttribute attribute)
	{		
		IdentityAttribute cour = this.find(attribute);
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
	
	
	public IdentityAttribute readAttribute(String name)
	{		
		return this.find(new IdentityAttribute(name, null));
	}
	
	
	private IdentityAttribute find(IdentityAttribute attribute)
	{
		IdentityAttribute cour = null;
	
		Set<IdentityAttribute> attributes = this.getAttributes();
		for(IdentityAttribute loop: attributes)
		{
			if(loop.getName().equals(attribute.getName()))
			{
				return loop;
			}
		}
		
		return cour;
	}
	//-------------------------------------------------------------------------------------------------
	
}
