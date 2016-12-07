/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.subscription;

import java.io.Serializable;
import org.openmobster.core.common.AttributeManager;

/**
 * @author openmobster@gmail
 *
 */
public class Subscription implements Serializable 
{
	private String clientId;
	private AttributeManager attributeManager;
	
	public Subscription()
	{
		this.attributeManager = new AttributeManager();		
	}

	public String getClientId() 
	{
		return clientId;
	}

	public void setClientId(String clientId) 
	{
		this.clientId = clientId;
	}
	
	public void setConfigValue(String name, String value)
	{
		this.attributeManager.setAttribute(name, value);
	}
	
	public String getConfigValue(String name, String value)
	{
		return this.attributeManager.getAttribute(name);
	}
	
	public void removeConfigValue(String name)
	{
		this.attributeManager.removeAttribute(name);
	}	
}
