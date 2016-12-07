/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.dataService.processor;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
public class TestNettyDeployment extends TestCase 
{
	private static Logger log = Logger.getLogger(TestNettyDeployment.class);
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
	}
	
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}
	//-----------------------------------------------------------------------------------------------------
	public void testSimpleInvocation() throws Exception
	{
		/*Socket socket = null;
		OutputStream os = null;
		try
		{					
			socket = getSocket();
			
			os = socket.getOutputStream();	
			
			String sessionInitPayload = 
			"<request>" +
				"<header>" +
					"<name>device-id</name>"+
					"<value><![CDATA[IMEI:4930051]]></value>"+
				"</header>"+
				"<header>" +
					"<name>nonce</name>"+
				"</header>"+
				"<header>" +
					"<name>processor</name>"+
					"<value>mobileservice</value>"+
				"</header>"+
			"</request>";
			
			IOUtilities.writePayLoad(sessionInitPayload, os);
		}
		finally
		{					
			if(socket != null)
			{
				socket.close();
			}
		}*/
	}
	//-----------------------------------------------------------------------------------------------------
	private Socket getSocket() throws Exception
	{
		Socket socket = null;
				
		//Create a Plain Socket
		InetAddress localhost = InetAddress.getLocalHost();
		String ip = localhost.getHostAddress();
		socket = new Socket(localhost, 1504);
		
		return socket;
	}					
}
