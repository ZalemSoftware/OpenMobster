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
public class SyncError
{
	public static String RESET_SYNC_STATE = "1";
	
	private String id;
	private String code;
	private String source;
	private String target;	
	
	/**
	 * 
	 *
	 */
	public SyncError()
	{
		
	}
	
	public SyncError(Record record)
	{
		this.id = record.getRecordId();
		this.code = record.getValue("code");
		this.source = record.getValue("source");
		this.target = record.getValue("target");
	}
	
	public Record getRecord()
	{
		Record record = new Record();
		
		if(this.id != null && this.id.trim().length() > 0)
		{
			record.setRecordId(this.id);
		}
		
		record.setValue("code", this.code);
		record.setValue("source", this.source);
		record.setValue("target", this.target);
		
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
	public String getCode()
	{
		return code;
	}

	/**
	 * 
	 * @param code
	 */
	public void setCode(String code)
	{
		this.code = code;
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
