/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin.sandbox.cloud;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 *
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="phonegap_channel", mobileBeanClass="org.openmobster.core.phonegap.plugin.sandbox.cloud.PhoneGapBean")
public class PhoneGapChannel implements Channel
{
	private static Logger log = Logger.getLogger(PhoneGapChannel.class);
	
	private Store objectStore;
	
	public void start()
	{
		log.info("************************************");
		log.info("PhoneGapChannel successfully started....");
		log.info("************************************");
	}
	
	
	
	public Store getObjectStore()
	{
		return objectStore;
	}



	public void setObjectStore(Store objectStore)
	{
		this.objectStore = objectStore;
	}



	@Override
	public List<? extends MobileBean> readAll() 
	{
		Collection<Object> all = this.objectStore.readAll();
		List<PhoneGapBean> beans = new ArrayList<PhoneGapBean>();
		if(all != null && !all.isEmpty())
		{
			for(Object bean:all)
			{
				beans.add((PhoneGapBean)bean);
			}
		}
		
		return beans;
	}

	@Override
	public List<? extends MobileBean> bootup() 
	{
		return this.readAll();
	}
	
	@Override
	public MobileBean read(String id) 
	{
		return (PhoneGapBean)this.objectStore.read(id);
	}

	@Override
	public String create(MobileBean mobileBean) 
	{
		PhoneGapBean toCreate = (PhoneGapBean)mobileBean;
		return this.objectStore.save(toCreate.getOid(), toCreate);
	}

	@Override
	public void update(MobileBean mobileBean) 
	{
		PhoneGapBean toUpdate = (PhoneGapBean)mobileBean;
		this.objectStore.save(toUpdate.getOid(), toUpdate);
	}

	@Override
	public void delete(MobileBean mobileBean) 
	{	
		PhoneGapBean toDelete = (PhoneGapBean)mobileBean;
		this.objectStore.delete(toDelete.getOid());
	}

	@Override
	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{
		return null;
	}

	@Override
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		return null;
	}

	@Override
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{
		return null;
	}
}
