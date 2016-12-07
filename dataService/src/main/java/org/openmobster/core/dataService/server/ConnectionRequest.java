/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.openmobster.core.common.AttributeManager;
import org.openmobster.core.common.XMLUtilities;

/**
 * @author openmobster@gmail.com
 */
public final class ConnectionRequest implements Serializable
{
	private static final long serialVersionUID = 4742981385043275266L;
	
	private static Logger log = Logger.getLogger(ConnectionRequest.class);
	
	private AttributeManager attributeManager;
	
	private ConnectionRequest()
	{
		this.attributeManager = new AttributeManager();
	}
	
	public static ConnectionRequest getInstance(String payload)
	{
		ConnectionRequest request = new ConnectionRequest();
		request.attributeManager = parse(payload);
		return request;
	}
	
	public String getHeader(String headerName)
	{
		return this.attributeManager.getAttribute(headerName);
	}
	
	public String[] getHeaderNames()
	{
		return this.attributeManager.getNames();
	}
	
	public String getDeviceId()
	{
		return this.getHeader("device-id");
	}
	
	public String getNonce()
	{
		return this.getHeader("nonce");
	}
	
	public String getCommand()
	{
		return this.getHeader("command");
	}
	
	public String getProcessor()
	{
		return this.getHeader("processor");
	}
	
	public boolean isAnonymous()
	{
		String deviceId = this.getDeviceId();
		String nonce = this.getNonce();
		
		if(deviceId == null && nonce == null)
		{
			return true;
		}
		
		return false;
	}
	//-----------------------------------------------------------------------------------------------------------------------
	private static AttributeManager parse(String payload)
	{
		try
		{
			if(payload == null)
			{
				throw new IllegalArgumentException("Payload cannot be null!!");
			}
			
			AttributeManager attributeManager = new AttributeManager();
			
			Document document = XMLUtilities.parse(payload);
			
			NodeList headerNodes = document.getElementsByTagName("header");
			if(headerNodes != null)
			{
				for(int i=0,length=headerNodes.getLength(); i<length; i++)
				{
					Element header = (Element)headerNodes.item(i);
					Element name = (Element)header.getElementsByTagName("name").item(0);
					Element value = (Element)header.getElementsByTagName("value").item(0);
					
					attributeManager.setAttribute(name.getTextContent(), value.getTextContent());
				}
			}
			
			return attributeManager;
		}
		catch(final Throwable e)
		{
			log.error(ConnectionRequest.class.getName(), e);
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * Estrutura adicionada na versão 2.4-M3.1
	 * Obtém o token de autenticação enviado do dispsitivo.
	 */
	
	public String getAuthenticationToken() {
		return this.getHeader("authToken");
	}
}
