/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import java.util.List;
import java.io.OutputStream;
import java.io.IOException;

import org.openmobster.core.mobileCloud.android.util.XMLUtil;
import org.openmobster.core.mobileCloud.android.filesystem.FileSystem;
import org.openmobster.core.mobileCloud.android.filesystem.File;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncXMLGenerator
{
	/**
	 * 
	 * @param session
	 * @param msgId
	 * @param isClientMessage
	 * @return
	 */
	public String generateInitMessage(Session session,SyncMessage syncMessage)
	{
		String xml = null;
				
		StringBuffer buffer = new StringBuffer();
		buffer.append("<"+SyncXMLTags.SyncML+">\n");
		buffer.append("<"+SyncXMLTags.SyncHdr+">\n");
		buffer.append("<"+SyncXMLTags.VerDTD+">"+"1.1"+"</"+SyncXMLTags.VerDTD+">\n");
		buffer.append("<"+SyncXMLTags.VerProto+">"+"SyncML/1.1"+"</"+SyncXMLTags.VerProto+">\n");
		buffer.append("<"+SyncXMLTags.SessionID+">"+XMLUtil.cleanupXML(session.getSessionId())+"</"+SyncXMLTags.SessionID+">\n");
		buffer.append("<"+SyncXMLTags.App+">"+XMLUtil.cleanupXML(session.getApp())+"</"+SyncXMLTags.App+">\n");
		buffer.append("<"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(session.getSource()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.Target+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(session.getTarget()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Target+">\n");
		
		buffer.append("<"+SyncXMLTags.MsgID+">"+XMLUtil.cleanupXML(syncMessage.getMessageId())+"</"+SyncXMLTags.MsgID+">\n");
		if(syncMessage.isClientInitiated())
		{
			int maxMsgSize = session.getClientInitPackage().findMessage(syncMessage.getMessageId()).getMaxClientSize();
			if(maxMsgSize > 0)
			{
				buffer.append("<"+SyncXMLTags.Meta+">\n");
				buffer.append("<"+SyncXMLTags.MaxMsgSize+" xmlns='"+SyncXMLTags.sycml_metinf+"'>"+maxMsgSize);
				buffer.append("</"+SyncXMLTags.MaxMsgSize+">\n");
				buffer.append("</"+SyncXMLTags.Meta+">\n");
			}
		}
		
		
		Credential credential = syncMessage.getCredential();
		if(credential != null)
		{			
			buffer.append("<"+SyncXMLTags.Cred+">\n");
			buffer.append("<"+SyncXMLTags.Meta+"> xmlns='"+SyncXMLTags.sycml_metinf+"'>" +
			"<"+SyncXMLTags.Type+">"+credential.getType()+"</"+SyncXMLTags.Type+">" +
			"</"+SyncXMLTags.Meta+">");
			buffer.append("<"+SyncXMLTags.Data+">"+credential.getData()+"</"+SyncXMLTags.Data+">\n");
			buffer.append("</"+SyncXMLTags.Cred+">\n");
		}
		
		buffer.append("</"+SyncXMLTags.SyncHdr+">\n");
		
		buffer.append("<"+SyncXMLTags.SyncBody+">\n");
		
		buffer.append(this.generateAlerts(syncMessage.getAlerts()));
		
		buffer.append(this.generateStatus(syncMessage.getStatus()));
		
		if(syncMessage.isFinal())
		{
			buffer.append("<"+SyncXMLTags.Final+"/>\n");
		}
		
		buffer.append("</"+SyncXMLTags.SyncBody+">\n");
		buffer.append("</"+SyncXMLTags.SyncML+">\n");
		
		xml = buffer.toString();
		
		return xml;
	}
	
	/**
	 * 
	 * @param session
	 * @param msgId
	 * @param isClientMessage
	 * @return
	 */
	public String generateSyncMessage(Session session,SyncMessage syncMessage)
	{
		String xml = null;
				
		StringBuffer buffer = new StringBuffer();
		buffer.append("<"+SyncXMLTags.SyncML+">\n");
		buffer.append("<"+SyncXMLTags.SyncHdr+">\n");
		buffer.append("<"+SyncXMLTags.VerDTD+">"+"1.1"+"</"+SyncXMLTags.VerDTD+">\n");
		buffer.append("<"+SyncXMLTags.VerProto+">"+"SyncML/1.1"+"</"+SyncXMLTags.VerProto+">\n");
		buffer.append("<"+SyncXMLTags.SessionID+">"+XMLUtil.cleanupXML(session.getSessionId())+"</"+SyncXMLTags.SessionID+">\n");
		buffer.append("<"+SyncXMLTags.App+">"+XMLUtil.cleanupXML(session.getApp())+"</"+SyncXMLTags.App+">\n");
		buffer.append("<"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(session.getSource()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.Target+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(session.getTarget()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Target+">\n");
		
		buffer.append("<"+SyncXMLTags.MsgID+">"+XMLUtil.cleanupXML(syncMessage.getMessageId())+"</"+SyncXMLTags.MsgID+">\n");
		if(syncMessage.isClientInitiated())
		{
			int maxMsgSize = session.getClientSyncPackage().findMessage(syncMessage.getMessageId()).getMaxClientSize();
			if(maxMsgSize > 0)
			{
				buffer.append("<"+SyncXMLTags.Meta+">\n");
				buffer.append("<"+SyncXMLTags.MaxMsgSize+" xmlns='"+SyncXMLTags.sycml_metinf+"'>"+maxMsgSize);
				buffer.append("</"+SyncXMLTags.MaxMsgSize+">\n");
				buffer.append("</"+SyncXMLTags.Meta+">\n");
			}
		}		
		buffer.append("</"+SyncXMLTags.SyncHdr+">\n");
		
		buffer.append("<"+SyncXMLTags.SyncBody+">\n");
		
		buffer.append(this.generateAlerts(syncMessage.getAlerts()));
		
		buffer.append(this.generateStatus(syncMessage.getStatus()));
		
		buffer.append(this.generateCommands(syncMessage.getSyncCommands()));
		
		if(syncMessage.getRecordMap() != null)
		{
			buffer.append(this.generateRecordMap(syncMessage.getRecordMap()));
		}
		
		if(syncMessage.isFinal())
		{
			buffer.append("<"+SyncXMLTags.Final+"/>\n");
		}
		
		buffer.append("</"+SyncXMLTags.SyncBody+">\n");
		buffer.append("</"+SyncXMLTags.SyncML+">\n");
		
		xml = buffer.toString();
		
		return xml;
	}
	
	public String generateSyncMessageStream(Session session,SyncMessage syncMessage)
	{
		File file = null;
		OutputStream os = null;
		try
		{
			file = FileSystem.getInstance().openOutputStream();
			os = file.getOutputStream();
			
			os.write(("<"+SyncXMLTags.SyncML+">\n").getBytes());
			os.write(("<"+SyncXMLTags.SyncHdr+">\n").getBytes());
			os.write(("<"+SyncXMLTags.VerDTD+">"+"1.1"+"</"+SyncXMLTags.VerDTD+">\n").getBytes());
			os.write(("<"+SyncXMLTags.VerProto+">"+"SyncML/1.1"+"</"+SyncXMLTags.VerProto+">\n").getBytes());
			os.write(("<"+SyncXMLTags.SessionID+">"+XMLUtil.cleanupXML(session.getSessionId())+"</"+SyncXMLTags.SessionID+">\n").getBytes());
			os.write(("<"+SyncXMLTags.App+">"+XMLUtil.cleanupXML(session.getApp())+"</"+SyncXMLTags.App+">\n").getBytes());
			os.write(("<"+SyncXMLTags.Source+">\n").getBytes());
			os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(session.getSource())).getBytes());
			os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
			os.write(("</"+SyncXMLTags.Source+">\n").getBytes());
			os.write(("<"+SyncXMLTags.Target+">\n").getBytes());
			os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(session.getTarget())).getBytes());
			os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
			os.write(("</"+SyncXMLTags.Target+">\n").getBytes());
			
			os.write(("<"+SyncXMLTags.MsgID+">"+XMLUtil.cleanupXML(syncMessage.getMessageId())+"</"+SyncXMLTags.MsgID+">\n").getBytes());
			
			if(syncMessage.isClientInitiated())
			{
				int maxMsgSize = session.getClientSyncPackage().findMessage(syncMessage.getMessageId()).getMaxClientSize();
				if(maxMsgSize > 0)
				{
					os.write(("<"+SyncXMLTags.Meta+">\n").getBytes());
					os.write(("<"+SyncXMLTags.MaxMsgSize+" xmlns='"+SyncXMLTags.sycml_metinf+"'>"+maxMsgSize).getBytes());
					os.write(("</"+SyncXMLTags.MaxMsgSize+">\n").getBytes());
					os.write(("</"+SyncXMLTags.Meta+">\n").getBytes());
				}
			}		
			os.write(("</"+SyncXMLTags.SyncHdr+">\n").getBytes());
			
			os.write(("<"+SyncXMLTags.SyncBody+">\n").getBytes());
			
			this.generateAlertsStream(syncMessage.getAlerts(), os);
			
			this.generateStatusStream(syncMessage.getStatus(),os);
			
			this.generateCommandsStream(syncMessage.getSyncCommands(),os);
			
			if(syncMessage.getRecordMap() != null)
			{
				this.generateRecordMapStream(syncMessage.getRecordMap(),os);
			}
			
			if(syncMessage.isFinal())
			{
				os.write(("<"+SyncXMLTags.Final+"/>\n").getBytes());
			}
			
			os.write(("</"+SyncXMLTags.SyncBody+">\n").getBytes());
			os.write(("</"+SyncXMLTags.SyncML+">\n").getBytes());
			
			os.flush();
			
			return file.getName();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
			return null;
		}
		finally
		{
			if(os != null)
			{
				try{os.close();}catch(Exception e){}
			}
		}
	}
	
	private void generateAlertsStream(List<Alert> alerts, OutputStream os) throws IOException
	{
		for(Alert alert: alerts)
		{
			os.write(("<"+SyncXMLTags.Alert+">\n").getBytes());
			
			os.write(("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(alert.getCmdId())+"</"+SyncXMLTags.CmdID+">\n").getBytes());
			os.write(("<"+SyncXMLTags.Data+">"+alert.getData()+"</"+SyncXMLTags.Data+">\n").getBytes());
			
			this.generateItemsStream(alert.getItems(),os);
						
			os.write(("</"+SyncXMLTags.Alert+">\n").getBytes());
		}
	}
	
	private void generateItemsStream(List<Item> items,OutputStream os) throws IOException
	{
		for(Item item:items)
		{			
			os.write(("<"+SyncXMLTags.Item+">\n").getBytes());
			
			if(item.getSource() != null)
			{
				os.write(("<"+SyncXMLTags.Source+">\n").getBytes());
				os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(item.getSource())).getBytes());
				os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
				os.write(("</"+SyncXMLTags.Source+">\n").getBytes());
			}
			
			if(item.getTarget() != null)
			{
				os.write(("<"+SyncXMLTags.Target+">\n").getBytes());
				os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(item.getTarget())).getBytes());
				os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
				os.write(("</"+SyncXMLTags.Target+">\n").getBytes());
			}			
			
			if(item.getData() != null)
			{					
				os.write(("<"+SyncXMLTags.Data+">"+
				XMLUtil.addCData(item.getData())+
				"</"+SyncXMLTags.Data+">\n").getBytes());
			}
			
			if(item.getMeta() != null)
			{					
				os.write(("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(item.getMeta())+"</"+SyncXMLTags.Meta+">\n").getBytes());
			}
			
			if(item.hasMoreData())
			{					
				os.write(("<"+SyncXMLTags.MoreData+"/>\n").getBytes());
			}
		
			os.write(("</"+SyncXMLTags.Item+">\n").getBytes());
		}
	}
	
	private void generateStatusStream(List<Status> status,OutputStream os) throws IOException
	{
		for(Status cour:status)
		{
			os.write(("<"+SyncXMLTags.Status+">\n").getBytes());
			
			os.write(("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n").getBytes());
			os.write(("<"+SyncXMLTags.Data+">"+cour.getData()+"</"+SyncXMLTags.Data+">\n").getBytes());
			os.write(("<"+SyncXMLTags.MsgRef+">"+cour.getMsgRef()+"</"+SyncXMLTags.MsgRef+">\n").getBytes());
			os.write(("<"+SyncXMLTags.CmdRef+">"+cour.getCmdRef()+"</"+SyncXMLTags.CmdRef+">\n").getBytes());
			os.write(("<"+SyncXMLTags.Cmd+">"+cour.getCmd()+"</"+SyncXMLTags.Cmd+">\n").getBytes());
			
			List<String> targetRefs = cour.getTargetRefs();
			for(String ref:targetRefs)
			{				
				os.write(("<"+SyncXMLTags.TargetRef+">"+ref+"</"+SyncXMLTags.TargetRef+">\n").getBytes());
			}
			
			List<String> sourceRefs = cour.getSourceRefs();
			for(String ref:sourceRefs)
			{
				os.write(("<"+SyncXMLTags.SourceRef+">"+ref+"</"+SyncXMLTags.SourceRef+">\n").getBytes());
			}
			
			this.generateItemsStream(cour.getItems(),os);
			
			os.write(("</"+SyncXMLTags.Status+">\n").getBytes());
		}
	}
	
	private void generateCommandsStream(List<SyncCommand> syncCommands,OutputStream os) throws IOException
	{
		for(SyncCommand command:syncCommands)
		{
			os.write(("<"+SyncXMLTags.Sync+">\n").getBytes());
			
			//CmdId
			os.write(("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(command.getCmdId())+"</"+SyncXMLTags.CmdID+">\n").getBytes());
			
			//Source
			if(command.getSource() != null)
			{
				os.write(("<"+SyncXMLTags.Source+">\n").getBytes());
				os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(command.getSource())).getBytes());
				os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
				os.write(("</"+SyncXMLTags.Source+">\n").getBytes());
			}
			
			//Target
			if(command.getTarget() != null)
			{
				os.write(("<"+SyncXMLTags.Target+">\n").getBytes());
				os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(command.getTarget())).getBytes());
				os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
				os.write(("</"+SyncXMLTags.Target+">\n").getBytes());
			}
			
			//Meta
			if(command.getMeta() != null)
			{
				os.write(("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(command.getMeta())+"</"+SyncXMLTags.Meta+">\n").getBytes());
			}
			
			//NumberOfChanges
			if(command.getNumberOfChanges() != null)
			{
				os.write(("<"+SyncXMLTags.NumberOfChanges+">"+XMLUtil.cleanupXML(command.getNumberOfChanges())+"</"+SyncXMLTags.NumberOfChanges+">\n").getBytes());
			}
			
			//Add Commands
			List<Add> commands = command.getAddCommands();
			for(Add cour:commands)
			{
				os.write(("<"+SyncXMLTags.Add+">\n").getBytes());
				
				//CmdId
				os.write(("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n").getBytes());
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					os.write(("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n").getBytes());
				}
				
				//Items
				if(!cour.getItems().isEmpty())
				{
					this.generateItemsStream(cour.getItems(),os);
				}
				
				os.write(("</"+SyncXMLTags.Add+">\n").getBytes());
			}
			
			//Replace Commands
			List<Replace> replace = command.getReplaceCommands();
			for(Replace cour:replace)
			{
				os.write(("<"+SyncXMLTags.Replace+">\n").getBytes());
				
				//CmdId
				os.write(("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n").getBytes());
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					os.write(("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n").getBytes());
				}
				
				//Items
				if(!cour.getItems().isEmpty())
				{
					this.generateItemsStream(cour.getItems(),os);
				}
				
				os.write(("</"+SyncXMLTags.Replace+">\n").getBytes());
			}
			
			//Delete Commands
			List<Delete> delete = command.getDeleteCommands();
			for(Delete cour:delete)
			{
				os.write(("<"+SyncXMLTags.Delete+">\n").getBytes());
				
				//CmdId
				os.write(("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n").getBytes());
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					os.write(("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n").getBytes());
				}
				
				//Items
				if(!cour.getItems().isEmpty())
				{
					this.generateItemsStream(cour.getItems(),os);
				}
				
				//Archive
				if(cour.isArchive())
				{
					os.write(("<"+SyncXMLTags.Archive+"/>\n").getBytes());
				}
				
				//SoftDelete
				if(cour.isSoftDelete())
				{
					os.write(("<"+SyncXMLTags.SftDel+"/>\n").getBytes());
				}
				
				os.write(("</"+SyncXMLTags.Delete+">\n").getBytes());
			}
			
			
			os.write(("</"+SyncXMLTags.Sync+">\n").getBytes());
		}
	}
	
	private void generateRecordMapStream(RecordMap recordMap,OutputStream os) throws IOException
	{
		os.write(("<"+SyncXMLTags.Map+">\n").getBytes());
		
		os.write(("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(recordMap.getCmdId())+"</"+SyncXMLTags.CmdID+">\n").getBytes());
		
		os.write(("<"+SyncXMLTags.Source+">\n").getBytes());
		os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(recordMap.getSource())).getBytes());
		os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
		os.write(("</"+SyncXMLTags.Source+">\n").getBytes());
		
		os.write(("<"+SyncXMLTags.Target+">\n").getBytes());
		os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(recordMap.getTarget())).getBytes());
		os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
		os.write(("</"+SyncXMLTags.Target+">\n").getBytes());
		
		if(recordMap.getMeta() != null && recordMap.getMeta().trim().length()>0)
		{
			os.write(("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(recordMap.getMeta())+"</"+SyncXMLTags.Meta+">\n").getBytes());
		}
		
		List<MapItem> mapItems = recordMap.getMapItems();
		for(MapItem mapItem:mapItems)
		{
			os.write(("<"+SyncXMLTags.MapItem+">\n").getBytes());
			
			os.write(("<"+SyncXMLTags.Source+">\n").getBytes());
			os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(mapItem.getSource())).getBytes());
			os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
			os.write(("</"+SyncXMLTags.Source+">\n").getBytes());
			
			os.write(("<"+SyncXMLTags.Target+">\n").getBytes());
			os.write(("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(mapItem.getTarget())).getBytes());
			os.write(("</"+SyncXMLTags.LocURI+">\n").getBytes());
			os.write(("</"+SyncXMLTags.Target+">\n").getBytes());
			
			os.write(("</"+SyncXMLTags.MapItem+">\n").getBytes());
		}
				
		os.write(("</"+SyncXMLTags.Map+">\n").getBytes());
	}
	
	/**
	 * 
	 * @param anchor
	 * @return
	 */
	public String generateAnchor(Anchor anchor)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<"+SyncXMLTags.Anchor+" xmlns='"+SyncXMLTags.sycml_metinf+"'>\n");
		buffer.append("<"+SyncXMLTags.Last+">"+XMLUtil.cleanupXML(anchor.getLastSync())+"</"+SyncXMLTags.Last+">\n");
		buffer.append("<"+SyncXMLTags.Next+">"+XMLUtil.cleanupXML(anchor.getNextSync())+"</"+SyncXMLTags.Next+">\n");
		buffer.append("</"+SyncXMLTags.Anchor+">\n");
		
		xml = buffer.toString();
		
		return xml;
	}
	//-----------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param alerts
	 * @return
	 */
	private String generateAlerts(List<Alert> alerts)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		
		for(Alert alert: alerts)
		{
			buffer.append("<"+SyncXMLTags.Alert+">\n");
			
			buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(alert.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
			buffer.append("<"+SyncXMLTags.Data+">"+alert.getData()+"</"+SyncXMLTags.Data+">\n");
			
			buffer.append(this.generateItems(alert.getItems()));
						
			buffer.append("</"+SyncXMLTags.Alert+">\n");
		}
		
		xml = buffer.toString();
		
		return xml;
	}
	
	/**
	 * 
	 * @param alerts
	 * @return
	 */
	private String generateStatus(List<Status> status)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		
		for(Status cour:status)
		{
			buffer.append("<"+SyncXMLTags.Status+">\n");
			
			buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
			buffer.append("<"+SyncXMLTags.Data+">"+cour.getData()+"</"+SyncXMLTags.Data+">\n");
			buffer.append("<"+SyncXMLTags.MsgRef+">"+cour.getMsgRef()+"</"+SyncXMLTags.MsgRef+">\n");
			buffer.append("<"+SyncXMLTags.CmdRef+">"+cour.getCmdRef()+"</"+SyncXMLTags.CmdRef+">\n");
			buffer.append("<"+SyncXMLTags.Cmd+">"+cour.getCmd()+"</"+SyncXMLTags.Cmd+">\n");
			
			List<String> targetRefs = cour.getTargetRefs();
			for(String ref:targetRefs)
			{				
				buffer.append("<"+SyncXMLTags.TargetRef+">"+ref+"</"+SyncXMLTags.TargetRef+">\n");
			}
			
			List<String> sourceRefs = cour.getSourceRefs();
			for(String ref:sourceRefs)
			{
				buffer.append("<"+SyncXMLTags.SourceRef+">"+ref+"</"+SyncXMLTags.SourceRef+">\n");
			}
			
			buffer.append(this.generateItems(cour.getItems()));
			
			buffer.append("</"+SyncXMLTags.Status+">\n");
		}
		
		xml = buffer.toString();
		
		return xml;
	}
	
	/**
	 * 
	 * @param items
	 * @return
	 */
	private String generateItems(List<Item> items)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		
		for(Item item:items)
		{			
			buffer.append("<"+SyncXMLTags.Item+">\n");
			
			if(item.getSource() != null)
			{
				buffer.append("<"+SyncXMLTags.Source+">\n");
				buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(item.getSource()));
				buffer.append("</"+SyncXMLTags.LocURI+">\n");
				buffer.append("</"+SyncXMLTags.Source+">\n");
			}
			
			if(item.getTarget() != null)
			{
				buffer.append("<"+SyncXMLTags.Target+">\n");
				buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(item.getTarget()));
				buffer.append("</"+SyncXMLTags.LocURI+">\n");
				buffer.append("</"+SyncXMLTags.Target+">\n");
			}			
			
			if(item.getData() != null)
			{					
				buffer.append("<"+SyncXMLTags.Data+">"+
				XMLUtil.addCData(item.getData())+
				"</"+SyncXMLTags.Data+">\n");
			}
			
			if(item.getMeta() != null)
			{					
				buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(item.getMeta())+"</"+SyncXMLTags.Meta+">\n");
			}
			
			if(item.hasMoreData())
			{					
				buffer.append("<"+SyncXMLTags.MoreData+"/>\n");
			}
		
			buffer.append("</"+SyncXMLTags.Item+">\n");
		}
		
		
		xml = buffer.toString();
		
		return xml;
	}
	
	/**
	 * 
	 * @param syncCommands
	 * @return
	 */
	private String generateCommands(List<SyncCommand> syncCommands)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		for(SyncCommand command:syncCommands)
		{
			buffer.append("<"+SyncXMLTags.Sync+">\n");
			
			//CmdId
			buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(command.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
			
			//Source
			if(command.getSource() != null)
			{
				buffer.append("<"+SyncXMLTags.Source+">\n");
				buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(command.getSource()));
				buffer.append("</"+SyncXMLTags.LocURI+">\n");
				buffer.append("</"+SyncXMLTags.Source+">\n");
			}
			
			//Target
			if(command.getTarget() != null)
			{
				buffer.append("<"+SyncXMLTags.Target+">\n");
				buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(command.getTarget()));
				buffer.append("</"+SyncXMLTags.LocURI+">\n");
				buffer.append("</"+SyncXMLTags.Target+">\n");
			}
			
			//Meta
			if(command.getMeta() != null)
			{
				buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(command.getMeta())+"</"+SyncXMLTags.Meta+">\n");
			}
			
			//NumberOfChanges
			if(command.getNumberOfChanges() != null)
			{
				buffer.append("<"+SyncXMLTags.NumberOfChanges+">"+XMLUtil.cleanupXML(command.getNumberOfChanges())+"</"+SyncXMLTags.NumberOfChanges+">\n");
			}
			
			//Add Commands
			List<Add> commands = command.getAddCommands();
			for(Add cour:commands)
			{
				buffer.append("<"+SyncXMLTags.Add+">\n");
				
				//CmdId
				buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n");
				}
				
				//Items
				if(!cour.getItems().isEmpty())
				{
					buffer.append(this.generateItems(cour.getItems()));
				}
				
				buffer.append("</"+SyncXMLTags.Add+">\n");
			}
			
			//Replace Commands
			List<Replace> replace = command.getReplaceCommands();
			for(Replace cour:replace)
			{
				buffer.append("<"+SyncXMLTags.Replace+">\n");
				
				//CmdId
				buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n");
				}
				
				//Items
				if(!cour.getItems().isEmpty())
				{
					buffer.append(this.generateItems(cour.getItems()));
				}
				
				buffer.append("</"+SyncXMLTags.Replace+">\n");
			}
			
			//Delete Commands
			List<Delete> delete = command.getDeleteCommands();
			for(Delete cour:delete)
			{
				buffer.append("<"+SyncXMLTags.Delete+">\n");
				
				//CmdId
				buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n");
				}
				
				//Items
				if(!cour.getItems().isEmpty())
				{
					buffer.append(this.generateItems(cour.getItems()));
				}
				
				//Archive
				if(cour.isArchive())
				{
					buffer.append("<"+SyncXMLTags.Archive+"/>\n");
				}
				
				//SoftDelete
				if(cour.isSoftDelete())
				{
					buffer.append("<"+SyncXMLTags.SftDel+"/>\n");
				}
				
				buffer.append("</"+SyncXMLTags.Delete+">\n");
			}
			
			
			buffer.append("</"+SyncXMLTags.Sync+">\n");
		}
		
		xml = buffer.toString();
		
		return xml;
	}
	
	/**
	 * 
	 * @param recordMap
	 * @return
	 */
	private String generateRecordMap(RecordMap recordMap)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<"+SyncXMLTags.Map+">\n");
		
		buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtil.cleanupXML(recordMap.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
		
		buffer.append("<"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(recordMap.getSource()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Source+">\n");
		
		buffer.append("<"+SyncXMLTags.Target+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(recordMap.getTarget()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Target+">\n");
		
		if(recordMap.getMeta() != null && recordMap.getMeta().trim().length()>0)
		{
			buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtil.cleanupXML(recordMap.getMeta())+"</"+SyncXMLTags.Meta+">\n");
		}
		
		List<MapItem> mapItems = recordMap.getMapItems();
		for(MapItem mapItem:mapItems)
		{
			buffer.append("<"+SyncXMLTags.MapItem+">\n");
			
			buffer.append("<"+SyncXMLTags.Source+">\n");
			buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(mapItem.getSource()));
			buffer.append("</"+SyncXMLTags.LocURI+">\n");
			buffer.append("</"+SyncXMLTags.Source+">\n");
			
			buffer.append("<"+SyncXMLTags.Target+">\n");
			buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtil.cleanupXML(mapItem.getTarget()));
			buffer.append("</"+SyncXMLTags.LocURI+">\n");
			buffer.append("</"+SyncXMLTags.Target+">\n");
			
			buffer.append("</"+SyncXMLTags.MapItem+">\n");
		}
				
		buffer.append("</"+SyncXMLTags.Map+">\n");
		
		xml = buffer.toString();
		
		return xml;
	}
}
