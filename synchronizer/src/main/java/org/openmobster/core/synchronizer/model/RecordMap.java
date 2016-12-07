/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author openmobster@gmail.com
 */
public class RecordMap implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4311464685511813381L;
	
	private String cmdId = null; //required
	private String source = null; //required
	private String target = null; //required
	
	private String meta = null; //not-required
	
	private List mapItems = null; //one-to-many
	
	/**
	 * 
	 *
	 */
	public RecordMap()
	{
		
	}

	public String getCmdId()
	{
		return cmdId;
	}

	public void setCmdId(String cmdId)
	{
		this.cmdId = cmdId;
	}

	public List getMapItems()
	{
		if(this.mapItems == null)
		{
			this.mapItems = new ArrayList();
		}
		return mapItems;
	}

	public void setMapItems(List mapItems)
	{
		this.mapItems = mapItems;
	}

	public String getMeta()
	{
		return meta;
	}

	public void setMeta(String meta)
	{
		this.meta = meta;
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
}
