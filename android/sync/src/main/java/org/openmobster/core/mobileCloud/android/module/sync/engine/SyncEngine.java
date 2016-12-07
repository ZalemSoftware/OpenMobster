/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync.engine;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;



import org.openmobster.android.utils.OpenMobsterBugUtils;
import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;
import org.openmobster.core.mobileCloud.android.module.mobileObject.DeviceSerializer;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.module.sync.AbstractOperation;
import org.openmobster.core.mobileCloud.android.module.sync.Add;
import org.openmobster.core.mobileCloud.android.module.sync.Anchor;
import org.openmobster.core.mobileCloud.android.module.sync.Delete;
import org.openmobster.core.mobileCloud.android.module.sync.Item;
import org.openmobster.core.mobileCloud.android.module.sync.Replace;
import org.openmobster.core.mobileCloud.android.module.sync.Session;
import org.openmobster.core.mobileCloud.android.module.sync.Status;
import org.openmobster.core.mobileCloud.android.module.sync.SyncAdapter;
import org.openmobster.core.mobileCloud.android.module.sync.SyncCommand;
import org.openmobster.core.mobileCloud.android.module.sync.SyncException;
import org.openmobster.core.mobileCloud.android.module.sync.SyncXMLTags;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.util.XMLUtil;
import org.openmobster.core.mobileCloud.android.module.bus.MobilePushInvocation;
import org.openmobster.core.mobileCloud.android.module.bus.MobilePushMetaData;

