/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.d2d;

import org.apache.log4j.Logger;
import org.openmobster.device.api.service.MobileService;
import org.openmobster.device.api.service.Request;
import org.openmobster.device.api.service.Response;

import junit.framework.TestCase;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestSend extends TestCase
{
	private static Logger log = Logger.getLogger(TestSend.class);
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testSend() throws Exception
	{
		Request request = new Request("/d2d/send");		
		request.setAttribute("from", "from1@gmail.com");		
		request.setAttribute("to", "blah2@gmail.com");
		request.setAttribute("message", "Hello World This Rocks!!!");
		request.setAttribute("source_deviceid", "IMEI:8675309");
		request.setAttribute("app_id", "com.chat.android.app");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		log.info("Status Message: "+response.getStatusMsg());
	}
	
	public void testPush() throws Exception
	{
		Request request = new Request("/test/unit/push");		
		request.setAttribute("app-id", "com.chat.android.app");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		log.info("Status Message: "+response.getStatusMsg());
	}
}
