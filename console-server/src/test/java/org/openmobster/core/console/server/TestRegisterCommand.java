/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

/**
 * @author openmobster@gmail.com
 */
public class TestRegisterCommand extends TestCase
{
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		Configuration configuration = Configuration.getInstance();
		configuration.setSecurityConfig((SecurityConfig)ServiceManager.locate("/cloudConnector/securityConfig"));
		configuration.setDeviceId("console:localhost");
		configuration.setAuthenticationHash(""); //empty
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
	
	public void testSuccess() throws Exception
	{
		Request request = new Request("/console/registerCommand");
		request.setAttribute("username", "blah2@gmail.com");
		request.setAttribute("password", "blahblah2");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "204");
	}
	
	public void testMissingCredential() throws Exception
	{
		Request request = new Request("/console/registerCommand");
		request.setAttribute("username", "blah2@gmail.com");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
	}
	
	public void testInvalidEmailFormat() throws Exception
	{
		Request request = new Request("/console/registerCommand");
		request.setAttribute("username", "blah");
		request.setAttribute("password", "blahblah2");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
	}
	
	public void testDuplicateIdentity() throws Exception
	{
		this.testSuccess();
		
		Request request = new Request("/console/registerCommand");
		request.setAttribute("username", "blah2@gmail.com");
		request.setAttribute("password", "blahblah2");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
	}
}
