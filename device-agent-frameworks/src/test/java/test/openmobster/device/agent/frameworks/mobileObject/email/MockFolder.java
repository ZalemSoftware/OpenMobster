/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject.email;

import java.util.List;

import org.openmobster.core.common.domain.BusinessObject;

/**
 * Represents a mail box folder where emails are stored
 * 
 * @author openmobster@gmail.com
 *
 */
public class MockFolder extends BusinessObject 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1597044223833948688L;
	
	private String name = null;
	private String fullName = null;
	private String urlName = null;
	
	private List<MockEmail> emails = null;
	
	/**
	 * 
	 *
	 */
	public MockFolder()
	{
		
	}
	
	public MockFolder(String name, String fullName, String urlName)
	{
		this.name = name;
		this.fullName = fullName;
		this.urlName = urlName;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		if(name == null || name.trim().length()==0)
		{
			name = "INBOX";
		}
		this.name = name;
	}

	public String getFullName() 
	{
		return fullName;
	}

	public void setFullName(String fullName) 
	{
		if(fullName == null || fullName.trim().length()==0)
		{
			fullName = "INBOX";
		}
		this.fullName = fullName;
	}

	public String getUrlName()
	{
		return urlName;
	}

	public void setUrlName(String urlName)
	{
		this.urlName = urlName;
	}

	public List<MockEmail> getEmails() 
	{
		return emails;
	}

	public void setEmails(List<MockEmail> emails) 
	{
		this.emails = emails;
	}		
}
