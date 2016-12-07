/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.ServiceInfo;

/**
 * MobileServiceMonitor provides management services for the Mobile Service Beans
 * 
 * @author openmobster@gmail.com
 */
public class MobileServiceMonitor 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(MobileServiceMonitor.class);
	
	/**
	 * 
	 */
	private Map<String, MobileServiceBean> registry = null;
	
	/**
	 * 
	 *
	 */
	public MobileServiceMonitor()
	{
		this.registry = new HashMap<String, MobileServiceBean>(); 
	}
	
	/**
	 * 
	 *
	 */
	public void start()
	{		
		log.info("--------------------------------------------------");
		log.info("Mobile Service Monitor succesfully started.........");
		log.info("--------------------------------------------------");
	}
	
	/**
	 * 
	 *
	 */
	public void stop()
	{
		this.registry = null;
	}
	
	/**
	 * 
	 * @param mobileObjectConnector
	 */
	public void notify(MobileServiceBean mobileServiceBean)
	{
		Class serviceClazz = mobileServiceBean.getClass();
		
		ServiceInfo serviceInfo = (ServiceInfo)serviceClazz.getAnnotation(ServiceInfo.class);
		String serviceName = serviceInfo.uri();
								
		this.registry.put(serviceName, mobileServiceBean);
	}
	
	/**
	 * 
	 * @param connectorId
	 * @return
	 */
	public MobileServiceBean lookup(String serviceName)
	{
		return this.registry.get(serviceName);
	}
	
	public Collection<MobileServiceBean> getConnectors()
	{
		Collection<MobileServiceBean> all = this.registry.values();
		return all;
	}
}
