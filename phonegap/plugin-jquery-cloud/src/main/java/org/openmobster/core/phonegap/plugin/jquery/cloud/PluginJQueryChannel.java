/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin.jquery.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 *
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="plugin_jquery_channel", mobileBeanClass="org.openmobster.core.phonegap.plugin.jquery.cloud.JQueryBean")
public class PluginJQueryChannel implements Channel
{
private static Logger log = Logger.getLogger(PluginJQueryChannel.class);
	
	private Store objectStore;
	
	public void start()
	{
		log.info("************************************");
		log.info("JQueryChannel successfully started....");
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
		List<JQueryBean> beans = new ArrayList<JQueryBean>();
		if(all != null && !all.isEmpty())
		{
			for(Object bean:all)
			{
				beans.add((JQueryBean)bean);
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
		return (JQueryBean)this.objectStore.read(id);
	}

	@Override
	public String create(MobileBean mobileBean) 
	{
		JQueryBean toCreate = (JQueryBean)mobileBean;
		return this.objectStore.save(toCreate.getOid(), toCreate);
	}

	@Override
	public void update(MobileBean mobileBean) 
	{
		JQueryBean toUpdate = (JQueryBean)mobileBean;
		this.objectStore.save(toUpdate.getOid(), toUpdate);
	}

	@Override
	public void delete(MobileBean mobileBean) 
	{	
		JQueryBean toDelete = (JQueryBean)mobileBean;
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
