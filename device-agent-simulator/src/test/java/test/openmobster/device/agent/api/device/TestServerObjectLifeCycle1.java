/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.api.device;

import java.util.List;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

import test.openmobster.device.agent.api.Ticket;

/**
 * Usecase being tested:
 * 
 * a/ Device Database and Server Database in sync
 * 
 * b/ A new object is created on the server and "synced"
 * 
 * c/ The new object is updated on the server and "synced"
 * 
 * d/ The new object is deleted on the server and "synced" 
 * 
 * @author openmobster@gmail.com
 */
public class TestServerObjectLifeCycle1 extends TestMobileBeanSpec 
{
	public void test() throws Exception
	{
		this.bootService();
		
		//create a server side object and sync up
		String objectId = this.newTickets();
		
		//update the server side object and sync up
		this.updateTickets(objectId);
		
		//delete the server side object and sync up
		this.deleteTickets(objectId);
	}	
	
	private String newTickets() throws Exception
	{
		String objectId = this.createNewServerObject();
		
		this.runner.syncService();
		
		List<MobileObject> beans = this.runner.getDeviceDatabase().readByStorage(this.runner.getService());
		assertTrue("On Device Ticket service should not be empty!!!", (beans != null && !beans.isEmpty()));		
		for(MobileObject currBean: beans)
		{		
			if(!currBean.isProxy())
			{
				this.assertBean(currBean, (Ticket)this.ticketConnector.read(currBean.getServerRecordId()));
			}
		}
		
		return objectId;
	}
	
	private void updateTickets(String objectId) throws Exception
	{
		this.updateServerObject(objectId);
		
		this.runner.syncService();
		
		MobileObject bean = this.runner.getDeviceDatabase().read(this.runner.getService(), objectId);
		
		this.assertBean(bean, (Ticket)this.ticketConnector.read(objectId));
	}
	
	private void deleteTickets(String objectId) throws Exception
	{
		this.deleteServerObject(objectId);
		
		this.runner.syncService();
		
		Ticket ticket = (Ticket)this.ticketConnector.read(objectId);
		MobileObject bean = this.runner.getDeviceDatabase().read(this.runner.getService(), objectId);
		
		assertNull("Ticket must be deleted on the server!!!", ticket);
		assertNull("Ticket must be deleted on the device!!!", bean);
	}
}
