/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

import java.io.Serializable;


/**
 * 
 * @author openmobster@gmail.com
 */
public class Item implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6100648877132242195L;
	
	private String target = null; //zero to one
	private String source = null; //zero to one
	private String data = null; //zero to one
	private String meta = null; //zero to one
	private boolean moreData = false; //zero to one
		
		
	/**
	 * 
	 *
	 */
	public Item()
	{
		
	}	
	//-------------------------------------------------------------------------------------------------------------
	/**
	 * 
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

	/**
	 * 
	 * @return
	 */
	public String getData()
	{
		return data;
	}

	/**
	 * 
	 * @param data
	 */
	public void setData(String data)
	{
		this.data = data;
	}

	/**
	 * 
	 * @return
	 */
	public String getMeta()
	{
		return meta;
	}

	/**
	 * 
	 * @param meta
	 */
	public void setMeta(String meta)
	{
		this.meta = meta;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasMoreData()
	{
		return moreData;
	}

	/**
	 * 
	 * @param moreData
	 */
	public void setMoreData(boolean moreData)
	{
		this.moreData = moreData;
	}

	/**
	 * 
	 */
	public Object clone() throws CloneNotSupportedException
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
