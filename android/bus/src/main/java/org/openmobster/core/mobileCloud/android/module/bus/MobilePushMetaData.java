/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

/**
 * MobileObjectMetaData contains meta information related to a particular MobileObject
 * 
 * @author openmobster@gmail.com
 *
 */
public final class MobilePushMetaData 
{	
	private String service;		
	private String id;
	private boolean isDeleted;
	private boolean isAdded;
	private boolean isUpdated;
	
	/**
	 * 
	 * @param service Service corresponding to the MobileBean
	 * @param id device side uid of the MobileBean
	 */
	public MobilePushMetaData(String service, String id)
	{
		this.service = service;
		this.id = id;
	}	
	
	/**
	 * Gets the Service corresponding to the MobileBean
	 * 
	 * @return
	 */
	public String getService()
	{
		return this.service;
	}
	
	/**
	 * Gets the Device Side UID of the MobileBean
	 * 
	 * @return
	 */
	public String getId()
	{
		return this.id;
	}

	public boolean isDeleted() 
	{
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) 
	{
		this.isDeleted = isDeleted;
	}

	public boolean isAdded() 
	{
		return isAdded;
	}

	public void setAdded(boolean isAdded) 
	{
		this.isAdded = isAdded;
	}

	public boolean isUpdated() 
	{
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) 
	{
		this.isUpdated = isUpdated;
	}	
}
