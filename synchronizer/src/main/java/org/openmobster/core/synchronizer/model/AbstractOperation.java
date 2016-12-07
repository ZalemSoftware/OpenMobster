/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openmobster.cloud.api.sync.MobileBean;


/**
 * 
 * @author openmobster@gmail.com
 */
public abstract class AbstractOperation implements Serializable
{
	protected String cmdId = null; //required
	protected String meta = null; //nullable
	protected List items = null; //one or many
	
	private MobileBean chunkedRecord = null;
	private LongObject chunkedObject = null;
	
	public AbstractOperation()
	{
		this.items = new ArrayList();
	}
	
	public String getCmdId()
	{
		return cmdId;
	}


	public void setCmdId(String cmdId)
	{
		this.cmdId = cmdId;
	}


	public List getItems()
	{
		if(this.items == null)
		{
			this.items = new ArrayList();
		}
		return items;
	}


	public void setItems(List items)
	{
		this.items = items;
	}


	public String getMeta()
	{
		return meta;
	}


	public void setMeta(String meta)
	{
		this.meta = meta;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isChunked()
	{
		boolean isChunked = false;
		
		for(int i=0;i<this.getItems().size();i++)
		{
			Item item = (Item)this.getItems().get(i);
			if(item.hasMoreData())
			{
				isChunked = true;
				break;
			}
		}
		
		return isChunked;
	}
	
	/**
	 * 
	 * @return
	 */
	public MobileBean getChunkedRecord()
	{
		return chunkedRecord;
	}

	/**
	 * 
	 * @param chunkedRecord
	 */
	public void setChunkedRecord(MobileBean chunkedRecord)
	{
		this.chunkedRecord = chunkedRecord;
	}
	
	/**
	 * 
	 * @return
	 */
	public LongObject getChunkedObject()
	{
		return chunkedObject;
	}

	/**
	 * 
	 * @param chunkedObject
	 */
	public void setChunkedObject(LongObject chunkedObject)
	{
		this.chunkedObject = chunkedObject;
	}

	/**
	 * 
	 */
	public Object clone() throws CloneNotSupportedException
	{
		Object clone = null;
		
		if(this instanceof Add)
		{
			clone = new Add();
		}
		else if(this instanceof Replace)
		{
			clone = new Replace();
		}
		else if(this instanceof Delete)
		{
			clone = new Delete();
			((Delete)clone).setArchive(((Delete)this).isArchive());
			((Delete)clone).setSoftDelete(((Delete)this).isSoftDelete());
		}
		
		((AbstractOperation)clone).chunkedRecord = this.chunkedRecord;
		((AbstractOperation)clone).chunkedObject = this.chunkedObject;
		
		((AbstractOperation)clone).cmdId = this.cmdId;
		((AbstractOperation)clone).meta = this.meta;		
		
		((AbstractOperation)clone).getItems().clear();
		for(int i=0;i<this.getItems().size();i++)
		{
			Item item = (Item)this.getItems().get(i);			
			Item clonedItem = (Item)item.clone();
			
			((AbstractOperation)clone).getItems().add(clonedItem);
		}
		
		return clone;
	}	
	
	public int totalSize()
	{
		int totalSize = 0;
		
		List items = this.getItems();
		if(items == null)
		{
			return 0;
		}
		for(Object local:items)
		{
			Item item = (Item)local;
			
			String data = item.getData();
			if(data != null)
			{
				totalSize += data.length();
			}
		}
		
		return totalSize;
	}
}
