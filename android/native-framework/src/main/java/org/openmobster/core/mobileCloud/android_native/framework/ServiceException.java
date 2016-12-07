/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

/**
 *
 * @author openmobster@gmail.com
 */
public final class ServiceException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7087838885566953762L;

	public ServiceException(Exception original)
	{
		super(original);
	}
	
	public ServiceException(String message)
	{
		super(message);
	}
}
