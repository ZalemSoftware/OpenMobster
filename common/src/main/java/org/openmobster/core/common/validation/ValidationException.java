/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.validation;

import org.openmobster.core.common.errors.SystemException;

/**
 * 
 * @author openmobster@gmail.com
 */
public class ValidationException extends SystemException
{	
	private static final long serialVersionUID = 7239245601280584968L;
	
	public ValidationException()
	{
		this("");
	}
	
	public ValidationException(String msg)
	{
		super(msg);
	}
	
	public ValidationException(Throwable t)
	{
		super(t.getMessage(), t);
	}
}
