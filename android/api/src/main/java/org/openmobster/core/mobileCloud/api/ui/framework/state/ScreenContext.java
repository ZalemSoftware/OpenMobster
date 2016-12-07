/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.state;

import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public final class ScreenContext implements AppState
{
	private GenericAttributeManager attrMgr;
	
	public ScreenContext()
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
	
	public String[] getNames()
	{
		return this.attrMgr.getNames();
	}
	
	public void removeAttribute(String name)
	{
		this.attrMgr.removeAttribute(name);
	}
}
