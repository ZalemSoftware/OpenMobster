/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.service.database;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class DBException extends Exception 
{
	public static final int ERROR_INIT_IMPOSSIBLE = 0;
	public static final int ERROR_INVALID_UID = 1;
	public static final int ERROR_UNINITIALIZED = 2;
	public static final int ERROR_NOT_CONNECTED = 3;
	public static final int ERROR_TABLE_ALREADY_EXISTS = 4;
	public static final int ERROR_CONFIG_TABLE_DELETE_NOT_ALLOWED = 5;
	public static final int ERROR_TABLE_NOTFOUND = 6;
	public static final int ERROR_RECORD_STALE = 7;
	public static final int ERROR_RECORD_DELETED = 8;
	
	private int errorCode;
	
	
	public DBException(Exception e)
	{
		super(e);
	}
	
		
	public DBException(int errorCode)
	{		
		this.errorCode = errorCode;
	}
	
	public int getErrorCode()
	{
		return this.errorCode;
	}
	
	public String getMessage()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.getMessage());
		
		switch(this.errorCode)
		{
			case ERROR_INIT_IMPOSSIBLE:
			buffer.append("Database cannot be initialized. This is serious and check the Reference Documentation for further details");
			break;
			
			case ERROR_INVALID_UID:
			buffer.append("Database UID is Invalid");
			break;
			
			case ERROR_UNINITIALIZED:
			buffer.append("Database is not initialized");
			break;
			
			case ERROR_NOT_CONNECTED:
			buffer.append("Database is not connected");
			break;
			
			case ERROR_TABLE_ALREADY_EXISTS:
			buffer.append("Specified Table already exists in the database");
			break;
			
			case ERROR_TABLE_NOTFOUND:
			buffer.append("Specified Table is not found in the database");
			break;
			
			case ERROR_CONFIG_TABLE_DELETE_NOT_ALLOWED:
			buffer.append("You are not authorized to delete the 'Configuration' table");
			break;
			
			case ERROR_RECORD_STALE:
			buffer.append("The Record is Stale. Please Refresh it from the Database before using it");
			break;
			
			case ERROR_RECORD_DELETED:
			buffer.append("The Record has been Deleted. Please Refresh it from the Database before using it");
			break;
		}
		
		buffer.append("\n");
		
		return buffer.toString();
	}
}
