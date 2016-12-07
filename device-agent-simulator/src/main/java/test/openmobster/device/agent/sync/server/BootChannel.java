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
import java.util.ArrayList;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.common.Utilities;



import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="bootChannel", mobileBeanClass="test.openmobster.device.agent.sync.server.BootObject")
public class BootChannel implements Channel
{
	private List<BootObject> data = new ArrayList<BootObject>();
	public BootChannel()
	{
	}
	
	public void start()
	{
		for(int i=0; i<5; i++)
		{
			BootObject local = new BootObject();
			local.setSyncId(Utilities.generateUID());
			
			this.data.add(local);
		}
	}
	//---MobileConnector Implementation--------------------------------------------------------------------------------------------------------	
	public List<? extends MobileBean> bootup() 
	{		
		throw new RuntimeException("Bootup Failure");
	}
	
	public List<? extends MobileBean> readAll() 
	{
		return this.data;
	}
	
	public MobileBean read(String id)
	{
		for(BootObject local:this.data)
		{
			if(local.getSyncId().equals(id))
			{
				return local;
			}
		}
		return null;
	}
	
	/**
	 * 
	 */
	public String create(MobileBean object)
	{
		return null;
	}
	
	/**
	 * 
	 */
	public void update(MobileBean object)
	{	
	}
	
	/**
	 * 
	 */
	public void delete(MobileBean object)
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
