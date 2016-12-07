/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.engine;

import java.io.Serializable;

/**
 * 
 * @author openmobster@gmail.com
 */
public class RecordMap implements Serializable
{
	private Long id = null;
	private String source = null;
	private String target = null;
	private Object guid = null;
	private Object luid = null;
	
	/**
	 * 
	 *
	 */
	public RecordMap()
	{
		
	}
		
	/**
	 * 
	 * @return
	 */
	public Long getId()
	{
		return id;
	}


	/**
	 * 
	 * @param id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public Object getGuid()
	{
		return guid;
	}

	/**
	 * 
	 * @param guid
	 */
	public void setGuid(Object guid)
	{
		this.guid = guid;
	}

	/**
	 * 
	 * @return
	 */
	public Object getLuid()
	{
		return luid;
	}

	/**
	 * 
	 * @param luid
	 */
	public void setLuid(Object luid)
	{
		this.luid = luid;
	}

	/**
	 * 
	 * @return
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
}
