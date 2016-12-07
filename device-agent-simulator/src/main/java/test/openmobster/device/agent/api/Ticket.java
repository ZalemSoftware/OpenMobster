/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.api;

import java.io.Serializable;
import java.util.List;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 * @author openmobster@gmail.com
 *
 */
public class Ticket implements Serializable, MobileBean 
{
	private static final long serialVersionUID = -8475636428340280221L;
		
	private long id;
	
	@MobileBeanId
	private String ticketId;
	
	private String name;	
	private CustomerInfo customerInfo;
	private Technician technician;
	private Equipment equipment;
	private List<Note> notes;
	private List<Part> parts;	
	
	public Ticket()
	{
		
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getTicketId() 
	{
		return ticketId;
	}

	public void setTicketId(String ticketId) 
	{
		this.ticketId = ticketId;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public CustomerInfo getCustomerInfo() 
	{
		return customerInfo;
	}

	public void setCustomerInfo(CustomerInfo customerInfo) 
	{
		this.customerInfo = customerInfo;
	}
	
	public Technician getTechnician() 
	{
		return technician;
	}
	
	public void setTechnician(Technician technician) 
	{
		this.technician = technician;
	}
	
	public List<Note> getNotes() 
	{
		return notes;
	}
	
	public void setNotes(List<Note> notes) 
	{
		this.notes = notes;
	}
	
	public List<Part> getParts() 
	{
		return parts;
	}
	
	public void setParts(List<Part> parts) 
	{
		this.parts = parts;
	}
	
	public Equipment getEquipment() 
	{
		return equipment;
	}
	
	public void setEquipment(Equipment equipment) 
	{
		this.equipment = equipment;
	}	
}
