/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.cloud.rpc;

import org.apache.log4j.Logger;
import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.device.agent.test.framework.MobileBeanRunner;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

/**
 * @author openmobster@gmail.com
 */
public class TestDemoRPC extends TestCase
{
	private static Logger log = Logger.getLogger(TestDemoRPC.class);
	
	protected MobileBeanRunner runner;
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		this.runner = (MobileBeanRunner)ServiceManager.locate("mobileBeanRunner");
		this.runner.activateDevice();
		
		Configuration configuration = Configuration.getInstance();
		configuration.setSecurityConfig((SecurityConfig)ServiceManager.locate("/cloudConnector/securityConfig"));
		configuration.setDeviceId(this.runner.getDeviceId());
		configuration.setAuthenticationHash(this.runner.getConfiguration().getAuthenticationHash()); //empty
		configuration.setServerIp("localhost");
		configuration.setServerId("localhost");
		configuration.setSecureServerPort("1500");
		configuration.setPlainServerPort("1502");
		configuration.bootup();
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testRPCCall() throws Exception
	{
		Request request = new Request("/demo/mobile-rpc");
		request.setAttribute("param1", "paramValue1");
		request.setAttribute("param2", "paramValue2");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		assertEquals(response.getAttribute("param1"), "response://paramValue1");
		assertEquals(response.getAttribute("param2"), "response://paramValue2");
	}
}
