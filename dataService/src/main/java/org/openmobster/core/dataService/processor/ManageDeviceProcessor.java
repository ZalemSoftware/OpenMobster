/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.processor;

import org.apache.log4j.Logger;

/**
 * 
 * @author openmobster@gmail.com
 */
public class ManageDeviceProcessor implements Processor 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(ManageDeviceProcessor.class);
	
	/**
	 * 
	 */
	private String id = "managedevice";
	
	/**
	 * 
	 */
	public ManageDeviceProcessor()
	{
		
	}
	
	/**
	 * 
	 *
	 */
	public void start()
	{
		
	}
	
	/**
	 * 
	 *
	 */
	public void stop()
	{
		
	}
	
	/**
	 * 
	 */
	public String getId() 
	{
		return this.id;
	}

	/**
	 * 
	 */
	public String process(Input input) throws ProcessorException 
	{		
		log.info("----------------------------------");
		log.info("Device Management successfully called...."+input.getMessage());
		
		return null;
	}
}
