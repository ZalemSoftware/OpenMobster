/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

import org.openmobster.core.mobileCloud.android.errors.BaseException;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class BusException extends BaseException 
{
	public static final int ERROR_STORAGE_ID = 1;
	public static final int ERROR_REQUEST_MISSING = 2;
	public static final int ERROR_REQUEST_TIMEOUT = 3;
	
	private int errorCode;
	
	/**
	 * 
	 * @param className
	 * @param methodName
	 * @param parameters
	 */
	public BusException(String className, String methodName, Object[] parameters)
	{
		super(className, methodName, parameters);
	}
	
	public BusException(String className, String methodName, Object[] parameters, int errorCode)
	{
		this(className, methodName, parameters);
		this.errorCode = errorCode;
	}
	
	public String getMessage()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.getMessage());
		
		switch(this.errorCode)
		{
			case ERROR_STORAGE_ID:
				buffer.append("Error Code="+ERROR_STORAGE_ID+"\n");
				buffer.append("Error Description=RuntimeStore StorageId Generation Failed\n");
			break;
			
			case ERROR_REQUEST_MISSING:
				buffer.append("Error Code="+ERROR_REQUEST_MISSING+"\n");
				buffer.append("Error Description=Sent Request is Missing from the RuntimeStore\n");
			break;
			
			case ERROR_REQUEST_TIMEOUT:
				buffer.append("Error Code="+ERROR_REQUEST_TIMEOUT+"\n");
				buffer.append("Error Description=Bus Request Timed Out\n");
			break;
		}
		
		return buffer.toString();
	}
}
