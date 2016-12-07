/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.dataService.processor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
public class TestServiceInvocationProcessor extends TestCase 
{
	private static Logger log = Logger.getLogger(TestServiceInvocationProcessor.class);
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
		
		//Provision some devices
		Provisioner provisioner = Provisioner.getInstance();	
		
		
		provisioner.registerIdentity("blah2@gmail.com", "blahblah2");
		provisioner.registerDevice("blah2@gmail.com", "blahblah2", "IMEI:4930051");
		
		provisioner.registerIdentity("blah@gmail.com", "blahblah");
		provisioner.registerDevice("blah@gmail.com", "blahblah", "IMEI:4930052");
	}
	
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}
	//-----------------------------------------------------------------------------------------------------
	public void testSimpleInvocation() throws Exception
	{
		Socket socket = null;
		OutputStream os = null;
		InputStream is = null;
		try
		{					
			socket = getSocket();
			
			is = socket.getInputStream();
			os = socket.getOutputStream();	
			
			Device device = DeviceController.getInstance().read("IMEI:4930051");
			
			String sessionInitPayload = 
			"<request>" +
				"<header>" +
					"<name>device-id</name>"+
					"<value><![CDATA[IMEI:4930051]]></value>"+
				"</header>"+
				"<header>" +
					"<name>nonce</name>"+
					"<value><![CDATA["+device.readAttribute("nonce").getValue()+"]]></value>"+
				"</header>"+
				"<header>" +
					"<name>processor</name>"+
					"<value>mobileservice</value>"+
				"</header>"+
			"</request>";
			
			IOUtilities.writePayLoad(sessionInitPayload, os);			
			
			String data = IOUtilities.readServerResponse(is);
			if(data.indexOf("status=200")!=-1)
			{
				for(int i=0; i<2; i++)
				{
					Map<String, String> attributes = new HashMap<String, String>();
					attributes.put("servicename", "mockMobileService");
					attributes.put("mock1", "mockValue1");
					attributes.put("mock2", "mockValue2");
					
					String stream = XMLUtilities.marshal(attributes);
					
					IOUtilities.writePayLoad(stream, os);
					
					String response = IOUtilities.readServerResponse(is);
					
					log.info("InvocationResponse........................");
					log.info("Response="+response);
					
					assertNotNull("Response should not be Null", response);
					Map<String, String> responseAttributes = (Map<String, String>)XMLUtilities.unmarshal(response);
					assertEquals("Mock1 Response Does Not Match", "response://mockValue1", 
					responseAttributes.get("mock1"));					
					assertEquals("Mock2 Response Does Not Match", "response://mockValue2", 
					responseAttributes.get("mock2"));
				}
			}
			else
			{
				log.info("Status="+data);
				throw new RuntimeException("Invocation Failed.........");
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
	//-----------------------------------------------------------------------------------------------------
	private Socket getSocket() throws Exception
	{
		Socket socket = null;
				
		//Create a Plain Socket
		InetAddress localhost = InetAddress.getLocalHost();
		String ip = localhost.getHostAddress();
		socket = new Socket(localhost, 1502);
		
		return socket;
	}					
}
