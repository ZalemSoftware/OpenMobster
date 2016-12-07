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
public class Anchor implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6100648877132242195L;
	
	/**
	 * 
	 */
	private Long id = null;
	private String target = null;
	private String lastSync = null;
	private String nextSync = null;
	private String app = null;
	
	/**
	 * 
	 *
	 */
	public Anchor()
	{
		
	}

	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}


	public String getLastSync()
	{
		return lastSync;
	}

	public void setLastSync(String lastSync)
	{
		this.lastSync = lastSync;
	}

	public String getNextSync()
	{
		return nextSync;
	}

	public void setNextSync(String nextSync)
	{
		this.nextSync = nextSync;
	}


	public String getTarget()
	{
		return target;
	}


	public void setTarget(String target)
	{
		this.target = target;
	}


	public String getApp()
	{
		return app;
	}


	public void setApp(String app)
	{
		this.app = app;
	}
}
