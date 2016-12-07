/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.openmobster.core.dataService.server.ConnectionRequest;

/**
 * @author openmobster@gmail.com
 */
public class TestConnectionRequest extends TestCase
{
	private static Logger log = Logger.getLogger(TestConnectionRequest.class);
	
	public void testSimpleRequest()
	{
		String payload = 
		"<request>" +
			"<header>" +
				"<name>device-id</name>"+
				"<value><![CDATA[IMEI:8675309]]></value>"+
			"</header>"+
			"<header>" +
				"<name>nonce</name>"+
				"<value><![CDATA[blahblah]]></value>"+
			"</header>"+
			"<header>" +
				"<name>command</name>"+
				"<value>notify</value>"+
			"</header>"+
			"<header>" +
				"<name>processor</name>"+
				"<value>sync</value>"+
			"</header>"+
			"<header>" +
				"<name>channel</name>"+
				"<value><![CDATA[twitter]]></value>"+
			"</header>"+
			"<header>" +
				"<name>platform</name>"+
				"<value><![CDATA[android]]></value>"+
			"</header>"+
		"</request>";
		
		ConnectionRequest request = ConnectionRequest.getInstance(payload);
		
		assertEquals(request.getDeviceId(),"IMEI:8675309");
		assertEquals(request.getNonce(),"blahblah");
		assertEquals(request.getCommand(),"notify");
		assertEquals(request.getProcessor(),"sync");
		assertEquals(request.getHeader("channel"),"twitter");
		assertEquals(request.getHeader("platform"),"android");
	}
}
