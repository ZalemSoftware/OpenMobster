/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite;


/**
 * @author openmobster@gmail.com
 *
 */
public final class TestContext 
{
	private GenericAttributeManager attMgr;
	
	public TestContext()
	{
		this.attMgr = new GenericAttributeManager();
	}
	//------------------------------------------------------------------------------------------------------------------------------------------
	public Object getAttribute(String name)
	{
		return this.attMgr.getAttribute(name);
	}
	
	public void setAttribute(String name, Object value)
	{
		if(value != null)
		{
			this.attMgr.setAttribute(name, value);
		}
		else
		{
			this.removeAttribute(name);
		}
	}
	
	public void removeAttribute(String name)
	{
		this.attMgr.removeAttribute(name);
	}
}
