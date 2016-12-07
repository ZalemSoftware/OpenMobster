/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.dataService.push.PushRPC;
import org.openmobster.core.dataService.push.PushCommandContext;

/**
 * Service Bean that will be invoked from the device. It returns the "Email" selected for viewing by the user
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/test/start/pushrpc")
public class StartPushRPC implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(StartPushRPC.class);
	
	public StartPushRPC()
	{
		
	}
	
	public void start()
	{
		log.info("--------------------------------------------------------------------------");
		log.info("/test/start/pushrpc: was successfully started......................");
		log.info("--------------------------------------------------------------------------");
	}
	
	public Response invoke(Request request) 
	{	
		Response response = new Response();
		
		PushCommandContext context = new PushCommandContext("/handle/rpc/push");
		context.setAttribute("xyz", "abc");
		PushRPC.startPush("blah2@gmail.com",context);
		
		return response;
	}
}
