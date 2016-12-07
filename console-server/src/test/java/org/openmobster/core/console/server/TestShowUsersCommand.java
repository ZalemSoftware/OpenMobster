/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server;

import java.util.Vector;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

import org.openmobster.core.security.Provisioner;

/**
 * @author openmobster@gmail.com
 */
public class TestShowUsersCommand extends TestCase
{
	private static Logger log = Logger.getLogger(TestShowUsersCommand.class);
	
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
	
	public void testShowUsers() throws Exception
	{
		Provisioner provisioner = Provisioner.getInstance();
		provisioner.registerIdentity("blah@gmail.com", "blahblah");
		provisioner.registerIdentity("blah2@gmail.com", "blahblah2");
		
		Request request = new Request("/console/showUsersCommand");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		
		//Assert the list of users
		Vector users = response.getListAttribute("users");
		assertTrue("Must not be empty!!", users!=null && !users.isEmpty());
		
		log.info("Users--------------------------------------");
		for(Object user: users)
		{
			log.info("User: "+user);
			assertTrue("User must match!!", user.equals("blah@gmail.com") || user.equals("blah2@gmail.com"));
		}
	}
	
	public void testNoRegisteredUsers() throws Exception
	{
		Request request = new Request("/console/showUsersCommand");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		this.assertEquals(response.getStatusCode(), "200");
		
		//Assert the list of users
		Vector users = response.getListAttribute("users");
		assertTrue("Must be empty!!", users==null || users.isEmpty());
	}
}
