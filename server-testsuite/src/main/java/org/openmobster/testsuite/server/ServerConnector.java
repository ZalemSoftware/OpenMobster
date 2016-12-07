/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.testsuite.server;

import java.util.Date;
import java.util.List;



import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="testServerBean", 
		       mobileBeanClass="org.openmobster.testsuite.server.ServerRecord")
public class ServerConnector implements Channel
{
	/**
	 * 
	 * 
	 */
	public ServerConnector()
	{		
	}	
	
	/**
	 * 
	 * @return
	 */
	protected ServerRecordController getServerController()
	{				
		return ServerRecordController.getInstance();
	}
	//---MobileConnector Implementation--------------------------------------------------------------------------------------------------------		
	/**
	 * 
	 */
	public List<? extends MobileBean> readAll() 
	{
		return this.getServerController().readAll();
	}
	
	/**
	 * 
	 */
	public MobileBean read(String id)
	{
		return this.getServerController().readServerRecord(id);
	}
	
	public List<? extends MobileBean> bootup() 
	{		
		//TODO: reimplement with the subset approach
		return this.readAll();
	}
	
	/**
	 * 
	 */
	public String create(MobileBean object)
	{
		String recordId = null;
		ServerRecord serverRecord = (ServerRecord)object;
		
		/**
         * Used to show server side creation of an object id different from the device
         */
		/* 
		ServerRecord newRecord = new ServerRecord();
		newRecord.setFrom(serverRecord.getFrom());
		newRecord.setTo(serverRecord.getTo());
		newRecord.setMessage(serverRecord.getMessage());
		newRecord.setSubject(serverRecord.getSubject());
		newRecord.setAttachment(serverRecord.getAttachment());
		*/
		
		serverRecord.setUid(null);		
						
		recordId = this.getServerController().create(serverRecord);
		
		return recordId;
	}
	
	/**
	 * 
	 */
	public void update(MobileBean object)
	{
		ServerRecord serverRecord = (ServerRecord)object;
		
		ServerRecord stored = this.getServerController().readServerRecord(serverRecord.getObjectId());
		stored.setFrom(serverRecord.getFrom());
		stored.setTo(serverRecord.getTo());
		stored.setMessage(serverRecord.getMessage());
		stored.setSubject(serverRecord.getSubject());
		stored.setAttachment(serverRecord.getAttachment());
		
		this.getServerController().save(stored);		
	}
	
	/**
	 * 
	 */
	public void delete(MobileBean object)
	{
		ServerRecord record = (ServerRecord)object;
		
		ServerRecord stored = this.getServerController().readServerRecord(record.getObjectId());
		
		this.getServerController().delete(stored);		
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
