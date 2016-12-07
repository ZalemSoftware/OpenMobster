/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.sync;

import org.openmobster.core.mobileCloud.android.errors.SystemException;

/**
 * @author openmobster@gmail.com
 *
 */
public final class CommitException extends Exception
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2728040691875529171L;

	/**
	 * 
	 * @param className
	 * @param method
	 * @param parameters
	 */
	public CommitException(String exception)
	{
		super(exception);
	}
	
	public CommitException(SystemException syse)
	{
		super(syse.getMessage());
	}
}
