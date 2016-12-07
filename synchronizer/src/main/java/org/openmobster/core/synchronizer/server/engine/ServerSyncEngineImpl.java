/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.engine;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanStreamable;
import org.openmobster.core.synchronizer.SyncException;
import org.openmobster.core.synchronizer.model.AbstractOperation;
import org.openmobster.core.synchronizer.model.Add;
import org.openmobster.core.synchronizer.model.Delete;
import org.openmobster.core.synchronizer.model.Item;
import org.openmobster.core.synchronizer.model.LongObject;
import org.openmobster.core.synchronizer.model.Replace;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.SyncXMLTags;
import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.SyncContext;

import org.apache.log4j.Logger;

import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.common.errors.ErrorHandler;



/**
 * 
 * @author openmobster@gmail.com
 */
public class ServerSyncEngineImpl implements ServerSyncEngine
{
	private static Logger logger = Logger.getLogger(ServerSyncEngineImpl.class);
		
	
	private MobileObjectGateway gateway = null;
	
		
	private HibernateManager hibernateManager = null;	
			
	
	private MapEngine mapEngine = null;
	
	private ConflictEngine conflictEngine;

	
	public ServerSyncEngineImpl()
	{
	}	
	
	
	public HibernateManager getHibernateManager()
	{
		return hibernateManager;
	}

	
	public void setHibernateManager(HibernateManager hibernateManager)
	{
		this.hibernateManager = hibernateManager;
	}
	
		
	public MobileObjectGateway getGateway() 
	{
		return gateway;
	}
	
	public void setGateway(MobileObjectGateway gateway) 
	{
		this.gateway = gateway;
	}

	
	public MapEngine getMapEngine()
	{
		return mapEngine;
	}
	
	public void setMapEngine(MapEngine mapEngine)
	{
		this.mapEngine = mapEngine;
	}
		
	public ConflictEngine getConflictEngine()
	{
		return conflictEngine;
	}


	public void setConflictEngine(ConflictEngine conflictEngine)
	{
		this.conflictEngine = conflictEngine;
	}


	public void start()
	{
		logger.info("ServerSide Synchronization Engine successfully started.........");
	}
	
