/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import org.openmobster.core.mobileCloud.android.errors.BaseException;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncException extends BaseException 
{
	/**
	 * 
	 * @param className
	 * @param methodName
	 * @param parameters
	 */
	public SyncException(String className, String methodName, Object[] parameters)
	{
		super(className, methodName, parameters);
	}
}
