/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileContainer;

import org.apache.log4j.Logger;
import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.common.event.Event;
import org.openmobster.core.common.event.EventManager;
import org.openmobster.core.services.MobileObjectMonitor;


/**
 * @author openmobster@gmail.com
 */
public class DeleteMobileBean implements ContainerService
{
	private static Logger log = Logger.getLogger(DeleteMobileBean.class);
	
	private String id;
	private MobileObjectMonitor monitor;
	private EventManager eventManager;
	
	public DeleteMobileBean()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
	{
		this.id = id;
	}
		
	public MobileObjectMonitor getMonitor() 
	{
		return monitor;
	}

	public void setMonitor(MobileObjectMonitor monitor) 
	{
		this.monitor = monitor;
	}
	
	public EventManager getEventManager()
	{
		return eventManager;
	}

	public void setEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	//---------------------------------------------------------------------------------------------------
	public InvocationResponse execute(Invocation invocation) throws InvocationException
	{
		InvocationResponse response = InvocationResponse.getInstance();
		MobileBean bean = null;
		
		String connectorId = invocation.getConnectorId();
		String beanId = invocation.getBeanId();
		
		//Retrieve the correct MobileObjectConnector		
		Channel connector = monitor.lookup(connectorId);
		if(connector == null)
		{
			response.setStatus(InvocationResponse.STATUS_NOT_FOUND);
			return response;
		}
		
		bean = connector.read(beanId);
		if(bean != null)
		{
			connector.delete(bean);
			response.setBeanId(beanId);
			
			//Send a bean deleted event
			Event event = new Event();
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Otimização para usar o id do bean no evento ao invés de buscar o bean inteiro desnecessariamente.
			 */
//			event.setAttribute("mobile-bean",bean);
			event.setAttribute("mobile-bean-id", beanId);
			
			event.setAttribute("action", "delete");
			this.eventManager.fire(event);
		}
		
		
		return response;
	}
}
