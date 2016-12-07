/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.api;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.common.Utilities;

/**
 * @author openmobster@gmail.com
 *
 */
public class MockDataGenerator 
{
	private TicketDS ticketds;
	
	public MockDataGenerator()
	{
		
	}
		
	public TicketDS getTicketds() 
	{
		return ticketds;
	}

	public void setTicketds(TicketDS ticketds) 
	{
		this.ticketds = ticketds;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	public Ticket newTicket(String ticketId)
	{
		Ticket ticket = new Ticket();
		ticket.setTicketId(ticketId);
		ticket.setName("blahblah chemicals ticket");		
		
		return ticket;
	}
	
	public Technician newTechnician()
	{
		Technician technician = new Technician();
		technician.setEmployeeId("employeeId:"+Utilities.generateUID());
		technician.setName("employeeblahblah");
		technician.setStatus("onsite");
		return technician;
	}
	
	public CustomerInfo newCustomerInfo()
	{
		CustomerInfo customerInfo = new CustomerInfo();
		customerInfo.setCustomerId("customerId:"+Utilities.generateUID());
		customerInfo.setName("blahblah chemicals");
		customerInfo.setComments("Refrigerator part was busted!!");
		
		return customerInfo;
	}
	
	public Equipment newEquipment()
	{
		Equipment equipment = new Equipment();
		equipment.setName("refrigerator");
		return equipment;		
	}
	
	public List<Note> newNotes()
	{
		List<Note> notes = new ArrayList<Note>();
		
		for(int i=0; i<5; i++)
		{
			Note note = new Note();
			note.setNote("note://"+i);
			notes.add(note);
		}
		
		return notes;
	}
	
	public List<Part> newParts()
	{
		List<Part> parts = new ArrayList<Part>();
		
		for(int i=0; i<5; i++)
		{
			Part part = new Part();
			part.setName("part://"+i);
			parts.add(part);
		}
		
		return parts;
	}
	
	public void generatePersistentData() throws Exception
	{
		for(int i=0; i<5; i++)
		{
			Ticket ticket = this.generateTicket("ticket://"+i);
			
			//TODO: don't use this until the Ticket <-> Technician Circular Reference Issue is resolved
			/*if(i >0)
			{				
				Ticket oldTicket = this.ticketds.readByTicketId("ticket://0");								
				
				ticket.setCustomerInfo(oldTicket.getCustomerInfo());				
				ticket.setTechnician(oldTicket.getTechnician());		
				ticket.setEquipment(oldTicket.getEquipment());
			}*/			
			
			//Create the ticket
			if(this.ticketds.readByTicketId(ticket.getTicketId()) == null)
			{
				this.ticketds.create(ticket);
			}
		}
	}
	
	public Ticket generateTransientData(String ticketId) throws Exception
	{
		return this.generateTicket(ticketId);
	}	
	//--------------------------------------------------------------------------------------------------------------------------------------------
	private Ticket generateTicket(String ticketId)
	{
		Ticket ticket = this.newTicket(ticketId);
		
		//Assign customer info
		ticket.setCustomerInfo(this.newCustomerInfo());
		
		//Assign equipment with the ticket
		ticket.setEquipment(this.newEquipment());
				
		//Assign a Technician to the ticket
		ticket.setTechnician(this.newTechnician());
				
		//Assign some notes to the ticket
		ticket.setNotes(this.newNotes());				
				
		//Assign parts to the ticket
		ticket.setParts(this.newParts());
		
		return ticket;
	}
}
