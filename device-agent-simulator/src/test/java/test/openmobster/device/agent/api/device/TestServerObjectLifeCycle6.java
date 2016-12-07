/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.api.device;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

import test.openmobster.device.agent.api.Ticket;

/**
 * Usecase being tested:
 * 
 * a/ Device Database and Server Database in sync
 * 
 * b/ A new object is created on the server and "not-synced"
 * 
 * c/ Long Bootup is performed 
 * 
 * @author openmobster@gmail.com
 */
public class TestServerObjectLifeCycle6 extends TestMobileBeanSpec 
{
	public void test() throws Exception
	{
		this.bootService();
		
		String objectId = this.newTickets();
		this.longBootup();	
		
		MobileObject bean = this.runner.getDeviceDatabase().read(this.runner.getService(), objectId);
		this.assertBean(bean, (Ticket)this.ticketConnector.read(objectId));
	}
	
	private String newTickets() throws Exception
	{
		String objectId = this.createNewServerObject();						
		return objectId;
	}		
}
