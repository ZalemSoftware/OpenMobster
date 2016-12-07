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
public final class AppException extends RuntimeException 
{
	private GenericAttributeManager attrMgr;
	
	public AppException()
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
	
	public void setType(String type)
	{
		this.attrMgr.setAttribute("type", type);
	}
	
	public String getType()
	{
		return (String)this.attrMgr.getAttribute("type");
	}
	
	public void setMessageKey(String messageKey)
	{
		this.attrMgr.setAttribute("messageKey", messageKey);
	}
	
	public String getMessageKey()
	{
		return (String)this.attrMgr.getAttribute("messageKey");
	}
	
	public void setMessage(String message)
	{
		this.attrMgr.setAttribute("message", message);
	}
	
	public String getMessage()
	{
		return (String)this.attrMgr.getAttribute("message");
	}
}
