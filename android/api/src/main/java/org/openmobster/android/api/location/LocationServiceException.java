/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.location;

import org.openmobster.core.mobileCloud.android.errors.BaseException;

/**
 * Any exception or error that occurs on the device side during a Location Service Request
 * 
 * @author openmobster@gmail.com
 *
 */
public final class LocationServiceException extends BaseException 
{
	/**
	 * 
	 * @param className className where the exception occurred
	 * @param methodName the method name where the exception occurred
	 * @param parameters Arbitrary data associated to carry more detailed information
	 */
	public LocationServiceException(String className, String methodName, Object[] parameters)
	{
		super(className, methodName, parameters);
	}
}
