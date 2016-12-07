/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.rpc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.XMLUtil;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.connection.NetSession;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;

/**
 * Mobile Service facilitates making invocations from the device to the server side Mobile Service Bean components
 * 
 * @author openmobster@gmail.com
 *
 */
public final class MobileService 
{
	/**
	 * Invokes the remote Mobile Service component
	 * 
	 * @param request invocation request
	 * @return invocation response
	 * @throws ServiceInvocationException
	 */
	public static Response invoke(Request request) throws ServiceInvocationException
	{
		try
		{
			Response beanResponse = null;
			
			//Create a Bus Invocation
			String beanRequest = serialize(request);			
			
			//Process the Bus Response
			String beanResponseStr = sendRequest(beanRequest);
			
			if(beanResponseStr != null && beanResponseStr.trim().length()!=0)
			{							
				//Parse the response returned from the server
				beanResponse = parse(beanResponseStr);
			}
			else
			{
				//Some exception occurred during invocation
				throw new RuntimeException("An unknown error occured in the device network layer!!");
			}
			
			return beanResponse;
		}		
		catch(Exception e)
		{
			Log.e("org.openmobster.android", e.getMessage(), e);
			
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(e);
			throw new ServiceInvocationException(MobileService.class.getName(), "invoke", new Object[]{
				"Service being Invoked="+request.getService(),
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	private static String sendRequest(String beanRequest)
	{
		NetSession session = null;
		try
		{
			//System.out.println("Starting MobileInvocation--------------------------------------------------");
			//System.out.println("BeanRequest: "+beanRequest);
			//System.out.println("--------------------------------------------------------------------------");
			Context context = Registry.getActiveInstance().getContext();
			Configuration configuration = Configuration.getInstance(context);
			boolean secure = configuration.isSSLActivated();
			session = NetworkConnector.getInstance().openSession(secure);
			
			String sessionInitPayload = null;
			if(configuration.isActive())
			{
				String deviceId = configuration.getDeviceId();
				String authHash = configuration.getAuthenticationHash();
				sessionInitPayload = 
					"<request>" +
						"<header>" +
							"<name>device-id</name>"+
							"<value><![CDATA["+deviceId+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>nonce</name>"+
							"<value><![CDATA["+authHash+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>processor</name>"+
							"<value>mobileservice</value>"+
						"</header>";
			
						/*
						 * Estrutura adicionada na versão 2.4-M3.1
						 * Se há um token de autenticação sendo utilizado atualmente, envia-o para o servidor. 
						 */
						if (configuration.getAuthenticationToken() != null) {
							sessionInitPayload +=
								"<header>" +
									"<name>authToken</name>" +
									"<value><![CDATA[" + configuration.getAuthenticationToken() + "]]></value>" +
								"</header>";
						}
						
						sessionInitPayload +=
					"</request>";
			}
			else
			{
				sessionInitPayload = 
					"<request>" +
						"<header>" +
							"<name>processor</name>"+
							"<value>mobileservice</value>"+
						"</header>"+
					"</request>";
			}
			
			//System.out.println("--------------------------------------------------");
			//System.out.println("InitMessage: "+sessionInitPayload);
			//System.out.println("--------------------------------------------------");
			
			String response = session.sendTwoWay(sessionInitPayload);
			
			//System.out.println("--------------------------------------------------");
			//System.out.println("Response: "+response);
			//System.out.println("--------------------------------------------------");
			
			if(response.indexOf("status=200")!=-1)
			{
				return session.sendPayloadTwoWay(beanRequest);				
			}
			return null;
		}	
		catch(Exception e)
		{
			Log.e("org.openmobster.android", e.getMessage(), e);
			
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(MobileService.class.getName(), "sendRequest", new Object[]{
				"Request="+beanRequest,
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			}));
			return null;
		}
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}
	
	public static String serialize(Request request)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<map>\n");
		
		buffer.append("<entry>\n");
		buffer.append("<string>servicename</string>\n");
		buffer.append("<string>"+request.getService()+"</string>\n");
		buffer.append("</entry>\n");
		
		String[] names = request.getNames();
		if(names != null)
		{
			for(int i=0, size=names.length; i<size; i++)
			{
				String value = request.getAttribute(names[i]);
				
				buffer.append("<entry>\n");
				buffer.append("<string>"+XMLUtil.addCData(names[i])+"</string>\n");
				buffer.append("<string>"+XMLUtil.addCData(value)+"</string>\n");
				buffer.append("</entry>\n");
			}
		}
		
		buffer.append("</map>\n");
		
		return buffer.toString();
	}
	
	public static Response parse(String response) throws Exception
	{		
		InputStream is = null;
		try
		{
			Response beanResponse = new Response();
			
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			is = new ByteArrayInputStream(response.getBytes());
			SAXHandler handler = new SAXHandler();
			parser.parse(is, handler);
			
			beanResponse = handler.beanResponse;
			
			return beanResponse;
		}		
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(Exception e){}
			}
		}
	}
	//-----------------SAX Parser-------------------------------------------------------------------------------------------------------------
	private static class SAXHandler extends DefaultHandler
	{
		private StringBuffer fullPath;
		private StringBuffer dataBuffer;
		
		private Response beanResponse;
		private String name;
		private String value;						
		//---DefaultHandler impl---------------------------------------------------------------------------		
		public void startDocument() throws SAXException 
		{			
			this.beanResponse = new Response();			
			this.fullPath = new StringBuffer();
			this.dataBuffer = new StringBuffer();
		}
				
		public void endDocument() throws SAXException 
		{			
		}				
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) 
		throws SAXException 
		{		
			this.fullPath.append("/"+localName.trim());
			this.dataBuffer = new StringBuffer();
									
			if(this.fullPath.toString().equals("/map/entry"))
			{
				this.name = null;
				this.value = null;
			}						
		}
						
		public void characters(char[] ch, int start, int length) throws SAXException 
		{		
			String data = new String(ch, start, length);	
			
			if(data != null && data.trim().length()>0)
			{
				this.dataBuffer.append(data);
			}						
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException 
		{
			//Populate with data
			//Process Session object related data
			if(this.fullPath.toString().equals("/map/entry/string"))
			{
				if(this.name == null)
				{
					this.name = this.dataBuffer.toString();
				}
				else 
				{
					this.value = this.dataBuffer.toString();
				}
			}
			else if(this.fullPath.toString().equals("/map/entry"))
			{
				this.beanResponse.setAttribute(this.name, this.value);
			}
			
			
			//Reset
			String cour = this.fullPath.toString();			
			int lastIndex = cour.lastIndexOf('/');
			this.fullPath = new StringBuffer(cour.substring(0, lastIndex));
		}
	}
}
