/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync.engine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObjectDatabase;
import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;
import org.openmobster.device.agent.service.database.DBException;
import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.XMLUtilities;

import org.openmobster.device.agent.sync.AbstractOperation;
import org.openmobster.device.agent.sync.Add;
import org.openmobster.device.agent.sync.Anchor;
import org.openmobster.device.agent.sync.Delete;
import org.openmobster.device.agent.sync.Item;
import org.openmobster.device.agent.sync.Replace;
import org.openmobster.device.agent.sync.Session;
import org.openmobster.device.agent.sync.Status;
import org.openmobster.device.agent.sync.SyncAdapter;
import org.openmobster.device.agent.sync.SyncCommand;
import org.openmobster.device.agent.sync.SyncException;
import org.openmobster.device.agent.sync.SyncXMLTags;

/**
 * TODO: Properly throw the SyncException such that no RuntimeException makes its way up the stack
 */


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public class SyncEngine 
{
	private static Logger log = Logger.getLogger(SyncEngine.class);
		
	public static String OPERATION_ADD = "Add";

	public static String OPERATION_UPDATE = "Replace";

	public static String OPERATION_DELETE = "Delete";
	
	public static String OPERATION_MAP = "Map";
	
	private MobileObjectDatabase mobileObjectDatabase = null;
	private SyncDataSource syncDataSource = null;
	
	public MobileObjectDatabase getMobileObjectDatabase()
	{
		return this.mobileObjectDatabase;
	}
	
	public void setMobileObjectDatabase(MobileObjectDatabase mobileObjectDatabase)
	{
		this.mobileObjectDatabase = mobileObjectDatabase;
	}
	
	public SyncDataSource getSyncDataSource()
	{
		return this.syncDataSource;
	}
	
	public void setSyncDataSource(SyncDataSource syncDataSource)
	{
		this.syncDataSource = syncDataSource;
	}
		
	public SyncEngine()
	{
	}	
	//------Synchronization related services-----------------------------------------------------------------------------------------------------------------	
	public Vector getSlowSyncCommands(int messageSize, String storageId) throws SyncException
	{
		try
		{
			Vector commands = new Vector();

			//Getting all the records of the client since this is a SlowSync
			List<MobileObject> allRecords = mobileObjectDatabase.readByStorage(storageId);
			if(allRecords != null)
			{
				for (MobileObject mo: allRecords)
				{	
					// Create a Sync Add Command from this record data
					commands.addElement(this.getAddCommand(mo));
				}
			}
			
			return commands;
		}
		catch (Exception e)
		{
			throw new SyncException(e);
		}
	}
				
	public Vector getAddCommands(int messageSize,String storageId, String syncType) throws SyncException
	{
		Vector changeLog = null;
		try
		{
			Vector commands = new Vector();

			changeLog = this.getChangeLog(storageId, SyncEngine.OPERATION_ADD);
			for (int i = 0,size=changeLog.size(); i < size; i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
				MobileObject cour = mobileObjectDatabase.read(storageId, entry.getRecordId());
				if (cour != null)
				{
					// Create a Sync Add Command from this record data				
					commands.addElement(this.getAddCommand(cour));
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{
				for (int i = 0,size=changeLog.size(); i < size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(e);
		}
	}
	
		
	public Vector getReplaceCommands(int messageSize,String storageId, String syncType)
			throws SyncException
	{
		Vector changeLog = null;
		try
		{
			Vector commands = new Vector();

			changeLog = this.getChangeLog(storageId,
					SyncEngine.OPERATION_UPDATE);
			for (int i = 0,size=changeLog.size(); i < size; i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
				MobileObject cour = mobileObjectDatabase.read(storageId, entry.getRecordId());
				if (cour != null)
				{
					// Create a Sync Add Command from this record data				
					commands.addElement(this.getReplaceCommand(cour));
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{				
				for (int i = 0,size=changeLog.size(); i < size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(e);
		}
	}
		
	public Vector getDeleteCommands(String storageId, String syncType)
			throws SyncException
	{
		Vector changeLog = null;
		try
		{
			Vector commands = new Vector();

			changeLog = this.getChangeLog(storageId,
					SyncEngine.OPERATION_DELETE);
			for (int i = 0,size=changeLog.size(); i < size; i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
				Delete delete = new Delete();
				
				Item item = new Item();
				item.setData(this.marshalId(entry.getRecordId()));
				delete.getItems().addElement(item);
				
				commands.addElement(delete);
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{				
				for (int i = 0,size=changeLog.size(); i < size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(e);
		}
	}
	
	public Vector processSlowSyncCommand(Session session,String storageId, SyncCommand syncCommand)
	throws SyncException
	{
		Vector status = new Vector();
		Vector allCommands = syncCommand.getAllCommands();
		
		
		for(int i=0,size=allCommands.size(); i<size; i++)
		{
			AbstractOperation command = (AbstractOperation)allCommands.elementAt(i);									
			/**
			 * Long Object Support
			 */
			if(command.isChunked())
			{
				continue;
			}
					
			Item item = (Item)command.getItems().elementAt(0);
			if(command instanceof Add)
			{								
				//Remove this item if it exists from the client database				
				//Add this item to the client database
				try
				{															
					MobileObject mobileObject = this.unmarshal(storageId,item.getData());
					
					this.deleteRecord(session, mobileObject);
					this.saveRecord(session, mobileObject);
					
					status.addElement(this.getStatus(SyncAdapter.SUCCESS, command));
				}
				catch (Exception e)
				{
					log.error(this, e);
					status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE, command));
				}
			}
			else if(command instanceof Delete)
			{								
				//Remove this item if it exists from the client database
				try
				{					
					MobileObject mobileObject = this.unmarshal(storageId,item.getData());
					
					this.deleteRecord(session, mobileObject);
					status.addElement(this.getStatus(SyncAdapter.SUCCESS, command));
				}
				catch (Exception e)
				{
					status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE, command));
				}
			}
		}

		return status;
	}
		
	public Vector processSyncCommand(Session session,String storageId, SyncCommand syncCommand)
	throws SyncException
	{
		Vector status = new Vector();

		// process Add commands
		Vector commands = syncCommand.getAddCommands();
		for (int i = 0,size=commands.size(); i < size; i++)
		{
			Add add = (Add) commands.elementAt(i);
			try
			{											
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(add.isChunked())
				{
					continue;
				}
				
				Item item = (Item) add.getItems().elementAt(0);			
				MobileObject mobileObject = this.unmarshal(storageId, item.getData());
				
				this.saveRecord(session, mobileObject);
				
				status.addElement(this.getStatus(SyncAdapter.SUCCESS, add));
			}
			catch (Exception e)
			{				
				status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE, add));
			}
		}

		// process Replace commands
		commands = syncCommand.getReplaceCommands();
		for (int i = 0,size=commands.size(); i < size; i++)
		{
			Replace replace = (Replace) commands.elementAt(i);
			try
			{											
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(replace.isChunked())
				{
					continue;
				}
	
				Item item = (Item) replace.getItems().elementAt(0);
				MobileObject mobileObject = this.unmarshal(storageId, item.getData());
				
				//With this strategy, in case of a conflict, the change on the client
				//wins over the change on the server				
				this.saveRecord(session, mobileObject);				
				
				status.addElement(this.getStatus(SyncAdapter.SUCCESS, replace));
			}
			catch (Exception e)
			{
				status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE,replace));
			}
		}

		// process Delete commands
		commands = syncCommand.getDeleteCommands();
		for (int i = 0,size=commands.size(); i < size; i++)
		{
			Delete delete = (Delete) syncCommand.getDeleteCommands().elementAt(i);			

			try
			{
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(delete.isChunked())
				{
					continue;
				}
				
				Item item = (Item) delete.getItems().elementAt(0);
				MobileObject mobileObject = this.unmarshal(storageId, item.getData());			
				
				this.deleteRecord(session, mobileObject);
				
				status.addElement(this.getStatus(SyncAdapter.SUCCESS, delete));
			}
			catch (Exception e)
			{
				status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE, delete));
			}
		}

		return status;
	}	
	
	public void startBootSync(Session session,String storageId) throws SyncException
	{		
		//Clear the on device data
		this.clearAll(session, storageId);
		
		//Clear the on device changelog
		this.clearChangeLog(storageId);
	}
	//-------ChangeLog Support----------------------------------------------------------------------------------------------------------------------------	
	public Vector getChangeLog(String nodeId, String operation) throws SyncException
	{
		try
		{
			Vector cour = new Vector();
			
			Vector changeLog = syncDataSource.readChangeLog();
			if(changeLog != null)
			{
				for(int i=0,size=changeLog.size(); i<size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry)changeLog.elementAt(i);
					if(entry.getNodeId().equals(nodeId) && entry.getOperation().equals(operation))
					{
						cour.addElement(entry);
					}
				}
			}
			
			return cour;
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
		
	public void addChangeLogEntries(Vector entries) throws SyncException
	{
		try
		{
			syncDataSource.createChangeLogEntries(entries);
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
		
	public void clearChangeLogEntry(ChangeLogEntry logEntry) throws SyncException
	{
		try
		{
			syncDataSource.deleteChangeLogEntry(logEntry);
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
		
	public void clearChangeLog() throws SyncException
	{
		try
		{
			syncDataSource.deleteChangeLog();
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
	
	public void clearChangeLog(String service) throws SyncException
	{
		try
		{						
			Vector changeLog = syncDataSource.readChangeLog();
			if(changeLog != null)
			{
				for(int i=0,size=changeLog.size(); i<size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry)changeLog.elementAt(i);
					if(entry.getNodeId().equals(service))
					{
						this.clearChangeLogEntry(entry);
					}
				}
			}
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
	//---Anchor Management---------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Returns a new anchor to be used at the start of the sync session
	 * 
	 * @param target -
	 *            Unique Id that identifies the device/client involved in the
	 *            sync session
	 */
	public Anchor createNewAnchor(String target) throws SyncException
	{
		try
		{
			Anchor currentAnchor = syncDataSource.readAnchor(target);
			
			if (currentAnchor != null)
			{	
				// Calculate next sync
				String nextSync = this.generateSync();
				currentAnchor.setNextSync(nextSync);
			}
			else
			{
				// This is the first time the anchor is established for the
				// target
				currentAnchor = new Anchor();
				currentAnchor.setTarget(target);
	
				// Calculate the last sync
				String lastSync = this.generateSync();
				currentAnchor.setLastSync(lastSync);
	
				// Calculate the next sync
				String nextSync = lastSync;
				currentAnchor.setNextSync(nextSync);
			}
			
			//Persist the new anchor
			this.updateAnchor(currentAnchor);
	
			return currentAnchor;
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
		
	/**
	 * This method updates the anchor value at the completion of a successfull
	 * sync session
	 * 
	 * @param anchor
	 */
	private void updateAnchor(Anchor anchor) throws SyncException
	{
		try
		{
			Anchor update = new Anchor();
			
			update.setId(anchor.getId());
			update.setTarget(anchor.getTarget());
			
			//The sync was successful so swap last sync value that should be used when the next sync will be done
			update.setLastSync(anchor.getNextSync());
			update.setNextSync(anchor.getNextSync());			
			
			syncDataSource.saveAnchor(update);
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}	
	//-------Map Support------------------------------------------------------------------------------------------------------------------------------------	
	/**
	 * This comes into play only when Map synchronization between the client and the server fails
	 * within a sync session
	 * 
	 * @param source
	 * @param target
	 * @param map
	 */
	public void saveRecordMap(String source, String target, Hashtable map) throws SyncException
	{
		try
		{
			syncDataSource.saveRecordMap(source, target, map);
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
	
	
	public Hashtable readRecordMap(String source, String target) throws SyncException
	{
		try
		{
			return syncDataSource.readRecordMap(source, target);
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
	
	/**
	 * Cleans up the device record map once Map information is successfully processed by the server
	 * during a sync session 
	 * 
	 * @param source
	 * @param target
	 */
	public void removeRecordMap(String source, String target) throws SyncException
	{
		try
		{
			syncDataSource.removeRecordMap(source, target);
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
	//---Error Support-----------------------------------------------------------------------------------------------------------------------------------	
	public void saveError(SyncError error) throws SyncException
	{
		try
		{
			syncDataSource.saveError(error);
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
		
	public SyncError readError(String source, String target, String code) throws SyncException
	{
		try
		{
			SyncError error = syncDataSource.readError(source, target, code);
			return error;
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
		
	public void removeError(String source, String target, String code) throws SyncException
	{
		try
		{
			syncDataSource.removeError(source, target, code);
		}
		catch(Exception dbe)
		{
			throw new SyncException(dbe);
		}
	}
	//------------------------------------------------------------------------------------------------------------------------------------------
	protected void saveRecord(Session session, MobileObject mobileObject)
	{
		String recordId = mobileObject.getRecordId();
		MobileObject mo = mobileObjectDatabase.read(mobileObject.getStorageId(), recordId);
		if(mo != null)
		{
			//Update
			mobileObject.setId(mo.getId());
			mobileObject.setVersion(mo.getVersion());
			mobileObjectDatabase.update(mobileObject);
		}
		else
		{
			//Create
			String serverId = recordId;
			String id = mobileObjectDatabase.create(mobileObject);
			MobileObject newlyAdded = mobileObjectDatabase.read(mobileObject.getStorageId(), id);
			String deviceId = newlyAdded.getRecordId();
			if(serverId != null && deviceId != null)
			{
				if(!deviceId.equals(serverId))
				{
					session.getRecordMap().put(serverId, deviceId);
				}
			}
		}
	}
	
	protected void deleteRecord(Session session, MobileObject mobileObject)
	{
		String recordId = mobileObject.getRecordId();
		MobileObject objectToDelete = mobileObjectDatabase.read(mobileObject.getStorageId(), recordId);
		if(objectToDelete != null)
		{
			mobileObjectDatabase.delete(objectToDelete);
		}
	}	
	//------------------------------------------------------------------------------------------------------------------------------------------
	private AbstractOperation getAddCommand(MobileObject mobileObject) throws SyncException
	{
		AbstractOperation commandInfo = null;

		commandInfo = new Add();		

		Item item = new Item();
		item.setData(this.marshal(mobileObject));

		commandInfo.getItems().addElement(item);

		return commandInfo;
	}
	
	private AbstractOperation getReplaceCommand(MobileObject mobileObject) throws SyncException
	{
		AbstractOperation commandInfo = null;

		commandInfo = new Replace();		

		Item item = new Item();
		item.setData(this.marshal(mobileObject));

		commandInfo.getItems().addElement(item);

		return commandInfo;
	}
	
	private String marshalId(String id)
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();	
		
		buffer.append("<mobileObject>\n");		
		buffer.append("<recordId>"+XMLUtilities.cleanupXML(id)+"</recordId>\n");		
		buffer.append("</mobileObject>\n");
		
		xml = buffer.toString();
		
		return xml;
	}
	
	private String marshal(MobileObject mobileObject) throws SyncException
	{
		try
		{
			String recordXml = null;
			StringBuffer buffer = new StringBuffer();
						
			buffer.append(DeviceSerializer.getInstance().serialize(mobileObject));
						
			recordXml = buffer.toString();
			return recordXml;
		}
		catch(Exception e)
		{
			throw new SyncException(e);
		}
	}	
	
	public MobileObject unmarshal(String storageId, String xml) throws SyncException
	{
		try
		{			
			MobileObject mobileObject = DeviceSerializer.getInstance().
			deserialize(xml);
			
			mobileObject.setStorageId(storageId);									
			return mobileObject;
		}
		catch(Exception e)
		{
			if(e instanceof SyncException)
			{
				throw (SyncException)e;
			}
			else
			{
				throw new SyncException(e);
			}
		}
	}	
	//-----Miscellaneous services--------------------------------------------------------------------------------------------------------------------------------------------------
	public String generateSync()
	{
		String sync = null;

		//sync = String.valueOf(System.currentTimeMillis());
		sync = Utilities.generateUID();

		return sync;
	}
	public void clearAll(Session session,String storageId) throws SyncException
	{
		try
		{
			mobileObjectDatabase.deleteAll(storageId);		
			
			//TODO: cleanup all local mappings
		}
		catch(Exception e)
		{
			throw new SyncException(e);
		}
	}
		
	private Status getStatus(String statusCode, AbstractOperation operation)
	{
		Status status = new Status();

		if (operation instanceof Add)
		{
			status.setCmd(SyncXMLTags.Add);
		}
		else if (operation instanceof Replace)
		{
			status.setCmd(SyncXMLTags.Replace);
		}
		else if (operation instanceof Delete)
		{
			status.setCmd(SyncXMLTags.Delete);
		}

		status.setData(statusCode);
		status.setCmdRef(operation.getCmdId());
		Item item = (Item) operation.getItems().elementAt(0);

		if (item.getSource() != null && item.getSource().trim().length() > 0)
		{
			status.getSourceRefs().addElement(item.getSource());
		}

		return status;
	}
	//---------------------------------------------------------------------------------------------------------
	private static class SAXHandler extends DefaultHandler
	{
		private String recordId;
		private String serverRecordId;
		private String mobileObjectXml;
		private boolean isProxy;
		
		private String currentElement;
		private StringBuffer buffer;
		
		private String getRecordId()
		{
			return this.recordId;
		}
		
		public String getServerRecordId()
		{
			return this.serverRecordId;
		}
		
		private String getMobileObjectXml()
		{
			return this.mobileObjectXml;
		}
		
		private boolean isProxy()
		{
			return this.isProxy;
		}
						
		public void startElement(String uri, String localName, String qName, Attributes attributes) 
		throws SAXException
		{
			this.buffer = new StringBuffer();
			this.currentElement = qName.trim();
		}
								
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			String data = new String(ch, start, length);
			if(data != null && data.trim().length()>0)
			{
				this.buffer.append(data);
			}
		}

		public void endElement(String uri, String localName, String name)
		throws SAXException 
		{
			if(this.currentElement.equals("recordId"))
			{
				this.recordId = this.buffer.toString();
			}
			else if(this.currentElement.equals("serverRecordId"))
			{
				this.serverRecordId = this.buffer.toString();
			}
			else if(this.currentElement.equals("mobileObject"))
			{
				this.mobileObjectXml = this.buffer.toString();
			}
			else if(this.currentElement.equals("proxy"))
			{
				this.isProxy = true;
			}
		}		
	}
}
