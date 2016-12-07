/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server.domain;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;
import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;

import junit.framework.TestCase;

/**
 * @author openmobster@gmail.com
 */
public class TestConsoleService extends TestCase
{
	public void setUp()
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
	
	public void tearDown()
	{
		ServiceManager.shutdown();
	}
	
	public void testConfigOTA() throws Exception
	{
		Request request = new Request("/console/consoleService");
		request.setAttribute("action", "configure");
		request.setAttribute("username", "root");
		request.setAttribute("password", "password");
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "204");
		
		//Perform successfull authentication
		request = new Request("/console/consoleService");
		request.setAttribute("action", "auth");
		request.setAttribute("username", "root");
		request.setAttribute("password", "password");
		response = service.invoke(request);
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "204");
		
		//Perform auth failure
		request = new Request("/console/consoleService");
		request.setAttribute("action", "auth");
		request.setAttribute("username", "root");
		request.setAttribute("password", "blahblah");
		response = service.invoke(request);
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		this.assertEquals(response.getAttribute("console-error"), "auth_failed");
	}
	
	public void testConfigOTAMissingFields() throws Exception
	{
		Request request = new Request("/console/consoleService");
		request.setAttribute("action", "configure");
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		this.assertEquals(response.getAttribute("console-error"), "missing_required_info");
	}
	
	public void testAuthOTAMissingFields() throws Exception
	{
		Request request = new Request("/console/consoleService");
		request.setAttribute("action", "auth");
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		this.assertEquals(response.getAttribute("console-error"), "missing_required_info");
	}
}
