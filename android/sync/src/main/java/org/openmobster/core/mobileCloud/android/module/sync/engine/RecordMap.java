/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync.engine;

import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class RecordMap
{
	private String id;
	private String source;
	private String target;
	private String guid;
	private String luid;
	
	/**
	 * 
	 *
	 */
	public RecordMap()
	{
		
	}
	
	public RecordMap(Record record)
	{
		this.id = record.getRecordId();
		this.source = record.getValue("source");
		this.target = record.getValue("target");
		this.guid = record.getValue("guid");
		this.luid = record.getValue("luid");
	}
	
	public Record getRecord()
	{
		Record record = new Record();
		
		if(this.id != null && this.id.trim().length() > 0)
		{
			record.setRecordId(this.id);
		}
		
		record.setValue("source", this.source);
		record.setValue("target", this.target);
		record.setValue("guid", this.guid);
		record.setValue("luid", this.luid);
		
		return record;
	}
		
	/**
	 * 
	 * @return
	 */
	public String getId()
	{
		return id;
	}


	/**
	 * 
	 * @param id
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getGuid()
	{
		return guid;
	}

	/**
	 * 
	 * @param guid
	 */
	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	/**
	 * 
	 * @return
	 */
	public String getLuid()
	{
		return luid;
	}

	/**
	 * 
	 * @param luid
	 */
	public void setLuid(String luid)
	{
		this.luid = luid;
	}

	/**
	 * 
	 * @return
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * 
	 * @param source
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * 
	 * @return
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * 
	 * @param target
	 */
	public void setTarget(String target)
	{
		this.target = target;
	}	
}
