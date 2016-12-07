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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import org.json.simple.JSONObject;
import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
public class TestLocationProcessor extends TestCase 
{
	private static Logger log = Logger.getLogger(TestLocationProcessor.class);
	
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
					"<value>org.openmobster.core.dataService.processor.LocationProcessor</value>"+
				"</header>"+
			"</request>";
			
			IOUtilities.writePayLoad(sessionInitPayload, os);			
			
			String data = IOUtilities.readServerResponse(is);
			
			log.info("Status: "+data);
			
			if(data.indexOf("status=200")!=-1)
			{
				String xml = this.generateRequest();
				log.info(xml);
				IOUtilities.writePayLoad(xml, os);
				
				String response = IOUtilities.readServerResponse(is);
				
				log.info("InvocationResponse........................");
				log.info("Response="+response);
				
				assertNotNull("Response should not be Null", response);
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
	
	private String generateRequest()
	{
		//Request payload
		JSONObject requestPayload = new JSONObject();
		for(int i=0; i<5; i++)
		{
			requestPayload.put("param"+i, "value"+i);
		}
		
		//list
		List<String> list = new ArrayList<String>();
		for(int i=0; i<5; i++)
		{
			list.add("listAttribute:"+i);
		}
		requestPayload.put("list", list);
		
		//map
		Map<String,String> map = new HashMap<String,String>();
		for(int i=0; i<5; i++)
		{
			map.put("key"+i, "value"+i);
		}
		requestPayload.put("map", map);
		
		String requestPayloadStr = requestPayload.toJSONString();
		
		//Location payload
		JSONObject locationPayload = new JSONObject();
		locationPayload.put("latitude", "-33.8670522");
		locationPayload.put("longitude", "151.1957362");
		
		//PlaceTypes
		List<String> placeTypes = new ArrayList<String>();
		placeTypes.add("restaurant");
		placeTypes.add("airport");
		//locationPayload.put("placeTypes", placeTypes);
		
		String placeReference = "CnRpAAAAynVKUt5Z5MHk4jb1MII3WW5tkVg6kgP_n1UcziRMHRR2iG3IGiv0KI1MX_qDbk2GQRrs6QJbiYC7A_qJbX2UMj7M87Eu0wpwODWCut1IPz5dlovH08DA28wbROyOISsSPFSvj4MssqCiERkbst_eIxIQLP0YzjmNHdmLawGBo9o8GRoUbRrEe-hOGOMCNPVRBnZshh0c-FM";
		locationPayload.put("placeReference", placeReference);
		
		String locationPayloadStr = locationPayload.toJSONString();
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("<location-request>\n");
		buffer.append("<service>coupons</service>\n");
		buffer.append("<request-payload>"+XMLUtilities.addCData(requestPayloadStr)+"</request-payload>\n");
		buffer.append("<location-payload>"+XMLUtilities.addCData(locationPayloadStr)+"</location-payload>\n");
		buffer.append("</location-request>\n");
		String xml = buffer.toString();
		
		return xml;
	}
}
