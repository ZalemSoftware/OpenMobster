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
public final class Alert
{	
	/**
	 * 
	 */
	private String cmdId; //required
	private String data; //zero to one
	private List<Item> items; //zero to many items
	
	/**
	 * 
	 *
	 */
	public Alert()
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

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public List<Item> getItems()
	{
		return items;
	}

	public void setItems(List<Item> items)
	{
		if(items != null)
		{
			this.items = items;
		}
		else
		{
			this.items = new ArrayList<Item>();
		}
	}
	
	/**
	 * 
	 * @param item
	 */
	public void addItem(Item item)
	{
		this.items.add(item);
	}
}
