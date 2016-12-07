/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.push;

import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public final class PushCommandContext 
{
	private GenericAttributeManager attrMgr;
	
	public PushCommandContext()
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
	
	public void setAttribute(String name, String value)
	{
		this.attrMgr.setAttribute(name, value);
	}
	
	public String getAttribute(String name)
	{
		return (String)this.attrMgr.getAttribute(name);
	}
}
