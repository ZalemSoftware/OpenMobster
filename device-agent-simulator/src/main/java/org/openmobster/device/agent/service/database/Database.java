/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.service.database;

import java.util.Enumeration;
import java.util.Vector;
import java.util.List;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmobster.core.common.database.HibernateManager;

import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.ServiceManager;

/**
 * @author openmobster@gmail.com
 * 
 * Concurrency Marker - Concurrent Component (concurrently used by multiple threads)
 *
 */
public final class Database 
{	
	private static Logger log = Logger.getLogger(Database.class);
	
	public static String config_table = "tb_config"; //stores container configuration
	public static String sync_changelog_table = "tb_changelog"; //stores changelog for the sync service
	public static String sync_anchor = "tb_anchor"; //stores anchor related data for the sync service
	public static String sync_recordmap = "tb_recordmap"; //stores record map related data for the sync service
	public static String sync_error = "tb_sync_error"; //stores sync errors
	
	public Database()
	{
		
	}
	
	private HibernateManager hibernateManager;
	
	public HibernateManager getHibernateManager()
	{
		return this.hibernateManager;
	}
	
	public void setHibernateManager(HibernateManager hibernateManager)
	{
		this.hibernateManager = hibernateManager;
	}
		
	public static Database getInstance() throws DBException
	{
		return (Database)ServiceManager.locate("simulator://Database");
	}				
	//----------Select operations------------------------------------------------------------------------------------------------------------	
	public Enumeration selectAll(String from) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			Enumeration allRecords = null;
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			List all = session.createQuery("from "+this.getTableName(from)).list();
			if(all != null)
			{
				allRecords = this.readAll(all);
			}
			
			tx.commit();
			return allRecords;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new DBException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	
	public Record select(String from, String recordId) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			Record record = null;
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			List all = session.createQuery("from "+this.getTableName(from)).list();
			if(all != null)
			{
				record = this.findRecord(all, recordId);
			}
			
			tx.commit();
			return record;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new DBException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}	
	//--------Insert operations------------------------------------------------------------------------------------------------------------------
	public String insert(String into, Record record) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			if(record.getRecordId() == null || record.getRecordId().trim().length() == 0)
			{
				record.setRecordId(Utilities.generateUID());
			}
			record.setDirtyStatus(String.valueOf(System.currentTimeMillis()));
			
			AbstractTable tableEntry = this.createTableEntry(into);
			tableEntry.setRecord(record);
			session.save(tableEntry);
			
			tx.commit();
			
			return tableEntry.getRecord().getRecordId();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new DBException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	//--------Update operations------------------------------------------------------------------------------------------------------------------
	public void update(String into, Record record) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			List all = session.createQuery("from "+this.getTableName(into)).list();
			if(all != null)
			{
				this.update(all, record);
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new DBException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	//--------Delete operations------------------------------------------------------------------------------------------------------------------
	public void delete(String from, Record record) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			List all = session.createQuery("from "+this.getTableName(from)).list();
			if(all != null)
			{
				AbstractTable stored = this.findTableEntry(all, record.getRecordId());
				session.delete(stored);
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new DBException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}
	
	public void deleteAll(String from) throws DBException
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			List all = session.createQuery("from "+this.getTableName(from)).list();
			if(all != null)
			{
				for(int i=0,size=all.size(); i<size; i++)
				{
					session.delete(all.get(i));
				}
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new DBException(e);
		}
		finally
		{
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
	}	
	//------Table related operations-------------------------------------------------------------------------------------------------------------		
	private Enumeration readAll(List table)
	{		
		Vector output = new Vector();
		int size = table.size();
		for(int i=0; i<size; i++)
		{
			AbstractTable curr = (AbstractTable)table.get(i);
			output.addElement(curr.getRecord());
		}
		return output.elements();
	}
	
	private void update(List table, Record record) throws DBException
	{
		AbstractTable stored = this.findTableEntry(table, record.getRecordId());
		
		if(stored != null)
		{
			Record storedRecord = stored.getRecord();
			
			//Make sure the input Record is not Stale			
			if(!record.getDirtyStatus().equals(storedRecord.getDirtyStatus()))
			{
				//The input record is stale
				throw new DBException(DBException.ERROR_RECORD_STALE);
			}
			
			//Record checks out, update it			
			record.setDirtyStatus(String.valueOf(System.currentTimeMillis()));
			stored.setRecord(record);
		}
		else
		{
			//The Record that user is trying to update has been deleted from the database
			throw new DBException(DBException.ERROR_RECORD_DELETED);
		}
	}
		
	private Record findRecord(List table, String recordId)
	{
		Record found = null;
		
		int size = table.size();
		for(int i=0; i<size; i++)
		{
			AbstractTable curr = (AbstractTable)table.get(i);
			Record cour = curr.getRecord();
			String courId = cour.getRecordId();
			if(recordId.equals(courId))
			{
				return cour;
			}
		}
		
		return found;
	}
	
	private AbstractTable findTableEntry(List table, String recordId)
	{
		AbstractTable found = null;
		
		int size = table.size();
		for(int i=0; i<size; i++)
		{
			AbstractTable curr = (AbstractTable)table.get(i);
			Record cour = curr.getRecord();
			String courId = cour.getRecordId();
			if(recordId.equals(courId))
			{
				return curr;
			}
		}
		
		return found;
	}
	
	private String getTableName(String tableAlias)
	{
		String table = null;
		
		if(tableAlias.equals(this.sync_changelog_table))
		{
			table = ChangeLogTable.class.getName();
		}
		else if(tableAlias.equals(this.sync_anchor))
		{
			table = AnchorTable.class.getName();
		}
		else if(tableAlias.equals(this.sync_recordmap))
		{
			table = RecordMapTable.class.getName();
		}
		else if(tableAlias.equals(this.sync_error))
		{
			table = SyncErrorTable.class.getName();
		}
		
		return table;
	}
	
	private AbstractTable createTableEntry(String tableAlias)
	{
		AbstractTable tableEntry = null;
		
		if(tableAlias.equals(this.sync_changelog_table))
		{
			tableEntry = new ChangeLogTable();
		}
		else if(tableAlias.equals(this.sync_anchor))
		{
			tableEntry = new AnchorTable();
		}
		else if(tableAlias.equals(this.sync_recordmap))
		{
			tableEntry = new RecordMapTable();
		}
		else if(tableAlias.equals(this.sync_error))
		{
			tableEntry = new SyncErrorTable();
		}
		
		return tableEntry;
	}
}
