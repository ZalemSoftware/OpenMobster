/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.cloud.crm.bootstrap;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;

/**
 * This component is invoked asynchronously by the ticket creation and ticket update forms on the device side
 * This is used to demonstrate the ease of implementing an ajaxian usecase from a native app
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/async/load/spinners")
public class AsyncLoadSpinners implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(AsyncLoadSpinners.class);
	
	public AsyncLoadSpinners()
	{
		
	}
	
	public void start()
	{
		log.info("--------------------------------------------------------------------------");
		log.info("/async/load/spinners: was successfully started....");
		log.info("--------------------------------------------------------------------------");
	}
	
	public Response invoke(Request request) 
	{	
		log.info("-------------------------------------------------");
		log.info(this.getClass().getName()+" successfully invoked...");		
		
		Response response = new Response();
		
		//Customer options
		List<String> customers = new ArrayList<String>();
		customers.add("Apple");
		customers.add("Google");
		customers.add("Oracle");
		customers.add("Microsoft");
		response.setListAttribute("customers", customers);
		
		//Specialist options
		List<String> specialists = new ArrayList<String>();
		specialists.add("Steve J");
		specialists.add("Eric S");
		specialists.add("Larry E");
		specialists.add("Steve B");
		response.setListAttribute("specialists", specialists);
		
		log.info("-------------------------------------------------");
		
		return response;
	}
}
