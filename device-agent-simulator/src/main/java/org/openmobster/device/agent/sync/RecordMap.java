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
public final class RecordMap
{
	/**
	 * 
	 */	
	private String cmdId; //required
	private String source; //required
	private String target; //required
	
	private String meta; //not-required
	
	private Vector mapItems; //one-to-many
	
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

	public Vector getMapItems()
	{
		if(this.mapItems == null)
		{
			this.mapItems = new Vector();
		}
		return mapItems;
	}

	public void setMapItems(Vector mapItems)
	{
		this.mapItems = mapItems;
	}
	
	public void addMapItem(MapItem mapItem)
	{
		this.getMapItems().addElement(mapItem);
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
