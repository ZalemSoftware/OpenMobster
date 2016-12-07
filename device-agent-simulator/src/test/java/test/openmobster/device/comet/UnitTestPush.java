/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.comet;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.push.PushService;
import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.security.device.Device;


/**
 * Service Bean that will be invoked from the device. It returns the "Email" selected for viewing by the user
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/test/unit/push")
public class UnitTestPush implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(UnitTestPush.class);
	
	public UnitTestPush()
	{
		
	}
	
	public void start()
	{
	}
	
	public Response invoke(Request request) 
	{	
		Response response = new Response();
		
		final Device device = ExecutionContext.getInstance().getDevice();
		
		final String appId = request.getAttribute("app-id");
		
		Thread t = new Thread(new Runnable(){
			public void run()
			{
				PushService pushService = PushService.getInstance(); 
				
				try{Thread.sleep(20000);}catch(Exception e){}
				
				log.info("Starting the Push..............");
				
				pushService.push("blah2@gmail.com", appId, "Hello From Push & Good Morning", "Title & Title", "Details & Details");
			}
		});
		t.start();
		
		return response;
	}
}
