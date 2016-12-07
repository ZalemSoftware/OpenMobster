/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.common;

/**
 * @author openmobster@gmail.com
 */
public class Bean1 
{
	private String name = null;
	
	private Bean2 bean2 = null;

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
		
	public Bean2 getBean2() 
	{
		return bean2;
	}

	public void setBean2(Bean2 bean2) 
	{
		this.bean2 = bean2;
	}

	public void start()
	{
		System.out.println("Bean1 successfully deployed........");
	}
	
	//Receive Bean2 deployment notification--------------------------------------------------------------
	/**
	 * 
	 */
	public void notify(Bean2 bean2)
	{
		System.out.println("Deployment Notification received for-----"+bean2.getName());
	}
}
