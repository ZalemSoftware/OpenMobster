/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.domain;

import java.io.Serializable;

/**
 * Represents a persistable business object common class
 * 
 * @author openmobster@gmail.com
 *
 */
public abstract class BusinessObject implements Serializable 
{
	//Unique database identifier for the object
	private Long id = null;
		
	public BusinessObject()
	{
		
	}

	/**
	 * Gets the unique database identifier for the Object
	 * 
	 * @return the unique database identifier
	 */
	public Long getId() 
	{
		return id;
	}

	/**
	 * Sets the unique database identifier for the Object
	 * 
	 * @param id the unique database identifier
	 */
	public void setId(Long id) 
	{
		this.id = id;
	}
}
