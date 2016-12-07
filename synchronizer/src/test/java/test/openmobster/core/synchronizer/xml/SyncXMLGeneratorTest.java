/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.synchronizer.xml;

import java.util.List;

import org.openmobster.core.synchronizer.model.*;
import org.openmobster.core.synchronizer.server.*;


import junit.framework.TestCase;

import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.ServiceManager;

/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncXMLGeneratorTest extends TestCase
{
	private SyncXMLGenerator generator = null;
	private SyncObjectGenerator objectGenerator = null;
	private String deviceId = "IMEI:4930051";

	/**
	 * @throws java.lang.Exception
	 */
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		this.generator = new SyncXMLGenerator();
		this.objectGenerator = new SyncObjectGenerator();
	}

	/**
	 * @throws java.lang.Exception
	 */
	public void tearDown() throws Exception
	{
		this.generator = null;
		ServiceManager.shutdown();
	}
	
	/**
	 * 
	 */
	public void testInitMessageBinding() throws Exception
	{
		//Setup the session
		Session session = new Session();
		session.setSessionId(Utilities.generateUID());
		session.setTarget("http://www.openmobster.org/sync-server");
		session.setSource(this.deviceId);
		session.setApp("testApp");
		
		//Add a Message to the package
		SyncMessage message = (SyncMessage)ServiceManager.locate("InitSyncMessage");				
		session.getClientInitPackage().addMessage(message);
		
		
		//Add an alert to the package
		Alert alert = (Alert)ServiceManager.locate("InitAlert");		
		message.addAlert(alert);
		
		
		//Add a Status to the package
		Status status = (Status)ServiceManager.locate("InitStatus");
		message.addStatus(status);
		message.addStatus(status);
		
		
		String xml = this.generator.generateInitMessage(session, message);
		
		System.out.println("-------------------------------");
		System.out.println(xml);
		System.out.println("-------------------------------");
		
		//Recreate business objects from the xml
		Session newSession = this.objectGenerator.parseClientInitMessage(xml);
		SyncMessage newMessage = newSession.getClientInitPackage().findMessage(message.getMessageId());
		System.out.println("-------------------------------");
		System.out.println("Session ID="+newSession.getSessionId());
		System.out.println("Target="+newSession.getTarget());
		System.out.println("Source="+newSession.getSource());
		System.out.println("MessageId="+newMessage.getMessageId());
		System.out.println("MaxMsgSize="+newMessage.getMaxClientSize());
		
		List newAlerts = newMessage.getAlerts();
		if(newAlerts != null)
		{			
			for(int i=0;i<newAlerts.size();i++)
			{
				System.out.println("-------------------------");
				Alert cour = (Alert)newAlerts.get(i);
				System.out.println("Alert Id ="+cour.getCmdId());
				System.out.println("Alert Data ="+cour.getData());
				for(int j=0;j<cour.getItems().size();j++)
				{
					Item item = (Item)cour.getItems().get(j);
					System.out.println("Item Source ="+item.getSource());
					System.out.println("Item Target ="+item.getTarget());
					System.out.println("Item Data ="+item.getData());
					System.out.println("Item Meta ="+item.getMeta());
					System.out.println("Item MoreData ="+item.hasMoreData());
				}
			}
		}
		
		List newStatus = newMessage.getStatus();
		if(newStatus != null)
		{			
			for(int i=0;i<newStatus.size();i++)
			{
				System.out.println("-------------------------");
				Status cour = (Status)newStatus.get(i);
				System.out.println("Status Id ="+cour.getCmdId());
				System.out.println("Status Data ="+cour.getData());
				System.out.println("Status MsgRef ="+cour.getMsgRef());
				System.out.println("Status CmdRef ="+cour.getCmdRef());
				System.out.println("Status Cmd ="+cour.getCmd());
				
				for(int j=0;j<cour.getTargetRefs().size();j++)
				{
					System.out.println("Target Ref ="+cour.getTargetRefs().get(j));					
				}
				
				for(int j=0;j<cour.getSourceRefs().size();j++)
				{
					System.out.println("Source Ref ="+cour.getSourceRefs().get(j));					
				}
				
				for(int j=0;j<cour.getItems().size();j++)
				{
					Item item = (Item)cour.getItems().get(j);
					System.out.println("Item Source ="+item.getSource());
					System.out.println("Item Target ="+item.getTarget());
					System.out.println("Item Data ="+item.getData());
					System.out.println("Item Meta ="+item.getMeta());
					System.out.println("Item MoreData ="+item.hasMoreData());
				}
			}
		}
		System.out.println("-------------------------------");
	}
	
	/**
	 * 
	 */
	public void testSyncMessageBinding() throws Exception
	{
		//Setup the session
		Session session = new Session();
		session.setSessionId(Utilities.generateUID());
		session.setSource("http://www.openmobster.org/sync-server");
		session.setTarget(this.deviceId);
		session.setApp("testApp");
		
		//Add a Message to the package
		SyncMessage message = (SyncMessage)ServiceManager.locate("SyncMessage");				
		session.getClientSyncPackage().addMessage(message);
				
		
		//Add a Status to the package
		Status status = (Status)ServiceManager.locate("SyncStatus");
		message.addStatus(status);
		
		SyncCommand syncCommand = (SyncCommand)ServiceManager.locate("SyncCommand");
		
		//Add SyncCommands
		Add add = (Add)ServiceManager.locate("SyncAdd");		
		syncCommand.getAddCommands().add(add);
		
		
		//Replace SyncCommands
		Replace replace = (Replace)ServiceManager.locate("SyncReplace");		
		syncCommand.getReplaceCommands().add(replace);
		
		
		//Delete SyncCommands
		Delete delete = (Delete)ServiceManager.locate("SyncDelete");		
		syncCommand.getDeleteCommands().add(delete);
		
		
		message.addSyncCommand(syncCommand);
		
		//Add a RecordMap to the syncMessage
		RecordMap recordMap = (RecordMap)ServiceManager.locate("SyncRecordMap");
		message.setRecordMap(recordMap);
		
		String xml = this.generator.generateSyncMessage(session, message);
		
		System.out.println("-------------------------------");
		System.out.println(xml);
		System.out.println("-------------------------------");
		
		//Recreate business objects from the xml
		Session newSession = this.objectGenerator.parseClientSyncMessage(xml);
		SyncMessage newMessage = newSession.getClientSyncPackage().findMessage(message.getMessageId());
		System.out.println("-------------------------------");
		System.out.println("Session ID="+newSession.getSessionId());
		System.out.println("Target="+newSession.getTarget());
		System.out.println("Source="+newSession.getSource());
		System.out.println("MessageId="+newMessage.getMessageId());
		System.out.println("MaxMsgSize="+newMessage.getMaxClientSize());
		
		List newAlerts = newMessage.getAlerts();
		if(newAlerts != null)
		{			
			for(int i=0;i<newAlerts.size();i++)
			{
				System.out.println("-------------------------");
				Alert cour = (Alert)newAlerts.get(i);
				System.out.println("Alert Id ="+cour.getCmdId());
				System.out.println("Alert Data ="+cour.getData());
				for(int j=0;j<cour.getItems().size();j++)
				{
					Item item = (Item)cour.getItems().get(j);
					System.out.println("Item Source ="+item.getSource());
					System.out.println("Item Target ="+item.getTarget());
					System.out.println("Item Data ="+item.getData());
					System.out.println("Item Meta ="+item.getMeta());
					System.out.println("Item MoreData ="+item.hasMoreData());
				}
			}
		}
		
		List newStatus = newMessage.getStatus();
		if(newStatus != null)
		{			
			for(int i=0;i<newStatus.size();i++)
			{
				System.out.println("-------------------------");
				Status cour = (Status)newStatus.get(i);
				System.out.println("Status Id ="+cour.getCmdId());
				System.out.println("Status Data ="+cour.getData());
				System.out.println("Status MsgRef ="+cour.getMsgRef());
				System.out.println("Status CmdRef ="+cour.getCmdRef());
				System.out.println("Status Cmd ="+cour.getCmd());
				
				for(int j=0;j<cour.getTargetRefs().size();j++)
				{
					System.out.println("Target Ref ="+cour.getTargetRefs().get(j));					
				}
				
				for(int j=0;j<cour.getSourceRefs().size();j++)
				{
					System.out.println("Source Ref ="+cour.getSourceRefs().get(j));					
				}
				
				for(int j=0;j<cour.getItems().size();j++)
				{
					Item item = (Item)cour.getItems().get(j);
					System.out.println("Item Source ="+item.getSource());
					System.out.println("Item Target ="+item.getTarget());
					System.out.println("Item Data ="+item.getData());
					System.out.println("Item Meta ="+item.getMeta());
					System.out.println("Item MoreData ="+item.hasMoreData());
				}
			}
		}
		
		List newSyncCommands = newMessage.getSyncCommands();
		if(newSyncCommands != null)
		{
			for(int i=0;i<newSyncCommands.size();i++)
			{
				SyncCommand cour = (SyncCommand)newSyncCommands.get(i);
				
				System.out.println("-------------------------");
				System.out.println("SyncCommand CmdId="+cour.getCmdId());
				System.out.println("SyncCommand Source="+cour.getSource());
				System.out.println("SyncCommand Target="+cour.getTarget());
				System.out.println("SyncCommand Meta="+cour.getMeta());
				System.out.println("SyncCommand NumberOfChanges="+cour.getNumberOfChanges());
				
				//Add
				for(int j=0;j<cour.getAddCommands().size();j++)
				{
					Add newAdd = (Add)cour.getAddCommands().get(j);
					System.out.println("Add-----------------------");
					System.out.println("CmdID="+newAdd.getCmdId());
					System.out.println("Meta="+newAdd.getMeta());
					for(int k=0;k<newAdd.getItems().size();k++)
					{
						Item item = (Item)newAdd.getItems().get(k);
						System.out.println("Item Source ="+item.getSource());
						System.out.println("Item Target ="+item.getTarget());
						System.out.println("Item Data ="+item.getData());
						System.out.println("Item Meta ="+item.getMeta());
						System.out.println("Item MoreData ="+item.hasMoreData());
					}
				}
				
				//Replace
				for(int j=0;j<cour.getReplaceCommands().size();j++)
				{
					Replace newReplace = (Replace)cour.getReplaceCommands().get(j);
					System.out.println("Replace-----------------------");
					System.out.println("CmdID="+newReplace.getCmdId());
					System.out.println("Meta="+newReplace.getMeta());
					for(int k=0;k<newReplace.getItems().size();k++)
					{
						Item item = (Item)newReplace.getItems().get(k);
						System.out.println("Item Source ="+item.getSource());
						System.out.println("Item Target ="+item.getTarget());
						System.out.println("Item Data ="+item.getData());
						System.out.println("Item Meta ="+item.getMeta());
						System.out.println("Item MoreData ="+item.hasMoreData());
					}
				}
				
				//Delete
				for(int j=0;j<cour.getDeleteCommands().size();j++)
				{
					Delete newDelete = (Delete)cour.getDeleteCommands().get(j);
					System.out.println("Delete-----------------------");
					System.out.println("CmdID="+newDelete.getCmdId());
					System.out.println("Meta="+newDelete.getMeta());
					for(int k=0;k<newDelete.getItems().size();k++)
					{
						Item item = (Item)newDelete.getItems().get(k);
						System.out.println("Item Source ="+item.getSource());
						System.out.println("Item Target ="+item.getTarget());
						System.out.println("Item Data ="+item.getData());
						System.out.println("Item Meta ="+item.getMeta());
						System.out.println("Item MoreData ="+item.hasMoreData());
					}
					System.out.println("Archive="+newDelete.isArchive());
					System.out.println("SoftDelete="+newDelete.isSoftDelete());
				}
			}
		}
		
		//Assert Record Map
		RecordMap assertRecordMap = newMessage.getRecordMap();
		assertNotNull(assertRecordMap);
		System.out.println("RecordMap-------------------------");
		System.out.println("CmdId="+assertRecordMap.getCmdId());
		System.out.println("Source="+assertRecordMap.getSource());
		System.out.println("Target="+assertRecordMap.getTarget());
		System.out.println("Meta="+assertRecordMap.getMeta());
		for(int i=0;i<assertRecordMap.getMapItems().size();i++)
		{
			MapItem cour = (MapItem)assertRecordMap.getMapItems().get(i);
			System.out.println("MapItem---------------");
			System.out.println("MapItem Source="+cour.getSource());
			System.out.println("MapItem Target="+cour.getTarget());
		}
		
		System.out.println("-------------------------------");
	}
}
