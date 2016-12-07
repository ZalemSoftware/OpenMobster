/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync.engine;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import org.openmobster.device.agent.sync.Anchor;

import org.openmobster.device.agent.service.database.Database;
import org.openmobster.device.agent.service.database.Record;
import org.openmobster.device.agent.service.database.DBException;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncDataSource
{
	private Database database;
	
	/**
	 * 
	 *
	 */
	public SyncDataSource()
	{
	}
	
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
		
	public Database getDatabase() 
	{
		return database;
	}


	public void setDatabase(Database database) 
	{
		this.database = database;
	}

	/**
	 * 
	 *
	 */
	public void clearAll()
	{
		try
		{
			this.database.deleteAll(Database.sync_recordmap);			
			this.database.deleteAll(Database.sync_error);	
			this.deleteChangeLog();
		}
		catch(DBException dbe)
		{
			throw new RuntimeException(dbe.toString());
		}
	}
	//------Anchor related data services-------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public Anchor readAnchor(String target) throws DBException
	{
		Enumeration anchors = this.database.selectAll(Database.sync_anchor);
		while(anchors.hasMoreElements())
		{
			Record record = (Record)anchors.nextElement();
			String localTarget = record.getValue("target");
			if(localTarget.equals(target))
			{
				return new Anchor(record);
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
		
		if(stored == null)
		{
			//Create a new anchor in the database
			Record record = anchor.getRecord();
			this.database.insert(Database.sync_anchor, record);
		}
		else
		{
			Enumeration anchors = this.database.selectAll(Database.sync_anchor);
			if(anchors != null)
			{
				while(anchors.hasMoreElements())
				{
					Record record = (Record)anchors.nextElement();
					String localTarget = record.getValue("target");
					if(localTarget.equals(target))
					{
						//Update the existing anchor in the database
						record.setValue("target", anchor.getTarget());			
						record.setValue("lastSync", anchor.getLastSync());			
						record.setValue("nextSync", anchor.getNextSync());			
						this.database.update(Database.sync_anchor, record);
					}
				}
			}
		}		
	}	
	
	/**
	 * 
	 * @throws DBException
	 */
	public void deleteAnchor() throws DBException
	{
		this.database.deleteAll(Database.sync_anchor);
	}
	//----ChangeLog Support-----------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param entries
	 */
	public void createChangeLogEntries(Vector entries) throws DBException
	{
		if(entries != null)
		{
			Enumeration enumeration = entries.elements();
			while(enumeration.hasMoreElements())
			{
				ChangeLogEntry cour = (ChangeLogEntry)enumeration.nextElement();
				Record record = cour.getRecord();
				this.database.insert(Database.sync_changelog_table, record);
			}
		}
	}
	
	/**
	 * 
	 */
	public Vector readChangeLog() throws DBException
	{
		Vector changeLog = new Vector();
		
		Enumeration changeLogRecords = this.database.selectAll(Database.sync_changelog_table);
		while(changeLogRecords.hasMoreElements())
		{
			Record record = (Record)changeLogRecords.nextElement();
			changeLog.addElement(new ChangeLogEntry(record));
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
		Enumeration log = this.readChangeLog().elements();
		while(log.hasMoreElements())
		{
			ChangeLogEntry cour = (ChangeLogEntry)log.nextElement();
			if(cour.equals(changeLogEntry))
			{
				toBeDeleted = cour.getRecord();
				break;
			}
		}
		if(toBeDeleted != null)
		{
			this.database.delete(Database.sync_changelog_table, toBeDeleted);
		}
	}
	
	/**
	 * 
	 *
	 */
	public void deleteChangeLog() throws DBException
	{
		this.database.deleteAll(Database.sync_changelog_table);
	}
	//-------Map Support------------------------------------------------------------------------------------
	/**
	 * 
	 */
	private Vector getMappedRecords() throws DBException
	{
		Vector mappedRecords = new Vector();
		
		Enumeration cour = this.database.selectAll(Database.sync_recordmap);
		while(cour.hasMoreElements())
		{
			Record record = (Record)cour.nextElement();
			
			mappedRecords.addElement(new RecordMap(record));
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
	public void saveRecordMap(String source, String target, Hashtable map) throws DBException
	{
		if(map != null)
		{
			Enumeration guids = map.keys();
			while(guids.hasMoreElements())
			{
				String guid = (String)guids.nextElement(); 
				String luid = (String)map.get(guid);
				
				RecordMap recordMap = new RecordMap();
				recordMap.setSource(source);
				recordMap.setTarget(target);
				recordMap.setGuid(guid);
				recordMap.setLuid(luid);
				
				this.database.insert(Database.sync_recordmap, recordMap.getRecord());
			}
		}
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public Hashtable readRecordMap(String source, String target) throws DBException
	{
		Hashtable map = null;
		
		map = new Hashtable();
		Vector recordMaps = this.getMappedRecords();
		for(int i=0,size=recordMaps.size(); i<size; i++)
		{
			RecordMap cour = (RecordMap)recordMaps.elementAt(i);
			if(cour.getSource().equals(source) && cour.getTarget().equals(target))
			{
				map.put(cour.getGuid(), cour.getLuid());
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
		Vector recordMaps = this.getMappedRecords();
		for(int i=0,size=recordMaps.size(); i<size; i++)
		{
			RecordMap cour = (RecordMap)recordMaps.elementAt(i);
			if(cour.getSource().equals(source) && cour.getTarget().equals(target))
			{
				Record record = this.database.select(Database.sync_recordmap, cour.getId());
				this.database.delete(Database.sync_recordmap, record);
			}
		}		
	}
	//---Error Support------------------------------------------------------------------------------------
	/**
	 * 
	 */
	private Vector getSyncErrors() throws DBException
	{
		Vector syncErrors = new Vector();
		
		Enumeration cour = this.database.selectAll(Database.sync_error);
		while(cour.hasMoreElements())
		{
			Record record = (Record)cour.nextElement();
			syncErrors.addElement(new SyncError(record));
		}
		
		return syncErrors;
	}
	
	/**
	 * 
	 */
	public void saveError(SyncError error) throws DBException
	{
		Record record = error.getRecord();
		this.database.insert(Database.sync_error, record);
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
		
		Vector syncErrors = this.getSyncErrors();
		for(int i=0,size=syncErrors.size(); i<size; i++)
		{
			SyncError cour = (SyncError)syncErrors.elementAt(i);
			if(cour.getSource().equals(source) && 
			   cour.getTarget().equals(target) && 
			   cour.getCode().equals(code))
			{
				syncError = cour;
				break;
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
		Vector syncErrors = this.getSyncErrors();
		for(int i=0,size=syncErrors.size(); i<size; i++)
		{
			SyncError cour = (SyncError)syncErrors.elementAt(i);
			if(cour.getSource().equals(source) && 
			   cour.getTarget().equals(target) && 
			   cour.getCode().equals(code))
			{
				this.database.delete(Database.sync_error, cour.getRecord());
				break;
			}
		}
	}
}
