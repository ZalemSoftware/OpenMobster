/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.d2d;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This represents an incoming message
 *
 * @author openmobster@gmail.com
 */
public final class D2DMessage implements Serializable
{
	private String from;
	private String to;
	private String message;
	private String senderDeviceId;
	private String timestamp;
	
	public D2DMessage()
	{
		
	}

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getTo()
	{
		return to;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getSenderDeviceId()
	{
		return senderDeviceId;
	}

	public void setSenderDeviceId(String senderDeviceId)
	{
		this.senderDeviceId = senderDeviceId;
	}

	public String getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}
	
	/**
	 * Produces an XML representation of the state of the message
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("<d2d-message>\n");
		buffer.append("<message><![CDATA["+this.message+"]]></message>\n");
		buffer.append("<to><![CDATA["+this.to+"]]></to>\n");
		buffer.append("<from><![CDATA["+this.from+"]]></from>\n");
		buffer.append("<sender-device-id><![CDATA["+this.senderDeviceId+"]]></sender-device-id>\n");
		buffer.append("<timestamp><![CDATA["+this.timestamp+"]]></timestamp>\n");
		buffer.append("</d2d-message>\n");
		
		return buffer.toString();
	}
	
	/**
	 * Parses the xml representation of the message into a message instance
	 * 
	 * @param xml
	 * @return
	 */
	public static D2DMessage parse(String xml)
	{
		try
		{
			D2DMessage message = new D2DMessage();
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document root = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
			//message
			NodeList local = root.getElementsByTagName("message");
			if(local != null && local.getLength()>0)
			{
				Element element = (Element)local.item(0);
				String value = element.getFirstChild().getNodeValue().trim();
				message.message = value;
			}
			
			//to
			local = root.getElementsByTagName("to");
			if(local != null && local.getLength()>0)
			{
				Element element = (Element)local.item(0);
				String value = element.getFirstChild().getNodeValue().trim();
				message.to = value;
			}
			
			//from
			local = root.getElementsByTagName("from");
			if(local != null && local.getLength()>0)
			{
				Element element = (Element)local.item(0);
				String value = element.getFirstChild().getNodeValue().trim();
				message.from = value;
			}
			
			//sender-device-id
			local = root.getElementsByTagName("sender-device-id");
			if(local != null && local.getLength()>0)
			{
				Element element = (Element)local.item(0);
				String value = element.getFirstChild().getNodeValue().trim();
				message.senderDeviceId = value;
			}
			
			//timestamp
			local = root.getElementsByTagName("timestamp");
			if(local != null && local.getLength()>0)
			{
				Element element = (Element)local.item(0);
				String value = element.getFirstChild().getNodeValue().trim();
				message.timestamp = value;
			}
			
			return message;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			return null;
		}
	}
}