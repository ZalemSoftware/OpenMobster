/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileContainer;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.mobileContainer.MobileContainer;
import org.openmobster.core.mobileContainer.Invocation;
import org.openmobster.core.mobileContainer.InvocationResponse;


/**
 * @author openmobster@gmail.com
 */
public class TestMobileServiceInvocation extends TestCase
{	
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(TestMobileServiceInvocation.class);
	
	private MobileContainer mobileContainer;
	
	
	public void setUp() throws Exception
	{		
		ServiceManager.bootstrap();
		
		this.mobileContainer = (MobileContainer)ServiceManager.
		locate("mobileContainer://MobileContainer");				
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	//-----------------------------------------------------------------------------------------------------	
	public void testInvocation() throws Exception
	{		
		Request serviceRequest = new Request("testMobileServiceInvocation");
		serviceRequest.setAttribute("param1", "blahblah");
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/service/invoke");
		invocation.setServiceRequest(serviceRequest);
		
		InvocationResponse response = this.mobileContainer.invoke(invocation);
		Response serviceResponse = response.getServiceResponse();
		
		//Assert state
		assertNotNull("Service Response must not be null!!", serviceResponse);
		assertEquals(serviceResponse.getAttribute("param1"), "boomerang");
	}	
}
