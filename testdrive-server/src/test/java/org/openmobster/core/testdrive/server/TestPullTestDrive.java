/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.testdrive.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.common.ServiceManager;

/**
 * @author openmobster@gmail.com
 *
 */
public class TestPullTestDrive extends TestCase
{
	private static Logger log = Logger.getLogger(TestPullTestDrive.class);
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testPull() throws Exception
	{
		//NOTE: this test is not needed anymore. It won't work with the new AuthenticationFilter
		/*Socket socket = null;
		OutputStream os = null;
		InputStream is = null;
		try
		{					
			socket = getSocket();
			
			is = socket.getInputStream();
			os = socket.getOutputStream();	
			
			String sessionInitPayload = 
				"<request>" +
					"<header>" +
						"<name>processor</name>"+
						"<value>/testdrive/pull</value>"+
					"</header>"+
				"</request>";
			
			IOUtilities.writePayLoad(sessionInitPayload, os);			
			
			String data = IOUtilities.readServerResponse(is);
			if(data.indexOf("status=200")!=-1)
			{
				String stream = "<pull><caller name='android'/></pull>";
				
				IOUtilities.writePayLoad(stream, os);
				
				String response = IOUtilities.readServerResponse(is);
				
				log.info("InvocationResponse........................");
				log.info("Response="+response);
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
		}*/
	}
	
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
