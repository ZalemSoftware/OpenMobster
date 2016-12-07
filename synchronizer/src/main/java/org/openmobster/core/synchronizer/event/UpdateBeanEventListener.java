/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.core.common.event.Event;
import org.openmobster.core.common.event.EventListener;
import org.openmobster.core.push.notification.Notification;
import org.openmobster.core.push.notification.Notifier;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ConflictEngine;
import org.openmobster.core.synchronizer.server.engine.ConflictEntry;
import org.openmobster.core.synchronizer.server.engine.Tools;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.core.synchronizer.server.SyncContext;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

/**
 *
 * @author openmobster@gmail.com
 */
public class UpdateBeanEventListener implements EventListener
{
	private static Logger log = Logger.getLogger(UpdateBeanEventListener.class);
	
	private ServerSyncEngine syncEngine = null;
	private ConflictEngine conflictEngine = null;
	private Notifier notifier = null;
	
	
	public ServerSyncEngine getSyncEngine()
	{
		return syncEngine;
	}



	public void setSyncEngine(ServerSyncEngine syncEngine)
	{
		this.syncEngine = syncEngine;
	}
	
	

	public ConflictEngine getConflictEngine()
	{
		return conflictEngine;
	}



	public void setConflictEngine(ConflictEngine conflictEngine)
	{
		this.conflictEngine = conflictEngine;
	}



	public Notifier getNotifier()
	{
		return notifier;
	}



	public void setNotifier(Notifier notifier)
	{
		this.notifier = notifier;
	}



	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void onEvent(Event event)
	{	
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Otimização para usar o id do bean no evento ao invés de buscar o bean inteiro desnecessariamente.
		 */
//		MobileBean mobileBean = (MobileBean)event.getAttribute("mobile-bean");
//		if(mobileBean == null)
//		{
//			return;
//		}
//		String oid = Tools.getOid(mobileBean);
		//Get the Bean that has been updated by a user
		String oid = (String) event.getAttribute("mobile-bean-id");
		if (oid == null) {
			return;
		}
		
		//Make sure this is an 'Update' operation
		String action = (String)event.getAttribute("action");
		if(!action.equalsIgnoreCase("update"))
		{
			return;
		}
		
		//Get data from the environment
		SyncContext context = (SyncContext)ExecutionContext.getInstance().getSyncContext();
		Session session = context.getSession();
		
		String deviceId = session.getDeviceId();
		String channel = session.getChannel();
		String operation = ServerSyncEngine.OPERATION_UPDATE;
		String app = session.getApp();
		
		log.debug("*************************************");
		log.debug("Bean Updated: "+oid);
		log.debug("DeviceId : "+deviceId);
		log.debug("Channel: "+channel);
		log.debug("Operation: "+operation);
		log.debug("App: "+app);
		
		//Get a List of Entries which represent an instance of this bean, but on other devices
		List<ConflictEntry> liveEntries = this.conflictEngine.findLiveEntries(channel, oid);
		if(liveEntries == null || liveEntries.isEmpty())
		{
			return;
		}
		
		Map<String, Notification> pushNotifications = new HashMap<String, Notification>();
		for(ConflictEntry entry:liveEntries)
		{
			if(entry.getDeviceId().equals(deviceId) &&
			   entry.getApp().equals(app) &&
			   entry.getChannel().equals(channel) &&
			   entry.getOid().equals(oid)
			)
			{
				//This is the originally updated bean (ignore)
				continue;
			}
			
			//Update the ChangeLog of this device so that the 'Updated' bean
			//is pushed to the device
			ChangeLogEntry changelogEntry = new ChangeLogEntry();
			changelogEntry.setTarget(entry.getDeviceId());
			changelogEntry.setNodeId(entry.getChannel());
			changelogEntry.setApp(entry.getApp());
			changelogEntry.setOperation(operation);
			changelogEntry.setRecordId(entry.getOid());
			
			//Check and make sure this ChangeLogEntry does not already exist
			boolean exists = this.syncEngine.changeLogEntryExists(changelogEntry);
			if(exists)
			{
				continue;
			}
			
			List entries = new ArrayList();
			entries.add(changelogEntry);
			this.syncEngine.addChangeLogEntries(entry.getDeviceId(), entry.getApp(), entries);
			
			//Prepare a Sync Push Notification for this device and channel
			Notification notification = Notification.createSilentSyncNotification(entry.getDeviceId(), channel);
			pushNotifications.put(entry.getDeviceId(), notification);
		}
		
		//Send Push Notifications
		if(pushNotifications.isEmpty())
		{
			return;
		}
		
		Set<String> deviceIds = pushNotifications.keySet();
		for(String id:deviceIds)
		{
			Notification notification = pushNotifications.get(id);
			
			log.debug("Notification----------------------------------------------");
			log.debug("Device: "+notification.getMetaDataAsString("device")+", Channel: "+
			notification.getMetaDataAsString("service"));
			log.debug("----------------------------------------------");
			
			this.notifier.process(notification);
		}
		log.debug("*************************************");
	}
}
