/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.event;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.transaction.TransactionHelper;

import org.openmobster.core.services.channel.ChannelBeanMetaData;
import org.openmobster.core.services.channel.ChannelUpdateType;
import org.openmobster.core.services.event.ChannelEvent;
import org.openmobster.core.services.event.ChannelEventListener;

import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.AppToChannelAssociation;

/**
 * @author openmobster@gmail.com
 */
public class SyncChannelEventListener implements ChannelEventListener
{
	private static Logger log = Logger.getLogger(SyncChannelEventListener.class);
	
	private ServerSyncEngine syncEngine;
	
		
	public ServerSyncEngine getSyncEngine() 
	{
		return syncEngine;
	}


	public void setSyncEngine(ServerSyncEngine syncEngine) 
	{
		this.syncEngine = syncEngine;
	}


	public void channelUpdated(ChannelEvent event) 
	{
		List<ChannelBeanMetaData> updateInfo = (List<ChannelBeanMetaData>)event.getAttribute(ChannelEvent.metadata);
		
		if(updateInfo != null)
		{
			Map<String, List> changeLogMap = new HashMap<String, List>();
			for(ChannelBeanMetaData cour: updateInfo)
			{
				String deviceId = cour.getDeviceId();
				String beanId = cour.getBeanId();
				String channel = cour.getChannel();
				ChannelUpdateType updateType = cour.getUpdateType();
				
				List changelog = this.findChangeLog(changeLogMap, deviceId);
				
				ChangeLogEntry entry = new ChangeLogEntry();
				entry.setNodeId(channel);
				entry.setTarget(deviceId);
				entry.setRecordId(beanId);
				
				if(updateType == ChannelUpdateType.ADD)
				{
					entry.setOperation(ServerSyncEngine.OPERATION_ADD);
				}
				else if(updateType == ChannelUpdateType.REPLACE)
				{
					entry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
				}
				else if(updateType == ChannelUpdateType.DELETE)
				{
					entry.setOperation(ServerSyncEngine.OPERATION_DELETE);
				}
				
				changelog.add(entry);
			}
			
			//Persist the ChangeLog related information with the sync engine
			Set<String> updatedDevices = changeLogMap.keySet();
			for(String updatedDevice: updatedDevices)
			{
				List changelogEntries = changeLogMap.get(updatedDevice);
				this.updateChangeLog(updatedDevice, changelogEntries);
			}
		}
	}
	
	private void updateChangeLog(String deviceId, List changelogEntries)
	{
		boolean started = TransactionHelper.startTx();
		try
		{
			if(changelogEntries != null && !changelogEntries.isEmpty())
			{
				Map<String,List> channelMap = new HashMap<String,List>();
				for(Object local:changelogEntries)
				{
					ChangeLogEntry entry = (ChangeLogEntry)local;
					String channel = entry.getNodeId();
					
					List channelEntries = this.findChannelMap(channelMap, channel);
					channelEntries.add(entry);
				}
				
				Set<String> channels = channelMap.keySet();
				for(String channel:channels)
				{
					List channelChangeLog = channelMap.get(channel);
					Set<String> apps = AppToChannelAssociation.getApps(deviceId, channel);
					if(apps != null && !apps.isEmpty())
					{
						for(String app:apps)
						{
							this.syncEngine.addChangeLogEntries(deviceId, app, channelChangeLog);
						}
					}
				}
			}
			if(started)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(started)
			{
				TransactionHelper.rollbackTx();
			}
			
			ErrorHandler.getInstance().handle(e);
		}
	}
	
	private List findChangeLog(Map<String, List> changeLogMap,String deviceId)
	{
		List changeLog = changeLogMap.get(deviceId);
		
		if(changeLog == null)
		{
			changeLog = new ArrayList();
			changeLogMap.put(deviceId, changeLog);
		}
		
		return changeLog;
	}
	
	private List findChannelMap(Map<String, List> channelMap, String channel)
	{
		List entries = (List)channelMap.get(channel);
		
		if(entries == null)
		{
			entries = new ArrayList();
			channelMap.put(channel, entries);
		}
		
		return entries;
	}
}
