/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.command;

import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public final class CommandContext 
{
	private GenericAttributeManager attrMgr;
	
	public CommandContext()
	{
		this.attrMgr = new GenericAttributeManager();
	}	
	//-----------------------------------------------------------------------------------------------------
	public void setTarget(String target)
	{
		this.attrMgr.setAttribute("target", target);
	}
	
	public String getTarget()
	{
		return (String)this.attrMgr.getAttribute("target");
	}
	
	public void setAttribute(String name, Object value)
	{
		this.attrMgr.setAttribute(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return this.attrMgr.getAttribute(name);
	}
	
	public void setAppException(AppException commandError)
	{
		this.attrMgr.setAttribute("error", commandError);
	}
	
	public AppException getAppException()
	{
		return (AppException)this.attrMgr.getAttribute("error");
	}
	
	public boolean hasErrors()
	{
		return (this.attrMgr.getAttribute("error") != null);
	}
	//--------------------------------------------------------------------------
	public void activateTimeout()
	{
		this.attrMgr.setAttribute("activate-timeout", "");
	}
	
	public void deactivateTimeout()
	{
		this.attrMgr.removeAttribute("activate-timeout");
	}
	
	public boolean isTimeoutActivated()
	{
		return (this.attrMgr.getAttribute("activate-timeout")!=null);
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
