/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Anchor
{	
	/**
	 * 
	 */
	private String id;
	private String target;
	private String lastSync;
	private String nextSync;
	
	/**
	 * 
	 *
	 */
	public Anchor()
	{
	}
	
	public Anchor(Record record)
	{
		this.id = record.getRecordId();
		this.target = record.getValue("target");
		this.lastSync = record.getValue("lastSync");
		this.nextSync = record.getValue("nextSync");
	}
	
	public Record getRecord()
	{
		Record record = new Record();
		
		if(this.id != null && this.id.trim().length() > 0)
		{
			record.setRecordId(this.id);
		}
		
		record.setValue("target", this.target);
		record.setValue("lastSync", this.lastSync);
		record.setValue("nextSync", this.nextSync);
		
		return record;
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}


	public String getLastSync()
	{
		return lastSync;
	}

	public void setLastSync(String lastSync)
	{
		this.lastSync = lastSync;
	}

	public String getNextSync()
	{
		return nextSync;
	}

	public void setNextSync(String nextSync)
	{
		this.nextSync = nextSync;
	}


	public String getTarget()
	{
		return target;
	}


	public void setTarget(String target)
	{
		this.target = target;
	}
}
