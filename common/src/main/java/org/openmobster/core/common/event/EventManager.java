/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.event;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.common.ServiceManager;

/**
 * TODO: put more meat into this implementation such that emitters and listeners are more decoupled
 * programmatically...For now, this will do
 * 
 * By keeping is stupidly simple, it will be easier to revamp with newer design
 * 
 * @author openmobster@gmail.com
 */
public final class EventManager 
{
	private List<EventListener> listeners;
	
	public EventManager()
	{
		this.listeners = new ArrayList<EventListener>();
	}
	
	public static EventManager getInstance()
	{
		return (EventManager)ServiceManager.locate("common://EventManager");
	}
	//------------------------------------------------------------------------------------
	public void addListener(EventListener listener)
	{
		String listenerClass = listener.getClass().getName();
		for(EventListener local:this.listeners)
		{
			if(local.getClass().getName().equals("org.openmobster.core.services.channel.ChannelDaemon"))
			{
				//makes sure multiple instances of ChannelDaemon class are allowed to receive notifications
				break;
			}
			if(local.getClass().getName().equals(listenerClass))
			{
				return;
			}
		}
		this.listeners.add(listener);
	}
	
	public void removeListener(EventListener listener)
	{
		this.listeners.remove(listener);
	}
	
	public void fire(Event event)
	{
		for(EventListener listener: this.listeners)
		{
			listener.onEvent(event);
		}
	}
}
