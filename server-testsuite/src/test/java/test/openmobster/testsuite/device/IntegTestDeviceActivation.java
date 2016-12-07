/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.testsuite.device;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;


import junit.framework.TestCase;

import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.core.common.IOUtilities;
import org.openmobster.device.agent.Tools;
import org.openmobster.device.api.service.Request;
import org.openmobster.device.api.service.Response;
import org.openmobster.device.api.service.MobileService;
import org.openmobster.device.agent.sync.SyncService;
import org.openmobster.device.agent.configuration.Configuration;

/**
 * @author openmobster@gmail.com
 */
public class IntegTestDeviceActivation extends TestCase 
{
	private static Logger log = Logger.getLogger(IntegTestDeviceActivation.class);
	
	private SyncService syncService;
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
		org.openmobster.cloudConnector.api.Configuration configuration = 
		org.openmobster.cloudConnector.api.Configuration.getInstance();
		configuration.setSecurityConfig((SecurityConfig)ServiceManager.locate("/cloudConnector/securityConfig"));
		
		this.syncService = (SyncService)ServiceManager.locate("simulator://SyncService");
		
		Socket socket = null;
		OutputStream os = null;
		InputStream is = null;
		try
		{					
			socket = Tools.getPlainSocket();
			
			is = socket.getInputStream();
			os = socket.getOutputStream();	
			
			String payload = 
			"<request>" +
				"<header>" +
					"<name>processor</name>"+
					"<value>testsuite</value>"+
				"</header>"+
			"</request>";
			IOUtilities.writePayLoad(payload, os);	
			
			String data = IOUtilities.readServerResponse(is);
			if(data.indexOf("status=200")!=-1)
			{
				payload = "setUp="+this.getClass().getName()+"/CleanUp\n";
				IOUtilities.writePayLoad(payload, os);		
				data = IOUtilities.readServerResponse(is);
				
				payload = "setUp="+this.getClass().getName()+"/DeviceActivation\n";
				IOUtilities.writePayLoad(payload, os);		
				data = IOUtilities.readServerResponse(is);
			}
		}
		finally
		{					
			if(socket != null)
			{
				socket.close();
			}
		}
	}	
	
	protected void tearDown() throws Exception
	{
		Configuration.cleanup();
		ServiceManager.shutdown();
	}
	//----------------------------------------------------------------------------------------------------
	public void testActivationSuccess() throws Exception
	{
		Request request = new Request("provisioning");		
		request.setAttribute("email", "blah2@gmail.com");
		request.setAttribute("password", "blahblah2");
		request.setAttribute("identifier", "IMEI:546789");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		assertNotNull("Response should not be null!!", response);
		
		String serverId = response.getAttribute("serverId");
		String serverIp = response.getAttribute("serverIp");
		String plainServerPort = response.getAttribute("plainServerPort");
		String secureServerPort = response.getAttribute("secureServerPort");
		String isSSlActive = response.getAttribute("isSSLActive");
		String maxPacketSize = response.getAttribute("maxPacketSize");
		String authenticationHash = response.getAttribute("authenticationHash");
		
		log.info("---------------------------------------------------------");	
		log.info("Server Id="+serverId);
		log.info("Server Ip="+serverIp);
		log.info("Plain Server Port="+plainServerPort);
		log.info("Secure Server Port="+secureServerPort);
		log.info("Is SSL Active="+isSSlActive);
		log.info("Max Packet Size="+maxPacketSize);
		log.info("Authentication Hash="+authenticationHash);
		
		//Perform a SlowSync and make sure data is of desired nature
		Configuration.getInstance().setAuthenticationHash(authenticationHash);
		
		//One sync
		this.syncService.startSync(SyncService.SLOW_SYNC, "IMEI:546789", 
		serverId, "testServerBean", "testServerBean");
		
		//Second sync using the new none
		this.syncService.startSync(SyncService.SLOW_SYNC, "IMEI:546789", 
		serverId, "testServerBean", "testServerBean");
	}
	
	public void testActivationCredentialMismatch() throws Exception
	{		
		Request request = new Request("provisioning");		
		request.setAttribute("email", "blah2@gmail.com");
		request.setAttribute("password", "blahblah");
		request.setAttribute("identifier", "IMEI:546789");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		assertNotNull("Response should not be null!!", response);
		String error = response.getAttribute("idm-error");
		String type = response.getAttribute("idm-error-type");
		assertEquals(error, "activation_credential_mismatch");
		assertEquals(type, "3");
	}
	
	public void testMissingInputData() throws Exception
	{		
		Request request = new Request("provisioning");		
		request.setAttribute("identifier", "IMEI:546789");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		assertNotNull("Response should not be null!!", response);	
		String error = response.getAttribute("idm-error");
		String type = response.getAttribute("idm-error-type");
		assertEquals(error, "invalid_input");
		assertEquals(type, "2");
	}
	
	public void testIdentityNotFound() throws Exception
	{		
		Request request = new Request("provisioning");		
		request.setAttribute("email", "blah3@gmail.com");
		request.setAttribute("password", "blahblah2");
		request.setAttribute("identifier", "IMEI:546789");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		assertNotNull("Response should not be null!!", response);
		String error = response.getAttribute("idm-error");
		String type = response.getAttribute("idm-error-type");
		assertEquals(error, "identity_not_found");
		assertEquals(type, "4");
	}
		
	//Usecase: Activated Device with a hard reset/data wipe trying to reconnect
	//Expected Result: Device will be re-activated if the credentials provided at initial registration
	//are provided
	public void testDeviceReactivation() throws Exception
	{
		//Using an already activated device...not IMEI:546789 which is not activated
		Request request = new Request("provisioning");		
		request.setAttribute("email", "blah@gmail.com");
		request.setAttribute("password", "blahblah");
		request.setAttribute("identifier", "IMEI:4930051");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		assertNotNull("Response should not be null!!", response);
		
		String serverId = response.getAttribute("serverId");
		String serverIp = response.getAttribute("serverIp");
		String plainServerPort = response.getAttribute("plainServerPort");
		String secureServerPort = response.getAttribute("secureServerPort");
		String isSSlActive = response.getAttribute("isSSLActive");
		String maxPacketSize = response.getAttribute("maxPacketSize");
		String authenticationHash = response.getAttribute("authenticationHash");
		
		log.info("---------------------------------------------------------");	
		log.info("Server Id="+serverId);
		log.info("Server Ip="+serverIp);
		log.info("Plain Server Port="+plainServerPort);
		log.info("Secure Server Port="+secureServerPort);
		log.info("Is SSL Active="+isSSlActive);
		log.info("Max Packet Size="+maxPacketSize);
		log.info("Authentication Hash="+authenticationHash);
	}
	
	//Usecase: Activated Device with a hard reset/data wipe trying to reconnect
	//         Activation with some random IMEI (IMEI not registred with the system)
	//
	//Expected Result: Device should not be activated. identity_already_has_a_different_device should occur
	public void testDeviceReactivationRandomIMEI() throws Exception
	{
		Request request = new Request("provisioning");		
		request.setAttribute("email", "blah@gmail.com");
		request.setAttribute("password", "blahblah");
		request.setAttribute("identifier", "IMEI:123456"); //random IMEI
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		assertNotNull("Response should not be null!!", response);
		
		String error = response.getAttribute("idm-error");
		String type = response.getAttribute("idm-error-type");
		assertEquals(error, "identity_already_has_a_different_device");
		assertEquals(type, "7");
	}
	
	//Usecase: Activated Device with a hard reset/data wipe trying to reconnect
	//         Activation with some non-random IMEI (IMEI registred with the system, but with another user)
	//
	//Expected Result: Device should not be activated. activation_credential_mismatch should occur
	public void testDeviceReactivationNonRandomIMEI() throws Exception
	{
		Request request = new Request("provisioning");		
		request.setAttribute("email", "blah@gmail.com");
		request.setAttribute("password", "blahblah");
		request.setAttribute("identifier", "IMEI:546789"); //IMEI but registered to blah2@gmail.com
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		assertNotNull("Response should not be null!!", response);
		
		String error = response.getAttribute("idm-error");
		String type = response.getAttribute("idm-error-type");
		assertEquals(error, "activation_credential_mismatch");
		assertEquals(type, "3");
	}
}
