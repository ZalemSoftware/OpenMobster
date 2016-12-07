/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.identity.Identity;

/**
 * @author openmobster@gmail.com
 */
public class TestAccountActivation extends TestCase
{
	private static Logger log = Logger.getLogger(TestAccountActivation.class);
	
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
		
		Provisioner provisioner = Provisioner.getInstance();
		provisioner.registerIdentity("blah@gmail.com", "blahblah");
		provisioner.registerIdentity("blah2@gmail.com", "blahblah2");
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testActivationToggle() throws Exception
	{
		Request request = new Request("/console/deactivateCommand");
		request.setAttribute("username", "blah@gmail.com");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "204");
		
		//Assert the active status of users
		Identity blah = Provisioner.getInstance().getIdentityController().read("blah@gmail.com");
		assertFalse("Must be inactive!!", blah.isActive());
		
		Identity blah2 = Provisioner.getInstance().getIdentityController().read("blah2@gmail.com");
		assertTrue("Must be active!!", blah2.isActive());
		
		request = new Request("/console/activateCommand");
		request.setAttribute("username", "blah@gmail.com");
		service.invoke(request);
		
		blah = Provisioner.getInstance().getIdentityController().read("blah@gmail.com");
		assertTrue("Must be active now!!", blah.isActive());
	}
	
	public void testActivateWithMissingInput() throws Exception
	{
		Request request = new Request("/console/activateCommand");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "500");
	}
	
	public void testDeActivateWithMissingInput() throws Exception
	{
		Request request = new Request("/console/deactivateCommand");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "500");
	}
	
	public void testDeactivateNonExistingAccount() throws Exception
	{
		Request request = new Request("/console/deactivateCommand");
		request.setAttribute("username", "blah3@gmail.com");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		this.assertEquals(response.getAttribute("idm-error"), "identity_not_found");
	}
	
	public void testActivateNonExistingAccount() throws Exception
	{
		Request request = new Request("/console/activateCommand");
		request.setAttribute("username", "blah3@gmail.com");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		this.assertEquals(response.getAttribute("idm-error"), "identity_not_found");
	}
}
