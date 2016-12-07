/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncObjectGenerator
{	
	/**
	 * 
	 *
	 */
	public SyncObjectGenerator()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}	
	
	/**
	 * 
	 * @param xml
	 */
	public Session parse(String xml) throws SyncException
	{
		Session session = null;
		InputStream is = null;
		try
		{			
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			is = new ByteArrayInputStream(xml.getBytes());
			SAXHandler handler = new SAXHandler();
			parser.parse(is, handler);
			
			session = handler.getSession();
		}
		catch(Exception e)
		{
			throw new SyncException(e);
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(Exception e){}
			}
		}
		return session;
	}
	
	/**
	 * 
	 * @author openmobster@gmail.com
	 *
	 */
	private static class SAXHandler extends DefaultHandler
	{
		private StringBuffer fullPath;
		private StringBuffer dataBuffer;
		
		private Session session;
		private SyncMessage syncMessage;
		
		private Alert courAlert;
		private Status courStatus;
		private Item courItem;
		private SyncCommand courCommand;
		private Add courAdd;
		private Replace courReplace;
		private Delete courDelete;
		private RecordMap courRecordMap;
		private MapItem courMapItem;
		
		/**
		 * 
		 * @return
		 */
		public Session getSession()
		{
			return this.session;
		}
		
		//---DefaultHandler impl---------------------------------------------------------------------------		
		public void startDocument() throws SAXException 
		{			
			this.session = new Session();
			this.syncMessage = new SyncMessage();
			this.fullPath = new StringBuffer();
			this.dataBuffer = new StringBuffer();
		}
				
		public void endDocument() throws SAXException 
		{			
			this.session.setCurrentMessage(this.syncMessage);
		}				
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) 
		throws SAXException 
		{		
			this.fullPath.append("/"+qName.trim());
			this.dataBuffer = new StringBuffer();
									
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Alert))
			{
				this.courAlert = new Alert();
			}
			
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status))
			{
				this.courStatus = new Status();
			}
			
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Sync))
			{
				this.courCommand = new SyncCommand();
			}
			
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Map))
			{
				this.courRecordMap = new RecordMap();
			}
			
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Map+"/"+SyncXMLTags.MapItem))
			{
				this.courMapItem = new MapItem();
			}
									
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Add))
			{
				this.courAdd = new Add();
			}
			
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Replace))
			{
				this.courReplace = new Replace();
			}
			
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Delete))
			{
				this.courDelete = new Delete();
			}
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Delete+"/"+SyncXMLTags.Archive))
			{
				this.courDelete.setArchive(true);
			}
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Delete+"/"+SyncXMLTags.SftDel))
			{
				this.courDelete.setSoftDelete(true);
			}
			
			if(this.fullPath.toString().endsWith("/"+SyncXMLTags.Item))
			{
				this.courItem = new Item();
			}
			
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Item+"/"+SyncXMLTags.MoreData))
			{
				this.courItem.setMoreData(true);
			}
			
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Final))
			{
				this.syncMessage.setFinal(true);
			}
		}
						
		public void characters(char[] ch, int start, int length) throws SAXException 
		{		
			String data = new String(ch, start, length);	
			
			if(data != null && data.trim().length()>0)
			{
				this.dataBuffer.append(data);
			}						
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException 
		{
			//Populate with data
			//Process Session object related data
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncHdr+"/"+SyncXMLTags.SessionID))
			{
				this.session.setSessionId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncHdr+"/"+SyncXMLTags.Source+"/"+SyncXMLTags.LocURI))
			{
				this.session.setSource(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncHdr+"/"+SyncXMLTags.Target+"/"+SyncXMLTags.LocURI))
			{
				this.session.setTarget(this.dataBuffer.toString());
			}
			
			//Processing SyncMessage related data
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncHdr+"/"+SyncXMLTags.MsgID))
			{
				this.syncMessage.setMessageId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncHdr+"/"+SyncXMLTags.Meta+"/"+SyncXMLTags.MaxMsgSize))
			{
				this.syncMessage.setMaxClientSize(Integer.parseInt(this.dataBuffer.toString()));
			}
			
			//Processing Alert related data
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Alert+"/"+SyncXMLTags.CmdID))
			{
				this.courAlert.setCmdId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Alert+"/"+SyncXMLTags.Data))
			{
				this.courAlert.setData(this.dataBuffer.toString());
			}
			
			//Processing Status related data
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.CmdID))
			{
				this.courStatus.setCmdId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.Data))
			{
				this.courStatus.setData(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.MsgRef))
			{
				this.courStatus.setMsgRef(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.CmdRef))
			{
				this.courStatus.setCmdRef(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.Cmd))
			{
				this.courStatus.setCmd(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.SourceRef))
			{
				this.courStatus.addSourceRef(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.TargetRef))
			{
				this.courStatus.addTargetRef(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.Chal+"/"+SyncXMLTags.Meta+"/"+SyncXMLTags.Type))
			{
				Credential credential = this.courStatus.getCredential();
				if(credential == null)
				{
					credential = new Credential();
					this.courStatus.setCredential(credential);
				}
				this.courStatus.getCredential().setType(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.Chal+"/"+SyncXMLTags.Meta+"/"+SyncXMLTags.Format))
			{
				Credential credential = this.courStatus.getCredential();
				if(credential == null)
				{
					credential = new Credential();
					this.courStatus.setCredential(credential);
				}
				this.courStatus.getCredential().setFormat(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status+"/"+SyncXMLTags.Chal+"/"+SyncXMLTags.Meta+"/"+SyncXMLTags.NextNonce))
			{
				Credential credential = this.courStatus.getCredential();
				if(credential == null)
				{
					credential = new Credential();
					this.courStatus.setCredential(credential);
				}
				this.courStatus.getCredential().setNextNonce(this.dataBuffer.toString());
			}
			
			//Processing SyncCommand related data
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.CmdID))
			{
				this.courCommand.setCmdId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Source+"/"+SyncXMLTags.LocURI))
			{
				this.courCommand.setSource(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Target+"/"+SyncXMLTags.LocURI))
			{
				this.courCommand.setTarget(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Meta))
			{
				this.courCommand.setMeta(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.NumberOfChanges))
			{
				this.courCommand.setNumberOfChanges(this.dataBuffer.toString());
			}
			
			//Process RecordMap related data
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Map+"/"+SyncXMLTags.CmdID))
			{
				this.courRecordMap.setCmdId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Map+"/"+SyncXMLTags.Source+"/"+SyncXMLTags.LocURI))
			{
				this.courRecordMap.setSource(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Map+"/"+SyncXMLTags.Target+"/"+SyncXMLTags.LocURI))
			{
				this.courRecordMap.setTarget(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Map+"/"+SyncXMLTags.Meta))
			{
				this.courRecordMap.setMeta(this.dataBuffer.toString());
			}
			
			//Process MapItem related data
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.MapItem+"/"+SyncXMLTags.Source+"/"+SyncXMLTags.LocURI))
			{
				this.courMapItem.setSource(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.MapItem+"/"+SyncXMLTags.Target+"/"+SyncXMLTags.LocURI))
			{
				this.courMapItem.setTarget(this.dataBuffer.toString());
			}

			
			//Processing Add operation related data
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Add+"/"+SyncXMLTags.CmdID))
			{
				this.courAdd.setCmdId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Add+"/"+SyncXMLTags.Meta))
			{
				this.courAdd.setMeta(this.dataBuffer.toString());
			}
			
			//Processing Replace operation related data
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Replace+"/"+SyncXMLTags.CmdID))
			{
				this.courReplace.setCmdId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Replace+"/"+SyncXMLTags.Meta))
			{
				this.courReplace.setMeta(this.dataBuffer.toString());
			}
			
			//Processing Delete operation related data
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Delete+"/"+SyncXMLTags.CmdID))
			{
				this.courDelete.setCmdId(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Delete+"/"+SyncXMLTags.Meta))
			{
				this.courDelete.setMeta(this.dataBuffer.toString());
			}
			
			
			//Processing Item related data
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Item+"/"+SyncXMLTags.Source+"/"+SyncXMLTags.LocURI))
			{
				this.courItem.setSource(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Item+"/"+SyncXMLTags.Target+"/"+SyncXMLTags.LocURI))
			{
				this.courItem.setTarget(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Item+"/"+SyncXMLTags.Data))
			{
				this.courItem.setData(this.dataBuffer.toString());
			}
			else if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Item+"/"+SyncXMLTags.Meta))
			{
				this.courItem.setMeta(this.dataBuffer.toString());
			}
			
			//Organize the populated object model
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Alert))
			{
				if(this.courAlert != null)
				{
					this.syncMessage.addAlert(courAlert);
				}
				this.courAlert = null;
			}
			
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Status))
			{
				if(this.courStatus != null)
				{
					this.syncMessage.addStatus(courStatus);
				}
				this.courStatus = null;
			}
			
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Sync))
			{
				if(this.courCommand != null)
				{
					this.syncMessage.addSyncCommand(courCommand);
				}
				this.courCommand = null;
			}
			
			if(this.fullPath.toString().equals(
			"/"+SyncXMLTags.SyncML+"/"+SyncXMLTags.SyncBody+"/"+SyncXMLTags.Map))
			{
				if(this.courRecordMap != null)
				{
					this.syncMessage.setRecordMap(courRecordMap);
				}
				this.courRecordMap = null;
			}
			
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Map+"/"+SyncXMLTags.MapItem))
			{
				if(this.courMapItem != null)
				{
					this.courRecordMap.addMapItem(this.courMapItem);
				}
				this.courMapItem = null;
			}
			
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Add))
			{
				if(this.courAdd != null)
				{
					this.courCommand.addOperationCommand(courAdd);
				}
				this.courAdd = null;
			}
			
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Replace))
			{
				if(this.courReplace != null)
				{
					this.courCommand.addOperationCommand(courReplace);
				}
				this.courReplace = null;
			}
			
			if(this.fullPath.toString().endsWith(
			"/"+SyncXMLTags.Sync+"/"+SyncXMLTags.Delete))
			{
				if(this.courDelete != null)
				{
					this.courCommand.addOperationCommand(courDelete);
				}
				this.courDelete = null;
			}
			
			if(this.fullPath.toString().endsWith("/"+SyncXMLTags.Item))
			{				
				if(this.fullPath.toString().endsWith("/"+SyncXMLTags.Alert+"/"+SyncXMLTags.Item))
				{
					this.courAlert.addItem(this.courItem);
				}
				else if(this.fullPath.toString().endsWith("/"+SyncXMLTags.Status+"/"+SyncXMLTags.Item))
				{
					this.courStatus.addItem(this.courItem);
				}
				else if(this.fullPath.toString().endsWith("/"+SyncXMLTags.Add+"/"+SyncXMLTags.Item))
				{
					this.courAdd.addItem(this.courItem);
				}
				else if(this.fullPath.toString().endsWith("/"+SyncXMLTags.Replace+"/"+SyncXMLTags.Item))
				{
					this.courReplace.addItem(this.courItem);
				}
				else if(this.fullPath.toString().endsWith("/"+SyncXMLTags.Delete+"/"+SyncXMLTags.Item))
				{
					this.courDelete.addItem(this.courItem);
				}
				this.courItem = null;
			}
			
			//Reset
			String cour = this.fullPath.toString();			
			int lastIndex = cour.lastIndexOf('/');
			this.fullPath = new StringBuffer(cour.substring(0, lastIndex));
		}
	}
}
