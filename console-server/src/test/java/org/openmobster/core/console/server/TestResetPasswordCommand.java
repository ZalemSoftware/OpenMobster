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
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.identity.Identity;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

/**
 * @author openmobster@gmail.com
 */
public class TestResetPasswordCommand extends TestCase
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
		
		
		Request request = new Request("/console/registerCommand");
		request.setAttribute("username", "blah2@gmail.com");
		request.setAttribute("password", "blahblah2");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testSuccess() throws Exception
	{
		Request request = new Request("/console/resetpasswdCommand");
		request.setAttribute("username", "blah2@gmail.com");
		request.setAttribute("oldpass", "blahblah2");
		request.setAttribute("newpass", "hohoho");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "204");
		
		Identity identity = Provisioner.getInstance().getIdentityController().read("blah2@gmail.com");
		assertEquals("New Password must be stored!!","hohoho",identity.getInactiveCredential());
	}
	
	public void testMissingInput() throws Exception
	{
		Request request = new Request("/console/resetpasswdCommand");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "500");
		
		Identity identity = Provisioner.getInstance().getIdentityController().read("blah2@gmail.com");
		assertEquals("New Password must be stored!!","blahblah2",identity.getInactiveCredential());
	}
	
	public void testIdentityNotFound() throws Exception
	{
		Request request = new Request("/console/resetpasswdCommand");
		request.setAttribute("username", "blah@gmail.com");
		request.setAttribute("oldpass", "blahblah2");
		request.setAttribute("newpass", "hohoho");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		this.assertEquals(response.getAttribute("idm-error"), "identity_not_found");
		
		Identity identity = Provisioner.getInstance().getIdentityController().read("blah2@gmail.com");
		assertEquals("New Password must be stored!!","blahblah2",identity.getInactiveCredential());
	}
	
	public void testOldPassMismatch() throws Exception
	{
		Request request = new Request("/console/resetpasswdCommand");
		request.setAttribute("username", "blah2@gmail.com");
		request.setAttribute("oldpass", "blahblah");
		request.setAttribute("newpass", "hohoho");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		this.assertEquals(response.getAttribute("idm-error"), "activation_credential_mismatch");
		
		Identity identity = Provisioner.getInstance().getIdentityController().read("blah2@gmail.com");
		assertEquals("New Password must be stored!!","blahblah2",identity.getInactiveCredential());
	}
}
