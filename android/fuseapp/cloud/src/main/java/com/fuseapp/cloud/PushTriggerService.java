/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.fuseapp.cloud;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/fuseapp/pushtrigger")
public class PushTriggerService implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(PushTriggerService.class);
	private DemoDataRepository demoRepository;
	
	public PushTriggerService()
	{
		
	}
	
	public void start()
	{
		log.info("--------------------------------------------------------------------------");
		log.info("/fuseapp/pushtrigger: was successfully started....");
		log.info("--------------------------------------------------------------------------");
	}
	
		
	public DemoDataRepository getDemoRepository()
	{
		return demoRepository;
	}

	public void setDemoRepository(DemoDataRepository demoRepository)
	{
		this.demoRepository = demoRepository;
	}

	public Response invoke(Request request) 
	{	
		log.info("-------------------------------------------------");
		log.info(this.getClass().getName()+" successfully invoked..");		
		log.info("-------------------------------------------------");
		
		for(int i=0; i<5; i++)
		{
			this.demoRepository.addNewBean();
		}
		
		return null;
	}
}
