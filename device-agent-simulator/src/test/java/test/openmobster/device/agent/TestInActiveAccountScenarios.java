/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.identity.Identity;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;
import org.openmobster.device.agent.sync.SyncService;


/**
 * @author openmobster@gmail.com
 */
public class TestInActiveAccountScenarios extends TestCase
{
	private static Logger log = Logger.getLogger(TestInActiveAccountScenarios.class);
	
	protected SyncService syncService;
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		this.syncService = (SyncService)ServiceManager.locate("simulator://SyncService");
		
		//Provision some devices
		Provisioner provisioner = Provisioner.getInstance();	
		provisioner.registerIdentity("blah3@gmail.com", "blahblah3");
		provisioner.registerDevice("blah3@gmail.com", "blahblah3", "IMEI:9999999");
		Identity identity = provisioner.getIdentityController().read("blah3@gmail.com");
		
		Configuration configuration = Configuration.getInstance();
		configuration.setSecurityConfig((SecurityConfig)ServiceManager.locate("/cloudConnector/securityConfig"));
		configuration.setDeviceId("IMEI:9999999");
		configuration.setAuthenticationHash(identity.getCredential()); //empty
		configuration.setServerIp("localhost");
		configuration.setServerId("localhost");
		configuration.setSecureServerPort("1500");
		configuration.setPlainServerPort("1502");
		configuration.bootup();
		
		org.openmobster.device.agent.configuration.Configuration conf =  org.openmobster.device.agent.configuration.Configuration.getInstance();
		conf.setDeviceId("IMEI:9999999");
		conf.setAuthenticationHash(identity.getCredential()); //empty
		conf.setServerIp("localhost");
		conf.setServerId("localhost");
		conf.setSecureServerPort("1500");
		conf.setPlainServerPort("1502");
		conf.bootup();
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testServiceInvocation() throws Exception
	{
		Request request = new Request("mockMobileService");
		request.setAttribute("mock1", "mock1Value");
		request.setAttribute("mock2", "mock2Value");
		Response response = new MobileService().invoke(request);
		
		String mock1 = response.getAttribute("mock1");
		String mock2 = response.getAttribute("mock2");
		log.info("Mock1: "+mock1);
		log.info("Mock2: "+mock2);
		assertEquals(mock1, "response://mock1Value");
		assertEquals(mock2, "response://mock2Value");
		
		Provisioner.getInstance().deactivate("blah3@gmail.com");
		response = new MobileService().invoke(request);
		assertNull(response);
	}
	
	public void testSync() throws Exception
	{
		org.openmobster.device.agent.configuration.Configuration conf =  org.openmobster.device.agent.configuration.Configuration.getInstance();
		
		Provisioner provisioner = Provisioner.getInstance();
		
		String oldHash = conf.getAuthenticationHash();
		this.syncService.startSync(SyncService.SLOW_SYNC, "IMEI:9999999", 
		"http://www.openmobster.org/sync-server", "testServerBean", "testServerBean");	
		this.assertNotSame(oldHash, conf.getAuthenticationHash());
		
		oldHash = conf.getAuthenticationHash();
		this.syncService.startSync(SyncService.SLOW_SYNC, "IMEI:9999999", 
				"http://www.openmobster.org/sync-server", "testServerBean", "testServerBean");	
		this.assertNotSame(oldHash, conf.getAuthenticationHash());
		
		provisioner.deactivate("blah3@gmail.com");
		
		oldHash = conf.getAuthenticationHash();
		this.syncService.startSync(SyncService.SLOW_SYNC, "IMEI:9999999", 
				"http://www.openmobster.org/sync-server", "testServerBean", "testServerBean");	
		this.assertSame(oldHash, conf.getAuthenticationHash());
	}
}
