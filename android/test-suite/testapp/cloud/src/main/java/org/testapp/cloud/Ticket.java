/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.testapp.cloud;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 * A 'Ticket' instance represents tickets entered into the CRM db managed by the 'TicketDS' datasource
 * 
 * It is also marked as a 'MobileBean' instance so that it can be managed by the 'Sync' + 'Push' engines of the mobile platform
 * 
 * @author openmobster@gmail.com
 */
public class Ticket implements MobileBean,Serializable 
{
	private static final long serialVersionUID = -13825574505549274L;
	
	private long id; //oid

	@MobileBeanId
	private String ticketId; //uniquely identifies the ticket to the mobile engine..not to be confused with oid
	
	private String title;
	private String comment;
	
	private List<String> mockList;
	
	
	public Ticket()
	{
		this.mockList = new ArrayList<String>();
		for(int i=0; i<10; i++)
		{
			mockList.add("mockList://"+i);
		}
	}

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

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public List<String> getMockList()
	{
		return mockList;
	}

	public void setMockList(List<String> mockList)
	{
		this.mockList = mockList;
	}
}
