/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.testapp.cloud;

/**
 * Loads demo data into the ticket database 
 * 
 * @author openmobster@gmail.com
 */
public class BootstrapData
{
	private static final String[] titles = new String[]{"Issue with NullPointerException","Data did not load",
		"Computer Crashed","Clustering is broken","Search Index Error","Blue Screen of Death","Blob issues with Oracle Driver","Search Appliance unavailable","Where the heck is the Home key?",
		"Where is my Flash on iPhone?"};
	private static final String[] comments = new String[]{"Issue with NullPointerException","Data did not load",
		"Computer Crashed","Clustering is broken","Search Index Error","Blue Screen of Death","Blob issues with Oracle Driver","Search Appliance unavailable","Where the heck is the Home key?",
	"Where is my Flash on iPhone?"};
	
	
	private TicketDS ds;
	
	public BootstrapData()
	{
		
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
		//bootstrap ticket data for testing
		for(int i=0; i<10; i++)
		{
			Ticket local = new Ticket();
			local.setTitle(titles[i]);
			local.setComment(comments[i]);
			
			this.ds.create(local);
		}
	}
	
	public void stop()
	{
	}
}
