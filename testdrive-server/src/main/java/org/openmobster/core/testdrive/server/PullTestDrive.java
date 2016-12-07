/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.testdrive.server;

import org.apache.log4j.Logger;

import org.openmobster.core.dataService.processor.Input;
import org.openmobster.core.dataService.processor.Processor;
import org.openmobster.core.dataService.processor.ProcessorException;

/**
 * @author openmobster@gmail.com
 *
 */
public class PullTestDrive implements Processor
{
	private static Logger log = Logger.getLogger(PullTestDrive.class);
	
	private String id;
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void start()
	{
		log.info("----------------------------------------------");
		log.info("PullTestDrive service successfully started....");
		log.info("----------------------------------------------");
	}

	public String process(Input input) throws ProcessorException
	{
		String payload = input.getMessage();
		
		log.info("-----------------------------------------------");
		log.info("Pull TestDrive: "+payload);
		log.info("-----------------------------------------------");
		
		return "success";
	}
}
