/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.storage;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author openmobster@gmail.com
 */
final class Cache
{
	private Map<String,Record> cache;
	
	private long size = 10000;
	
	Cache()
	{
	}
	
	void start()
	{
		this.cache = new HashMap<String,Record>();
	}
	
	void stop()
	{
		this.cache = null;
	}
	//----------------------------------------------------------------------------------------
	synchronized void put(String table,String recordId,Record record) throws DBException
	{
		try
		{
			//clear the cache if size limit is reached
			if(this.cache.size() >= this.size)
			{
				this.clear(table);
			}
			
			this.cache.put(table+":"+recordId, record);
		}
		catch(Exception e)
		{
			throw new DBException(Cache.class.getName(),"put", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
	}
	
	synchronized Record get(String table,String recordId) throws DBException
	{
		try
		{
			Record record = this.cache.get(table+":"+recordId);
			return record;
		}
		catch(Exception e)
		{
			throw new DBException(Cache.class.getName(),"get", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
	}
	
	synchronized Map<String,Record> all(String table) throws DBException
	{
		try
		{
			Map<String,Record> all = new HashMap<String,Record>();
			
			Set<String> keys = this.cache.keySet();
			for(String key:keys)
			{
				if(key.startsWith(table+":"))
				{
					Record record = this.cache.get(key);
					all.put(record.getRecordId(),record);
				}
			}
			
			return all;
		}
		catch(Exception e)
		{
			throw new DBException(Cache.class.getName(),"all", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
		}
	}
	
	synchronized void invalidate(String table,String recordId) throws DBException
	{
		this.cache.remove(table+":"+recordId);
	}
	
	synchronized void clear(String table) throws DBException
	{
		Set<String> keys = this.cache.keySet();
		Set<String> delete = new HashSet<String>();
		for(String key:keys)
		{
			if(key.startsWith(table+":"))
			{
				delete.add(key);
			}
		}
		
		//now delete them
		for(String deleteMe:delete)
		{
			this.cache.remove(deleteMe);
		}
	}
}