import android.util.Log;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public class SyncEngine 
{
	/**
	 * 
	 */
	public static String OPERATION_ADD = "Add";

	public static String OPERATION_UPDATE = "Replace";

	public static String OPERATION_DELETE = "Delete";
	
	public static String OPERATION_MAP = "Map";
	
	/**
	 * 
	 * 
	 */
	public SyncEngine()
	{
	}	
	//------Synchronization related services--------------------------------------------------------------------	
	public List<? extends AbstractOperation> getSlowSyncCommands(int messageSize, 
	String storageId) throws SyncException
	{
		try
		{
			List<AbstractOperation> commands = new ArrayList<AbstractOperation>();

			//Getting all the records of the client since this is a SlowSync
			Set<MobileObject> allRecords = MobileObjectDatabase.getInstance().
			readAll(storageId);
			if(allRecords != null)
			{
				for (MobileObject mo:allRecords)
				{
					// Create a Sync Add Command from this record data
					commands.add(this.getAddCommand(mo));
				}
			}
			
			return commands;
		}
		catch (Exception e)
		{
			throw new SyncException(this.getClass().getName(), "getSlowSyncCommands", new Object[]
			  {
				"StorageId=" + storageId,				
			  }
			);
		}
	}
		
	public List<? extends AbstractOperation> getAddCommands(int messageSize,
	String storageId, String syncType) throws SyncException
	{
		List<ChangeLogEntry> changeLog = null;
		try
		{
			List<AbstractOperation> commands = new ArrayList<AbstractOperation>();

			changeLog = this.getChangeLog(storageId, SyncEngine.OPERATION_ADD);
			for (ChangeLogEntry entry:changeLog)
			{
				MobileObject cour = MobileObjectDatabase.getInstance().
				read(storageId, entry.getRecordId());
				if (cour != null)
				{
					// Create a Sync Add Command from this record data				
					commands.add(this.getAddCommand(cour));
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{
				for (ChangeLogEntry entry:changeLog)
				{
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(this.getClass().getName(), "getAddCommands", new Object[]
			  {
				"StorageId=" + storageId + ", SyncType="+syncType,
				"ChangeLogIds=" + buffer.toString()
			  }
			);
		}
	}
		
	public List<? extends AbstractOperation> getReplaceCommands(int messageSize,
	String storageId, String syncType)
			throws SyncException
	{
		List<ChangeLogEntry> changeLog = null;
		try
		{
			List<AbstractOperation> commands = new ArrayList<AbstractOperation>();

			changeLog = this.getChangeLog(storageId,
					SyncEngine.OPERATION_UPDATE);
			for (ChangeLogEntry entry:changeLog)
			{
				MobileObject cour = MobileObjectDatabase.getInstance().
				read(storageId, entry.getRecordId());
				if (cour != null)
				{
					// Create a Sync Add Command from this record data				
					commands.add(this.getReplaceCommand(cour));
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{				
				for (ChangeLogEntry entry:changeLog)
				{
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(this.getClass().getName(), "getReplaceCommands", new Object[]
			  {
				"StorageId=" + storageId + ", SyncType="+syncType,
				"ChangeLogIds=" + buffer.toString()
			  }
			);
		}
	}
		
	public List<? extends AbstractOperation> getDeleteCommands(String storageId, 
	String syncType)
			throws SyncException
	{
		List<ChangeLogEntry> changeLog = null;
		try
		{
			List<AbstractOperation> commands = new ArrayList<AbstractOperation>();

			changeLog = this.getChangeLog(storageId,
					SyncEngine.OPERATION_DELETE);
			for (ChangeLogEntry entry:changeLog)
			{
				Delete delete = new Delete();
				
				Item item = new Item();
				item.setData(this.marshalId(entry.getRecordId()));
				delete.getItems().add(item);
				
				commands.add(delete);
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{				
				for (ChangeLogEntry entry:changeLog)
				{
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(this.getClass().getName(), "getDeleteCommands", new Object[]
			  {
				"StorageId=" + storageId + ", SyncType="+syncType,
				"ChangeLogIds=" + buffer.toString()
			  }
			);
		}
	}
	
	public List<Status> processSlowSyncCommand(Session session,String storageId, SyncCommand syncCommand)
	throws SyncException
	{
		try
		{
			List<Status> status = new ArrayList<Status>();
			List<? extends AbstractOperation> allCommands = syncCommand.getAllCommands();
			
			if(allCommands != null)
			{
				for(AbstractOperation command:allCommands)
				{									
					/**
					 * Long Object Support
					 */
					if(command.isChunked())
					{
						continue;
					}
					
					Item item = command.getItems().iterator().next();			
					if(command instanceof Add)
					{
						//Remove this item if it exists from the client database				
						//Add this item to the client database
						try
						{										
							MobileObject mobileObject = this.unmarshal(storageId,
							item.getData());
							this.deleteRecord(session, mobileObject);
							this.saveRecord(session, mobileObject);
							
							status.add(this.getStatus(SyncAdapter.SUCCESS, command));
						}
						catch (Exception e)
						{					
							status.add(this.getStatus(SyncAdapter.COMMAND_FAILURE, command));
						}
					}
					else if(command instanceof Delete)
					{
						//Remove this item if it exists from the client database
						try
						{
							MobileObject mobileObject = this.unmarshal(storageId,
							item.getData());
							this.deleteRecord(session, mobileObject);
							status.add(this.getStatus(SyncAdapter.SUCCESS, command));
						}
						catch (Exception e)
						{
							status.add(this.getStatus(SyncAdapter.COMMAND_FAILURE, command));
						}
					}
				}
			}
	
			return status;
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "processSlowSyncCommand", new Object[]
			  {
				"StorageId=" + storageId				
			  }
			);
		}
	}
		
	public List<Status> processSyncCommand(Session session,String storageId, SyncCommand syncCommand)
	throws SyncException
	{
		try
		{
			List<Status> status = new ArrayList<Status>();	
			
			MobilePushInvocation invocation = session.getPushInvocation();
			if(invocation == null && session.isBackgroundSync())
			{
				invocation = new MobilePushInvocation(AppSystemConfig.getInstance().getCustomPushNotificationHandler());
				session.setPushInvocation(invocation);
			}
	
			// process Add commands
			List<Add> commands = syncCommand.getAddCommands();
			for (Add add:commands)
			{
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(add.isChunked())
				{
					continue;
				}
				
				try
				{
					Item item = (Item) add.getItems().iterator().next();			
					MobileObject mobileObject = this.unmarshal(storageId, 
					item.getData());
					
					String objectId = this.saveRecord(session, mobileObject);
					
					if(invocation != null)
					{
						MobilePushMetaData metaData = new MobilePushMetaData(storageId, objectId);
						metaData.setAdded(true);
						invocation.addMobilePushMetaData(metaData);
					}
					
					status.add(this.getStatus(SyncAdapter.SUCCESS, add));
				}
				catch (Exception e)
				{				
					status.add(this.getStatus(SyncAdapter.COMMAND_FAILURE, add));
				}
			}
	
			// process Replace commands
			List<Replace> replaceCommands = syncCommand.getReplaceCommands();
			for (Replace replace:replaceCommands)
			{
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(replace.isChunked())
				{
					continue;
				}
				
				try
				{
					Item item = (Item) replace.getItems().iterator().next();
					MobileObject mobileObject = this.unmarshal(storageId, 
					item.getData());
					
					//With this strategy, in case of a conflict, the change on the client
					//wins over the change on the server				
					String objectId = this.saveRecord(session, mobileObject);
					
					if(invocation != null)
					{
						MobilePushMetaData metaData = new MobilePushMetaData(storageId, objectId);
						metaData.setUpdated(true);
						invocation.addMobilePushMetaData(metaData);
					}
					
					status.add(this.getStatus(SyncAdapter.SUCCESS, replace));
				}
				catch (Exception e)
				{
					status.add(this.getStatus(SyncAdapter.COMMAND_FAILURE,replace));
				}
			}
	
			// process Delete commands
			List<Delete> deleteCommands = syncCommand.getDeleteCommands();
			for (Delete delete:deleteCommands)
			{
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(delete.isChunked())
				{
					continue;
				}
										
				try
				{				
					Item item = (Item) delete.getItems().iterator().next();
					MobileObject mobileObject = this.unmarshal(storageId, 
					item.getData());
					
					this.deleteRecord(session, mobileObject);
					
					if(invocation != null)
					{
						MobilePushMetaData metaData = new MobilePushMetaData(storageId, mobileObject.getRecordId());
						metaData.setDeleted(true);
						invocation.addMobilePushMetaData(metaData);
					}
					
					status.add(this.getStatus(SyncAdapter.SUCCESS, delete));
				}
				catch (Exception e)
				{
					status.add(this.getStatus(SyncAdapter.COMMAND_FAILURE, delete));
				}
			}
			
			return status;
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "processSyncCommand", new Object[]
			  {
				"StorageId=" + storageId				
			  }
			);
		}
	}	
	
	public void startBootSync(Session session,String storageId) throws SyncException
	{	
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Contorno para o problema do OpenMobster apagar dados não sincronizados. Evita a exclusão total dos dados
		 * de canais persistentes, pois o Bootup irá apenas sobrescrever o que precisa.
		 */
		if (OpenMobsterBugUtils.getInstance().isPersistentChannel(storageId)) {
			return;
		}
		
		try
		{
			//Clear the on device data
			this.clearAll(session, storageId);
			
			//Clear the on device changelog
			this.clearChangeLog(storageId);
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "startBootSync", new Object[]
			  {
				"StorageId=" + storageId				
			  }
			);
		}
	}		
	//-------ChangeLog Support---------------------------------------------------------------------------------------------------------------------------	
	public List<ChangeLogEntry> getChangeLog(String nodeId, String operation) throws SyncException
	{
		try
		{
			List<ChangeLogEntry> cour = new ArrayList<ChangeLogEntry>();
			
			List<ChangeLogEntry> changeLog = SyncDataSource.getInstance().
			readChangeLog();
			if(changeLog != null)
			{
				for(ChangeLogEntry entry:changeLog)
				{
					if(entry.getNodeId().equals(nodeId) && 
					entry.getOperation().equals(operation))
					{
						cour.add(entry);
					}
				}
			}
			
			return cour;
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "getChangeLog", new Object[]{
				"NodeId="+nodeId,
				"Operation="+operation,
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public void addChangeLogEntries(List<ChangeLogEntry> entries) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().createChangeLogEntries(entries);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "addChangeLogEntries", new Object[]{
				"Database Error="+dbe.getMessage()
			});
		}
	}
	
	public void addChangeLogEntry(ChangeLogEntry entry) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().createChangeLogEntry(entry);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "addChangeLogEntries", new Object[]{
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public void clearChangeLogEntry(ChangeLogEntry logEntry) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().deleteChangeLogEntry(logEntry);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "clearChangeLogEntry", new Object[]{
				"ChangeLogEntry="+logEntry.toString(),				
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public void clearChangeLog() throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().deleteChangeLog();
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "clearChangeLog", new Object[]{
				"Database Error="+dbe.getMessage()
			});
		}
	}
	
	public void clearChangeLog(String service) throws SyncException
	{
		try
		{			
			List<ChangeLogEntry> changeLog = SyncDataSource.getInstance().
			readChangeLog();
			if(changeLog != null)
			{
				for(ChangeLogEntry entry:changeLog)
				{
					if(entry.getNodeId().equals(service))
					{
						this.clearChangeLogEntry(entry);
					}
				}
			}
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "getChangeLog", new Object[]{
				"NodeId="+service,
				"Database Error="+dbe.getMessage()
			});
		}
	}
	//---Anchor Management-----------------------------------------------------------------------------------------------------------------------------------	
	public Anchor createNewAnchor(String target) throws SyncException
	{
		try
		{
			Anchor currentAnchor = SyncDataSource.getInstance().readAnchor(target);
			
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
			
			
			//System.out.println("***********************************************************");
			//System.out.println("Anchor Target: "+currentAnchor.getTarget());
			//System.out.println("Anchor LastSync: "+currentAnchor.getLastSync());
			//System.out.println("Anchor NextSync: "+currentAnchor.getNextSync());
			//System.out.println("************************************************************");
	
			return currentAnchor;
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "createNewAnchor", new Object[]{
				"Target="+target,
				"Database Error="+dbe.getMessage()
			});
		}
	}
			
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
			
			SyncDataSource.getInstance().saveAnchor(update);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "updateAnchor", new Object[]{
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public String generateSync() throws SyncException
	{
		try
		{
			String sync = null;
	
			sync = String.valueOf(GeneralTools.generateUniqueId());
	
			return sync;
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "generateSync", new Object[]
			  {
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			  }
			);
		}
	}
	//-------Map Support--------------------------------------------------------------------------------------------------------------------------------------	
	/**
	 * This comes into play only when Map synchronization between the client and the server fails
	 * within a sync session
	 * 
	 * @param source
	 * @param target
	 * @param map
	 */
	public void saveRecordMap(String source, String target, Map<String,String> map) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().saveRecordMap(source, target, map);
		}
		catch(Exception dbe)
		{
			//Log colocado na versão 2.4-M3.1 do OpenMobster.
			if (OpenMobsterBugUtils.getInstance().isPersistentChannel(source)) {
				Log.e("OpenMobster Error", String.format("An Exception ocurred while saving a record map! The local data of the following channel will not be affected: %s/%s.", source, target));
				dbe.printStackTrace();
			}
			
			throw new SyncException(this.getClass().getName(), "saveRecordMap", new Object[]{
				"Source="+source,
				"Target="+target,
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public Map<String,String> readRecordMap(String source, String target) throws SyncException
	{
		try
		{
			return SyncDataSource.getInstance().readRecordMap(source, target);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "readRecordMap", new Object[]{
				"Source="+source,
				"Target="+target,
				"Database Error="+dbe.getMessage()
			});
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
			SyncDataSource.getInstance().removeRecordMap(source, target);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "removeRecordMap", new Object[]{
				"Source="+source,
				"Target="+target,
				"Database Error="+dbe.getMessage()
			});
		}
	}
	//---Error Support------------------------------------------------------------------------------------------------------------------------------------	
	public void saveError(SyncError error) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().saveError(error);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "saveError", new Object[]{				
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public SyncError readError(String source, String target, String code) throws SyncException
	{
		try
		{
			SyncError error = SyncDataSource.getInstance().readError(source, target, code);
			return error;
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "saveError", new Object[]{
				"Source="+source,
				"Target="+target,
				"Code="+code,
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public void removeError(String source, String target, String code) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().removeError(source, target, code);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "removeError", new Object[]{
				"Source="+source,
				"Target="+target,
				"Code="+code,
				"Database Error="+dbe.getMessage()
			});
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------
	private String saveRecord(Session session, MobileObject mobileObject)
	{
		String recordId = mobileObject.getRecordId();
		MobileObject mo = MobileObjectDatabase.getInstance().read(mobileObject.getStorageId(), recordId);
		if(mo != null)
		{
			//Update
			mobileObject.setRecordId(mo.getRecordId());
			MobileObjectDatabase.getInstance().update(mobileObject);
		}
		else
		{
			//Create
			String serverId = recordId;
			String deviceId = MobileObjectDatabase.getInstance().create(mobileObject);
			recordId = deviceId;
			if(serverId != null && deviceId != null)
			{
				if(!deviceId.equals(serverId))
				{
					session.getRecordMap().put(serverId, deviceId);
				}
			}
		}
		
		return recordId;
	}
	
	private void deleteRecord(Session session, MobileObject mobileObject)
	{
		String recordId = mobileObject.getRecordId();
		MobileObject objectToDelete = MobileObjectDatabase.getInstance().read(mobileObject.getStorageId(), recordId);
		if(objectToDelete != null)
		{
			MobileObjectDatabase.getInstance().delete(objectToDelete);
		}				
	}	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	private AbstractOperation getAddCommand(MobileObject mobileObject) throws SyncException
	{
		AbstractOperation commandInfo = null;

		commandInfo = new Add();		

		Item item = new Item();
		item.setData(this.marshal(mobileObject));

		commandInfo.getItems().add(item);

		return commandInfo;
	}
	
	private AbstractOperation getReplaceCommand(MobileObject mobileObject) throws SyncException
	{
		AbstractOperation commandInfo = null;

		commandInfo = new Replace();		

		Item item = new Item();
		item.setData(this.marshal(mobileObject));

		commandInfo.getItems().add(item);

		return commandInfo;
	}
	
	private String marshalId(String id) throws SyncException
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();	
		
		buffer.append("<mobileObject>\n");		
		buffer.append("<recordId>"+XMLUtil.cleanupXML(id)+"</recordId>\n");		
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
			throw new SyncException(this.getClass().getName(), "marshal", new Object[]
	              {
					DeviceSerializer.getInstance().serialize(mobileObject)
	              }
			);
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
				throw new SyncException(this.getClass().getName(), "unmarshal", new Object[]
		           {
						storageId,
						xml,
						e.getMessage()
		           }
				);
			}
		}
	}
	//-----Miscellaneous services-----------------------------------------------------------------------------------------------------------------------------------------------------
	public void clearAll(Session session,String storageId) throws SyncException
	{
		try
		{
			MobileObjectDatabase.getInstance().bootup(storageId);	
			
			//TODO: cleanup all local mappings....
			//not urgent...the object sync technology does not use the
			//mapping feature of the sync engine, yet
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "clearAll", new Object[]
			  {
				"StorageId=" + storageId,
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			  }
			);
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
		Item item = (Item) operation.getItems().iterator().next();

		if (item.getSource() != null && item.getSource().trim().length() > 0)
		{
			status.getSourceRefs().add(item.getSource());
		}

		return status;
	}	
}
