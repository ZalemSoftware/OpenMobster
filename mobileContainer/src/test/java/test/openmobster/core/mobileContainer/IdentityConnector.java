/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileContainer;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="identity",
		     mobileBeanClass="test.openmobster.core.mobileContainer.Identity")
public class IdentityConnector implements Channel
{
	private static Logger log = Logger.getLogger(IdentityConnector.class);
	
	private List identities = new ArrayList();
	
	public List<? extends MobileBean> bootup() 
	{		
		return this.readAll();
	}
	
	/**
	 * 
	 */
	public List<MobileBean> readAll() 
	{
		return this.identities;
	}
	
	/**
	 * 
	 */
	public MobileBean read(String id) 
	{			
		return this.find(id);
	}
	
	/**
	 * 
	 */
	public String create(MobileBean object)
	{
		Identity identity = (Identity)object;
		
		identity.setId(this.generateNewId());		
		
		this.identities.add(identity);
		
		return String.valueOf(identity.getId());
	}
	
	/**
	 * 
	 */
	public void update(MobileBean object)
	{
		Identity identity = (Identity)object;
		
		if(this.find(identity.getId()) != null)
		{						
			this.identities.remove(this.find(identity.getId()));
			this.identities.add(identity);
		}		
	}
	
	/**
	 * 
	 */
	public void delete(MobileBean object) 
	{		
		Identity identity = (Identity)object;
		if(this.find(identity.getId()) != null)
		{
			this.identities.remove(identity);
		}
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
	//--------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	private Identity find(String id)
	{
		Identity identity = null;
		
		for(int i=0; i<this.identities.size(); i++)
		{
			Identity cour = (Identity)this.identities.get(i);
			if(cour.getId().equals(id))
			{
				identity = cour;
				break;
			}
		}
		
		return identity;
	}
	
	/**
	 * 
	 * @return
	 */
	private String generateNewId()
	{
		String newId = null;
		
		if(this.identities.size()>0)
		{
			Identity identity = (Identity)this.identities.get(this.identities.size()-1);
			long cour = Long.parseLong(identity.getId());
			newId = String.valueOf(cour +1);
		}
		else
		{
			newId = "1";
		}
		
		return newId;
	}
}
