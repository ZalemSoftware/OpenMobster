/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileContainer;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.mobileContainer.MobileContainer;
import org.openmobster.core.mobileContainer.Invocation;
import org.openmobster.core.mobileContainer.InvocationResponse;

/**
 * @author openmobster@gmail.com
 */
public class TestMobileContainerBootstrap extends TestCase
{
	private static Logger log = Logger.getLogger(TestMobileContainerBootstrap.class);
	
	protected void setUp() throws Exception
	{
		ServiceManager.bootstrap();
	}
	
	protected void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	//--------------------------------------------------------------------------------------------------
	public void test() throws Exception
	{
		MobileContainer mobileContainer = (MobileContainer)ServiceManager.locate("mobileContainer://MobileContainer");
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/all");
		invocation.setConnectorId("identity");
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		
		//Assert state
		assertEquals(response.getStatus(), InvocationResponse.STATUS_SUCCESS);
		List<MobileBean> allBeans = response.getAllBeans();
		assertNotNull(allBeans);
	}
	
	public void test404Status() throws Exception
	{
		MobileContainer mobileContainer = (MobileContainer)ServiceManager.locate("mobileContainer://MobileContainer");
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/all");
		invocation.setConnectorId("blahblah");
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		
		//Assert state
		String status = response.getStatus();
		assertEquals(status, InvocationResponse.STATUS_NOT_FOUND);
	}
}
