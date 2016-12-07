/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.openmobster.device.agent.sync.server;

import java.util.Date;
import java.util.List;

import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 *
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="txcheck", 
	       mobileBeanClass="test.openmobster.device.agent.sync.server.TXBean")
public class TXCheckChannel implements Channel
{
	private TXCheckDAO dao;
	
	public TXCheckDAO getDao()
	{
		return dao;
	}

	public void setDao(TXCheckDAO dao)
	{
		this.dao = dao;
	}

	public List<? extends MobileBean> bootup()
	{
		return null;
	}
	
	public MobileBean read(String id)
	{
		return null;
	}

	public List<? extends MobileBean> readAll()
	{
		return null;
	}

	public String create(MobileBean mobileBean)
	{	
		List all = this.dao.readAll();
		
		if(all == null || all.isEmpty())
		{
			return this.dao.create((TXBean)mobileBean, false);
		}
		else
		{
			return this.dao.create((TXBean)mobileBean, true);
		}
	}

	public void update(MobileBean mobileBean)
	{
	}
	
	public void delete(MobileBean mobileBean)
	{
	}

	public String[] scanForDeletions(Device device, Date lastScanTimestamp)
	{
		return null;
	}

	public String[] scanForNew(Device device, Date lastScanTimestamp)
	{
		return null;
	}

	public String[] scanForUpdates(Device device, Date lastScanTimestamp)
	{
		return null;
	}
}
