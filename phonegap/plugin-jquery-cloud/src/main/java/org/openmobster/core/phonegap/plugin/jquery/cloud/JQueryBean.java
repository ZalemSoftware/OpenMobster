/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin.jquery.cloud;

import java.io.Serializable;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 *
 * @author openmobster@gmail.com
 */
public class JQueryBean implements MobileBean,Serializable
{
	@MobileBeanId
	private String oid;
	
	private String title;
	private String customer;
	private String specialist;
	private String comments;
	
	public JQueryBean()
	{
		
	}

	public String getOid()
	{
		return oid;
	}

	public void setOid(String oid)
	{
		this.oid = oid;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getCustomer()
	{
		return customer;
	}

	public void setCustomer(String customer)
	{
		this.customer = customer;
	}

	public String getSpecialist()
	{
		return specialist;
	}

	public void setSpecialist(String specialist)
	{
		this.specialist = specialist;
	}

	public String getComments()
	{
		return comments;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}
}
