/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cluster;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.Address;

/**
 *
 * @author openmobster@gmail.com
 */
public final class ClusterService extends ReceiverAdapter
{
	private static Logger log = Logger.getLogger(ClusterService.class);
	
	private Set<ClusterListener> services;
	private boolean active;
	
	//Jgroups
	private JChannel channel;
	private boolean started;
	
	public ClusterService()
	{
		this.services = new HashSet<ClusterListener>();
	}
	
	
	
	public boolean isActive()
	{
		return active;
	}



	public void setActive(boolean active)
	{
		this.active = active;
	}



	public void start()
	{
		try
		{
			if(this.active)
			{
				this.channel = new JChannel();
				this.channel.setReceiver(this);
				this.channel.connect("openmobster");
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
	public void stop()
	{
		if(this.channel != null)
		{
			this.channel.close();
		}
		
		//clear the state
		this.services = null;
		this.active = false;
		this.started = false;
	}
	
	public void register(ClusterListener listener)
	{
		this.services.add(listener);
		if(!this.active)
		{
			try
			{
				listener.startService(new ClusterEvent());
			}
			catch(Exception e)
			{
				log.error(this, e);
				throw new RuntimeException(e);
			}
		}
	}
	
	public void startServices()
	{
		try
		{
			if(!started)
			{
				for(ClusterListener service:this.services)
				{
					service.startService(new ClusterEvent());
					started = true;
				}
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void viewAccepted(View view) 
	{
		Address coordinator = view.getMembers().get(0);
		Address myAddress = this.channel.getAddress();
		
		//Check if I am the coordinator...If I am, start all my remaining services
		if(myAddress.toString().equals(coordinator.toString()))
		{
			this.startServices();
		}
	}
}
