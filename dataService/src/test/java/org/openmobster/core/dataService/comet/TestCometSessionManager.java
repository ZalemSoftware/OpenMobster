/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.comet;

import java.util.List;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.common.bus.Bus;
import org.openmobster.core.common.bus.BusMessage;
import org.openmobster.core.dataService.Constants;

/**
 * @author openmobster@gmail.com
 */
public class TestCometSessionManager extends TestCase 
{
	private static Logger log = Logger.getLogger(TestCometSessionManager.class);
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();		
		
		//Provision some devices
		Provisioner provisioner = Provisioner.getInstance();	
		
		
		provisioner.registerIdentity("blah2@gmail.com", "blahblah2");
		provisioner.registerDevice("blah2@gmail.com", "blahblah2", "IMEI:4930051");
		
		provisioner.registerIdentity("blah@gmail.com", "blahblah");
		provisioner.registerDevice("blah@gmail.com", "blahblah", "IMEI:4930052");				
	}
	
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}
	//-----------------------------------------------------------------------------------------------------
	public void testCometSessionManagerBootup() throws Exception
	{
		CometSessionManager sessionManager = CometSessionManager.getInstance();
		
		List<CometSession> cometSessions = sessionManager.getCometSessions();
		
		log.info("******************************************");
		for(CometSession cometSession:cometSessions)
		{	
			log.info("Comet Session Id: "+cometSession.getUri());
		}
		log.info("*****************************************");
		
		assertTrue("Should not be empty!!", cometSessions != null && !cometSessions.isEmpty());
		assertEquals("Must have two sessions", 2, cometSessions.size());
	}
	
	public void testSendingCometMessage() throws Exception
	{
		CometSessionManager sessionManager = CometSessionManager.getInstance();		
		List<CometSession> cometSessions = sessionManager.getCometSessions();
		
		for(CometSession deviceSession: cometSessions)
		{
			BusMessage busMessage = new BusMessage();
			busMessage.setBusUri(deviceSession.getUri());
			busMessage.setSenderUri("channelName");
			
			StringBuilder commandBuilder = new StringBuilder();
			commandBuilder.append(Constants.command+"="+Constants.sync+Constants.separator);
			commandBuilder.append(Constants.service+"="+"channelName");						
			String command = commandBuilder.toString()+Constants.endOfCommand;
			
			busMessage.setAttribute(Constants.command, command);
			
			Bus.sendMessage(busMessage);
		}
	}
}
