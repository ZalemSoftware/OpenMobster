/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.openmobster.device.agent.sync.server;

import org.openmobster.cloud.api.sync.MobileBeanId;
import org.openmobster.cloud.api.sync.MobileBean;

/**
 *
 * @author openmobster@gmail.com
 */
public class TXBean implements MobileBean
{
	private Long uid;
	
	@MobileBeanId
	private String oid;
	private String name;
	
	public TXBean()
	{
		
	}

	public Long getUid()
	{
		return uid;
	}

	public void setUid(Long uid)
	{
		this.uid = uid;
	}

	public String getOid()
	{
		return oid;
	}

	public void setOid(String oid)
	{
		this.oid = oid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
