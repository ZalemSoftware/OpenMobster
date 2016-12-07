/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import java.util.StringTokenizer;

import org.apache.mina.core.session.IoSession;

import org.openmobster.core.dataService.Constants;
import org.openmobster.core.dataService.comet.CometSessionManager;
import org.openmobster.core.dataService.comet.CometSession;

import org.openmobster.core.services.subscription.SubscriptionManager;

/**
 * @author openmobster@gmail.com
 */
public class CommandController 
{
	private CometSessionManager cometSessionManager;
	private int pulseInterval;
	
	public CommandController()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
			
	public CometSessionManager getCometSessionManager() 
	{
		return cometSessionManager;
	}

	public void setCometSessionManager(CometSessionManager cometSessionManager) 
	{
		this.cometSessionManager = cometSessionManager;
	}	
	
	
	public int getPulseInterval()
	{
		return pulseInterval;
	}

	public void setPulseInterval(int pulseInterval)
	{
		this.pulseInterval = pulseInterval;
	}
	//---------------------------------------------------------------------------------------------------------------------
	public void execute(IoSession session, String payload, ConnectionRequest request) throws Exception
	{						
		String command = request.getCommand();
		if(command.equals(Constants.notify))
		{
			String channel = request.getHeader("channel");
			String platform = request.getHeader("platform");
			String device = request.getHeader("device");
			
			//Start Notification Session
			session.setAttribute(Constants.notify, Boolean.TRUE);				
			
			//Activate a Comet Session associated with this device
			SubscriptionManager subscriptionMgr = (SubscriptionManager)session.getAttribute(Constants.subscription);
			cometSessionManager.activate(subscriptionMgr.getSubscription().getClientId(), 
			session);
			
			//Channel Processing
			StringTokenizer channels = new StringTokenizer(channel, "|");
			while(channels.hasMoreTokens())
			{
				String cour = channels.nextToken().trim();
				subscriptionMgr.addMyChannel(cour);
			}
			
			//Platform Processing
			session.setAttribute("platform",platform);
			
			//Device Processing
			session.setAttribute("device", device);
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	private long computeKeepAliveInterval(String platform, String device)
	{
		long keepAliveInterval = (this.pulseInterval)*60*1000; 
		
		//This customization is needed because due to a TCP stack issue shipped with
		//the 833x blackberry devices, the default READ_WRITE timeout of 2 minutes does
		//not in fact hold. After lots of trial and error, 55 seconds turns out to be 
		//the best option for keep alive interval. After running it on an actual device
		//even with 55 seconds, the device's battery is not impacted too much
		/*if(platform.equals("blackberry") && device.startsWith("833"))
		{
			keepAliveInterval = 55000;
		}*/
		
		return keepAliveInterval;
	}
}