	public void stop()
	{
		
	}	
	// ----------Synchronization related services--------------------------------------------------------------------------------------------------------------	
	public List getSlowSyncCommands(int messageSize, String pluginId)
	{
		try
		{
			List commands = new ArrayList();
			String app = SyncContext.getInstance().getApp();

			//Getting all the records of the client since this is a SlowSync
			List<MobileBean> allRecords = this.gateway.readAllRecords(pluginId);
			for (int i = 0; i < allRecords.size(); i++)
			{
				MobileBean record = allRecords.get(i);

				// Create a Sync Add Command from this record data				
				commands.add(this.getCommand(record, messageSize, 
				ServerSyncEngine.OPERATION_ADD));
				
				//Start an optimistic lock for this record
				this.conflictEngine.startOptimisticLock(app, pluginId,record);
			}
			
			//also get any records that are deleted on the server
			List deletedEntries = this.getChangeLog(SyncContext.getInstance().getDeviceId(),
			pluginId, SyncContext.getInstance().getApp(),
			ServerSyncEngine.OPERATION_DELETE);
			
			if(deletedEntries != null)
			{
				for(int entryCtr=0; entryCtr<deletedEntries.size(); entryCtr++)
				{
					ChangeLogEntry entry = (ChangeLogEntry)deletedEntries.get(entryCtr);
					
					Delete delete = new Delete();																
					Item item = new Item();					
					item.setData(this.gateway.marshalId(entry.getRecordId()));									
					delete.getItems().add(item);
					
					commands.add(delete);
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			throw new SyncException(e);
		}
	}
				
	public List getAddCommands(int messageSize,String pluginId, String syncType)			
	{
		try
		{
			List commands = new ArrayList();
			String app = SyncContext.getInstance().getApp();

			List changeLog = this
					.getChangeLog(SyncContext.getInstance().getDeviceId(),
					pluginId, SyncContext.getInstance().getApp(), ServerSyncEngine.OPERATION_ADD);
			
			for (int i = 0; i < changeLog.size(); i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.get(i);
			    MobileBean record = this.gateway.readRecord(pluginId, entry.getRecordId());
				if (record != null)
				{
					// Create a Sync Add Command from this record data					
					commands.add(this.getCommand(record, messageSize, 
							ServerSyncEngine.OPERATION_ADD));
					
					//Start an optimistic lock for this record
					this.conflictEngine.startOptimisticLock(app, pluginId,record);
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			throw new SyncException(e);
		}
	}
		
	public List getReplaceCommands(int messageSize,String pluginId, String syncType)			
	{
		try
		{
			String app = SyncContext.getInstance().getApp();
			
			List commands = new ArrayList();

			List changeLog = this.getChangeLog(SyncContext.getInstance().getDeviceId(),
					pluginId,
					SyncContext.getInstance().getApp(),
					ServerSyncEngine.OPERATION_UPDATE);
			
			for (int i = 0; i < changeLog.size(); i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.get(i);
				MobileBean record = this.gateway.readRecord(pluginId, entry.getRecordId());
				if (record != null)
				{
					// Create a Sync Add Command from this record data					
					commands.add(this.getCommand(record, messageSize, 
							ServerSyncEngine.OPERATION_UPDATE));
					
					//Start an optimistic lock for this record
					this.conflictEngine.startOptimisticLock(app, pluginId, record);
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			throw new SyncException(e);
		}
	}
		
	public List getDeleteCommands(String pluginId, String syncType)	
	{
		try
		{
			List commands = new ArrayList();

			List changeLog = this.getChangeLog(SyncContext.getInstance().getDeviceId(),
					pluginId,
					SyncContext.getInstance().getApp(),
					ServerSyncEngine.OPERATION_DELETE);
			
			for (int i = 0; i < changeLog.size(); i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.get(i);
				Delete delete = new Delete();
																	
				Item item = new Item();				
				item.setData(this.gateway.marshalId(entry.getRecordId()));
				
				delete.getItems().add(item);
				commands.add(delete);
			}

			return commands;
		}
		catch (Exception e)
		{
			throw new SyncException(e);
		}
	}
	
	public Add getStream(org.openmobster.core.synchronizer.server.Session session,
			String pluginId, SyncCommand syncCommand)
	{
		try
		{
			String app = SyncContext.getInstance().getApp();
			
			Add stream = null;
			String streamRecordId = ((Add)syncCommand.getAddCommands().get(0)).getMeta();
			streamRecordId = this.gateway.mapIdFromLocalToServer(streamRecordId);
			
			MobileBean record = this.gateway.readRecord(pluginId, streamRecordId);
			if (record != null)
			{
				stream = (Add)this.getStreamCommand(record, session.getMaxClientSize(), 
						ServerSyncEngine.OPERATION_ADD);	
				
				this.conflictEngine.startOptimisticLock(app, pluginId, record);
			}
			
			return stream;
		}
		catch(Exception e)
		{
			throw new SyncException(e);
		}
	}
	
	public List processSlowSyncCommand(org.openmobster.core.synchronizer.server.Session session,
	String pluginId, SyncCommand syncCommand)
	{
		List status = new ArrayList();
		List allCommands = syncCommand.getAllCommands();
		
		for(int i=0; i<allCommands.size(); i++)
		{
			AbstractOperation command = (AbstractOperation)allCommands.get(i);										
			//If the item already exists, check for conflicts				
			//If the item does not exist, add it to the server database, only if this item
			//has not been deleted on the serverside
			try
			{								
				Item item = (Item)command.getItems().get(0);
				
				String recordId = this.gateway.parseId(item.getData());
				
				boolean saveRecord = true;
				
				List deletedEntries = this.getChangeLog(SyncContext.getInstance().getDeviceId(),
				pluginId, SyncContext.getInstance().getApp(), ServerSyncEngine.OPERATION_DELETE);
				
				if(deletedEntries != null)
				{
					for(int entryCtr=0; entryCtr<deletedEntries.size(); entryCtr++)
					{
						ChangeLogEntry entry = (ChangeLogEntry)deletedEntries.get(entryCtr);
						if(entry.getRecordId().equals(recordId))
						{
							//This entry is deleted from the server
							//Dont add it back in
							saveRecord = false;
							break;
							
						}
					}
				}
				
				//With this strategy, in case of a conflict, the change on the client
				//wins over the change on the server
				if(saveRecord)
				{					
					this.saveRecord(pluginId, item.getData());					
				}
				
				status.add(this.getStatus(SyncServer.SUCCESS, command));
			}
			catch (Exception e)
			{
				ErrorHandler.getInstance().handle(e);
				logger.error(this, e);
				session.setRollback(true);
				
				if(e.toString().contains("optimistic_lock_error"))
				{
					status.add(this.getStatus(SyncServer.OPTIMISTIC_LOCK_ERROR, command));
				}
				else
				{
					status.add(this.getStatus(SyncServer.COMMAND_FAILURE, command));
				}
			}
		}

		return status;
	}
	
	public List processSyncCommand(org.openmobster.core.synchronizer.server.Session session,
	String pluginId, SyncCommand syncCommand)
	{
		List status = new ArrayList();
		boolean errorOccured = false;

		// process Add commands
		for (int i = 0; i < syncCommand.getAddCommands().size(); i++)
		{
			Add add = (Add) syncCommand.getAddCommands().get(i);
			
			if(errorOccured)
			{
				status.add(this.getStatus(SyncServer.COMMAND_FAILURE, add));
				continue;
			}
			
			//Make sure this is not chunked
			if(add.isChunked())
			{
				continue;
			}

			try
			{
				Item item = (Item) add.getItems().get(0);
				
				this.saveRecord(pluginId, item.getData());				
				status.add(this.getStatus(SyncServer.SUCCESS, add));
			}
			catch (Exception e)
			{
				ErrorHandler.getInstance().handle(e);
				logger.error(this, e);
				session.setRollback(true);
				
				if(e.toString().contains("optimistic_lock_error"))
				{
					status.add(this.getStatus(SyncServer.OPTIMISTIC_LOCK_ERROR, add));
				}
				else
				{
					errorOccured = true;
					status.add(this.getStatus(SyncServer.COMMAND_FAILURE, add));
					this.updateBulkErrorStatus(status);
				}
			}
		}

		// process Replace commands
		for (int i = 0; i < syncCommand.getReplaceCommands().size(); i++)
		{
			Replace replace = (Replace) syncCommand.getReplaceCommands().get(i);
			
			if(errorOccured)
			{
				status.add(this.getStatus(SyncServer.COMMAND_FAILURE, replace));
				continue;
			}
			
			//Make sure this is not chunked
			if(replace.isChunked())
			{				
				continue;
			}

			try
			{
				Item item = (Item) replace.getItems().get(0);
				
				//With this strategy, in case of a conflict, the change on the client
				//wins over the change on the server
				this.saveRecord(pluginId, item.getData());
				
				status.add(this.getStatus(SyncServer.SUCCESS, replace));
			}
			catch (Exception e)
			{
				ErrorHandler.getInstance().handle(e);
				logger.error(this, e);
				session.setRollback(true);
				
				if(e.toString().contains("optimistic_lock_error"))
				{
					status.add(this.getStatus(SyncServer.OPTIMISTIC_LOCK_ERROR, replace));
				}
				else
				{
					errorOccured = true;
					status.add(this.getStatus(SyncServer.COMMAND_FAILURE, replace));
					this.updateBulkErrorStatus(status);
				}
			}
		}

		// process Delete commands
		for (int i = 0; i < syncCommand.getDeleteCommands().size(); i++)
		{
			Delete delete = (Delete) syncCommand.getDeleteCommands().get(i);
			
			if(errorOccured)
			{
				status.add(this.getStatus(SyncServer.COMMAND_FAILURE, delete));
				continue;
			}
			
			//Make sure this is not chunked
			if(delete.isChunked())
			{
				continue;
			}
						
			try
			{				
				Item item = (Item) delete.getItems().get(0);
				String deleteRecordId = this.gateway.parseId(item.getData());
				
				this.deleteRecord(pluginId, deleteRecordId);								
				status.add(this.getStatus(SyncServer.SUCCESS, delete));
			}
			catch (Exception e)
			{
				errorOccured = true;
				ErrorHandler.getInstance().handle(e);
				logger.error(this, e);
				session.setRollback(true);
				
				status.add(this.getStatus(SyncServer.COMMAND_FAILURE, delete));
				this.updateBulkErrorStatus(status);
			}
		}

		return status;
	}	
	
	public List<Add> processBootSync(org.openmobster.core.synchronizer.server.Session session,String service)
	{
		List<Add> commands = new ArrayList<Add>();	
		String app = SyncContext.getInstance().getApp();

		//Getting selected records from the server since this is a boot sync
		List<MobileBean> allRecords = this.gateway.bootup(service);
		if(allRecords == null)
		{
			return commands;
		}
		for (int i = 0; i < allRecords.size(); i++)
		{
			MobileBean record = allRecords.get(i);
			
			if(record == null)
			{
				continue;
			}

			// Create a Sync Add Command from this record data				
			commands.add((Add)this.getCommand(record, session.getMaxClientSize(), 
			ServerSyncEngine.OPERATION_ADD));
			
			this.conflictEngine.startOptimisticLock(app, service, record);
		}
		
		return commands;
	}
	// ----------ChangeLog related services-----------------------------------------------------------------------------------------------------------------	
	public void addChangeLogEntries(String target, String app, List entries)
	{
		if (entries != null && !entries.isEmpty())
		{
			Session session = this.hibernateManager.getSessionFactory()
					.getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			ChangeLogEntry entry = null;
			try
			{
				for (int i = 0; i < entries.size(); i++)
				{
					entry = (ChangeLogEntry) entries.get(i);
					entry.setTarget(target);
					entry.setApp(app);
					
					ChangeLogEntry stored = this.getChangeLogEntry(session, entry);
					if(stored == null)
					{
						session.save(entry);
					}
				}
				tx.commit();
			} catch (ConstraintViolationException e) {
				/*
				 * Adicionado na versão 2.4-M3.1.
				 * Contorno para um problema do OpenMobster em que, apesar da verificação, uma entrada igual a uma já 
				 * existente tenta ser inserida.
				 * Pode ser causado porque mais de um serviço tenta adicionar a mesma entrada ao mesmo tempo, passando
				 * na verificação no exato momento em que o registro ainda não existe. Entretanto, não convém utilizar
				 * um lock pois a ocorrência é bem rara, fazendo com que seu custo não valha a pena.
				 * 
				 * O contorno simplesmente remove a entrada que já existe no banco e chama o método novamente.
				 */
				if (entries.remove(entry)) {
					addChangeLogEntries(target, app, entries);
					logger.warn(String.format("Workaround for duplicated change log entry applied: app = %s, target = %s, channel = %s, recordId = %s, operation = %s.", app, target, entry.getNodeId(), entry.getRecordId(), entry.getOperation()));
				}
			} catch (Exception e) {
				logger.error(this, e);
				if (tx != null)
				{
					tx.rollback();
				}
				throw new SyncException(e);
			}			
		}
	}
	
	private ChangeLogEntry getChangeLogEntry(Session session, ChangeLogEntry entry)
	{
		try
		{
			String query = "from ChangeLogEntry entry where entry.target=? AND entry.nodeId=? AND entry.operation=? AND entry.app=? AND entry.recordId=?";
		
			ChangeLogEntry stored = (ChangeLogEntry)session.createQuery(query).
				setString(0, entry.getTarget()).
				setString(1, entry.getNodeId()).
				setString(2, entry.getOperation()).
				setString(3, entry.getApp()).
				setString(4, entry.getRecordId()).
			uniqueResult();
			
			return stored;
		}
		catch (Exception e)
		{
			logger.error(this, e);
			throw new SyncException(e);
		}		
	}
	
	public List getChangeLog(String target, String nodeId, String app, String operation)
	{
		Session session = this.hibernateManager.getSessionFactory()
				.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = "from ChangeLogEntry entry where entry.target=? AND entry.nodeId=? AND entry.operation=? AND entry.app=?";

			List changeLog = session.createQuery(query).setString(0, target).setString(1, nodeId)
					.setString(2, operation).setString(3, app).list();

			tx.commit();
			
			if(changeLog == null)
			{
				changeLog = new ArrayList();
			}
			
			return changeLog;
		}
		catch (Exception e)
		{
			logger.error(this, e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}		
	}
	
	private int doesEntryExist(String deviceId,String channel,String app,String beanId, String operation)
	{
		Session session = this.hibernateManager.getSessionFactory()
				.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = "from ChangeLogEntry entry where entry.target=? AND entry.nodeId=? AND entry.app=? AND entry.recordId=? AND entry.operation=?";

			List changelog = session.createQuery(query).setString(0, deviceId).
			setString(1, channel).
			setString(2,app).
			setString(3, beanId).setString(4, operation).list();
			
			int count = 0;
			if(changelog != null && changelog.size()>0)
			{
				count = changelog.size();
			}

			tx.commit();
			
			return count;
		}
		catch (Exception e)
		{
			logger.error(this, e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}		
	}
	
	public boolean changeLogEntryExists(ChangeLogEntry entry)
	{
		Session session = this.hibernateManager.getSessionFactory()
		.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = "from ChangeLogEntry entry where entry.target=? AND entry.nodeId=? AND entry.operation=? AND entry.app=? AND entry.recordId=?";
		
			List changeLog = session.createQuery(query).setString(0, entry.getTarget()).
			setString(1, entry.getNodeId()).
			setString(2, entry.getOperation()).
			setString(3, entry.getApp()).setString(4, entry.getRecordId()).list();
			
			boolean exists = false;
			if(changeLog != null && !changeLog.isEmpty())
			{
				exists = true;
			}
		
			tx.commit();
			
			return exists;
		}
		catch (Exception e)
		{
			logger.error(this, e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}		
	}
	
	public void clearChangeLogEntry(String target, String app, ChangeLogEntry logEntry)
	{					
		String recordId = this.gateway.parseId(logEntry.getItem().getData());
		
		logEntry.setRecordId(recordId);
				
		Session session = this.hibernateManager.getSessionFactory()
				.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = "delete ChangeLogEntry entry where entry.target=? AND entry.nodeId=? " +
		    "AND entry.recordId=? AND entry.operation=? AND entry.app=?";

			session.createQuery(query).setString(0, target).
			setString(1, logEntry.getNodeId())
			.setString(2, logEntry.getRecordId()).setString(3, logEntry.getOperation()).
			setString(4, app).
			executeUpdate();
									

			tx.commit();
		}
		catch (Exception e)
		{
			logger.error(this, e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}		
	}
		
	public void clearChangeLog(String target, String service, String app)
	{				
		Session session = this.hibernateManager.getSessionFactory()
				.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String query = "delete ChangeLogEntry entry where entry.target=? AND entry.nodeId=? AND entry.app=?";

			session.createQuery(query).setString(0, target).setString(1, service).setString(2, app).executeUpdate();
						
			tx.commit();
		}
		catch (Exception e)
		{
			logger.error(this, e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}		
	}
	// ------Anchor Management related services--------------------------------------------------------------------------------------------------------------		
	/**
	 * Returns a the current latest sync anchor
	 * 
	 * @param target -
	 *            Unique Id that identifies the device/client involved in the
	 *            sync session
	 */
	public Anchor getAnchor(String target,String app)
	{
		Session session = this.hibernateManager.getSessionFactory()
				.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			Anchor anchor = (Anchor) session.createQuery(
					"from Anchor anchor where anchor.target=? AND anchor.app=?").setString(0,
					target).setString(1, app).uniqueResult();

			tx.commit();
			
			return anchor;
		}
		catch (Exception e)
		{
			logger.error(this, e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}		
	}

	/**
	 * This method updates the anchor value at the completion of a successfull
	 * sync session
	 * 
	 * @param anchor
	 */
	public void updateAnchor(Anchor anchor)
	{
		Session session = this.hibernateManager.getSessionFactory()
				.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			Anchor currentAnchor = (Anchor) session.createQuery(
					"from Anchor anchor where anchor.target=? AND anchor.app=?").setString(0,
					anchor.getTarget()).setString(1, anchor.getApp()).uniqueResult();

			if (currentAnchor != null)
			{
				// Update the anchor
				currentAnchor.setLastSync(anchor.getLastSync());
				currentAnchor.setNextSync(anchor.getNextSync());
				session.update(currentAnchor);
			}
			else
			{
				// Create the first instance of the anchor for this target
				session.save(anchor);
			}

			tx.commit();
		}
		catch (Exception e)
		{
			logger.error(this, e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}		
	}
	
	public void deleteAnchor(String target,String app)
	{
		Session session = this.hibernateManager.getSessionFactory()
				.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			Anchor currentAnchor = (Anchor) session.createQuery(
					"from Anchor anchor where anchor.target=? AND anchor.app=?").setString(0,
					target).setString(1, app).uniqueResult();

			if (currentAnchor != null)
			{
				// Update the anchor
				session.delete(currentAnchor);
			}

			tx.commit();
		}
		catch (Exception e)
		{
			logger.error(this, e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}		
	}		
	//---------------RecordMap related functionality------------------------------------------------------------------------------------------------------	
	public void saveRecordMap(String source, String target, Map recordMap)
	{
		mapEngine.saveRecordMap(source, target, recordMap);
	}	
	
	public void clearRecordMap()
	{
		mapEngine.clearAll();
	}
	//-------helpers-------------------------------------------------------------------------------------------------------------------------------------	
	public String marshal(MobileBean record) throws SyncException
	{
		return this.gateway.marshal(record);
	}
		
	protected void saveRecord(String pluginId, String xml)
	{	
		String beforeRecordId = this.gateway.parseId(xml);
		String recordId = this.gateway.mapIdFromLocalToServer(beforeRecordId);	
		String app = SyncContext.getInstance().getApp();
				
		MobileBean cour = this.gateway.readRecord(pluginId, recordId);		
		if(cour != null)
		{
			//Check against Optimistic Lock Error
			boolean safe = this.conflictEngine.checkOptimisticLock(app, pluginId,cour);
			
			//Update this record
			if(safe)
			{
				this.gateway.updateRecord(pluginId, recordId, xml);
				
				//update the optimistic lock state
				MobileBean updatedBean = this.gateway.readRecord(pluginId, recordId);
				this.conflictEngine.startOptimisticLock(app, pluginId, updatedBean);
			}
			else
			{
				throw new SyncException("optimistic_lock_error");
			}
		}
		else
		{
			//Check and make sure the bean is not deleted
			if(this.isBeanDeleted(recordId))
			{
				//This bean has been deleted from the system.
				//This is a form of conflict
				//this should not be allowed to recreate the bean in the Cloud
				//it has been deleted everywhere
				return;
			}
			
			//Create this record			
			String newRecordId = this.gateway.createRecord(pluginId, xml);
			if(!newRecordId.equals(recordId))
			{
				//Record this mapping between local and server record
				Map recordMap = new HashMap();
				recordMap.put(newRecordId, recordId);
				mapEngine.saveRecordMap(recordMap);
			}
			
			MobileBean newBean = this.gateway.readRecord(pluginId, newRecordId);
			this.conflictEngine.startOptimisticLock(app, pluginId, newBean);
		}		
	}
		
	protected void deleteRecord(String pluginId, String recordId)
	{								
		MobileBean cour = this.gateway.readRecord(pluginId, recordId);		
		if(cour != null)
		{
			this.gateway.deleteRecord(pluginId, recordId);
		}				
	}
			
	private AbstractOperation getCommand(MobileBean record, 
	int messageSize, String operation) throws SyncException
	{
		AbstractOperation commandInfo = null;
		
		/*if(record instanceof MobileBeanStreamable)
		{
			record = ((MobileBeanStreamable)record).getPartial();
		}*/
		
		boolean isAdd = false;
		if (operation.equals(ServerSyncEngine.OPERATION_ADD))
		{
			commandInfo = new Add();
			isAdd = true;
		}
		else if (operation.equals(ServerSyncEngine.OPERATION_UPDATE))
		{
			commandInfo = new Replace();
		}
		
		Item item = new Item();
		commandInfo.getItems().add(item);
		
		//Setup any LongObject related data
		String data = this.marshal(record);
		LongObject longObject = new LongObject(messageSize, data);
		if(longObject.getDataChunks() != null && longObject.getDataChunks().size() >1)
		{
			item.setMoreData(true);
			item.setData(longObject.getCurrentChunk());
			commandInfo.setChunkedObject(longObject);
			commandInfo.setChunkedRecord(record);
		}
		else
		{
			item.setMoreData(false);
			item.setData(data);
			commandInfo.setChunkedObject(null);
			commandInfo.setChunkedRecord(null);
		}		
		
		
		return commandInfo;
	}
		
	private AbstractOperation getStreamCommand(MobileBean record, 
	int messageSize, String operation) throws SyncException
	{
		AbstractOperation commandInfo = null;
		
		/*if(record instanceof MobileBeanStreamable)
		{
			record = ((MobileBeanStreamable)record).getFull();
		}*/
		
		boolean isAdd = false;
		if (operation.equals(ServerSyncEngine.OPERATION_ADD))
		{
			commandInfo = new Add();
			isAdd = true;
		}
		else if (operation.equals(ServerSyncEngine.OPERATION_UPDATE))
		{
			commandInfo = new Replace();
		}
		
		Item item = new Item();
		commandInfo.getItems().add(item);
		
		//Setup any LongObject related data
		String data = this.marshal(record);
		LongObject longObject = new LongObject(messageSize, data);
		if(longObject.getDataChunks() != null && longObject.getDataChunks().size() >1)
		{
			item.setMoreData(true);
			item.setData(longObject.getCurrentChunk());
			commandInfo.setChunkedObject(longObject);
			commandInfo.setChunkedRecord(record);
		}
		else
		{
			item.setMoreData(false);
			item.setData(data);
			commandInfo.setChunkedObject(null);
			commandInfo.setChunkedRecord(null);
		}		
		
		
		return commandInfo;
	}
	
	private boolean isBeanDeleted(String beanId)
	{
		String deviceId = Tools.getDeviceId();
		String channel = Tools.getChannel();
		String app = SyncContext.getInstance().getApp();
		String operation = ServerSyncEngine.OPERATION_DELETE;
		
		int count = this.doesEntryExist(deviceId,channel,app,beanId,operation);
		if(count == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	//------Miscellaneous services---------------------------------------------------------------------------------------------------------------------------		
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
		Item item = (Item) operation.getItems().get(0);

		if (item.getSource() != null && item.getSource().trim().length() > 0)
		{
			status.getSourceRefs().add(item.getSource());
		}

		return status;
	}
	
	private void updateBulkErrorStatus(List statuses)
	{
		if(statuses == null || statuses.isEmpty())
		{
			return;
		}
		
		for(Object local:statuses)
		{
			Status status = (Status)local;
			status.setData(SyncServer.COMMAND_FAILURE);
		}
	}
	
	private String generateSync()
	{
		String sync = null;

		sync = Utilities.generateUID();

		return sync;
	}
	
	public void clearConflictEngine()
	{
		this.conflictEngine.clearAll();
	}
}
