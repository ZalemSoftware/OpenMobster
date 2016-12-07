/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services;

import java.util.Date;
import java.util.List;

import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="twitterChannel", 
		   mobileBeanClass="org.openmobster.core.services.TwitterBean")
public class TwitterChannel implements Channel
{
	private boolean newScanned = false;
	private int newScannedCount = 2;
	
	//---Channel implementation-----------------------------------------------------------------------------------
	public List<? extends MobileBean> bootup() 
	{
		return null;
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
		if(this.newScannedCount > 0)
		{
			this.newScanned = true;
			String[] newBeans = new String[]{"new://1/"+newScannedCount,"new://2/"+newScannedCount,"new://3/"+newScannedCount};
			this.newScannedCount--;
			return newBeans;
		}
		else
		{
			return null;
		}
	}

	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{		
		return null;
	}

	public void update(MobileBean mobileBean) 
	{				
	}	
}
