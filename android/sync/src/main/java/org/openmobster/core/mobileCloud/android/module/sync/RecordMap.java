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
public final class RecordMap
{
	/**
	 * 
	 */	
	private String cmdId; //required
	private String source; //required
	private String target; //required
	
	private String meta; //not-required
	
	private List<MapItem> mapItems; //one-to-many
	
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

	public List<MapItem> getMapItems()
	{
		if(this.mapItems == null)
		{
			this.mapItems = new ArrayList<MapItem>();
		}
		return mapItems;
	}

	public void setMapItems(List<MapItem> mapItems)
	{
		this.mapItems = mapItems;
	}
	
	public void addMapItem(MapItem mapItem)
	{
		this.getMapItems().add(mapItem);
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
