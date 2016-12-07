/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security;

import org.openmobster.core.common.errors.SystemException;

/**
 * 
 * @author openmobster@gmail.com
 */
public class DeviceSecurityException extends SystemException
{		
	private static final long serialVersionUID = -2294095894100486056L;

	
	public DeviceSecurityException()
	{
		this("");
	}

	
	public DeviceSecurityException(String msg)
	{
		super(msg);
	}

	
	public DeviceSecurityException(Throwable t)
	{
		super(t.getMessage(), t);
	}
}
