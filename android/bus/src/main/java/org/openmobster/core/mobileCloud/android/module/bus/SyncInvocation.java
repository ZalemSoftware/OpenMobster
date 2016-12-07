/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

/**
 * @author openmobster@gmail.com
 *
 */
public final class SyncInvocation extends Invocation 
{
	public static final int slow = 1;
	public static final int twoWay = 2;
	public static final int oneWayDeviceOnly = 3;
	public static final int oneWayServerOnly = 4;
	public static final int stream = 5;
	public static final int updateChangeLog = 6;
	public static final int bootSync = 7;
	public static final int proxySync = 8;
	public static final int changelogOnly = 9;
	public static final int scheduleSync = 10;
	public static final int synchronousSave = 11;
	
	public static String OPERATION_ADD = "Add";
	public static String OPERATION_UPDATE = "Replace";
	public static String OPERATION_DELETE = "Delete";	
	public static String OPERATION_MAP = "Map";
	
	public SyncInvocation(String target)
	{
		super(target);
	}
	
	public SyncInvocation(String target, int type, String service)
	{
		this(target);
		
		if(type != slow && type != twoWay && type != oneWayDeviceOnly && type != oneWayServerOnly && type != stream &&
		   type != updateChangeLog && type != bootSync && type != proxySync && type != changelogOnly && type != scheduleSync
		   && type != synchronousSave
		)
		{
			throw new IllegalArgumentException("Unsupported Sync Type specified!!");
		}
		
		if(service == null || service.trim().length() == 0)
		{
			throw new IllegalArgumentException("Service should not be empty!!");
		}
		
		this.setValue("type", String.valueOf(type));
		this.setValue("service", service);
	}
	
	public SyncInvocation(String target, int type)
	{
		this(target);
		
		if(type != slow && type != twoWay && type != oneWayDeviceOnly && type != oneWayServerOnly && type != stream &&
		   type != updateChangeLog && type != bootSync && type != proxySync && type != changelogOnly && type != scheduleSync &&
		   type != synchronousSave
		)
		{
			throw new IllegalArgumentException("Unsupported Sync Type specified!!");
		}
		
		this.setValue("type", String.valueOf(type));
	}
	
		
	public SyncInvocation(String target, int type, String service, String recordId)
	{
		this(target, type, service);
		
		if(recordId == null || recordId.trim().length() == 0)
		{
			throw new IllegalArgumentException("RecordId should not be empty!!");
		}
		
		this.setValue("recordId", recordId);
	}
	
	public SyncInvocation(String target, int type, String service, String recordId, String operation)
	{
		this(target, type, service, recordId);
		
		if(operation == null || operation.trim().length() == 0)
		{
			throw new IllegalArgumentException("Operation should not be empty!!");
		}
		
		this.setValue("operation", operation);
	}
	
		
	public int getType()
	{
		return Integer.parseInt(this.getValue("type"));
	}
	
	public String getService()
	{
		return this.getValue("service");
	}
	
	public String getRecordId()
	{
		return this.getValue("recordId");
	}	
	
	public String getOperation()
	{
		return this.getValue("operation");
	}
	
	public void activateBackgroundSync()
	{
		this.setValue("backgroundSync", "true");
	}
	
	public void deactivateBackgroundSync()
	{
		this.setValue("backgroundSync", "false");
	}
}
