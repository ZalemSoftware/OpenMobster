/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Manages registry for Storage services
 * 
 * @author openmobster@gmail.com
 */
public class StorageMonitor 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(StorageMonitor.class);
	
	/**
	 * 
	 */
	private Map<String, StorageListener> registry = null;
	
	/**
	 * 
	 *
	 */
	public StorageMonitor()
	{
		this.registry = new HashMap<String, StorageListener>(); 
	}
	
	/**
	 * 
	 *
	 */
	public void start()
	{		
		log.info("--------------------------------------------------");
		log.info("Mobile Storage Monitor succesfully started.........");
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
	public void notify(StorageListener storageListener)
	{
		Class storageListenerClazz = storageListener.getClass();
		
		StorageId storageId = (StorageId)storageListenerClazz.getAnnotation(
		StorageId.class);
		String id = storageId.id();
		
		this.registry.put(id, storageListener);
	}
	
	/**
	 * 
	 * @param connectorId
	 * @return
	 */
	public StorageListener lookup(String storageId)
	{
		return this.registry.get(storageId);
	}
}
