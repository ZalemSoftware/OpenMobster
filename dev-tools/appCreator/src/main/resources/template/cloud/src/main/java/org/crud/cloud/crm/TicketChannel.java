/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.cloud.crm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.crud.cloud.crm.hibernate.TicketDS;
import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;

/**
 * 'TicketChannel' is a channel that mobilizes the tickets stored in a CRM system. It integrates with
 * the core 'Sync' + 'Push' engines
 * 
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="crm_ticket_channel", mobileBeanClass="org.crud.cloud.crm.Ticket")
public class TicketChannel implements Channel
{
	private TicketDS ds;
	private NewTicketDetector newTicketDetector;
		
	public TicketChannel()
	{
		newTicketDetector=new NewTicketDetector();
	}
	
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
		//Register this device with the NewTicketDetector
		ExecutionContext context = ExecutionContext.getInstance();
		Device device = context.getDevice();
		this.newTicketDetector.load(device);
				
		List<Ticket> bootup = new ArrayList<Ticket>();
		
		List<Ticket> all = this.ds.readAll();
		if(all != null && !all.isEmpty())
		{
			bootup.add(all.get(0));
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
		ExecutionContext context = ExecutionContext.getInstance();
		Device device = context.getDevice();
		Ticket local = (Ticket)mobileBean;
		String syncid=this.ds.create(local);
		newTicketDetector.addSyncId(device,syncid);
		return syncid;
	}

	/**
	 * Synchronizes device side update with the backend db
	 */
	public void update(MobileBean mobileBean)
	{
		Ticket local = (Ticket)mobileBean;
		
		Ticket stored = this.ds.readByTicketId(local.getTicketId());
		local.setId(stored.getId());
		
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
	//--------Used by 'Push'-------------------------------------------------------------------
	/**
	 * 'Pushes' any new instances that are created in the db down to the device in real time
	 */
	public String[] scanForNew(Device device, Date lastScanTimestamp)
	{
		Set<String> newBeans = newTicketDetector.scan(device);
		if(newBeans != null && !newBeans.isEmpty())
		{
			return newBeans.toArray(new String[0]); 
		}		
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
	
	//--------------------------------------------------------------------------------------------------------------------
	private class NewTicketDetector 
	{
		private Map<String,Set<String>> device_to_ticket_bean_map;
		
		public NewTicketDetector()
		{		
			this.device_to_ticket_bean_map = new HashMap<String,Set<String>>();		
		}
		
		public void load(Device device)
		{
			String identifier=device.getIdentifier();
			Set<String> allticketBean = this.readAll();
			device_to_ticket_bean_map.put(identifier,allticketBean);
		}
		
		public Set<String> readAll()
		{
			Set <String>allticketSyncId=new HashSet<String>();
			List <Ticket>allticketBeanList=ds.readAll();
			for(Ticket ticketBean:allticketBeanList){
				String syncid=ticketBean.getTicketId();
				allticketSyncId.add(syncid);			
			}		
			return allticketSyncId;
		}
		
		public Set<String> scan(Device device)
		{
			Set <String>newBeanSyncIdSet=new HashSet<String>();
			String deviceIdentifier=device.getIdentifier();
			Set<String> allticketBeanIdentifier = this.readAll();
			Set <String>ticketBeanIdentifierListForDevice=device_to_ticket_bean_map.get(deviceIdentifier);
			if(ticketBeanIdentifierListForDevice==null)
			{
				device_to_ticket_bean_map.put(deviceIdentifier,allticketBeanIdentifier);
			}
			else
			{
				for(String syncid:allticketBeanIdentifier){
					if(!ticketBeanIdentifierListForDevice.contains(syncid)){
						newBeanSyncIdSet.add(syncid);
						ticketBeanIdentifierListForDevice.add(syncid);
					}				
				}			
			}
			return newBeanSyncIdSet;
		}
		
		public void addSyncId(Device device,String syncId){		
			String identifier=device.getIdentifier();
			Set <String>ticketBeanSet=device_to_ticket_bean_map.get(identifier);
			if(ticketBeanSet==null)
			{
				Set <String>syncIdSet=new HashSet<String>();
				syncIdSet.add(syncId);		
				device_to_ticket_bean_map.put(identifier,syncIdSet);
			}
			ticketBeanSet.add(syncId);
		}	
	}
}
