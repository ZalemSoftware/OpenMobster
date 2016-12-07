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
@ChannelInfo(uri="queryChannel", mobileBeanClass="test.openmobster.device.agent.sync.server.QueryObject")
public class QueryChannel implements Channel
{
	private List<QueryObject> objects;
	
	/**
	 * 
	 * 
	 */
	public QueryChannel()
	{
		this.objects = new ArrayList<QueryObject>();
	}
	
	public void start()
	{
		for(int i=0; i<5; i++)
		{
			QueryObject queryObject = new QueryObject();
			Message message = new Message();
			queryObject.setMessage(message);
			
			queryObject.setSyncId(Utilities.generateUID());
			queryObject.setFrom(i+"/from");
			queryObject.setTo(i+"/to");
			message.setFrom(i+"/message/from");
			message.setTo(i+"/message/to");
			
			this.objects.add(queryObject);
		}
	}
	//---MobileConnector Implementation--------------------------------------------------------------------------------------------------------		
	/**
	 * 
	 */
	public List<? extends MobileBean> readAll() 
	{
		return this.objects;
	}
	
	/**
	 * 
	 */
	public MobileBean read(String id)
	{
		for(QueryObject object:this.objects)
		{
			if(object.getSyncId().equals(id))
			{
				return object;
			}
		}
		return null;
	}
	
	public List<? extends MobileBean> bootup() 
	{		
		return this.objects;
	}
	
	/**
	 * 
	 */
	public String create(MobileBean object)
	{
		this.objects.add((QueryObject)object);
		return ((QueryObject)object).getSyncId();
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
