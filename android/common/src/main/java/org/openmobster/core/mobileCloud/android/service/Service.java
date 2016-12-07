/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.service;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class Service 
{
	private String id;
	
	protected Service()
	{
		
	}			
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public abstract void start();	
	public abstract void stop();
	
	public final String getId()
	{
		if(this.id == null)
		{
			throw new IllegalStateException("Service is unregistered");
		}
		
		return this.id;
	}
	
	public final void setId(String id)
	{
		if(id == null || id.trim().length() == 0)
		{
			throw new IllegalArgumentException("Invalid registration key for the Service");
		}
		
		this.id = id;
	}
}
