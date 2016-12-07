/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.deploy;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;


/**
 * @author openmobster@gmail.com
 */
public class BootLoader implements BootLoaderMBean
{	
	private static Logger log = Logger.getLogger(BootLoader.class);
		
	public void start() throws Exception
	{
		log.info("OpenMobster BootLoader initialized........");
		ServiceManager.bootstrap();
		
		//Setup client side SSL
		//Protocol trustedHttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
		//Protocol.registerProtocol("https", trustedHttps);
		
		//Ping the MobileServiceInvocation system
		//this.pingMobileServiceInvocationSystem();
		
		//TODO: Ping the MobileBean synchronization system
	}
		
	public void stop() throws Exception
	{
		log.info("OpenMobster BootLoader stopped........");
		ServiceManager.shutdown();
	}
	//--------------------------------------------------------------------------------------------------------
	/*private void pingMobileServiceInvocationSystem() throws Exception
	{
		String url = "/service/invoke";
		
		Map<String, String> request = new HashMap<String, String>();
		request.put("servicename", "ping");
		request.put("input1", "ping...");
		
		Map<String, String>results = this.executeGenericPut(url, XMLUtilities.marshal(request));
		
		String status = results.get("status");
		
		if(status.equalsIgnoreCase("200"))
		{
			log.info("--------------------------------------------------------");
			log.info("MobileServiceInvocation System successfully pinged......");
		}
		else
		{
			throw new RuntimeException("MobileServiceInvocation System Failure!!!, Status="+status);
		}
	}
	
	private Map<String, String> executeGenericPut(String url, String objectStream) throws Exception
	{
		Map<String, String> results = new HashMap<String, String>();
				
		HttpClient httpClient = new HttpClient();		
		
		PutMethod put = new PutMethod("https://localhost:1501"+url);
		InputStream bodyStream = null;
		try
		{			
			StringRequestEntity entity = new StringRequestEntity(objectStream, "text/xml", 
			Charset.defaultCharset().name());
			put.setRequestEntity(entity);		
						
			int status = httpClient.executeMethod(put);
			bodyStream = put.getResponseBodyAsStream();
			String data = new String(IOUtilities.readBytes(bodyStream));
			
			results.put("status", String.valueOf(status));
			results.put("data", data);
			
			log.debug("--------------------------------------------");
			log.debug("Status="+results.get("status"));
			log.debug("Data="+results.get("data"));
			log.debug("--------------------------------------------");
		}
		finally
		{
			if(bodyStream != null)
			{
				bodyStream.close();
			}
			put.releaseConnection();
		}
		
		return results;
	}*/
}
