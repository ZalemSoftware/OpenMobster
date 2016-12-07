/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server.domain;

import org.openmobster.core.common.domain.BusinessObject;

/**
 * @author openmobster@gmail.com
 */
public class ConsoleUser extends BusinessObject
{
	private static final long serialVersionUID = -7029364714711997696L;
	
	private String principal;
	private String credential;
	
	public ConsoleUser()
	{
		
	}

	public String getPrincipal()
	{
		return principal;
	}

	public void setPrincipal(String principal)
	{
		this.principal = principal;
	}

	public String getCredential()
	{
		return credential;
	}

	public void setCredential(String credential)
	{
		this.credential = credential;
	}
}
