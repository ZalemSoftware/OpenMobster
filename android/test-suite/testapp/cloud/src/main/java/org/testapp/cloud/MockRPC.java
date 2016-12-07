/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.testapp.cloud;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;

/**
 * Service Bean that will be invoked from the device. It returns a "List" of Email "Subject" values.
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/user/profile")
public class MockRPC implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(MockRPC.class);
	
	public MockRPC()
	{
		
	}
	
	public void start()
	{
	}
	
	public Response invoke(Request request) 
	{	
		Response response = new Response();
		
		String firstName = request.getAttribute("firstName");
		String lastName = request.getAttribute("lastName");
		
		log.info("******************************************");
		log.info("FirstName: "+firstName);
		log.info("LastName: "+lastName);
		log.info("******************************************");
		
		response.setAttribute("phoneNumber", "867-5309");
		
		return response;
	}
}
