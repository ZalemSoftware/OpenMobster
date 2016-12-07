/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.sync;

import java.io.Serializable;

/**
 * MobileBeanMetaData contains meta information related to a particular MobileBean
 * 
 * @author openmobster@gmail.com
 *
 */
public final class MobileBeanMetaData implements Serializable
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
	public MobileBeanMetaData(String service, String id)
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

	/**
	 * Gets if this MobileBean was deleted from the device during a sync 
	 * 
	 * @return
	 */
	public boolean isDeleted() 
	{
		return isDeleted;
	}

	/**
	 * Sets if this MobileBean was deleted from the device during a sync
	 * 
	 * @param isDeleted
	 */
	public void setDeleted(boolean isDeleted) 
	{
		this.isDeleted = isDeleted;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAdded()
	{
		return isAdded;
	}

	/**
	 * 
	 * @param isAdded
	 */
	public void setAdded(boolean isAdded)
	{
		this.isAdded = isAdded;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUpdated()
	{
		return isUpdated;
	}

	/**
	 * 
	 * @param isUpdated
	 */
	public void setUpdated(boolean isUpdated)
	{
		this.isUpdated = isUpdated;
	}
}
