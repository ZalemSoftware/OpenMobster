/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.event;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

import org.openmobster.core.push.notification.Notification;
import org.openmobster.core.push.notification.Notifier;
import org.openmobster.core.services.event.NetworkEventListener;
import org.openmobster.core.services.event.NetworkEvent;
import org.openmobster.core.services.channel.ChannelBeanMetaData;


/**
 * @author openmobster@gmail.com
 */
public class ServerEventListener implements NetworkEventListener
{
	private static Logger log = Logger.getLogger(ServerEventListener.class);
	
	private Notifier notifier;
	
		
	public Notifier getNotifier() 
	{
		return notifier;
	}



	public void setNotifier(Notifier notifier) 
	{
		this.notifier = notifier;
	}



	public void serverPush(NetworkEvent event) 
	{								
		List<ChannelBeanMetaData> metadata = (List<ChannelBeanMetaData>)event.
		getAttribute(event.metadata);
		
		if(metadata != null)
		{
			List<Notification> notifications = new ArrayList<Notification>();
			Set<String> cour = new HashSet<String>();
			for(ChannelBeanMetaData info: metadata)
			{
				String device = info.getDeviceId();
				String channel = info.getChannel();
				cour.add(device+"="+channel);
			}
			
			for(String notificationData: cour)
			{
				int index = notificationData.indexOf('=');
				String device = notificationData.substring(0, index);
				String channel = notificationData.substring(index+1);
				
				Notification notification = Notification.createSyncNotification(device, 
						channel);
				notifications.add(notification);
			}
			
			//Send out the notifications
			if(!notifications.isEmpty())
			{
				for(Notification notification: notifications)
				{
					log.debug("Notification----------------------------------------------");
					log.debug("Device: "+notification.getMetaDataAsString("device")+", Channel: "+
					notification.getMetaDataAsString("service"));
					log.debug("----------------------------------------------");
					this.notifier.process(notification);
				}
			}
		}
	}	
}
