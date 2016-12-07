/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.processor;

import org.openmobster.core.common.errors.BaseException;

/**
 * 
 * @author openmobster@gmail.com
 */
public class ProcessorException extends BaseException 
{
	/**
	 * 
	 */
	public ProcessorException()
	{
		this("");
	}
	
	/**
	 * 
	 * @param message
	 */
	public ProcessorException(String message)
	{
		super(message);
	}
	
	/**
	 * 
	 * @param throwable
	 */
	public ProcessorException(Throwable throwable)
	{
		super(throwable.getMessage(), throwable);
	}
}
