/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import java.util.List;

import org.openmobster.core.synchronizer.model.Add;
import org.openmobster.core.synchronizer.model.Alert;
import org.openmobster.core.synchronizer.model.Delete;
import org.openmobster.core.synchronizer.model.Item;
import org.openmobster.core.synchronizer.model.MapItem;
import org.openmobster.core.synchronizer.model.RecordMap;
import org.openmobster.core.synchronizer.model.Replace;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.model.SyncXMLTags;
import org.openmobster.core.synchronizer.model.Credential;
import org.openmobster.core.synchronizer.server.engine.Anchor;

import org.openmobster.core.common.XMLUtilities;




/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncXMLGenerator
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
		buffer.append("<"+SyncXMLTags.SessionID+">"+XMLUtilities.cleanupXML(session.getSessionId())+"</"+SyncXMLTags.SessionID+">\n");
		buffer.append("<"+SyncXMLTags.App+">"+XMLUtilities.cleanupXML(session.getApp())+"</"+SyncXMLTags.App+">\n");
		buffer.append("<"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(session.getSource()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.Target+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(session.getTarget()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Target+">\n");
		
		buffer.append("<"+SyncXMLTags.MsgID+">"+XMLUtilities.cleanupXML(syncMessage.getMessageId())+"</"+SyncXMLTags.MsgID+">\n");
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
		buffer.append("<"+SyncXMLTags.SessionID+">"+XMLUtilities.cleanupXML(session.getSessionId())+"</"+SyncXMLTags.SessionID+">\n");
		buffer.append("<"+SyncXMLTags.App+">"+XMLUtilities.cleanupXML(session.getApp())+"</"+SyncXMLTags.App+">\n");
		buffer.append("<"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(session.getSource()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.Target+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(session.getTarget()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Target+">\n");
		
		buffer.append("<"+SyncXMLTags.MsgID+">"+XMLUtilities.cleanupXML(syncMessage.getMessageId())+"</"+SyncXMLTags.MsgID+">\n");
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
		buffer.append("<"+SyncXMLTags.Last+">"+XMLUtilities.cleanupXML(anchor.getLastSync())+"</"+SyncXMLTags.Last+">\n");
		buffer.append("<"+SyncXMLTags.Next+">"+XMLUtilities.cleanupXML(anchor.getNextSync())+"</"+SyncXMLTags.Next+">\n");
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
	private String generateAlerts(List alerts)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0;i<alerts.size();i++)
		{
			Alert alert = (Alert)alerts.get(i);
			
			buffer.append("<"+SyncXMLTags.Alert+">\n");
			
			buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtilities.cleanupXML(alert.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
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
	private String generateStatus(List status)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0;i<status.size();i++)
		{
			Status cour = (Status)status.get(i);
			
			buffer.append("<"+SyncXMLTags.Status+">\n");
			
			buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtilities.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
			buffer.append("<"+SyncXMLTags.Data+">"+cour.getData()+"</"+SyncXMLTags.Data+">\n");
			buffer.append("<"+SyncXMLTags.MsgRef+">"+cour.getMsgRef()+"</"+SyncXMLTags.MsgRef+">\n");
			buffer.append("<"+SyncXMLTags.CmdRef+">"+cour.getCmdRef()+"</"+SyncXMLTags.CmdRef+">\n");
			buffer.append("<"+SyncXMLTags.Cmd+">"+cour.getCmd()+"</"+SyncXMLTags.Cmd+">\n");
			
			for(int j=0;j<cour.getTargetRefs().size();j++)
			{				
				buffer.append("<"+SyncXMLTags.TargetRef+">"+cour.getTargetRefs().get(j)+"</"+SyncXMLTags.TargetRef+">\n");
			}
			
			for(int j=0;j<cour.getSourceRefs().size();j++)
			{
				buffer.append("<"+SyncXMLTags.SourceRef+">"+cour.getSourceRefs().get(j)+"</"+SyncXMLTags.SourceRef+">\n");
			}
			
			buffer.append(this.generateItems(cour.getItems()));
			
			Credential credential = cour.getCredential();
			if(credential != null)
			{
				buffer.append("<"+SyncXMLTags.Chal+">\n");
				buffer.append("<"+SyncXMLTags.Meta+">\n");
				buffer.append("<"+SyncXMLTags.Type+" xmlns='"+SyncXMLTags.sycml_metinf+"'>");
				buffer.append(credential.getType());
				buffer.append("</"+SyncXMLTags.Type+">");
				buffer.append("<"+SyncXMLTags.Format+" xmlns='"+SyncXMLTags.sycml_metinf+"'>");
				buffer.append(credential.getFormat());
				buffer.append("</"+SyncXMLTags.Format+">");
				buffer.append("<"+SyncXMLTags.NextNonce+" xmlns='"+SyncXMLTags.sycml_metinf+"'>");
				buffer.append(credential.getNextNonce());
				buffer.append("</"+SyncXMLTags.NextNonce+">");
				buffer.append("</"+SyncXMLTags.Meta+">\n");
				buffer.append("</"+SyncXMLTags.Chal+">\n");
			}
			
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
	private String generateItems(List items)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0;i<items.size();i++)
		{
			Item item = (Item)items.get(i);
			
			buffer.append("<"+SyncXMLTags.Item+">\n");
			
			if(item.getSource() != null)
			{
				buffer.append("<"+SyncXMLTags.Source+">\n");
				buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(item.getSource()));
				buffer.append("</"+SyncXMLTags.LocURI+">\n");
				buffer.append("</"+SyncXMLTags.Source+">\n");
			}
			
			if(item.getTarget() != null)
			{
				buffer.append("<"+SyncXMLTags.Target+">\n");
				buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(item.getTarget()));
				buffer.append("</"+SyncXMLTags.LocURI+">\n");
				buffer.append("</"+SyncXMLTags.Target+">\n");
			}			
			
			if(item.getData() != null)
			{					
				buffer.append("<"+SyncXMLTags.Data+">"+
				XMLUtilities.addCData(item.getData())+
				"</"+SyncXMLTags.Data+">\n");
			}
			
			if(item.getMeta() != null)
			{					
				buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtilities.cleanupXML(item.getMeta())+"</"+SyncXMLTags.Meta+">\n");
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
	private String generateCommands(List syncCommands)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<syncCommands.size();i++)
		{
			SyncCommand command = (SyncCommand)syncCommands.get(i);
			buffer.append("<"+SyncXMLTags.Sync+">\n");
			
			//CmdId
			buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtilities.cleanupXML(command.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
			
			//Source
			if(command.getSource() != null)
			{
				buffer.append("<"+SyncXMLTags.Source+">\n");
				buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(command.getSource()));
				buffer.append("</"+SyncXMLTags.LocURI+">\n");
				buffer.append("</"+SyncXMLTags.Source+">\n");
			}
			
			//Target
			if(command.getTarget() != null)
			{
				buffer.append("<"+SyncXMLTags.Target+">\n");
				buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(command.getTarget()));
				buffer.append("</"+SyncXMLTags.LocURI+">\n");
				buffer.append("</"+SyncXMLTags.Target+">\n");
			}
			
			//Meta
			if(command.getMeta() != null)
			{
				buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtilities.cleanupXML(command.getMeta())+"</"+SyncXMLTags.Meta+">\n");
			}
			
			//NumberOfChanges
			if(command.getNumberOfChanges() != null)
			{
				buffer.append("<"+SyncXMLTags.NumberOfChanges+">"+XMLUtilities.cleanupXML(command.getNumberOfChanges())+"</"+SyncXMLTags.NumberOfChanges+">\n");
			}
			
			//Add Commands
			for(int j=0;j<command.getAddCommands().size();j++)
			{
				Add cour = (Add)command.getAddCommands().get(j);
				buffer.append("<"+SyncXMLTags.Add+">\n");
				
				//CmdId
				buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtilities.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtilities.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n");
				}
				
				//Items
				if(!cour.getItems().isEmpty())
				{
					buffer.append(this.generateItems(cour.getItems()));
				}
				
				buffer.append("</"+SyncXMLTags.Add+">\n");
			}
			
			//Replace Commands
			for(int j=0;j<command.getReplaceCommands().size();j++)
			{
				Replace cour = (Replace)command.getReplaceCommands().get(j);
				buffer.append("<"+SyncXMLTags.Replace+">\n");
				
				//CmdId
				buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtilities.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtilities.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n");
				}
				
				//Items
				if(!cour.getItems().isEmpty())
				{
					buffer.append(this.generateItems(cour.getItems()));
				}
				
				buffer.append("</"+SyncXMLTags.Replace+">\n");
			}
			
			//Delete Commands
			for(int j=0;j<command.getDeleteCommands().size();j++)
			{
				Delete cour = (Delete)command.getDeleteCommands().get(j);
				buffer.append("<"+SyncXMLTags.Delete+">\n");
				
				//CmdId
				buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtilities.cleanupXML(cour.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
				
				//Meta
				if(cour.getMeta() != null && cour.getMeta().trim().length()>0)
				{
					buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtilities.cleanupXML(cour.getMeta())+"</"+SyncXMLTags.Meta+">\n");
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
		
		buffer.append("<"+SyncXMLTags.CmdID+">"+XMLUtilities.cleanupXML(recordMap.getCmdId())+"</"+SyncXMLTags.CmdID+">\n");
		
		buffer.append("<"+SyncXMLTags.Source+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(recordMap.getSource()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Source+">\n");
		
		buffer.append("<"+SyncXMLTags.Target+">\n");
		buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(recordMap.getTarget()));
		buffer.append("</"+SyncXMLTags.LocURI+">\n");
		buffer.append("</"+SyncXMLTags.Target+">\n");
		
		if(recordMap.getMeta() != null && recordMap.getMeta().trim().length()>0)
		{
			buffer.append("<"+SyncXMLTags.Meta+">"+XMLUtilities.cleanupXML(recordMap.getMeta())+"</"+SyncXMLTags.Meta+">\n");
		}
		
		for(int i=0;i<recordMap.getMapItems().size();i++)
		{
			MapItem mapItem = (MapItem)recordMap.getMapItems().get(i);
			buffer.append("<"+SyncXMLTags.MapItem+">\n");
			
			buffer.append("<"+SyncXMLTags.Source+">\n");
			buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(mapItem.getSource()));
			buffer.append("</"+SyncXMLTags.LocURI+">\n");
			buffer.append("</"+SyncXMLTags.Source+">\n");
			
			buffer.append("<"+SyncXMLTags.Target+">\n");
			buffer.append("<"+SyncXMLTags.LocURI+">"+XMLUtilities.cleanupXML(mapItem.getTarget()));
			buffer.append("</"+SyncXMLTags.LocURI+">\n");
			buffer.append("</"+SyncXMLTags.Target+">\n");
			
			buffer.append("</"+SyncXMLTags.MapItem+">\n");
		}
				
		buffer.append("</"+SyncXMLTags.Map+">\n");
		
		xml = buffer.toString();
		
		return xml;
	}
}
