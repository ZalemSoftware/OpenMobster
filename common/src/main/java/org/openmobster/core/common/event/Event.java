/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.event;

import java.io.Serializable;

import org.openmobster.core.common.InVMAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public final class Event implements Serializable
{
	private InVMAttributeManager attributes;
	
	public Event()
	{
		this.attributes = new InVMAttributeManager();
	}
	
	public Object getAttribute(String name)
	{
		return this.attributes.getAttribute(name);
	}
	
	public void setAttribute(String name, Object value)
	{
		this.attributes.setAttribute(name, value);
	}
	
	public void removeAttribute(String name)
	{
		this.attributes.removeAttribute(name);
	}
	//-------------------------------------------------------------------------------------------------------
}
