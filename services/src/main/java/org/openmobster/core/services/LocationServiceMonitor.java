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

import org.openmobster.cloud.api.location.LocationServiceBean;
import org.openmobster.cloud.api.location.BeanURI;

/**
 * LocationServiceMonitor provides management services for the Location Service Beans
 * 
 * @author openmobster@gmail.com
 */
public class LocationServiceMonitor 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(LocationServiceMonitor.class);
	
	/**
	 * 
	 */
	private Map<String, LocationServiceBean> registry = null;
	
	/**
	 * 
	 *
	 */
	public LocationServiceMonitor()
	{
		this.registry = new HashMap<String, LocationServiceBean>(); 
	}
	
	/**
	 * 
	 *
	 */
	public void start()
	{		
		log.info("--------------------------------------------------");
		log.info("Location Service Monitor succesfully started.........");
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
	public void notify(LocationServiceBean serviceBean)
	{
		Class serviceClazz = serviceBean.getClass();
		
		BeanURI beanUri = (BeanURI)serviceClazz.getAnnotation(BeanURI.class);
		String serviceName = beanUri.uri();
								
		this.registry.put(serviceName, serviceBean);
	}
	
	/**
	 * 
	 * @param connectorId
	 * @return
	 */
	public LocationServiceBean lookup(String serviceName)
	{
		return this.registry.get(serviceName);
	}
	
	public Collection<LocationServiceBean> getAll()
	{
		Collection<LocationServiceBean> all = this.registry.values();
		return all;
	}
}
