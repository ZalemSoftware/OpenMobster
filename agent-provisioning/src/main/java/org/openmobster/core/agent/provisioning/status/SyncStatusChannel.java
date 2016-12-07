/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.agent.provisioning.status;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="syncstatuschannel", 
		   mobileBeanClass="org.openmobster.core.agent.provisioning.status.SyncStatusBean")
public class SyncStatusChannel implements Channel
{
	//---Channel implementation-----------------------------------------------------------------------------------
	public List<? extends MobileBean> bootup() 
	{
		List<MobileBean> list = new ArrayList<MobileBean>();
		
		for(int i=0; i<5; i++)
		{
			SyncStatusBean bean = new SyncStatusBean();
			bean.setBeanId(""+i);
			bean.setValue("/status/"+i);
			list.add(bean);
		}
		
		return list;
	}

	public String create(MobileBean mobileBean) 
	{		
		return null;
	}

	public void delete(MobileBean mobileBean) 
	{		
		
	}

	public MobileBean read(String id) 
	{		
		return null;
	}

	public List<? extends MobileBean> readAll() 
	{		
		return null;
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

	public void update(MobileBean mobileBean) 
	{				
	}	
}
