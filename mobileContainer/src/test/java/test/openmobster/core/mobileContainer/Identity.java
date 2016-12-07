/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileContainer;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 * @author openmobster@gmail.com
 */
public class Identity implements MobileBean
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6292388708208410284L;

	/**
	 * 
	 */
	@MobileBeanId
	private String id;
	
	/**
	 * 
	 */
	private String principal;
	
	/**
	 * 
	 */
	private String credential;
	
	/**
	 * 
	 */
	private List<IdentityAttribute> attributes;
		
	/**
	 * 
	 */
	private IdentityAttribute[] arrayTest;	
		
		
	
	/**
	 * 
	 *
	 */
	public Identity()
	{
		
	}
	
	/**
	 * 
	 * @param principal
	 * @param credential
	 * @param attributes
	 */
	public Identity(String principal, String credential, List<IdentityAttribute> attributes)
	{
		this.principal = principal;
		this.credential = credential;
		this.attributes = attributes;	
	}

	/**
	 * 
	 * @return
	 */
	public String getId() 
	{
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) 
	{
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getCredential() 
	{
		return credential;
	}

	/**
	 * 
	 * @param credentials
	 */
	public void setCredential(String credential) 
	{
		this.credential = credential;
	}

	/**
	 * 
	 * @return
	 */
	public String getPrincipal() 
	{
		return principal;
	}

	/**
	 * 
	 * @param principal
	 */
	public void setPrincipal(String principal) 
	{
		this.principal = principal;
	}

	/**
	 * 
	 * @return
	 */
	public List<IdentityAttribute> getAttributes() 
	{
		if(this.attributes == null)
		{
			this.attributes = new ArrayList<IdentityAttribute>();
		}
		return attributes;
	}

	/**
	 * 
	 * @param attributes
	 */
	public void setAttributes(List<IdentityAttribute> attributes) 
	{
		this.attributes = attributes;
	}
		
	public IdentityAttribute[] getArrayTest() 
	{
		return arrayTest;
	}

	public void setArrayTest(IdentityAttribute[] arrayTest) 
	{
		this.arrayTest = arrayTest;
	}	
	//------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public boolean isActive()
	{
		boolean isActive = false;
		
		IdentityAttribute active = this.readAttribute("active");
		if(active != null && active.getValue().equals("true"))
		{
			isActive = true;
		}
		
		return isActive;
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
	//-------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param attribute
	 */
	public void addAttribute(IdentityAttribute attribute)
	{				
		this.getAttributes().add(attribute);
	}
	
	/**
	 * 
	 * @param attribute
	 */
	public void removeAttribute(IdentityAttribute attribute)
	{		
		IdentityAttribute cour = this.find(attribute);
		if(cour != null)
		{
			this.getAttributes().remove(cour);
		}
	}
	
	/**
	 * 
	 * @param attribute
	 */
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
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public IdentityAttribute readAttribute(String name)
	{		
		return this.find(new IdentityAttribute(name, null));
	}
	
	/**
	 * 
	 * @param attribute
	 * @return
	 */
	private IdentityAttribute find(IdentityAttribute attribute)
	{
		IdentityAttribute cour = null;
		
		for(IdentityAttribute loop: this.getAttributes())
		{
			if(loop.getName().equals(attribute.getName()))
			{
				return loop;
			}
		}
		
		return cour;
	}
}
