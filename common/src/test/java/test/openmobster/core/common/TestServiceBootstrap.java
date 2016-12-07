/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.common;

import org.openmobster.core.common.ServiceManager;

import junit.framework.TestCase;


/**
 * @author openmobster@gmail.com
 */
public class TestServiceBootstrap extends TestCase 
{
	/**
	 * 
	 *
	 */
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
	}
	
	/**
	 * 
	 *
	 */
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testBeanDeployment() throws Exception
	{
		Bean1 bean1 = (Bean1)ServiceManager.locate("test://Bean1");
		
		if(bean1 != null)
		{
			System.out.println("--------------------------------");
			System.out.println("Bean Name="+bean1.getName());
			System.out.println("Bean Name="+bean1.getBean2().getName());
		}
		
		
		Bean2 bean2 = (Bean2)ServiceManager.locate("test://Bean2");
		
		if(bean2 != null)
		{
			System.out.println("--------------------------------");
			System.out.println("Bean Name="+bean2.getName());
		}
		
		assertNotNull(bean1);
		assertNotNull(bean2);
	}
}
