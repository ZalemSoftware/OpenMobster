/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.dataService.processor;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="mockMobileService")
public class MockMobileBeanService implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(MockMobileBeanService.class);
	
	public MockMobileBeanService()
	{
		
	}
	
	public Response invoke(Request request) 
	{	
		log.info("-------------------------------------------------");
		log.info(this.getClass().getName()+" successfully invoked...");		
		
		Response response = new Response();
		String[] names = request.getNames();
		for(String name: names)
		{
			String value = request.getAttribute(name);
			log.info("Name="+name+", Value="+value);
			response.setAttribute(name, "response://"+value);
		}		
		log.info("-------------------------------------------------");
		
		return response;
	}
}
