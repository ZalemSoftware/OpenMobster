/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import java.util.List;
import java.util.ArrayList;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractOperation
{
	protected String cmdId; //required
	protected String meta; //nullable
	protected List<Item> items; //one or many
	
			
	public AbstractOperation()
	{
		this.items = new ArrayList<Item>();
	}
	
	public String getCmdId()
	{
		return cmdId;
	}


	public void setCmdId(String cmdId)
	{
		this.cmdId = cmdId;
	}


	public List<Item> getItems()
	{
		if(this.items == null)
		{
			this.items = new ArrayList<Item>();
		}
		return items;
	}


	public void setItems(List<Item> items)
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
	
	public void addItem(Item item)
	{
		this.getItems().add(item);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isChunked()
	{
		boolean isChunked = false;
		
		for(Item item:this.getItems())
		{
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
	 */
	public Object clone()
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
				
		((AbstractOperation)clone).cmdId = this.cmdId;
		((AbstractOperation)clone).meta = this.meta;
		
		((AbstractOperation)clone).getItems().clear();
		for(Item item: this.getItems())
		{			
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
