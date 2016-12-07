/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.testapp.cloud;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 * 'TicketChannel' is a channel that mobilizes the tickets stored in a CRM system. It integrates with
 * the core 'Sync' + 'Push' engines
 * 
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="testapp_ticket_channel", mobileBeanClass="org.testapp.cloud.Ticket")
public class TicketChannel implements Channel
{
	private TicketDS ds;
	
		
	public TicketDS getDs()
	{
		return ds;
	}

	public void setDs(TicketDS ds)
	{
		this.ds = ds;
	}
	
	public void start()
	{
		
	}

	public void stop()
	{
		
	}
	//-------Used by 'Sync'-----------------------------------------------------------------------------------------
	/**
	 * 'bootup' provides just enough number of ticket instances to make sure the mobile app is functional
	 * 
	 * This saves the user to having to wait on a long sync cycle before app can be used. How many instances are provided
	 * at channel boot time is completely at the discretion of the App requirement
	 */
	public List<? extends MobileBean> bootup()
	{
		List<Ticket> bootup = new ArrayList<Ticket>();
		
		List<Ticket> all = this.ds.readAll();
		if(all != null)
		{
			for(int i=0; i<5; i++)
			{
				bootup.add(all.get(i));
			}
		}
		
		return bootup; 
	}
	
	/**
	 * Reads all the instances stored in the db
	 */
	public List<? extends MobileBean> readAll()
	{
		return this.ds.readAll();
	}

	/**
	 * Reads a specific ticket instance based on the unique 'ticketId'
	 */
	public MobileBean read(String ticketId)
	{
		return this.ds.readByTicketId(ticketId);
	}

	/**
	 * Adds a new ticket created on the device to the backend db
	 */
	public String create(MobileBean mobileBean)
	{
		Ticket local = (Ticket)mobileBean;
		return this.ds.create(local);
	}

	/**
	 * Synchronizes device side update with the backend db
	 */
	public void update(MobileBean mobileBean)
	{
		Ticket local = (Ticket)mobileBean;
		this.ds.update(local);
	}
	
	/**
	 * Deletes any instances that are deleted on the device side
	 */
	public void delete(MobileBean mobileBean)
	{
		Ticket local = (Ticket)mobileBean;
		this.ds.delete(local);
	}
	//--------Used by 'Push'---Leaving Push alone for now...This sample is focused on the HTML5 client side of things----------------------------------------------------------------
	/**
	 * 'Pushes' any new instances that are created in the db down to the device in real time
	 */
	public String[] scanForNew(Device device, Date lastScanTimestamp)
	{
		return null;
	}
	
	/**
	 * More on this later
	 */
	public String[] scanForUpdates(Device device, Date lastScanTimestamp)
	{
		return null;
	}

	/**
	 * More on this later
	 */
	public String[] scanForDeletions(Device device, Date lastScanTimestamp)
	{
		return null;
	}
}
