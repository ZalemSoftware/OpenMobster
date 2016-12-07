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
@ChannelInfo(uri="large_object_channel", mobileBeanClass="test.openmobster.device.agent.sync.server.LargeObject")
public class LargeObjectChannel implements Channel
{
	private List<LargeObject> largeObjects;
	
	/**
	 * 
	 * 
	 */
	public LargeObjectChannel()
	{
		this.largeObjects = new ArrayList<LargeObject>();
	}
	
	public void start()
	{
		StringBuilder messageBuilder = new StringBuilder();
		
		/*StringBuilder packetBuilder = new StringBuilder();
		for(int i=0; i<1000; i++)
		{
			packetBuilder.append("a");
		}
		
		String packet = packetBuilder.toString();
		for(int i=0; i<100; i++)
		{
			messageBuilder.append(packet);
		}*/
		
		String message = messageBuilder.toString();
		for(int i=0; i<2; i++)
		{
			LargeObject largeObject = new LargeObject();
			largeObject.setSyncId(Utilities.generateUID());
			largeObject.setMessage(message);
			
			this.largeObjects.add(largeObject);
		}
	}
	//---MobileConnector Implementation--------------------------------------------------------------------------------------------------------		
	/**
	 * 
	 */
	public List<? extends MobileBean> readAll() 
	{
		return this.largeObjects;
	}
	
	/**
	 * 
	 */
	public MobileBean read(String id)
	{
		for(LargeObject largeObject:this.largeObjects)
		{
			if(largeObject.getSyncId().equals(id))
			{
				return largeObject;
			}
		}
		return null;
	}
	
	public List<? extends MobileBean> bootup() 
	{		
		return null;
	}
	
	/**
	 * 
	 */
	public String create(MobileBean object)
	{
		LargeObject newLargeObject = (LargeObject)object;
		
		this.addNewLargeObject(newLargeObject);
		
		return newLargeObject.getSyncId();
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
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public void addNewLargeObject(LargeObject largeObject)
	{
		this.largeObjects.add(largeObject);
	}
}
