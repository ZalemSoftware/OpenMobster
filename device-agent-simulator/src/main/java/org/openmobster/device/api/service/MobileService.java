/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.api.service;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.Socket;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.log4j.Logger;

import org.openmobster.core.common.IOUtilities;
import org.openmobster.device.agent.Tools;
import org.openmobster.device.agent.configuration.Configuration;

import org.openmobster.device.agent.test.framework.MobileBeanRunner;


/**
 * Mobile Service facilitates making invocations from the device to the server side Mobile Service Bean components
 * 
 * @author openmobster@gmail.com
 *
 */
public final class MobileService 
{
	private static Logger log = Logger.getLogger(MobileService.class);
	
	/**
	 * Invokes the remote Mobile Service
	 * 
	 * @param request
	 * @return
	 * @throws ServiceInvocationException
	 */
	public static Response invoke(Request request)
	{
		Socket socket = null;
		try
		{
			Response beanResponse = null;
			
			socket = Tools.getPlainSocket();
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			
			String serializedRequest = serialize(request);
			
			String deviceId = Configuration.getInstance().getDeviceId();
			String authHash = Configuration.getInstance().getAuthenticationHash();	
			
			String sessionInitPayload = null;
			if(deviceId != null && authHash != null)
			{
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
					"</header>"+
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
			
			log.info("--------------------------------------------------");
			log.info("SessionPayload="+sessionInitPayload);
			log.info("--------------------------------------------------");
			
			IOUtilities.writePayLoad(sessionInitPayload, os);
			
			
			String response = IOUtilities.readServerResponse(is);
			IOUtilities.writePayLoad(serializedRequest, os);
			response = IOUtilities.readServerResponse(is);
			
			log.info("MobileService Response-------------------------------------------------------");
			log.info(response);
			log.info("-----------------------------------------------------------------------------");
			
			beanResponse = parse(response);
			
			return beanResponse;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}		
		finally
		{
			if(socket != null)
			{
				try
				{
					socket.close();
				}
				catch(IOException ioe){}
			}
		}
	}
	
	public static Response invoke(MobileBeanRunner runner,Request request)
	{
		Socket socket = null;
		try
		{
			Response beanResponse = null;
			
			socket = Tools.getPlainSocket();
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			
			String serializedRequest = serialize(request);
			
			String deviceId = runner.getConfiguration().getDeviceId();
			String authHash = runner.getConfiguration().getAuthenticationHash();	
			
			String sessionInitPayload = null;
			if(deviceId != null && authHash != null)
			{
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
					"</header>"+
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
			
			//log.info("--------------------------------------------------");
			//log.info("SessionPayload="+sessionInitPayload);
			//log.info("--------------------------------------------------");
			
			IOUtilities.writePayLoad(sessionInitPayload, os);
			
			
			String response = IOUtilities.readServerResponse(is);
			IOUtilities.writePayLoad(serializedRequest, os);
			response = IOUtilities.readServerResponse(is);
			
			//log.info("MobileService Response-------------------------------------------------------");
			//log.info(response);
			//log.info("-----------------------------------------------------------------------------");
			
			beanResponse = parse(response);
			
			return beanResponse;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}		
		finally
		{
			if(socket != null)
			{
				try
				{
					socket.close();
				}
				catch(IOException ioe){}
			}
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	private static String serialize(Request request)
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
				buffer.append("<string>"+names[i]+"</string>\n");
				buffer.append("<string>"+value+"</string>\n");
				buffer.append("</entry>\n");
			}
		}
		
		buffer.append("</map>\n");
		
		return buffer.toString();
	}
		
	private static Response parse(String response) throws Exception
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
			this.fullPath.append("/"+qName.trim());
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
