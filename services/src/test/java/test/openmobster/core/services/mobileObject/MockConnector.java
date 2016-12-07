/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.services.mobileObject;

import java.util.Date;
import java.util.List;


import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="mock", 
		       mobileBeanClass="java.lang.String")
public class MockConnector implements Channel
{
	/**
	 * 
	 */
	public MobileBean read(String id) 
	{	
		return null;
	}

	/**
	 * 
	 */
	public List<MobileBean> readAll() 
	{		
		return null;
	}
	
	/**
	 * 
	 */
	public List<? extends MobileBean> bootup() 
	{		
		return null;
	}

	/**
	 * 
	 */
	public String create(MobileBean object) 
	{		
		return null;
	}

	public void delete(MobileBean object) 
	{		
	}

	public void update(MobileBean object) 
	{		
	}
	
	public String[] scanForUpdates(Device device, Date lastScanTimestamp)
	{
		return null;
	}
	
	
	public String[] scanForNew(Device device, Date lastScanTimestamp)
	{
		return null;
	}
	
	
	public String[] scanForDeletions(Device device, Date lastScanTimestamp)
	{
		return null;
	}
}
