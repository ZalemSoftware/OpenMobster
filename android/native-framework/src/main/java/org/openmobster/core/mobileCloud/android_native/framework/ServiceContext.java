/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public final class ServiceContext 
{
	private GenericAttributeManager attrMgr;
	
	public ServiceContext()
	{
		this.attrMgr = new GenericAttributeManager();
	}	
	//-----------------------------------------------------------------------------------------------------
	public void setAttribute(String name, Object value)
	{
		this.attrMgr.setAttribute(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return this.attrMgr.getAttribute(name);
	}
	//----------------------------------------------------------------------------
	public Object getAppContext()
	{
		return this.attrMgr.getAttribute("app-context");
	}
	
	public void setAppContext(Object appContext)
	{
		this.attrMgr.setAttribute("app-context", appContext);
	}
}
