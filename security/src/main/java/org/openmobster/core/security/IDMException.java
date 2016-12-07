/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security;

import java.util.Map;
import org.openmobster.core.common.errors.SystemException;

/**
 * 
 * @author openmobster@gmail.com
 */
public class IDMException extends SystemException
{	
	
	private static final long serialVersionUID = -2294095894100486056L;
	
	public static final int SYSTEM_ERROR = 1;
	public static final int VALIDATION_ERROR = 2;	
	public static final int ACTIVATION_CREDENTIAL_MISMATCH = 3;
	public static final int IDENITITY_NOT_FOUND = 4;
	public static final int IDENITITY_ALREADY_EXISTS = 5;
	public static final int DEVICE_ALREADY_EXISTS = 6;
	public static final int IDENTITY_ALREADY_HAS_A_DIFFERENT_DEVICE=7;
	
	private Map<String, String[]> validationErrors;
	private int type;
	
	public IDMException()
	{
		this("");
	}

	
	public IDMException(String msg)
	{
		super(msg);
	}

	
	public IDMException(Throwable t)
	{
		super(t.getMessage(), t);
	}
	
	public IDMException(int type)
	{
		this("");
		this.type = type;
	}
	
	public IDMException(String msg, int type)
	{
		this(msg);
		this.type = type;
	}
	
	public IDMException(Throwable t, int type)
	{
		this(t);
		this.type = type;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------
	public Map<String, String[]> getValidationErrors() 
	{
		return validationErrors;
	}


	public void setValidationErrors(Map<String, String[]> validationErrors) 
	{
		this.validationErrors = validationErrors;
	}
	
	public int getType()
	{
		return this.type;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public String getMessage() 
	{	
		String message = super.getMessage();
		
		if((message == null || message.trim().length() == 0) && this.type > 0)
		{
			switch(this.type)
			{
				case IDMException.SYSTEM_ERROR:
					message = "unknown_system_error";
				break;
				case IDMException.VALIDATION_ERROR:
					message = "validation_error";
				break;
				case IDMException.ACTIVATION_CREDENTIAL_MISMATCH:
					message = "activation_credential_mismatch";
				break;
				case IDMException.DEVICE_ALREADY_EXISTS:
					message = "device_already_registered";
				break;
				case IDMException.IDENITITY_ALREADY_EXISTS:
					message = "identity_already_registered";
				break;
				case IDMException.IDENITITY_NOT_FOUND:
					message = "identity_not_found";
				break;
				case IDMException.IDENTITY_ALREADY_HAS_A_DIFFERENT_DEVICE:
					message = "identity_already_has_a_different_device";
				break;
				default:
					message = "";
				break;
			}
		}
		
		return message;
	}	
}
