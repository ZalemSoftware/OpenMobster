/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync;

import java.util.Vector;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractOperation
{
	protected String cmdId; //required
	protected String meta; //nullable
	protected Vector items; //one or many
	
			
	public AbstractOperation()
	{
		this.items = new Vector();
	}
	
	public String getCmdId()
	{
		return cmdId;
	}


	public void setCmdId(String cmdId)
	{
		this.cmdId = cmdId;
	}


	public Vector getItems()
	{
		if(this.items == null)
		{
			this.items = new Vector();
		}
		return items;
	}


	public void setItems(Vector items)
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
		this.getItems().addElement(item);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isChunked()
	{
		boolean isChunked = false;
		
		for(int i=0, size=this.getItems().size();i<size;i++)
		{
			Item item = (Item)this.getItems().elementAt(i);
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
		
		((AbstractOperation)clone).getItems().removeAllElements();
		for(int i=0, size=this.getItems().size();i<size;i++)
		{
			Item item = (Item)this.getItems().elementAt(i);			
			Item clonedItem = (Item)item.clone();
			
			((AbstractOperation)clone).getItems().addElement(clonedItem);
		}
		
		return clone;
	}
}
