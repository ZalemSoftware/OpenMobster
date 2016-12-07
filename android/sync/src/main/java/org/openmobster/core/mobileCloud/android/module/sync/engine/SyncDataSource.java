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
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.sync.Anchor;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.storage.DBException;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncDataSource extends Service
{		
	/**
	 * 
	 *
	 */
	public SyncDataSource()
	{
	}
	
	/**
	 * 
	 * @return
	 */
	public static SyncDataSource getInstance()
	{
		return (SyncDataSource)Registry.getActiveInstance().lookup(SyncDataSource.class);
	}
	
	public void start()
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Database database = Database.getInstance(context);
			
			//Initialize the changelog table
			if(!database.doesTableExist(Database.sync_changelog_table))
			{
				database.createTable(Database.sync_changelog_table);
			}
			
			//Initialize the anchor table
			if(!database.doesTableExist(Database.sync_anchor))
			{
				database.createTable(Database.sync_anchor);
			}
			
			//Initialize the recordmap table
			if(!database.doesTableExist(Database.sync_recordmap))
			{
				database.createTable(Database.sync_recordmap);
			}
			
			//Initialize the syncerror table
			if(!database.doesTableExist(Database.sync_error))
			{
				database.createTable(Database.sync_error);
			}
		}
		catch(DBException dbe)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{
				"Database Exception="+dbe.getMessage()
			});
		}
	}
	
	public void stop()
	{
		
	}
	
	/**
	 * 
	 *
	 */
	public void clearAll()
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Database.getInstance(context).deleteAll(Database.sync_recordmap);			
			Database.getInstance(context).deleteAll(Database.sync_error);	
			this.deleteChangeLog();
		}
		catch(DBException dbe)
		{
			throw new SystemException(this.getClass().getName(), "clearAll", new Object[]{
				"Database Exception="+dbe.getMessage()
			}); 
		}
	}
	//------Anchor related data services-------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public Anchor readAnchor(String target) throws DBException
	{
		Context context = Registry.getActiveInstance().getContext();
		Set<Record> anchors = Database.getInstance(context).selectAll(Database.sync_anchor);
		if(anchors!=null && !anchors.isEmpty())
		{
			for(Record local:anchors)
			{
				if(local.getValue("target").equals(target))
				{
					return new Anchor(local);
				}
			}
		}
		
		return null;		
	}
	
	/**
	 * 
	 *
	 */
	public void saveAnchor(Anchor anchor) throws DBException
	{
		String target = anchor.getTarget();
		Anchor stored = this.readAnchor(target);
		Context context = Registry.getActiveInstance().getContext();
		
		if(stored == null)
		{
			//Create a new anchor in the database
			Record record = anchor.getRecord();
			Database.getInstance(context).insert(Database.sync_anchor, record);
		}
		else
		{
			Set<Record> anchors = Database.getInstance(context).selectAll(Database.sync_anchor);
			if(anchors != null)
			{
				for(Record local:anchors)
				{
					String localTarget = local.getValue("target");
					
					if(localTarget.equals(target))
					{
						//Update the existing anchor in the database
						local.setValue("target", anchor.getTarget());			
						local.setValue("lastSync", anchor.getLastSync());			
						local.setValue("nextSync", anchor.getNextSync());			
						Database.getInstance(context).update(Database.sync_anchor, local);
					}
				}
			}
		}		
	}
	
	/**
	 * 
	 * @throws DBException
	 */
	public void deleteAllAnchors() throws DBException
	{
		Context context = Registry.getActiveInstance().getContext();
		Database.getInstance(context).deleteAll(Database.sync_anchor);
	}
	//----ChangeLog Support-----------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param entries
	 */
	public void createChangeLogEntries(List<ChangeLogEntry> entries) throws DBException
	{
		if(entries != null)
		{
			Context context = Registry.getActiveInstance().getContext();
			List<ChangeLogEntry> changelog = this.readChangeLog();
			for(ChangeLogEntry cour:entries)
			{
				if(changelog.contains(cour))
				{
					continue;
				}
				Record record = cour.getRecord();
				Database.getInstance(context).insert(Database.sync_changelog_table, record);
			}
		}
	}
	
	public void createChangeLogEntry(ChangeLogEntry entry) throws DBException
	{
		if(entry != null)
		{
			Context context = Registry.getActiveInstance().getContext();
			Record record = entry.getRecord();
			Database.getInstance(context).insert(Database.sync_changelog_table, record);
		}
	}
	
	/**
	 * 
	 */
	public List<ChangeLogEntry> readChangeLog() throws DBException
	{
		List<ChangeLogEntry> changeLog = new ArrayList<ChangeLogEntry>();
		
		Context context = Registry.getActiveInstance().getContext();
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Obtém o change log de forma ordenada para que os registros sejam enviados para o servidor de acordo
		 * com a ordem que foram salvos.
		 */
		Set<Record> changeLogRecords = Database.getInstance(context).selectAll(Database.sync_changelog_table, true);
		if(changeLogRecords != null)
		{
			for(Record record:changeLogRecords)
			{
				changeLog.add(new ChangeLogEntry(record));
			}
		}
		
		return changeLog;
	}			

	/**
	 * 
	 * @param changeLogEntry
	 */
	public void deleteChangeLogEntry(ChangeLogEntry changeLogEntry) throws DBException
	{
		Record toBeDeleted = null;
		
		Context context = Registry.getActiveInstance().getContext();
		List<ChangeLogEntry> log = this.readChangeLog();
		
		if(log != null)
		{
			for(ChangeLogEntry cour:log)
			{
				if(cour.equals(changeLogEntry))
				{
					toBeDeleted = cour.getRecord();
					break;
				}
			}
			if(toBeDeleted != null)
			{
				Database.getInstance(context).delete(Database.sync_changelog_table, 
				toBeDeleted);
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	public void deleteChangeLog() throws DBException
	{
		Context context = Registry.getActiveInstance().getContext();
		Database.getInstance(context).deleteAll(Database.sync_changelog_table);
	}
	
	public void hasChangeLog() throws DBException
	{
		Context context = Registry.getActiveInstance().getContext();
		Database.getInstance(context).isTableEmpty(Database.sync_changelog_table);
	}
	//-------Map Support------------------------------------------------------------------------------------
	/**
	 * 
	 */
	private List<RecordMap> getMappedRecords() throws DBException
	{
		List<RecordMap> mappedRecords = new ArrayList<RecordMap>();
		
		Context context = Registry.getActiveInstance().getContext();
		Set<Record> records = Database.getInstance(context).
		selectAll(Database.sync_recordmap);
		if(records != null)
		{
			for(Record record:records)
			{
				mappedRecords.add(new RecordMap(record));
			}
		}
		
		return mappedRecords;
	}
	
	/**
	 * This comes into play only when Map synchronization between the client and the server fails
	 * within a sync session
	 * 
	 * @param source
	 * @param target
	 * @param map
	 */
	public void saveRecordMap(String source, String target, Map<String,String> map) throws DBException
	{
		if(map != null)
		{
			Context context = Registry.getActiveInstance().getContext();
			Set<String> guids = map.keySet();
			for(String guid:guids)
			{ 
				String luid = map.get(guid);
				
				RecordMap recordMap = new RecordMap();
				recordMap.setSource(source);
				recordMap.setTarget(target);
				recordMap.setGuid(guid);
				recordMap.setLuid(luid);
				
				Database.getInstance(context).insert(Database.sync_recordmap, 
				recordMap.getRecord());
			}
		}
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public Map<String,String> readRecordMap(String source, String target) throws DBException
	{
		Map<String,String> map = null;
		
		map = new HashMap<String,String>();
		List<RecordMap> recordMaps = this.getMappedRecords();
		if(recordMaps != null)
		{
			for(RecordMap cour:recordMaps)
			{
				if(cour.getSource().equals(source) && cour.getTarget().equals(target))
				{
					map.put(cour.getGuid(), cour.getLuid());
				}
			}
		}
		
		return map;
	}
	
	/**
	 * Cleans up the device record map once Map information is successfully processed by the server
	 * during a sync session 
	 * 
	 * @param source
	 * @param target
	 */
	public void removeRecordMap(String source, String target) throws DBException
	{	
		Context context = Registry.getActiveInstance().getContext();
		List<RecordMap> recordMaps = this.getMappedRecords();
		if(recordMaps != null)
		{
			for(RecordMap cour:recordMaps)
			{
				if(cour.getSource().equals(source) && cour.getTarget().equals(target))
				{
					Record record = Database.getInstance(context).select(Database.sync_recordmap, cour.getId());
					Database.getInstance(context).
					delete(Database.sync_recordmap, record);
				}
			}
		}
	}
	//---Error Support------------------------------------------------------------------------------------
	/**
	 * 
	 */
	private List<SyncError> getSyncErrors() throws DBException
	{
		List<SyncError> syncErrors = new ArrayList<SyncError>();
		
		Context context = Registry.getActiveInstance().getContext();
		Set<Record> records = Database.getInstance(context).selectAll(Database.sync_error);
		if(records != null)
		{
			for(Record record:records)
			{
				syncErrors.add(new SyncError(record));
			}
		}
		
		return syncErrors;
	}
	
	/**
	 * 
	 */
	public void saveError(SyncError error) throws DBException
	{
		Record record = error.getRecord();
		Context context = Registry.getActiveInstance().getContext();
		Database.getInstance(context).insert(Database.sync_error, record);
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param code
	 * @return
	 */
	public SyncError readError(String source, String target, String code) throws DBException
	{
		SyncError syncError = null;
		
		List<SyncError> syncErrors = this.getSyncErrors();
		if(syncErrors != null)
		{
			for(SyncError cour:syncErrors)
			{
				if(cour.getSource().equals(source) && 
				   cour.getTarget().equals(target) && 
				   cour.getCode().equals(code))
				{
					syncError = cour;
					break;
				}
			}
		}
		
		return syncError;
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param code
	 */
	public void removeError(String source, String target, String code) throws DBException
	{
		Context context = Registry.getActiveInstance().getContext();
		List<SyncError> syncErrors = this.getSyncErrors();
		if(syncErrors != null)
		{
			for(SyncError cour:syncErrors)
			{
				if(cour.getSource().equals(source) && 
				   cour.getTarget().equals(target) && 
				   cour.getCode().equals(code))
				{
					Database.getInstance(context).
					delete(Database.sync_error, cour.getRecord());
					break;
				}
			}
		}
	}
}
