/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.synchronizer.SyncException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import org.openmobster.core.common.XMLUtilities;

/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncObjectGenerator
{
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public Session parseClientInitMessage(String xml)
	{
		try
		{
			Session session = new Session();
			
			Document document = XMLUtilities.parse(xml);
			Element syncHeader = (Element)document.getElementsByTagName(SyncXMLTags.SyncHdr).item(0);
			Element syncBody = (Element)document.getElementsByTagName(SyncXMLTags.SyncBody).item(0);
			
			
			this.processSession(session, syncHeader);
						
			
			SyncMessage message = new SyncMessage();						
			this.processMessage(message, syncHeader);
			this.processMessageBody(message, syncBody);
			
					
			session.getClientInitPackage().addMessage(message);
			
			return session;
		}
		catch(Exception e)
		{
			throw new SyncException(e);
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public Session parseServerInitMessage(String xml)
	{
		try
		{
			Session session = new Session();
			
			Document document = XMLUtilities.parse(xml);
			Element syncHeader = (Element)document.getElementsByTagName(SyncXMLTags.SyncHdr).item(0);
			Element syncBody = (Element)document.getElementsByTagName(SyncXMLTags.SyncBody).item(0);
			
			
			this.processSession(session, syncHeader);
						
			
			SyncMessage message = new SyncMessage();						
			this.processMessage(message, syncHeader);
			this.processMessageBody(message, syncBody);
			
					
			session.getServerInitPackage().addMessage(message);
			
			return session;
		}
		catch(Exception e)
		{
			throw new SyncException(e);
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public Session parseClientSyncMessage(String xml)
	{
		try
		{
			Session session = new Session();
			
			Document document = XMLUtilities.parse(xml);
			Element syncHeader = (Element)document.getElementsByTagName(SyncXMLTags.SyncHdr).item(0);
			Element syncBody = (Element)document.getElementsByTagName(SyncXMLTags.SyncBody).item(0);
			
			
			this.processSession(session, syncHeader);
						
			
			SyncMessage message = new SyncMessage();						
			this.processMessage(message, syncHeader);
			this.processMessageBody(message, syncBody);
						
			session.getClientSyncPackage().addMessage(message);
			
			return session;
		}
		catch(Exception e)
		{
			throw new SyncException(e);
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public Session parseServerSyncMessage(String xml)
	{
		try
		{
			Session session = new Session();
			
			Document document = XMLUtilities.parse(xml);
			Element syncHeader = (Element)document.getElementsByTagName(SyncXMLTags.SyncHdr).item(0);
			Element syncBody = (Element)document.getElementsByTagName(SyncXMLTags.SyncBody).item(0);
			
			
			this.processSession(session, syncHeader);
						
			
			SyncMessage message = new SyncMessage();						
			this.processMessage(message, syncHeader);
			this.processMessageBody(message, syncBody);
			
						
			session.getServerSyncPackage().addMessage(message);
			
			return session;
		}
		catch(Exception e)
		{
			throw new SyncException(e);
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public Session parseCurrentSyncMessage(String xml)
	{
		try
		{
			Session session = new Session();
			
			Document document = XMLUtilities.parse(xml);
			Element syncHeader = (Element)document.getElementsByTagName(SyncXMLTags.SyncHdr).item(0);
			Element syncBody = (Element)document.getElementsByTagName(SyncXMLTags.SyncBody).item(0);
			
			
			this.processSession(session, syncHeader);
						
			
			SyncMessage message = new SyncMessage();						
			this.processMessage(message, syncHeader);
			this.processMessageBody(message, syncBody);
			
						
			session.setCurrentMessage(message);
			
			return session;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new SyncException(e);
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public Anchor parseAnchor(String xml)
	{
		Anchor anchor = new Anchor();
		
		Document document = XMLUtilities.parse(xml);
		
		Element lastSync = (Element)document.getElementsByTagName(SyncXMLTags.Last).item(0);
		Element nextSync = (Element)document.getElementsByTagName(SyncXMLTags.Next).item(0);
		
		anchor.setLastSync(lastSync.getFirstChild().getNodeValue());
		anchor.setNextSync(nextSync.getFirstChild().getNodeValue());
		
		return anchor;
	}
	//-------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param session
	 * @param syncHeader
	 */
	private void processSession(Session session,Element syncHeader) throws Exception
	{
		Element sessionId = (Element)syncHeader.getElementsByTagName(SyncXMLTags.SessionID).item(0);
		session.setSessionId(sessionId.getFirstChild().getNodeValue());
		
		Element target = (Element)syncHeader.getElementsByTagName(SyncXMLTags.Target).item(0);
		Element loc = (Element)target.getElementsByTagName(SyncXMLTags.LocURI).item(0);
		session.setTarget(loc.getFirstChild().getNodeValue());
		
		Element source = (Element)syncHeader.getElementsByTagName(SyncXMLTags.Source).item(0);
		loc = (Element)source.getElementsByTagName(SyncXMLTags.LocURI).item(0);
		session.setSource(loc.getFirstChild().getNodeValue());
		
		Element app = (Element)syncHeader.getElementsByTagName(SyncXMLTags.App).item(0);
		String appValue = app.getFirstChild().getNodeValue();
		session.setApp(appValue);
	}
	
	/**
	 * 
	 * @param message
	 * @param syncHeader
	 * @throws Exception
	 */
	private void processMessage(SyncMessage message,Element syncHeader) throws Exception
	{
		Element msgId = (Element)syncHeader.getElementsByTagName(SyncXMLTags.MsgID).item(0);
		message.setMessageId(msgId.getFirstChild().getNodeValue());
				
		if(XMLUtilities.contains(syncHeader, SyncXMLTags.MaxMsgSize))
		{
			Element maxMsgSize = (Element)syncHeader.getElementsByTagName(SyncXMLTags.MaxMsgSize).item(0);
			message.setMaxClientSize(Integer.parseInt(maxMsgSize.getFirstChild().getNodeValue()));
		}
		
		if(XMLUtilities.contains(syncHeader, SyncXMLTags.Cred))
		{
			Element credElem = (Element)syncHeader.getElementsByTagName(SyncXMLTags.Cred).item(0);
			Element typeElem = (Element)credElem.getElementsByTagName(SyncXMLTags.Type).item(0);
			Element dataElem = (Element)credElem.getElementsByTagName(SyncXMLTags.Data).item(0);
			
			String type = typeElem.getTextContent();
			String data = dataElem.getTextContent();
			message.setCredential(new Credential(type, data));
		}
	}
	
	/**
	 * 
	 * @param session
	 * @param syncBody
	 */
	private void processMessageBody(SyncMessage syncMessage,Element syncBody)
	{
		//Process Alerts
		NodeList alerts = syncBody.getElementsByTagName(SyncXMLTags.Alert);
		if(alerts != null)
		{			
			for(int i=0;i<alerts.getLength();i++)
			{
				Element cour = (Element)alerts.item(i);
				Alert alert = new Alert();
				
				//Process cmdId and data
				Element cmdId = (Element)cour.getElementsByTagName(SyncXMLTags.CmdID).item(0);				
				alert.setCmdId(cmdId.getFirstChild().getNodeValue());
				
				if(XMLUtilities.contains(cour, SyncXMLTags.Data))
				{
					Element data = (Element)cour.getElementsByTagName(SyncXMLTags.Data).item(0);
					alert.setData(data.getFirstChild().getNodeValue());
				}
				
				//Process the Items here
				NodeList itemList = cour.getElementsByTagName(SyncXMLTags.Item);
				if(itemList != null && itemList.getLength() > 0)
				{
					for(int j=0;j<itemList.getLength();j++)
					{
						Item item = this.processItem((Element)itemList.item(j));
						alert.addItem(item);
					}
				}
				
				syncMessage.addAlert(alert);
			}
		}
		
		//Process Status
		NodeList statusList = syncBody.getElementsByTagName(SyncXMLTags.Status);
		if(statusList != null)
		{			
			for(int i=0;i<statusList.getLength();i++)
			{
				Element cour = (Element)statusList.item(i);
				Status status = new Status();
				
				//Process cmdId and data
				Element cmdId = (Element)cour.getElementsByTagName(SyncXMLTags.CmdID).item(0);
				Element data = (Element)cour.getElementsByTagName(SyncXMLTags.Data).item(0);
				Element msgRef = (Element)cour.getElementsByTagName(SyncXMLTags.MsgRef).item(0);
				Element cmdRef = (Element)cour.getElementsByTagName(SyncXMLTags.CmdRef).item(0);
				Element cmd = (Element)cour.getElementsByTagName(SyncXMLTags.Cmd).item(0);
				
				
				status.setCmdId(cmdId.getFirstChild().getNodeValue());
				status.setData(data.getFirstChild().getNodeValue());
				status.setMsgRef(msgRef.getFirstChild().getNodeValue());
				status.setCmdRef(cmdRef.getFirstChild().getNodeValue());
				status.setCmd(cmd.getFirstChild().getNodeValue());
				
				if(XMLUtilities.contains(cour, SyncXMLTags.SourceRef))
				{
					NodeList sourceRefs = cour.getElementsByTagName(SyncXMLTags.SourceRef);
					for(int j=0;j<sourceRefs.getLength();j++)
					{
						Element sourceRef = (Element)sourceRefs.item(j);
						status.addSourceRef(sourceRef.getFirstChild().getNodeValue());
					}
				}
				
				if(XMLUtilities.contains(cour, SyncXMLTags.TargetRef))
				{
					NodeList targetRefs = cour.getElementsByTagName(SyncXMLTags.TargetRef);
					for(int j=0;j<targetRefs.getLength();j++)
					{
						Element targetRef = (Element)targetRefs.item(j);
						status.addTargetRef(targetRef.getFirstChild().getNodeValue());
					}
				}
				
				//Process the Item here
				NodeList itemList = cour.getElementsByTagName(SyncXMLTags.Item);
				if(itemList != null && itemList.getLength() > 0)
				{
					for(int j=0;j<itemList.getLength();j++)
					{
						Item item = this.processItem((Element)itemList.item(j));
						status.addItem(item);
					}
				}
				
				syncMessage.addStatus(status);
			}
		}
		
		//Process SyncCommands
		NodeList syncCommandList = syncBody.getElementsByTagName(SyncXMLTags.Sync);
		if(syncCommandList != null)
		{			
			for(int i=0;i<syncCommandList.getLength();i++)
			{
				Element cour = (Element)syncCommandList.item(i);
				SyncCommand syncCommand = new SyncCommand();
				
				//Process cmdId and data
				Element cmdId = (Element)cour.getElementsByTagName(SyncXMLTags.CmdID).item(0);
				syncCommand.setCmdId(cmdId.getFirstChild().getNodeValue());
				
				if(XMLUtilities.contains(cour, SyncXMLTags.Source))
				{
					Element source = (Element)cour.getElementsByTagName(SyncXMLTags.Source).item(0);
					Element loc = (Element)source.getElementsByTagName(SyncXMLTags.LocURI).item(0);
					syncCommand.setSource(loc.getFirstChild().getNodeValue());
				}
				
				if(XMLUtilities.contains(cour, SyncXMLTags.Target))
				{
					Element target = (Element)cour.getElementsByTagName(SyncXMLTags.Target).item(0);
					Element loc = (Element)target.getElementsByTagName(SyncXMLTags.LocURI).item(0);
					syncCommand.setTarget(loc.getFirstChild().getNodeValue());
				}
				
				if(XMLUtilities.contains(cour, SyncXMLTags.Meta))
				{
					Element meta = (Element)cour.getElementsByTagName(SyncXMLTags.Meta).item(0);					
					syncCommand.setMeta(meta.getFirstChild().getNodeValue());
				}
				
				if(XMLUtilities.contains(cour, SyncXMLTags.NumberOfChanges))
				{
					Element numberOfChanges = (Element)cour.getElementsByTagName(SyncXMLTags.NumberOfChanges).
					item(0);
					syncCommand.setNumberOfChanges(numberOfChanges.getFirstChild().getNodeValue());
				}
				
				if(XMLUtilities.contains(cour, SyncXMLTags.Add))
				{
					syncCommand.setAddCommands(this.processAdd(
					cour.getElementsByTagName(SyncXMLTags.Add)));
				}
				
				if(XMLUtilities.contains(cour, SyncXMLTags.Replace))
				{
					syncCommand.setReplaceCommands(this.processReplace(
					cour.getElementsByTagName(SyncXMLTags.Replace)));
				}
				
				if(XMLUtilities.contains(cour, SyncXMLTags.Delete))
				{
					syncCommand.setDeleteCommands(this.processDelete(
					cour.getElementsByTagName(SyncXMLTags.Delete)));
				}
								
				syncMessage.addSyncCommand(syncCommand);
			}
		}
		
		//Process RecordMap
		if(XMLUtilities.contains(syncBody, SyncXMLTags.Map))
		{
			syncMessage.setRecordMap(this.processRecordMap(syncBody));
		}
		
		if(XMLUtilities.contains(syncBody, SyncXMLTags.Final))
		{
			syncMessage.setFinal(true);
		}
	}
	
	/**
	 * 
	 * @param itemElement
	 * @return
	 */
	private Item processItem(Element itemElement)
	{
		Item item = new Item();
		
		if(XMLUtilities.contains(itemElement, SyncXMLTags.Source))
		{
			Element source = (Element)itemElement.getElementsByTagName(SyncXMLTags.Source).item(0);
			Element loc = (Element)source.getElementsByTagName(SyncXMLTags.LocURI).item(0); 
			item.setSource(loc.getFirstChild().getNodeValue());
		}
		
		if(XMLUtilities.contains(itemElement, SyncXMLTags.Target))
		{
			Element target = (Element)itemElement.getElementsByTagName(SyncXMLTags.Target).item(0);
			Element loc = (Element)target.getElementsByTagName(SyncXMLTags.LocURI).item(0);
			item.setTarget(loc.getFirstChild().getNodeValue());
		}
		
		if(XMLUtilities.contains(itemElement, SyncXMLTags.Data))
		{
			Element data = (Element)itemElement.getElementsByTagName(SyncXMLTags.Data).item(0);			
			String dataValue = data.getTextContent();						
			item.setData(dataValue);
		}
		
		if(XMLUtilities.contains(itemElement, SyncXMLTags.Meta))
		{
			Element meta = (Element)itemElement.getElementsByTagName(SyncXMLTags.Meta).item(0);
			item.setMeta(meta.getFirstChild().getNodeValue());
		}
		
		item.setMoreData(XMLUtilities.contains(itemElement, SyncXMLTags.MoreData));
		
		
		return item;
	}
	
	/**
	 * 
	 * @param addList
	 * @return
	 */
	private List processAdd(NodeList addList)
	{
		List add = new ArrayList();
		
		for(int i=0;i<addList.getLength();i++)
		{
			Add cour = new Add();
			
			Element addElement = (Element)addList.item(i);
			
			Element cmdId = (Element)addElement.getElementsByTagName(SyncXMLTags.CmdID).item(0);
			cour.setCmdId(cmdId.getFirstChild().getNodeValue());
			
			if(XMLUtilities.contains(addElement, SyncXMLTags.Meta))
			{
				Element meta = (Element)addElement.getElementsByTagName(SyncXMLTags.Meta).item(0);
				cour.setMeta(meta.getFirstChild().getNodeValue());
			}
			
			NodeList items = addElement.getElementsByTagName(SyncXMLTags.Item);
			for(int j=0;j<items.getLength();j++)
			{
				Item item = this.processItem((Element)items.item(j));
				cour.getItems().add(item);
			}
			
			add.add(cour);			
		}
		
		return add;
	}
	
	/**
	 * 
	 * @param replaceList
	 * @return
	 */
	private List processReplace(NodeList replaceList)
	{
		List replace = new ArrayList();
		
		for(int i=0;i<replaceList.getLength();i++)
		{
			Replace cour = new Replace();
			
			Element replaceElement = (Element)replaceList.item(i);
			
			Element cmdId = (Element)replaceElement.getElementsByTagName(SyncXMLTags.CmdID).item(0);
			cour.setCmdId(cmdId.getFirstChild().getNodeValue());
			
			if(XMLUtilities.contains(replaceElement, SyncXMLTags.Meta))
			{
				Element meta = (Element)replaceElement.getElementsByTagName(SyncXMLTags.Meta).item(0);
				cour.setMeta(meta.getFirstChild().getNodeValue());
			}
			
			NodeList items = replaceElement.getElementsByTagName(SyncXMLTags.Item);
			for(int j=0;j<items.getLength();j++)
			{
				Item item = this.processItem((Element)items.item(j));
				cour.getItems().add(item);
			}
			
			replace.add(cour);			
		}
		
		return replace;
	}
	
	/**
	 * 
	 * @param deleteList
	 * @return
	 */
	private List processDelete(NodeList deleteList)
	{
		List delete = new ArrayList();
		
		for(int i=0;i<deleteList.getLength();i++)
		{
			Delete cour = new Delete();
			
			Element deleteElement = (Element)deleteList.item(i);
			
			Element cmdId = (Element)deleteElement.getElementsByTagName(SyncXMLTags.CmdID).item(0);
			cour.setCmdId(cmdId.getFirstChild().getNodeValue());
			
			if(XMLUtilities.contains(deleteElement, SyncXMLTags.Meta))
			{
				Element meta = (Element)deleteElement.getElementsByTagName(SyncXMLTags.Meta).item(0);
				cour.setMeta(meta.getFirstChild().getNodeValue());
			}
			
			if(XMLUtilities.contains(deleteElement, SyncXMLTags.Archive))
			{
				cour.setArchive(true);
			}
			
			if(XMLUtilities.contains(deleteElement, SyncXMLTags.SftDel))
			{
				cour.setSoftDelete(true);
			}
			
			NodeList items = deleteElement.getElementsByTagName(SyncXMLTags.Item);
			for(int j=0;j<items.getLength();j++)
			{
				Item item = this.processItem((Element)items.item(j));
				cour.getItems().add(item);
			}			
			
			delete.add(cour);			
		}
		
		return delete;
	}	
	
	/**
	 * 
	 * @param syncBody
	 * @return
	 */
	private RecordMap processRecordMap(Element syncBody)
	{
		RecordMap recordMap = new RecordMap();
		
		NodeList cour = syncBody.getElementsByTagName(SyncXMLTags.Map);
		Element map = (Element)cour.item(0);
		
		Element cmdId = (Element)map.getElementsByTagName(SyncXMLTags.CmdID).item(0);
		recordMap.setCmdId(cmdId.getFirstChild().getNodeValue());
		
		Element source = (Element)map.getElementsByTagName(SyncXMLTags.Source).item(0);
		Element loc = (Element)source.getElementsByTagName(SyncXMLTags.LocURI).item(0);
		recordMap.setSource(loc.getFirstChild().getNodeValue());
		
		Element target = (Element)map.getElementsByTagName(SyncXMLTags.Target).item(0);
		loc = (Element)target.getElementsByTagName(SyncXMLTags.LocURI).item(0);
		recordMap.setTarget(loc.getFirstChild().getNodeValue());
		
		if(XMLUtilities.contains(map, SyncXMLTags.Meta))
		{
			Element meta = (Element)map.getElementsByTagName(SyncXMLTags.Meta).item(0);
			recordMap.setMeta(meta.getFirstChild().getNodeValue());
		}
		
		NodeList mapItems = map.getElementsByTagName(SyncXMLTags.MapItem);
		for(int i=0; i<mapItems.getLength(); i++)
		{
			Element mapItem = (Element)mapItems.item(i);
			MapItem x = new MapItem();
			
			Element itemSrc = (Element)mapItem.getElementsByTagName(SyncXMLTags.Source).item(0);
			Element itemLoc = (Element)itemSrc.getElementsByTagName(SyncXMLTags.LocURI).item(0);
			x.setSource(itemLoc.getFirstChild().getNodeValue());
			
			Element itemTarget = (Element)mapItem.getElementsByTagName(SyncXMLTags.Target).item(0);
			itemLoc = (Element)itemTarget.getElementsByTagName(SyncXMLTags.LocURI).item(0);
			x.setTarget(itemLoc.getFirstChild().getNodeValue());
			
			recordMap.getMapItems().add(x);
		}
		
		return recordMap;
	}
}
