/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.api;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;


/**
 * @author openmobster@gmail.com
 *
 */
@ChannelInfo(
		uri="mockMobileTicket", 
		mobileBeanClass="test.openmobster.device.agent.api.Ticket")
public class TicketConnector implements Channel
{
	private static Logger log = Logger.getLogger(TicketConnector.class);
	
	private List<Ticket> newTickets;
	private TicketDS ticketds;
	
	private static boolean newScanned = false;
	private static boolean updateScanned = false;
	private static boolean deleteScanned = false;
	
	public TicketConnector()
	{		
	}
	
	public void start()
	{
		this.newTickets = new ArrayList<Ticket>();
	}	
	
	public void stop()
	{		
		this.newTickets = null;
	}
		
	public TicketDS getTicketds() 
	{
		return ticketds;
	}

	public void setTicketds(TicketDS ticketds) 
	{
		this.ticketds = ticketds;
	}	
		
	public List<Ticket> getNewTickets() 
	{
		return newTickets;
	}

	public void setNewTickets(List<Ticket> newTickets) 
	{
		this.newTickets = newTickets;
	}
	
	public static void resetPush()
	{
		newScanned = false;
		updateScanned = false;
		deleteScanned = false;
	}
	//-------Channel operations---------------------------------------------------------------------------------------------------------------
	public String create(MobileBean mobileBean) 
	{						
		try
		{
			Ticket newTicket = (Ticket)mobileBean;
			String ticketId = this.ticketds.create(newTicket);
			
			//For the testsuite
			this.newTickets.add(newTicket);
			
			return ticketId;
		}
		catch(ModelException mde)
		{
			log.error(this, mde);
			throw new RuntimeException(mde);
		}
	}

	public MobileBean read(String id) 
	{								
		try
		{
			MobileBean mobileTicket = this.ticketds.readByTicketId(id);
			this.ticketds.getHibernateManager().makePOJO(mobileTicket);
			return mobileTicket;
		}
		catch(ModelException mde)
		{
			log.error(this, mde);
			throw new RuntimeException(mde);
		}
	}

	public List<? extends MobileBean> readAll() 
	{		
		try
		{
			List<Ticket> tickets = this.ticketds.readAll();	
			this.ticketds.getHibernateManager().makePOJO(tickets);
			return (List<? extends MobileBean>)tickets;
		}
		catch(ModelException mde)
		{
			log.error(this, mde);
			throw new RuntimeException(mde);
		}
	}
	
	public List<? extends MobileBean> bootup() 
	{		
		List<Ticket> tickets = new ArrayList<Ticket>();
		
		List<Ticket> all = (List<Ticket>)this.readAll();
		tickets.add(all.get(0));
		tickets.add(all.get(1));
	
		return tickets;
	}
	
	public void update(MobileBean mobileBean) 
	{	
		try
		{
			Ticket input = (Ticket)mobileBean;
			
			if(input.getId() == 0)
			{
				Ticket currentTicket = this.ticketds.readByTicketId(input.getTicketId());
				input.setId(currentTicket.getId());
			}
			
			this.ticketds.update(input);
		}
		catch(ModelException mde)
		{
			log.error(this, mde);
			throw new RuntimeException(mde);
		}
	}
	
	public void delete(MobileBean mobileBean) 
	{	
		try
		{
			String ticketId = ((Ticket)mobileBean).getTicketId();
			Ticket ticket = this.ticketds.readByTicketId(ticketId);
			this.ticketds.delete(ticket);
		}
		catch(ModelException mde)
		{
			log.error(this, mde);
			throw new RuntimeException(mde);
		}
	}

	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{		
		if(!newScanned)
		{
			newScanned = true;						
			return new String[]{"new://1","new://2","new://3"};
		}
		else
		{
			return null;
		}
	}

	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{		
		if(!updateScanned)
		{			
			updateScanned = true;						
			return new String[]{"update://1","update://2","update://3"};
		}
		else
		{
			return null;
		}
	}
	
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{		
		if(!deleteScanned)
		{			
			deleteScanned = true;			
			return new String[]{"delete://1","delete://2","delete://3"};
		}
		else
		{
			return null;
		}
	}
}
