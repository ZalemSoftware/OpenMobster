/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Item
{	
	private String target; //zero to one
	private String source; //zero to one
	private String data; //zero to one
	private String meta; //zero to one
	private boolean moreData; //zero to one
		
		
	/**
	 * 
	 *
	 */
	public Item()
	{
		
	}

	public String getSource()
	{
		return source;
	}


	public void setSource(String source)
	{
		this.source = source;
	}


	public String getTarget()
	{
		return target;
	}


	public void setTarget(String target)
	{
		this.target = target;
	}


	public String getData()
	{
		return data;
	}


	public void setData(String data)
	{
		this.data = data;
	}

	public String getMeta()
	{
		return meta;
	}

	public void setMeta(String meta)
	{
		this.meta = meta;
	}

	public boolean hasMoreData()
	{
		return moreData;
	}

	public void setMoreData(boolean moreData)
	{
		this.moreData = moreData;
	}

	/**
	 * 
	 */
	public Object clone()
	{		
		Object clone = null;
		
		clone = new Item();
		
		((Item)clone).target = this.target;
		((Item)clone).source = this.source;
		((Item)clone).data = this.data;
		((Item)clone).meta = this.meta;
		((Item)clone).moreData = this.moreData;
		
		return clone;
	}	
}
