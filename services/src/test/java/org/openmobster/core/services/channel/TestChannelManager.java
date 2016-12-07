/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.channel;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.Provisioner;

import org.openmobster.core.common.bus.Bus;

/**
 * @author openmobster@gmail.com
 */
public class TestChannelManager extends TestCase
{
	private static Logger log = Logger.getLogger(TestChannelManager.class);
	
	private String deviceId = "IMEI:12345";
	
	public void setUp()
	{
		ServiceManager.bootstrap();

		Provisioner.getInstance().registerIdentity("blah@gmail.com", "blahblah");
		Provisioner.getInstance().registerDevice("blah@gmail.com", "blahblah", this.deviceId);
	}
	
	public void tearDown()
	{
		ServiceManager.shutdown();
	}
	
	public void testChannelEventPropagation() throws Exception
	{	
		//Deprecated
		/*Thread.currentThread().sleep(10000);
		
		//restart the BusReader thread
		Bus.restartBus("twitterChannel");
		log.info("------------------------------------------------");
		log.info("Channel Bus Restarted......................");
		log.info("------------------------------------------------");
		
		Thread.currentThread().sleep(10000);*/
	}
}
