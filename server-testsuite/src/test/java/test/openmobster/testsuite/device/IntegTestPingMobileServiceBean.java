/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.testsuite.device;

import org.apache.log4j.Logger;

import org.openmobster.device.api.service.Request;
import org.openmobster.device.api.service.Response;
import org.openmobster.device.api.service.MobileService;

/**
 * @author openmobster@gmail.com
 */
public class IntegTestPingMobileServiceBean extends AbstractSync 
{
	private static Logger log = Logger.getLogger(IntegTestPingMobileServiceBean.class);
		
	//----------------------------------------------------------------------------------------------------
	public void test() throws Exception
	{						
		Request request = new Request("mockMobileService");		
		request.setAttribute("input1", "ping");		
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		assertNotNull("Response should not be null!!", response);		
		
		String input1 = response.getAttribute("input1");
		assertEquals("response://ping", input1);
		
		log.info("Info1="+input1);
	}
	//-----------------------------------------------------------------------------------------------------	
}
